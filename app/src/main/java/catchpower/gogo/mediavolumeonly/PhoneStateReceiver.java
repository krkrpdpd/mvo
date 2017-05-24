package catchpower.gogo.mediavolumeonly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver {
    private static String mLastState = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null || !TelephonyManager.ACTION_PHONE_STATE_CHANGED.equals(intent.getAction()))
            return;

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
//        Log.e(TAG, "state : " + state);
        if(state == null && mLastState == state)
            return;
        mLastState = state;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isCallingOn = pref.getBoolean("calling_on", false);

        if (TelephonyManager.EXTRA_STATE_OFFHOOK.equals(state) || TelephonyManager.EXTRA_STATE_RINGING.equals(state)){
            if(isCallingOn == false) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("calling_on", true);
                editor.commit();
            }
        }
        else {
            if (isCallingOn == true) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("calling_on", false);
                editor.commit();
            }
            if( pref.getBoolean("volume_key_lock", false) == true) {
                Utils.setMusicKey(context);
            }
        }
    }
}
