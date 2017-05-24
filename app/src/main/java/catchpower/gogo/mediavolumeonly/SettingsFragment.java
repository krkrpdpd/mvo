package catchpower.gogo.mediavolumeonly;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;

public class SettingsFragment extends PreferenceFragment {
    final static int PERM_REQ_CODE = 1;
    final static int NEED_TO_DEFAULT = 0;
    final static int NEED_TO_ENABLE = 1;
    final static int NEED_TO_DISABLE = 2;

    private Preference.OnPreferenceChangeListener sBindPreferenceToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            int needToAccessbilityCheck = NEED_TO_DEFAULT;
            String stringValue = value.toString();
            if("volume_key_lock".equals(preference.getKey()) && value instanceof Boolean) {
                boolean savedVal = pref.getBoolean("volume_key_lock", false);
                boolean newVal = (Boolean)value;
                if(savedVal == newVal)
                    return true;

                if (newVal == true) {
                    Utils.forceVolumeControlStream(getActivity(), AudioManager.STREAM_MUSIC);
                    if(!Utils.checkReadPhonePermission(getActivity())) {
                        setReadPhoneStatePermission();
                    }
                    if (!Utils.checkAccessibilityPermission(getActivity())) {
                        needToAccessbilityCheck = NEED_TO_ENABLE;
                    }
                } else {
                    Utils.forceVolumeControlStream(getActivity(), -1);
                    if (Utils.checkAccessibilityPermission(getActivity())) {
                        needToAccessbilityCheck = NEED_TO_DISABLE;
                    }
                }
            }
            if(needToAccessbilityCheck == NEED_TO_ENABLE){
                setAccessibilityPermission(true);
            }else if(needToAccessbilityCheck == NEED_TO_DISABLE){
                setAccessibilityPermission(false);
            }
            return true;
        }
    };

    public void setAccessibilityPermission(boolean needToEnable) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.setting_accessibility_dialog_title);
        dialog.setMessage(needToEnable ? R.string.setting_enable_accessibility_dialog_summary : R.string.setting_disable_accessibility_dialog_summary);
        dialog.setNegativeButton(R.string.setting_volumekey_dialog_CLOSE, null);
        dialog.setPositiveButton(R.string.setting_volumekey_dialog_OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
        dialog.create().show();
    }

    public void setReadPhoneStatePermission() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle(R.string.setting_phonestate_dialog_title);
        dialog.setMessage(R.string.setting_phonestate_dialog_summary);
        dialog.setNegativeButton(R.string.setting_volumekey_dialog_CLOSE, null);
        dialog.setPositiveButton(R.string.setting_volumekey_dialog_OK, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, PERM_REQ_CODE);
            }
        });
        dialog.create().show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_settings);
        setHasOptionsMenu(false);
        bindPreferenceSummaryToValueBoolean(getActivity(), findPreference("volume_key_lock"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindPreferenceSummaryToValueString(Context context, Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceToValueListener);
        sBindPreferenceToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
    }

    private void bindPreferenceSummaryToValueInt(Context context, Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceToValueListener);
        sBindPreferenceToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getInt(preference.getKey(), 0));
    }

    private void bindPreferenceSummaryToValueBoolean(Context context, Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceToValueListener);
        sBindPreferenceToValueListener.onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), false));
    }

    private boolean isEnabledVolumeKeyFix(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return pref.getBoolean("volume_key_lock", false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
