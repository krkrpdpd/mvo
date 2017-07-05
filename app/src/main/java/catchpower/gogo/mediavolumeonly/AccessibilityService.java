package catchpower.gogo.mediavolumeonly;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class AccessibilityService extends android.accessibilityservice.AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event == null)   return;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if( pref.getBoolean("volume_key_lock", false) == true){
                Utils.setMusicKey(this);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Utils.setMusicKey(AccessibilityService.this);
                    }
                }, 100);
            }
        }
    }

//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        info.feedbackType = AccessibilityServiceInfo.DEFAULT | AccessibilityServiceInfo.FEEDBACK_HAPTIC;
//        info.notificationTimeout = 100; // millisecond
//        setServiceInfo(info);
//    }

    @Override
    public void onInterrupt() { }
}