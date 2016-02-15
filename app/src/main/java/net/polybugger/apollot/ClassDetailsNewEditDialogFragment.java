package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.polybugger.apollot.db.AcademicTermDbAdapter;
import net.polybugger.apollot.db.ClassDbAdapter;

import java.io.Serializable;

public class ClassDetailsNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_details_dialog_fragment";
    public static final String DIALOG_ARG = "net.polybugger.apollot.dialog_arg";
    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private String mFragmentTag;
    private EditText mCodeEditText;
    private EditText mDescriptionEditText;
    private Spinner mAcademicTermSpinner;
    private EditText mYearEditText;
    private CheckBox mCurrentCheckBox;

    public ClassDetailsNewEditDialogFragment() {
    }

    public interface ClassDetailsDialogListener {
        void onClassDetailsDialogSubmit(ClassDbAdapter.Class class_, String fragmentTag);
    }

    public static ClassDetailsNewEditDialogFragment newInstance(DialogArgs dialogArgs, Class class_, String fragmentTag) {
        ClassDetailsNewEditDialogFragment f = new ClassDetailsNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(DIALOG_ARG, dialogArgs);
        args.putSerializable(CLASS_ARG, class_);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
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
        DialogArgs dialogArgs = (DialogArgs) args.getSerializable(DIALOG_ARG);
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_details_new_edit_dialog, null);
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogArgs.getTitle());
        mCodeEditText = (EditText) view.findViewById(R.id.code_edit_text);
        mDescriptionEditText = (EditText) view.findViewById(R.id.description_edit_text);

        ArrayAdapter<AcademicTermDbAdapter.AcademicTerm> spinnerAdapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_spinner_item, AcademicTermDbAdapter.getList());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mAcademicTermSpinner = (Spinner) view.findViewById(R.id.academic_term_spinner);
        mAcademicTermSpinner.setAdapter(spinnerAdapter);
        mYearEditText = (EditText) view.findViewById(R.id.year_edit_text);
        mCurrentCheckBox = (CheckBox) view.findViewById(R.id.current_checkbox);
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button addSaveButton = (Button) view.findViewById(R.id.add_save_button);
        addSaveButton.setText(dialogArgs.getAddSaveButtonText());
        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                String code = mCodeEditText.getText().toString();
                if(StringUtils.isBlank(code)) {
                    mCodeEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_code_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String description = mDescriptionEditText.getText().toString();
                AcademicTerm academicTerm = (mAcademicTermSpinner.getSelectedItemPosition() == mAcademicTermSpinner.getCount()) ? null : (AcademicTerm) mAcademicTermSpinner.getSelectedItem();
                String year = mYearEditText.getText().toString();
                boolean current = mCurrentCheckBox.isChecked();
                if(mClass == null) {
                    mClass = new Class(-1, code, description, academicTerm, year, current);
                }
                else {
                    mClass.setCode(code);
                    mClass.setDescription(description);
                    mClass.setAcademicTerm(academicTerm);
                    mClass.setYear(year);
                    mClass.setCurrent(current);
                }
                */
                try {
                    ((ClassDetailsDialogListener) mActivity).onClassDetailsDialogSubmit(mClass, mFragmentTag);
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + ClassDetailsDialogListener.class.toString());
                }
                dismiss();
            }
        });

        if(mClass != null) {
            mCodeEditText.setText(mClass.getCode());
            mDescriptionEditText.setText(mClass.getDescription());
            AcademicTermDbAdapter.AcademicTerm academicTerm = mClass.getAcademicTerm();
            if(academicTerm != null)
                mAcademicTermSpinner.setSelection(spinnerAdapter.getPosition(academicTerm));
            mYearEditText.setText(mClass.getYear());
            mCurrentCheckBox.setChecked(mClass.isCurrent());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }

    @SuppressWarnings("serial")
    public static class DialogArgs implements Serializable {

        private String mTitle;
        private String mAddSaveButtonText;

        public DialogArgs(String title, String addSaveButtonText) {
            mTitle = title;
            mAddSaveButtonText = addSaveButtonText;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getAddSaveButtonText() {
            return mAddSaveButtonText;
        }
    }

}
