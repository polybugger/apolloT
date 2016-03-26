package net.polybugger.apollot.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import net.polybugger.apollot.FinalGradeCalculationActivity;
import net.polybugger.apollot.R;

public class OneToFiveCalculation {

    private Context mContext;
    private float mPassingGradePercentage;
    private float mPassingGradeMark;

    private float mHigherMarkMultiplier;
    private float mLowerMarkMultiplier;

    public OneToFiveCalculation(Context context) {
        refreshCalculationData(context);
    }

    public boolean isSet() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPref.getBoolean(mContext.getString(R.string.pref_one_to_five_selected_key), true);
    }

    public float calculateFinalGrade(float rawPercentage) {
        float finalGrade = (float) 0.0;
        if(rawPercentage > mPassingGradePercentage) {
            finalGrade = ((rawPercentage - mPassingGradePercentage) * mHigherMarkMultiplier) + mPassingGradeMark;
        }
        else if(rawPercentage < mPassingGradePercentage) {
            finalGrade = (rawPercentage * mLowerMarkMultiplier) + 5;
        }
        else {
            finalGrade = mPassingGradeMark;
        }
        return finalGrade;
    }

    public void refreshCalculationData(Context context) {
        mContext = context;
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPassingGradePercentage = sharedPref.getFloat(mContext.getString(R.string.pref_passing_grade_percentage_key), FinalGradeCalculationActivity.DEFAULT_PASSING_GRADE_PERCENTAGE);
        mPassingGradeMark =  sharedPref.getFloat(mContext.getString(R.string.pref_one_to_five_passing_grade_mark_key), FinalGradeCalculationActivity.DEFAULT_ONE_TO_FIVE_PASSING_GRADE_MARK);
        mHigherMarkMultiplier = (1 - mPassingGradeMark) / (100 - mPassingGradePercentage);
        mLowerMarkMultiplier = (mPassingGradeMark - 5) / (mPassingGradePercentage);
    }

    public float getPassingGradeMark() {
        return mPassingGradeMark;
    }
}
