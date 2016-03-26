package net.polybugger.apollot.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import net.polybugger.apollot.FinalGradeCalculationActivity;
import net.polybugger.apollot.R;

public class FourToOneCalculation {

    private Context mContext;
    private float mPassingGradePercentage;
    private float mPassingGradeMark;

    private float mHigherMarkMultiplier;
    private float mLowerMarkMultiplier;

    private boolean mSet = true;

    public FourToOneCalculation(Context context) {
        refreshCalculationData(context);
    }

    public boolean isSet() {
        return mSet;
    }

    public float calculateFinalGrade(float rawPercentage) {
        float finalGrade = (float) 0.0;
        if(rawPercentage > mPassingGradePercentage) {
            finalGrade = ((rawPercentage - mPassingGradePercentage) * mHigherMarkMultiplier) + mPassingGradeMark;
        }
        else if(rawPercentage < mPassingGradePercentage) {
            finalGrade = (rawPercentage * mLowerMarkMultiplier);
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
        mPassingGradeMark =  sharedPref.getFloat(mContext.getString(R.string.pref_four_to_one_passing_grade_mark_key), FinalGradeCalculationActivity.DEFAULT_FOUR_TO_ONE_PASSING_GRADE_MARK);
        mHigherMarkMultiplier = (4 - mPassingGradeMark) / (100 - mPassingGradePercentage);
        mLowerMarkMultiplier = (mPassingGradeMark) / (mPassingGradePercentage);
        mSet = sharedPref.getBoolean(mContext.getString(R.string.pref_four_to_one_selected_key), true);
    }

    public float getPassingGradeMark() {
        return mPassingGradeMark;
    }

}
