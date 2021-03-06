package net.orleaf.android;

import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MyLog {
    public static final int LEVEL_ERROR = 1;
    public static final int LEVEL_WARN = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_DEBUG = 4;
    public static final int LEVEL_VERBOSE = 5;
    public static final int LEVEL_MAX = LEVEL_VERBOSE;

    /**
     * ログ保持期間
     */
    private static final long LOG_ROTATE_LIMIT_SEC = 2 * 24 * 60 * 60; // 2 days

    private static SQLiteDatabase mDb;

    /**
     * データベースを取得
     */
    public static SQLiteDatabase getDb(Context ctx) {
        if (mDb == null) {
            MyLogOpenHelper h = new MyLogOpenHelper(ctx);
            mDb = h.getWritableDatabase();
        }
        return mDb;
    }

    /**
     * 古いログを消去する
     */
    private static void rotate(Context ctx) {
        Calendar cal = Calendar.getInstance();
        long t = cal.getTimeInMillis() / 1000 - LOG_ROTATE_LIMIT_SEC;
        getDb(ctx).delete(MyLogOpenHelper.TABLE_LOG, "created_at < " + t, null);
    }

    /**
     * ログをすべて消去する
     */
    public static void clearAll(Context ctx) {
        getDb(ctx).delete(MyLogOpenHelper.TABLE_LOG, null, null);
    }

    /**
     * ログ採取
     *
     * @param text ログ文字列
     */
    private static void add(Context ctx, int level, @SuppressWarnings("UnusedParameters") String tag, String text) {
        ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        values.put("created_at", cal.getTimeInMillis() / 1000);
        values.put("created_date", cal.getTime().toLocaleString());
        values.put("level", level);
        values.put("log_text", text);
        getDb(ctx).insert(MyLogOpenHelper.TABLE_LOG, null, values);
        rotate(ctx);
    }

    public static void e(Context ctx, String tag, String text) {
        Log.e(tag, text);
        add(ctx, LEVEL_ERROR, tag, text);
    }
    public static void w(Context ctx, String tag, String text) {
        Log.w(tag, text);
        add(ctx, LEVEL_WARN, tag, text);
    }
    public static void i(Context ctx, String tag, String text) {
        Log.i(tag, text);
        add(ctx, LEVEL_INFO, tag, text);
    }
    public static void d(Context ctx, String tag, String text) {
        Log.d(tag, text);
        add(ctx, LEVEL_DEBUG, tag, text);
    }
    public static void v(Context ctx, String tag, String text) {
        Log.v(tag, text);
        add(ctx, LEVEL_VERBOSE, tag, text);
    }

    /**
     * ログを取得
     *
     * @return カーソル
     */
    @SuppressWarnings("unused")
    public static Cursor getLogCursor(Context ctx, int level) {
        return getDb(ctx).query(MyLogOpenHelper.TABLE_LOG,
                null, "level <= " + level, null,
                null, null, "created_at", null);
    }

    /**
     * ログを取得
     *
     * @param maxlevel 取得するログレベルの閾値
     * @return 全ログの文字列
     */
    public static String getLogText(Context ctx, int maxlevel) { 
        Cursor c = getDb(ctx).query(MyLogOpenHelper.TABLE_LOG,
                null, "level <= " + maxlevel, null,
                null, null, "created_at", null);
        StringBuilder buf = new StringBuilder();
        if (c.moveToFirst()) {
            do {
                int level = c.getInt(c.getColumnIndex("level"));
                String date = c.getString(c.getColumnIndex("created_date"));
                String log_text = c.getString(c.getColumnIndex("log_text"));
                buf.append(date).append(" ").append(getLevelString(level)).append(" ").append(log_text).append("\n");
            } while (c.moveToNext());
        }
        c.close();
        return buf.toString();
    }

    public static String getLogText(Context ctx) {
        return getLogText(ctx, LEVEL_MAX);
    }

    /**
     * ログレベルを文字で取得
     * 
     * @param level ログレベル
     * @return ログレベル文字
     */
    private static String getLevelString(int level) {
        if (level == LEVEL_ERROR) {
            return "E";
        } else if (level == LEVEL_WARN) {
            return "W";
        } else if (level == LEVEL_INFO) {
            return "I";
        } else if (level == LEVEL_DEBUG) {
            return "D";
        } else if (level == LEVEL_VERBOSE) {
            return "V";
        }
        return "?";
    }

}
