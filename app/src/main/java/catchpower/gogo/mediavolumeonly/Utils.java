package catchpower.gogo.mediavolumeonly;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;

import java.util.List;

public class Utils {
    static Toast toast;

    public static void forceVolumeControlStream(Context context, int type){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Class param1 = Integer.TYPE;
        Object ret;
        try{
            ret = am.getClass().getMethod("forceVolumeControlStream", new Class[] { param1 }).invoke(am, new Object[] { type });
        }
        catch (Exception e)
        {
            e.printStackTrace();
//            Log.d(TAG, "Exception : " + e.toString());
            return;
        }
    }

    public static boolean checkAccessibilityPermission(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager)context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> list = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo info : list) {
            if (info.getResolveInfo().serviceInfo.packageName.equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkReadPhonePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                return true;
            return false;
        }
        return true;
    }

    public static void setMusicKey(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCallingOn = pref.getBoolean("calling_on", false);
        if(isCallingOn == false) {
            Utils.forceVolumeControlStream(context, AudioManager.STREAM_MUSIC);
        }else{
            Utils.forceVolumeControlStream(context, -1);
        }
    }

    public static void showSmallToast(Context context, String msg, int duration){
        if(toast != null){
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, duration);
        toast.show();
    }
}
