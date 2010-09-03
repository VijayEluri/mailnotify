package net.assemble.mailnotify;

import net.assemble.android.MyLog;

import com.android.internal.telephony.WspTypeDecoder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class EmailNotifyReceiver extends BroadcastReceiver {
    private static final String TAG = "EmailNotify";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent: " + intent.getAction());

        if (!EmailNotifyPreferences.getEnable(context)) {
            return;
        }

        if (intent.getAction() != null) {
            // WAP PUSH
            if (intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) {
                byte[] data = intent.getByteArrayExtra("data");
                WapPdu pdu = new WapPdu(WspTypeDecoder.CONTENT_TYPE_B_PUSH_SL, data);
                if (!pdu.decode()) {
                    MyLog.w(context, TAG, "Unexpected PDU: " + pdu.getHexString());
                    return;
                }
                MyLog.i(context, TAG ,"Received: mailbox=" + pdu.getMailbox() + " (" + pdu.getHexString() + ")");
                if (EmailNotifyPreferences.getServiceImode(context)) {
                    EmailNotifyNotification.doNotify(context, pdu.getMailbox());
                }
            }
            // Restart
            else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Log.i(TAG, "EmailNotify restarted.");
                EmailNotifyService.startService(context);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)
                    || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                EmailNotifyService.startService(context);
            }
            return;
        }

        EmailNotifyService.startService(context);
    }

}