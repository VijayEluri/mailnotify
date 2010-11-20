package net.assemble.emailnotify;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import net.assemble.android.MyLog;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.WspTypeDecoder;

/**
 * メール監視サービス
 */
public class EmailNotifyService extends Service {
    private static final String TAG = "EmailNotify";

    private static ComponentName mService;
    private static boolean mActive = false;

    private LogCheckThread mLogCheckThread;
    private boolean mStopLogCheckThread;
    private long mLastCheck;

    @Override
    public void onCreate() {
        if (EmailNotify.DEBUG) Log.d(TAG, "EmailNotifyService.onCreate()");
        super.onCreate();
        mActive = true;

        // すでに通知したものは通知しないようにする
        mLastCheck = EmailNotifyPreferences.getLastCheck(this);
        if (EmailNotify.DEBUG) Log.d(TAG, "Last notify: " + mLastCheck);
        if (mLastCheck == 0) {
            // 前回通知日時が存在しない場合、サービス開始以前を通知しない。
            mLastCheck = Calendar.getInstance().getTimeInMillis();
            EmailNotifyPreferences.setLastCheck(this, mLastCheck);
        }

        // リアルタイムログ監視開始
        startLogCheckThread();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        if (EmailNotify.DEBUG) Log.d(TAG, "EmailNotifyService.onStart()");
        super.onStart(intent, startId);
        mActive = true;

        // リアルタイムログ監視開始
        startLogCheckThread();
    }

    public void onDestroy() {
        if (EmailNotify.DEBUG) Log.d(TAG, "EmailNotifyService.onDestroy()");
        super.onDestroy();
        mActive = false;

        // リアルタイムログ監視停止
        stopLogCheckThread();

        // 正常に停止した場合は、次に開始するまでに受信した通知は
        // 通知しないようにするため、前回通知日時をクリアする。
        EmailNotifyPreferences.setLastCheck(this, 0);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * ログ日時解析
     * @param line ログ行
     * @return 日時
     */
    private Calendar getLogDate(String line) {
        // 日付
        Calendar cal = Calendar.getInstance();
        String[] days = line.split(" ")[0].split("-");
        String[] times = line.split(" ")[1].split(":");
        cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Integer.valueOf(days[0]) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(days[1]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(times[0]));
        cal.set(Calendar.MINUTE, Integer.valueOf(times[1]));
        cal.set(Calendar.SECOND, Integer.valueOf(times[2].substring(0, 2)));
        cal.set(Calendar.MILLISECOND, 0);

        return cal;
    }

    /**
     * ログ行をチェック
     *
     * @param line ログ文字列
     * @return WapPdu WAP PDU (null:メール通知ではない)
     */
    private WapPdu checkLogLine(String line) {
        if (EmailNotify.DEBUG) Log.v(TAG, "> " + line);
        if (line.length() >= 19 && line.substring(19).startsWith("D/WAP PUSH")/* && line.contains(": Rx: ")*/) {
            Calendar ccal = getLogDate(line);
            if (ccal.getTimeInMillis() <= mLastCheck) {
                // チェック済
                if (EmailNotify.DEBUG) Log.d(TAG, "Already checked (" + ccal.getTimeInMillis() + " <= " + mLastCheck + " )");
                return null;
            }

            // LYNX(SH-01B)対応
            if (line.endsWith(": Receive EMN")) {
                MyLog.i(this, TAG, "Received EMN");
                mLastCheck = ccal.getTimeInMillis();
                return new WapPdu(0x030a);
            }

            if (line.contains(": Rx: ")) {
                String data = line.split(": Rx: ")[1];

                // データ解析
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int i = 0; i < data.length(); i += 2){
                    int b = Integer.parseInt(data.substring(i, i + 2), 16);
                    baos.write(b);
                }
                WapPdu pdu = new WapPdu(baos.toByteArray());
                if (!pdu.decode()) {
                    MyLog.w(this, TAG, "Unexpected PDU: " + data);
                    return null;
                }
                if (pdu.getBinaryContentType() == WspTypeDecoder.CONTENT_TYPE_B_PUSH_SL) {
                    // application/vnd.wap.slc は、Receiverで受信する ので無視
                    if (EmailNotify.DEBUG) {
                        Log.d(TAG, "Received PDU: " + data);
                        Log.i(TAG, "Received: " + pdu.getMailbox());
                    }
                    return null;
                }
                MyLog.d(this, TAG, "Received PDU: " + data);
                MyLog.i(this, TAG, "Received: " + pdu.getMailbox());
                mLastCheck = ccal.getTimeInMillis();
                return pdu;
            }
        }
        return null;
    }

    /**
     * リアルタイムログ監視スレッド
     */
    private class LogCheckThread extends Thread {
        private Context mCtx;

