package net.assemble.mailnotify;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 設定管理
 */
public class EmailNotifyPreferences
{
    public static final String PREF_KEY_LICENSE = "license";
    public static final boolean PREF_LICENSE_DEFAULT = false;

    public static final String PREF_KEY_ENABLE = "enable";
    public static final boolean PREF_ENABLE_DEFAULT = true;

    public static final String PREF_KEY_NOTIFY = "notify";
    public static final boolean PREF_NOTIFY_DEFAULT = true;

    public static final String PREF_KEY_NOTIFICATION_ICON = "notification_icon";
    public static final boolean PREF_NOTIFICATION_ICON_DEFAULT = false;

    public static final String PREF_KEY_SERVICE_OMAEMN = "service_omaemn";
    public static final boolean PREF_SERVICE_OMAEMN_DEFAULT = true;

    public static final String PREF_KEY_SERVICE_SPMODE = "service_spmode";
    public static final boolean PREF_SERVICE_SPMODE_DEFAULT = true;

    public static final String PREF_KEY_SERVICE_MOPERA = "service_mopera";
    public static final boolean PREF_SERVICE_MOPERA_DEFAULT = true;

    public static final String PREF_KEY_SERVICE_IMODE = "service_imode";
    public static final boolean PREF_SERVICE_IMODE_DEFAULT = false;

    public static final String PREF_KEY_SERVICE_ANY = "service_any";
    public static final boolean PREF_SERVICE_ANY_DEFAULT = true;

    public static final String PREF_KEY_NOTIFY_VIEW = "notify_view";
    public static final boolean PREF_NOTIFY_VIEW_DEFAULT = true;

    public static final String PREF_KEY_SOUND = "sound";
    public static final String PREF_SOUND_DEFAULT = "content://settings/system/notification_sound";

    public static final String PREF_KEY_VIBRATION = "vibration";
    public static final boolean PREF_VIBRATION_DEFAULT = true;

    public static final String PREF_KEY_VIBRATION_PATTERN = "vibration_pattern";
    public static final String PREF_VIBRATION_PATTERN_DEFAULT = "0";
    public static final long[][] PREF_VIBRATION_PATTERN = {
        { 250, 250, 250, 1000 },        // パターン1
        { 500, 250, 500, 1000 },        // パターン2
        { 1000, 1000, 1000, 1000 },      // パターン3
        { 2000, 500 },                  // パターン4
        { 250, 250, 1000, 1000 },       // パターン5
    };

    public static final String PREF_KEY_VIBRATION_LENGTH = "vibration_length";
    public static final int PREF_VIBRATION_LENGTH_DEFAULT = 3;
    public static final int PREF_VIBRATION_LENGTH_MIN = 1;
    public static final int PREF_VIBRATION_LENGTH_MAX = 30;

    public static final String PREF_KEY_NOTIFY_LED = "notify_led";
    public static final boolean PREF_NOTIFY_LED_DEFAULT = true;

    public static final String PREF_KEY_LED_COLOR = "led_color";
    public static final String PREF_LED_COLOR_DEFAULT = "ff00ff00";

    public static final String PREF_KEY_LAUNCH = "launch";
    public static final boolean PREF_LAUNCH_DEFAULT = false;

    public static final String PREF_KEY_LAUNCH_APP_PACKAGE = "launch_app_package_name";
    public static final String PREF_KEY_LAUNCH_APP_CLASS = "launch_app_class_name";

    public static boolean getEnable(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_ENABLE,
                EmailNotifyPreferences.PREF_ENABLE_DEFAULT);
    }

    public static void setEnable(Context ctx, boolean val) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        e.putBoolean(EmailNotifyPreferences.PREF_KEY_ENABLE, val);
        e.commit();
    }

    public static boolean getLicense(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_LICENSE,
                EmailNotifyPreferences.PREF_LICENSE_DEFAULT);
    }

    public static void setLicense(Context ctx, boolean val) {
        Editor e = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        e.putBoolean(EmailNotifyPreferences.PREF_KEY_LICENSE, val);
        e.commit();
    }

    public static boolean getNotify(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_NOTIFY,
                EmailNotifyPreferences.PREF_NOTIFY_DEFAULT);
    }

    public static boolean getNotificationIcon(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_NOTIFICATION_ICON,
                EmailNotifyPreferences.PREF_NOTIFICATION_ICON_DEFAULT);
    }

    public static boolean getServiceOmaEmn(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_SERVICE_OMAEMN,
                EmailNotifyPreferences.PREF_SERVICE_OMAEMN_DEFAULT);
    }

    public static boolean getServiceSpmode(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_SERVICE_SPMODE,
                EmailNotifyPreferences.PREF_SERVICE_SPMODE_DEFAULT);
    }

    public static boolean getServiceMopera(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_SERVICE_MOPERA,
                EmailNotifyPreferences.PREF_SERVICE_MOPERA_DEFAULT);
    }

    public static boolean getServiceImode(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_SERVICE_IMODE,
                EmailNotifyPreferences.PREF_SERVICE_IMODE_DEFAULT);
    }

    public static boolean getServiceAny(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_SERVICE_ANY,
                EmailNotifyPreferences.PREF_SERVICE_ANY_DEFAULT);
    }

    public static boolean getNotifyView(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_NOTIFY_VIEW,
                EmailNotifyPreferences.PREF_NOTIFY_VIEW_DEFAULT);
    }

    public static String getSound(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                EmailNotifyPreferences.PREF_KEY_SOUND,
                EmailNotifyPreferences.PREF_SOUND_DEFAULT);
    }

    public static boolean getVibration(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_VIBRATION,
                EmailNotifyPreferences.PREF_VIBRATION_DEFAULT);
    }

    public static long[] getVibrationPattern(Context ctx) {
        String val = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                EmailNotifyPreferences.PREF_KEY_VIBRATION_PATTERN, 
                EmailNotifyPreferences.PREF_VIBRATION_PATTERN_DEFAULT);
        int idx =Integer.parseInt(val);
        return PREF_VIBRATION_PATTERN[idx];
    }

    public static int getVibrationLength(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getInt(
                EmailNotifyPreferences.PREF_KEY_VIBRATION_LENGTH,
                EmailNotifyPreferences.PREF_VIBRATION_LENGTH_DEFAULT);
    }

    public static boolean getNotifyLed(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_NOTIFY_LED,
                EmailNotifyPreferences.PREF_NOTIFY_LED_DEFAULT);
    }

    public static int getLedColor(Context ctx) {
        String color = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                EmailNotifyPreferences.PREF_KEY_LED_COLOR,
                EmailNotifyPreferences.PREF_LED_COLOR_DEFAULT);
        int argb = 0;
        for(int i = 0; i < color.length(); i += 2){
            argb *= 256;
            argb += Integer.parseInt(color.substring(i, i + 2), 16);
        }
        return argb;
    }

    public static boolean getLaunch(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(
                EmailNotifyPreferences.PREF_KEY_LAUNCH,
                EmailNotifyPreferences.PREF_LAUNCH_DEFAULT);
    }

    public static ComponentName getComponent(Context ctx) {
        String packageName = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                EmailNotifyPreferences.PREF_KEY_LAUNCH_APP_PACKAGE, null);
        String className = PreferenceManager.getDefaultSharedPreferences(ctx).getString(
                EmailNotifyPreferences.PREF_KEY_LAUNCH_APP_CLASS, null);
        if (packageName == null || className == null) {
            return null;
        }
        return new ComponentName(packageName, className);
    }

}
