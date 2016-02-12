package net.polybugger.apollot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPreferenceFragment()).commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }
    }
}
