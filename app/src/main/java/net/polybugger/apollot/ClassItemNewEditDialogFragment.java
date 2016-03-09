package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassItemNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_item_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String ITEM_ARG = "net.polybugger.apollot.item_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    public static final String STATE_ITEM_DATE = "net.polybugger.apollot.item_date";
    public static final String STATE_DUE_DATE = "net.polybugger.apollot.due_date";

    public ClassItemNewEditDialogFragment() {}

    public interface NewEditListener {
        void onNewEditItem(ClassItemDbAdapter.ClassItem item, String fragmentTag);
    }

    private Activity mActivity;
    private ClassItemDbAdapter.ClassItem mClassItem;
    private String mFragmentTag;
    private EditText mDescriptionEditText;
    private Button mItemDateButton;
    private Spinner mItemTypeSpinner;
    private CheckBox mCheckAttendanceCheckBox;
    private CheckBox mRecordScoresCheckBox;
    private EditText mPerfectScoreEditText;
    private CheckBox mRecordSubmissionsCheckBox;
    private Button mSubmissionDueDateButton;

    private AlertDialog mCurrentDialog;
    private boolean mDialogShown = false;

    public static ClassItemNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassItemDbAdapter.ClassItem classItem, String fragmentTag) {
        ClassItemNewEditDialogFragment f = new ClassItemNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(ITEM_ARG, classItem);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onDestroyView() {
        if(mCurrentDialog != null && mCurrentDialog.isShowing())
            mCurrentDialog.dismiss();
        super.onDestroyView();
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
        mClassItem = (ClassItemDbAdapter.ClassItem) args.getSerializable(ITEM_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        final SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_item_new_edit_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        // http://stackoverflow.com/questions/6602339/android-spinner-hint
        ArrayAdapter<ClassItemTypeDbAdapter.ItemType> spinnerAdapter = new ArrayAdapter<ClassItemTypeDbAdapter.ItemType>(mActivity, android.R.layout.simple_spinner_item, ClassItemTypeDbAdapter.getList(false, -1));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemTypeSpinner = (Spinner) view.findViewById(R.id.item_type_spinner);
        mItemTypeSpinner.setAdapter(spinnerAdapter);
        mItemDateButton = (Button) view.findViewById(R.id.item_date_button);
        Date dateNow = new Date();
        mItemDateButton.setText(sdf.format(dateNow));
        mItemDateButton.setTag(dateNow);
        View.OnClickListener dateClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogShown)
                    return;
                mDialogShown = true;
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, (Button) view, new DatePickerDialog.DatePickerDialogCallback() {
                    @Override
                    public void onSet(Date date, Button sourceButton) {
                        sourceButton.setText(sdf.format(date));
                        sourceButton.setTag(date);
                    }
                });
                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogShown = false;
                    }
                });
                mCurrentDialog = datePickerDialog.show();
            }
        };
        mItemDateButton.setOnClickListener(dateClickListener);
        mDescriptionEditText = (EditText) view.findViewById(R.id.description_edit_text);
        mCheckAttendanceCheckBox = (CheckBox) view.findViewById(R.id.check_attendance_checkbox);
        mRecordScoresCheckBox = (CheckBox) view.findViewById(R.id.record_scores_checkbox);
        mRecordScoresCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    mPerfectScoreEditText.setVisibility(View.VISIBLE);
                    mPerfectScoreEditText.requestFocus();
                }
                else
                    mPerfectScoreEditText.setVisibility(View.GONE);
            }
        });
        mPerfectScoreEditText = (EditText) view.findViewById(R.id.perfect_score_edit_text);
        mRecordSubmissionsCheckBox = (CheckBox) view.findViewById(R.id.record_submissions_checkbox);
        mRecordSubmissionsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mSubmissionDueDateButton.setVisibility(View.VISIBLE);
                else
                    mSubmissionDueDateButton.setVisibility(View.GONE);
            }
        });
        mSubmissionDueDateButton = (Button) view.findViewById(R.id.submission_due_date_button);
        mSubmissionDueDateButton.setOnClickListener(dateClickListener);

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
                String description = mDescriptionEditText.getText().toString();
                Date itemDate = (Date) mItemDateButton.getTag();
                boolean ib = TextUtils.isEmpty(description);
                if(ib) {
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_item_description_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                ClassItemTypeDbAdapter.ItemType itemType = (mItemTypeSpinner.getSelectedItemPosition() == mItemTypeSpinner.getCount()) ? null : (ClassItemTypeDbAdapter.ItemType) mItemTypeSpinner.getSelectedItem();
                boolean checkAttendance = mCheckAttendanceCheckBox.isChecked();
                boolean recordScores = mRecordScoresCheckBox.isChecked();
                Float perfectScore;
                try {
                    perfectScore = Float.parseFloat(mPerfectScoreEditText.getText().toString());
                }
                catch(Exception e)
                {
                    perfectScore = null;
                }
                if(recordScores && perfectScore == null) {
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_perfect_score_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    mPerfectScoreEditText.requestFocus();
                    return;
                }
                boolean recordSubmissions = mRecordSubmissionsCheckBox.isChecked();
                Date submissionDueDate = (Date) mSubmissionDueDateButton.getTag();
                if(recordSubmissions && submissionDueDate == null) {
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_submission_due_date_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(mClassItem == null) {
                    mClassItem = new ClassItemDbAdapter.ClassItem(-1, -1, description, itemType, itemDate, checkAttendance, recordScores, perfectScore, recordSubmissions, submissionDueDate);
                }
                else {
                    mClassItem.setDescription(description);
                    mClassItem.setItemType(itemType);
                    mClassItem.setItemDate(itemDate);
                    mClassItem.setCheckAttendance(checkAttendance);
                    mClassItem.setRecordScores(recordScores);
                    mClassItem.setPerfectScore(perfectScore);
                    mClassItem.setRecordSubmissions(recordSubmissions);
                    mClassItem.setSubmissionDueDate(submissionDueDate);
                }
                try {
                    ((NewEditListener) mActivity).onNewEditItem(mClassItem, mFragmentTag);
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

        if(mClassItem != null) {
            ClassItemTypeDbAdapter.ItemType itemType = mClassItem.getItemType();
            if(itemType != null)
                mItemTypeSpinner.setSelection(spinnerAdapter.getPosition(itemType));
            Date itemDate = mClassItem.getItemDate();
            if(itemDate != null) {
                mItemDateButton.setText(sdf.format(itemDate));
                mItemDateButton.setTag(itemDate);
            }
            mDescriptionEditText.setText(mClassItem.getDescription());
            mCheckAttendanceCheckBox.setChecked(mClassItem.getCheckAttendance());
            boolean recordScores = mClassItem.getRecordScores();
            mRecordScoresCheckBox.setChecked(recordScores);
            if(recordScores) {
                mPerfectScoreEditText.setVisibility(View.VISIBLE);
                Float perfectScore = mClassItem.getPerfectScore();
                if(perfectScore != null)
                    mPerfectScoreEditText.setText(String.valueOf(perfectScore));
            }
            boolean recordSubmissions = mClassItem.getRecordSubmissions();
            mRecordSubmissionsCheckBox.setChecked(recordSubmissions);
            if(recordSubmissions) {
                mSubmissionDueDateButton.setVisibility(View.VISIBLE);
                Date submissionDueDate = mClassItem.getSubmissionDueDate();
                if(submissionDueDate != null) {
                    mSubmissionDueDateButton.setText(sdf.format(submissionDueDate));
                    mSubmissionDueDateButton.setTag(submissionDueDate);
                }
            }
        }
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_ITEM_DATE)) {
                Date itemDate = (Date) savedInstanceState.getSerializable(STATE_ITEM_DATE);
                mItemDateButton.setText(sdf.format(itemDate));
                mItemDateButton.setTag(itemDate);
            }
            if(savedInstanceState.containsKey(STATE_DUE_DATE)) {
                Date submissionDueDate = (Date) savedInstanceState.getSerializable(STATE_DUE_DATE);
                mSubmissionDueDateButton.setText(sdf.format(submissionDueDate));
                mSubmissionDueDateButton.setTag(submissionDueDate);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }
}


