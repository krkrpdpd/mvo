package catchpower.gogo.mediavolumeonly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BootReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d(TAG, "Boot complete");
        if(intent == null || intent.getAction() == null || !ACTION.equals(intent.getAction()))
            return;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if(pref.getBoolean("calling_on", false) == true) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("calling_on", false);
            editor.commit();
        }
    }
}
