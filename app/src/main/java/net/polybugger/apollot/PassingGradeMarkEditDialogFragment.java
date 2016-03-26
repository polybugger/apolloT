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
import android.widget.TextView;
import android.widget.Toast;

public class PassingGradeMarkEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_passing_grade_mark_dialog_fragment";
    public static final String TYPE_ARG = "net.polybugger.apollot.type_arg";

    public interface EditListener {
        void onEditPassingGradeMark(float passingGradeMark, FinalGradeCalculationActivity.GradeSystemType type);
    }

    private Activity mActivity;
    private EditText mPassingGradeMarkEditText;
    private FinalGradeCalculationActivity.GradeSystemType mType;

    public PassingGradeMarkEditDialogFragment() {}

    public static PassingGradeMarkEditDialogFragment newInstance(FinalGradeCalculationActivity.GradeSystemType type) {
        PassingGradeMarkEditDialogFragment f = new PassingGradeMarkEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(TYPE_ARG, type);
        f.setArguments(args);
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

        Bundle args = getArguments();
        mType = (FinalGradeCalculationActivity.GradeSystemType) args.getSerializable(TYPE_ARG);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_passing_grade_mark_edit_dialog, null);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);

        float passingGradeMark = sharedPref.getFloat(getString(R.string.pref_one_to_five_passing_grade_mark_key), FinalGradeCalculationActivity.DEFAULT_ONE_TO_FIVE_PASSING_GRADE_MARK);
        String type = getString(R.string.one_to_five) + " " + getString(R.string.grade_system);
        String instructions = getString(R.string.one_to_five_instructions);
        switch(mType) {
            case ONE_TO_FIVE:
                type = getString(R.string.one_to_five) + " " + getString(R.string.grade_system);
                instructions = getString(R.string.one_to_five_instructions);
                passingGradeMark = sharedPref.getFloat(getString(R.string.pref_one_to_five_passing_grade_mark_key), FinalGradeCalculationActivity.DEFAULT_ONE_TO_FIVE_PASSING_GRADE_MARK);
                break;
            case FOUR_TO_ONE:
                type = getString(R.string.four_to_one) + " " + getString(R.string.grade_system);
                instructions = getString(R.string.four_to_one_instructions);
                passingGradeMark = sharedPref.getFloat(getString(R.string.pref_four_to_one_passing_grade_mark_key), FinalGradeCalculationActivity.DEFAULT_FOUR_TO_ONE_PASSING_GRADE_MARK);
                break;
        }
        ((TextView) view.findViewById(R.id.passing_grade_mark_type_text_view)).setText(type);
        ((TextView) view.findViewById(R.id.passing_grade_mark_instructions_text_view)).setText(instructions);

        mPassingGradeMarkEditText = (EditText) view.findViewById(R.id.passing_grade_mark_edit_text);
        mPassingGradeMarkEditText.setText(String.format("%.2f", passingGradeMark));

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
                String strGradeMark = mPassingGradeMarkEditText.getText().toString();
                if (TextUtils.isEmpty(strGradeMark)) {
                    mPassingGradeMarkEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_passing_grade_mark_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Float gradeMark = Float.parseFloat(strGradeMark);

                switch(mType) {
                    case ONE_TO_FIVE:
                        if(!(gradeMark > 1.0 && gradeMark < 5.0)) {
                            mPassingGradeMarkEditText.requestFocus();
                            Toast toast = Toast.makeText(mActivity, R.string.fragment_status_passing_grade_mark_one_to_five_error, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        break;
                    case FOUR_TO_ONE:
                        if(!(gradeMark > 0.0 && gradeMark < 4.0)) {
                            mPassingGradeMarkEditText.requestFocus();
                            Toast toast = Toast.makeText(mActivity, R.string.fragment_status_passing_grade_mark_four_to_one_error, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        break;
                }

                try {
                    ((EditListener) mActivity).onEditPassingGradeMark(gradeMark, mType);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + EditListener.class.toString());
                }
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }
}
