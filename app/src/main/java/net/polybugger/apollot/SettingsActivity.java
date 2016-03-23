package net.polybugger.apollot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.polybugger.apollot.db.ApolloDbAdapter;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new SettingsPreferenceFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsPreferenceFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference academicTermsPref = findPreference(getString(R.string.pref_academic_terms_key));
            academicTermsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent academicTermsIntent = new Intent(getActivity(), SQLiteTableActivity.class);
                    academicTermsIntent.putExtra(SQLiteTableActivity.TABLE_NAME_ARG, getString(R.string.db_academic_terms_table));
                    academicTermsIntent.putExtra(SQLiteTableActivity.ID_COLUMN_ARG, getString(R.string.db_academic_terms_id_column));
                    academicTermsIntent.putExtra(SQLiteTableActivity.DATA_COLUMN_ARG, getString(R.string.db_academic_terms_data_column));
                    academicTermsIntent.putExtra(SQLiteTableActivity.COLOR_COLUMN_ARG, getString(R.string.db_academic_terms_color_column));
                    academicTermsIntent.putExtra(SQLiteTableActivity.TITLE_ARG, getString(R.string.pref_academic_terms_title));
                    academicTermsIntent.putExtra(SQLiteTableActivity.DIALOG_TITLE_ARG, getString(R.string.pref_academic_terms_dialog_title));
                    startActivity(academicTermsIntent);
                    return true;
                }
            });

            Preference classItemTypesPref = findPreference(getString(R.string.pref_class_item_types_key));
            classItemTypesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent classItemTypesIntent = new Intent(getActivity(), SQLiteTableActivity.class);
                    classItemTypesIntent.putExtra(SQLiteTableActivity.TABLE_NAME_ARG, getString(R.string.db_class_item_types_table));
                    classItemTypesIntent.putExtra(SQLiteTableActivity.ID_COLUMN_ARG, getString(R.string.db_class_item_types_id_column));
                    classItemTypesIntent.putExtra(SQLiteTableActivity.DATA_COLUMN_ARG, getString(R.string.db_class_item_types_data_column));
                    classItemTypesIntent.putExtra(SQLiteTableActivity.COLOR_COLUMN_ARG, getString(R.string.db_class_item_types_color_column));
                    classItemTypesIntent.putExtra(SQLiteTableActivity.TITLE_ARG, getString(R.string.pref_class_item_types_title));
                    classItemTypesIntent.putExtra(SQLiteTableActivity.DIALOG_TITLE_ARG, getString(R.string.pref_class_item_types_dialog_title));
                    startActivity(classItemTypesIntent);
                    return true;
                }
            });

            Preference finalGradeCalcPref = findPreference(getString(R.string.pref_final_grade_calculation_key));
            finalGradeCalcPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent finalGradeCalcIntent = new Intent(getActivity(), FinalGradeCalculationActivity.class);
                    startActivity(finalGradeCalcIntent);
                    return true;
                }
            });

        }

        @Override
        public void onCreatePreferences(Bundle bundle, String s) {

        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment fragment;
            if(preference instanceof UnlockPasswordDialogPreference) {
                fragment = UnlockPasswordPreferenceDialogFragmentCompat.newInstance(preference);
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            }
            else if(preference instanceof TimeStartDialogPreference) {
                fragment = TimeStartPreferenceDialogFragmentCompat.newInstance(preference);
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            }
            else if(preference instanceof StudentNameDisplayFormatDialogPreference) {
                fragment = StudentNameDisplayFormatPreferenceDialogFragmentCompat.newInstance(preference);
                fragment.setTargetFragment(this, 0);
                fragment.show(getFragmentManager(), "android.support.v7.preference.PreferenceFragment.DIALOG");
            }
            else {
                super.onDisplayPreferenceDialog(preference);
            }
        }
    }
}
