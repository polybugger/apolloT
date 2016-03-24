package net.polybugger.apollot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import net.polybugger.apollot.db.ApolloDbAdapter;

public class FinalGradeCalculationActivity extends AppCompatActivity implements PassingGradePercentageDialogFragment.EditListener {

    public static float DEFAULT_PASSING_GRADE_PERCENTAGE = (float) 75.0;
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
        float passingGradePercentage = sharedPref.getFloat(getString(R.string.pref_passing_grade_percentage_key), DEFAULT_PASSING_GRADE_PERCENTAGE);
        mPassingGradePercentageTextView.setText(String.format("%.2f%%", passingGradePercentage));

        findViewById(R.id.passing_grade_percentage_relative_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                PassingGradePercentageDialogFragment df = (PassingGradePercentageDialogFragment) fm.findFragmentByTag(PassingGradePercentageDialogFragment.TAG);
                if (df == null) {
                    df = PassingGradePercentageDialogFragment.newInstance();
                    df.show(fm, PassingGradePercentageDialogFragment.TAG);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onEditPassingGradePercentage(float passingGradePercentage) {
        mPassingGradePercentageTextView.setText(String.format("%.2f%%", passingGradePercentage));
    }
}
