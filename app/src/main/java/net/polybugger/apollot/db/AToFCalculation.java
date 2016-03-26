package net.polybugger.apollot.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import net.polybugger.apollot.R;

import java.util.HashMap;
import java.util.Map;

public class AToFCalculation {

    public static final String A_PLUS = "A+";
    public static final float A_PLUS_PERCENTAGE = (float) 95.0;
    public static final String A = "A";
    public static final float A_PERCENTAGE = (float) 93.0;
    public static final String A_MINUS = "A-";
    public static final float A_MINUS_PERCENTAGE = (float) 90.0;
    public static final String B_PLUS = "B+";
    public static final float B_PLUS_PERCENTAGE = (float) 87.0;
    public static final String B = "B";
    public static final float B_PERCENTAGE = (float) 83.0;
    public static final String B_MINUS = "B-";
    public static final float B_MINUS_PERCENTAGE = (float) 80.0;
    public static final String C_PLUS = "C+";
    public static final float C_PLUS_PERCENTAGE = (float) 77.0;
    public static final String C = "C";
    public static final float C_PERCENTAGE = (float) 73.0;
    public static final String C_MINUS = "C-";
    public static final float C_MINUS_PERCENTAGE = (float) 70.0;
    public static final String D_PLUS = "D+";
    public static final float D_PLUS_PERCENTAGE = (float) 67.0;
    public static final String D = "D";
    public static final float D_PERCENTAGE = (float) 63.0;
    public static final String D_MINUS = "D-";
    public static final float D_MINUS_PERCENTAGE = (float) 60.0;
    public static final String F = "F";
    public static final float F_PERCENTAGE = (float) 0.0;

    private Map<Integer, GradeMark> mGradeMarks;
    private Context mContext;

    private boolean mSet = true;

    public void setmGradeMarks(Map<Integer, GradeMark> mGradeMarks) {
        this.mGradeMarks = mGradeMarks;
    }

    public AToFCalculation(Context context) {

        mContext = context;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);

        mSet = sharedPref.getBoolean(mContext.getString(R.string.pref_a_to_f_selected_key), true);

        mGradeMarks = new HashMap<>();
        mGradeMarks.put(1, new GradeMark(A_PLUS, sharedPref.getFloat(mContext.getString(R.string.pref_a_plus_percentage_key), A_PLUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_a_plus_hidden_key), false)));
        mGradeMarks.put(2, new GradeMark(A, sharedPref.getFloat(mContext.getString(R.string.pref_a_percentage_key), A_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_a_hidden_key), false)));
        mGradeMarks.put(3, new GradeMark(A_MINUS, sharedPref.getFloat(mContext.getString(R.string.pref_a_minus_percentage_key), A_MINUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_a_minus_hidden_key), false)));

        mGradeMarks.put(4, new GradeMark(B_PLUS, sharedPref.getFloat(mContext.getString(R.string.pref_b_plus_percentage_key), B_PLUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_b_plus_hidden_key), false)));
        mGradeMarks.put(5, new GradeMark(B, sharedPref.getFloat(mContext.getString(R.string.pref_b_percentage_key), B_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_b_hidden_key), false)));
        mGradeMarks.put(6, new GradeMark(B_MINUS, sharedPref.getFloat(mContext.getString(R.string.pref_b_minus_percentage_key), B_MINUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_b_minus_hidden_key), false)));

        mGradeMarks.put(7, new GradeMark(C_PLUS, sharedPref.getFloat(mContext.getString(R.string.pref_c_plus_percentage_key), C_PLUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_c_plus_hidden_key), false)));
        mGradeMarks.put(8, new GradeMark(C, sharedPref.getFloat(mContext.getString(R.string.pref_c_percentage_key), C_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_c_hidden_key), false)));
        mGradeMarks.put(9, new GradeMark(C_MINUS, sharedPref.getFloat(mContext.getString(R.string.pref_c_minus_percentage_key), C_MINUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_c_minus_hidden_key), false)));

        mGradeMarks.put(10, new GradeMark(D_PLUS, sharedPref.getFloat(mContext.getString(R.string.pref_d_plus_percentage_key), D_PLUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_d_plus_hidden_key), false)));
        mGradeMarks.put(11, new GradeMark(D, sharedPref.getFloat(mContext.getString(R.string.pref_d_percentage_key), D_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_d_hidden_key), false)));
        mGradeMarks.put(12, new GradeMark(D_MINUS, sharedPref.getFloat(mContext.getString(R.string.pref_d_minus_percentage_key), D_MINUS_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_d_minus_hidden_key), false)));

        mGradeMarks.put(13, new GradeMark(F, sharedPref.getFloat(mContext.getString(R.string.pref_f_percentage_key), F_PERCENTAGE), sharedPref.getBoolean(mContext.getString(R.string.pref_f_hidden_key), false)));
    }

    public boolean isSet() {
        return mSet;
    }

    public static class GradeMark {

        private boolean mHidden;
        private String mGradeMark;
        private float mPercentage;

        public GradeMark(String gradeMark, float percentage, boolean hidden) {
            mGradeMark = gradeMark;
            mPercentage = percentage;
            mHidden = hidden;
        }

        public boolean getHidden() {
            return mHidden;
        }
        public void setHidden(boolean hidden) {
            mHidden = hidden;
        }
        public String getGradeMark() {
            return mGradeMark;
        }
        public void setGradeMark(String gradeMark) {
            this.mGradeMark = gradeMark;
        }
        public float getPercentage() {
            return mPercentage;
        }
        public void setPercentage(float percentage) {
            this.mPercentage = percentage;
        }
    }
}
