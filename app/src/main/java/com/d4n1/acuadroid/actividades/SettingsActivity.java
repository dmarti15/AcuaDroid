package com.d4n1.acuadroid.actividades;


import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.util.DisplayMetrics;

import com.d4n1.acuadroid.R;

import java.util.List;


public class SettingsActivity extends PreferenceActivity {


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // Comprobar que el fragmento esté relacionado con la actividad
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onIsMultiPane() {
        // Determinar que siempre sera multipanel
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return ((float)metrics.densityDpi / (float)metrics.widthPixels) < 0.30;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            String settings = getArguments().getString("settings");
            if ("luxa".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_luxa);
            } else if ("luxb".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_luxb);
            } else if ("temp".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_temp);
            } else if ("levl".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_levl);
            } else if ("feed".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_feed);
            } else if ("twitter".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_twitter);
            } else if ("otros".equals(settings)) {
                addPreferencesFromResource(R.xml.settings_otros);
            }
            //getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onResume() {
            super.onResume();
            // Registrar escucha
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
            for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); ++i) {
                Preference preference = getPreferenceScreen().getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    PreferenceGroup preferenceGroup = (PreferenceGroup) preference;
                    for (int j = 0; j < preferenceGroup.getPreferenceCount(); ++j) {
                        Preference singlePref = preferenceGroup.getPreference(j);
                        updatePreference(singlePref, singlePref.getKey());
                    }
                } else {
                    updatePreference(preference, preference.getKey());
                }
            }
        }

        @Override
        public void onPause() {
            super.onPause();
            // Eliminar registro de la escucha
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            updatePreference(findPreference(key), key);
        }
        private void updatePreference(Preference preference, String key) {
            if (preference == null) return;
            //if (preference instanceof ListPreference) {
            //    ListPreference listPreference = (ListPreference) preference;
            //    listPreference.setSummary(listPreference.getEntry());
            //    return;
            //}
            SharedPreferences sharedPrefs = getPreferenceManager().getSharedPreferences();
            // Actualizar el resumen de la preferencia
            if (key.equals("temp_max")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "27"));
            }
            if (key.equals("temp_min")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "22"));
            }
            if (key.equals("amazi")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "15"));
            }
            if (key.equals("amaze")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "17"));
            }
            if (key.equals("pmazi")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "22"));
            }
            if (key.equals("pmaze")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "24"));
            }
            if (key.equals("powerA")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "75"));
            }
            if (key.equals("ambli")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "16"));
            }
            if (key.equals("amble")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "18"));
            }
            if (key.equals("pmbli")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "21"));
            }
            if (key.equals("pmble")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "23"));
            }
            if (key.equals("powerB")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "75"));
            }
            if (key.equals("levl_size")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "23"));
            }
            if (key.equals("LevlAlarm")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "15"));
            }
            if (key.equals("feed1active")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("feed2active")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("feed3active")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("feed4active")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("feed5active")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("feed1hora")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "14"));
            }
            if (key.equals("feed2hora")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "16"));
            }
            if (key.equals("feed3hora")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "18"));
            }
            if (key.equals("feed4hora")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "20"));
            }
            if (key.equals("feed5hora")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "22"));
            }
            if (key.equals("feed1vueltas")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "1"));
            }
            if (key.equals("feed2vueltas")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "1"));
            }
            if (key.equals("feed3vueltas")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "1"));
            }
            if (key.equals("feed4vueltas")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "1"));
            }
            if (key.equals("feed5vueltas")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "1"));
            }
            //if (key.equals("usuario_twitter")) {
            //    //Preference preference = findPreference(key);
            //    preference.setSummary(sharedPrefs.getString(key, "Introduce tu usuario de Twitter"));
            //}
            //if (key.equals("contraseña_twitter")) {
            //    //Preference preference = findPreference(key);
            //    preference.setSummary(sharedPrefs.getString(key, "********"));
            //}
            if (key.equals("usar_twitter")) {
                //Preference preference = findPreference(key);
                preference.setSummary(String.valueOf(sharedPrefs.getBoolean(key, false)));
            }
            if (key.equals("TiempoManual")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "10"));
            }
            if (key.equals("BatteryMin")) {
                //Preference preference = findPreference(key);
                preference.setSummary(sharedPrefs.getString(key, "10"));
            }
        }
    }
}