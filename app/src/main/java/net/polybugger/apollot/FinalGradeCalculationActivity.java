package net.polybugger.apollot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import net.polybugger.apollot.db.ApolloDbAdapter;

public class FinalGradeCalculationActivity extends AppCompatActivity {

    private TextView mPassingGradePercentageTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        setContentView(R.layout.activity_final_grade_calculation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPassingGradePercentageTextView = (TextView) findViewById(R.id.passing_grade_percentage_text_view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float passingGradePercentage = sharedPref.getFloat(getString(R.string.pref_passing_grade_percentage_key), (float) 75.0);
        mPassingGradePercentageTextView.setText(String.format("%.2f%%", passingGradePercentage));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