        /**
         * logcatクリア
         */
        private void clearLog() {
            try {
                Process process = Runtime.getRuntime().exec(new String[] {"logcat", "-c" });
                process.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MyLog.d(mCtx, TAG, "Logcat cleared.");
        }

        /**
         * エラー出力を取得する
         */
        private String getErrorMessage(Process process) throws IOException {
            String line;
            BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()), 1024);
            StringBuffer errMsg = new StringBuffer();
            while ((line = errReader.readLine()) != null) {
                errMsg.append(line + "\n");
            }
            return errMsg.toString().trim();
        }

        @Override
        public void run() {
            mCtx = EmailNotifyService.this;
            MyLog.d(mCtx, TAG, "Starting log check thread.");

            String[] command = new String[] {
                "logcat",
                "-v", "time",
                "-s", "*:D"
                 // ほんとうは「WAP PUSH」でフィルタしたいんだけどスペースがあるとうまくいかない…
            };
            int errCount = 0;
            while (true) {
                try {
                    Process process = Runtime.getRuntime().exec(command);
                    BufferedReader bufferedReader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()), 1024);
                    int readCount = 0;
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (mStopLogCheckThread) {
                            break;
                        }
                        readCount++;
                        WapPdu pdu = checkLogLine(line);
                        if (pdu != null) {
                            // 最後に通知した日時を保持しておく
                            EmailNotifyPreferences.setLastCheck(mCtx, mLastCheck);
                            EmailNotifyNotification.showNotify(EmailNotifyService.this, pdu.getMailbox());
                        }
                    }
                    bufferedReader.close();
                    String errMsg = getErrorMessage(process);
                    process.destroy();
                    if (!mStopLogCheckThread) {
                        // 不正終了
                        MyLog.w(mCtx, TAG, "Unexpectedly suspended. read=" + readCount);
                        process.waitFor();
                        MyLog.d(mCtx, TAG, "exitValue=" + process.exitValue()+ "\n" + errMsg);

                        // 5回連続して全く読めなかった場合は通知を出して停止
                        if (readCount == 0) {
                            errCount++;
                            if (errCount >= 5) {
                                break;
                            }
                        } else {
                            errCount = 0;
                        }

                        // ログをクリアして再試行する。
                        clearLog();
                        Thread.sleep(5000);
                        continue;
                    }
                } catch (IOException e) {
                    MyLog.e(mCtx, TAG, "Unexpected error on log checking.");
                    stopSelf();
                } catch (InterruptedException e) {
                    MyLog.e(mCtx, TAG, "Interrupted on log checking.");
                    e.printStackTrace();
                }
                break;
            }

            MyLog.d(mCtx, TAG, "Exiting log check thread.");
            mLogCheckThread = null;
            stopSelf();
        }
    }

    /**
     * リアルタイムログ監視スレッド開始
     */
    private void startLogCheckThread() {
        if (mLogCheckThread != null) {
            if (EmailNotify.DEBUG) Log.d(TAG, "Log check thread already running.");
            return;
        }
        mStopLogCheckThread = false;
        mLogCheckThread = new LogCheckThread();
        mLogCheckThread.start();
    }

    /**
     * リアルタイムログ監視スレッド停止指示
     */
    private void stopLogCheckThread() {
        if (mLogCheckThread == null) {
            if (EmailNotify.DEBUG) Log.d(TAG, "Log check thread not running.");
            return;
        }
        mStopLogCheckThread = true;
    }


    /**
     * サービス開始
     */
    public static boolean startService(Context ctx) {
        boolean result;
        boolean restart = mActive;
        mService = ctx.startService(new Intent(ctx, EmailNotifyService.class));
        if (mService == null) {
            MyLog.e(ctx, TAG, "Service start failed!");
            result = false;
        } else {
            Log.d(TAG, "EmailNotifyService started: " + mService);
            result = true;
        }
        if (!restart && result) {
            Toast.makeText(ctx, R.string.service_started, Toast.LENGTH_SHORT).show();
            MyLog.i(ctx, TAG, "Service started.");
        }
        return result;
    }

    /**
     * サービス停止
     */
    public static void stopService(Context ctx) {
        if (mService != null) {
            Intent i = new Intent();
            i.setComponent(mService);
            boolean res = ctx.stopService(i);
            if (res == false) {
                Log.e(TAG, "EmailNotifyService could not stop!");
            } else {
                Log.d(TAG, "EmailNotifyService stopped: " + mService);
                Toast.makeText(ctx, R.string.service_stopped, Toast.LENGTH_SHORT).show();
                MyLog.i(ctx, TAG, "Service stopped.");
                mService = null;
            }
        }
    }
}