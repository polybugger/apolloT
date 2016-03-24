package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PassingGradePercentageDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_passing_grade_percentage_dialog_fragment";

    public interface EditListener {
        void onEditPassingGradePercentage(float passingGradePercentage);
    }

    private Activity mActivity;
    private EditText mPassingGradePercentageEditText;

    public PassingGradePercentageDialogFragment() {}

    public static PassingGradePercentageDialogFragment newInstance() {
        PassingGradePercentageDialogFragment f = new PassingGradePercentageDialogFragment();
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_passing_grade_percentage_edit_dialog, null);

        mPassingGradePercentageEditText = (EditText) view.findViewById(R.id.passing_grade_percentage_edit_text);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPercentage = mPassingGradePercentageEditText.getText().toString();
                if (TextUtils.isEmpty(strPercentage)) {
                    mPassingGradePercentageEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_passing_grade_percentage_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Float percentage = Float.parseFloat(strPercentage);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
                sharedPref.edit().putFloat(getString(R.string.pref_passing_grade_percentage_key), percentage).apply();

                try {
                    ((EditListener) mActivity).onEditPassingGradePercentage(percentage);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + EditListener.class.toString());
                }

                dismiss();
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
        float passingGradePercentage = sharedPref.getFloat(getString(R.string.pref_passing_grade_percentage_key), FinalGradeCalculationActivity.DEFAULT_PASSING_GRADE_PERCENTAGE);
        mPassingGradePercentageEditText.setText(String.format("%.2f", passingGradePercentage));

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }


}
