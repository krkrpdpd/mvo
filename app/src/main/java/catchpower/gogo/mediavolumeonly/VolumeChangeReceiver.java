package catchpower.gogo.mediavolumeonly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class VolumeChangeReceiver extends BroadcastReceiver {
    private static final String ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final int interval[] = {100, 2200, 6000};
    private static int mLastVol = -1;
    Context mContext = null;

    @Override
    public synchronized void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null || !ACTION.equals(intent.getAction()))
            return;

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int phoneState = manager.getCallState();
        if (phoneState != TelephonyManager.CALL_STATE_IDLE) {
            return;
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCallingOn = pref.getBoolean("calling_on", false);
        if (isCallingOn == true) {
            return;
        }
        if (pref.getBoolean("volume_key_lock", false) == true) {
            mContext = context;
            Utils.setMusicKey(mContext);
            for (int i = 0; i < interval.length; i++) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.setMusicKey(mContext);
                    }
                }, interval[i]);
            }
            if (!Utils.checkAccessibilityPermission(context)) {
                Utils.showSmallToast(context, context.getString(R.string.toast_alert_enable_volkey_without_accessibility), Toast.LENGTH_LONG);
            }
        }
    }
}
