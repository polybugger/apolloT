package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.StudentDbAdapter;

public class StudentNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_student_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";
    public static final String STATE_GENDER = "net.polybugger.apollot.gender";

    private Activity mActivity;
    private StudentDbAdapter.Student mStudent;
    private String mFragmentTag;
    private EditText mLastNameEditText;
    private EditText mFirstNameEditText;
    private EditText mMiddleNameEditText;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private EditText mEmailAddressEditText;
    private EditText mContactNoEditText;

    public interface NewEditListener {
        void onNewEditStudent(StudentDbAdapter.Student student, String fragmentTag);
    }

    public StudentNewEditDialogFragment() {}

    public static StudentNewEditDialogFragment newInstance(String dialogTitle, String buttonText, StudentDbAdapter.Student student, String fragmentTag) {
        StudentNewEditDialogFragment f = new StudentNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(STUDENT_ARG, student);
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
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);
        mStudent = (StudentDbAdapter.Student) args.getSerializable(STUDENT_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        final String male = getString(R.string.male);
        final String female = getString(R.string.female);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_student_new_edit_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        mLastNameEditText = (EditText) view.findViewById(R.id.last_name_edit_text);
        mFirstNameEditText = (EditText) view.findViewById(R.id.first_name_edit_text);
        mMiddleNameEditText = (EditText) view.findViewById(R.id.middle_name_edit_text);
        mEmailAddressEditText = (EditText) view.findViewById(R.id.email_address_edit_text);
        mContactNoEditText = (EditText) view.findViewById(R.id.contact_no_edit_text);
        mMaleRadio = (RadioButton) view.findViewById(R.id.male_radio);
        mFemaleRadio = (RadioButton) view.findViewById(R.id.female_radio);
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button addSaveButton = (Button) view.findViewById(R.id.add_save_button);
        addSaveButton.setText(buttonText);
        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lastName = mLastNameEditText.getText().toString();
                if(TextUtils.isEmpty(lastName)) {
                    mLastNameEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_last_name_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                String firstName = mFirstNameEditText.getText().toString();
                String middleName = mMiddleNameEditText.getText().toString();
                String emailAddress = mEmailAddressEditText.getText().toString();
                String contactNo = mContactNoEditText.getText().toString();
                String gender = null;
                if(mMaleRadio.isChecked())
                    gender = male;
                if(mFemaleRadio.isChecked())
                    gender = female;
                if(mStudent == null) {
                    mStudent = new StudentDbAdapter.Student(-1, lastName, firstName, middleName, gender, emailAddress, contactNo);
                }
                else {
                    mStudent.setLastName(lastName);
                    mStudent.setFirstName(firstName);
                    mStudent.setMiddleName(middleName);
                    mStudent.setGender(gender);
                    mStudent.setEmailAddress(emailAddress);
                    mStudent.setContactNo(contactNo);
                }
                try {
                    ((NewEditListener) mActivity).onNewEditStudent(mStudent, mFragmentTag);
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });
        if(mStudent != null) {
            mLastNameEditText.setText(mStudent.getLastName());
            mFirstNameEditText.setText(mStudent.getFirstName());
            mMiddleNameEditText.setText(mStudent.getMiddleName());
            mEmailAddressEditText.setText(mStudent.getEmailAddress());
            String gender = mStudent.getGender();
            mMaleRadio.setChecked(male.equals(gender));
            mFemaleRadio.setChecked(female.equals(gender));
            mContactNoEditText.setText(mStudent.getContactNo());
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_GENDER)) {
            String gender = savedInstanceState.getString(STATE_GENDER);
            mMaleRadio.setChecked(male.equals(gender));
            mFemaleRadio.setChecked(female.equals(gender));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        String gender = null;
        if(mMaleRadio.isChecked())
            gender = getString(R.string.male);
        if(mFemaleRadio.isChecked())
            gender = getString(R.string.female);
        outState.putString(STATE_GENDER, gender);
        super.onSaveInstanceState(outState);
    }

}
