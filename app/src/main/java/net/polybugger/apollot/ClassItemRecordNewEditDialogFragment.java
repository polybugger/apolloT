package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;

public class ClassItemRecordNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_item_record_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String CLASS_ITEM_ARG = "net.polybugger.apollot.item_arg";
    public static final String CLASS_ITEM_RECORD_ARG = "net.polybugger.apollot.item_record_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    public static final String STATE_SUBMISSION_DATE = "net.polybugger.apollot.submission_date";
    public static final String STATE_ATTENDANCE = "net.polybugger.apollot.present_radio";

    public ClassItemRecordNewEditDialogFragment() {}

    public interface NewEditListener {
        void onNewEditItemRecord(ClassItemRecordDbAdapter.ClassItemRecord itemRecord, String fragmentTag);
    }

    private Activity mActivity;
    private ClassItemDbAdapter.ClassItem mClassItem;
    private ClassItemRecordDbAdapter.ClassItemRecord mClassItemRecord;
    private String mFragmentTag;
    private RadioButton mPresentRadio;
    private RadioButton mAbsentRadio;
    private EditText mScoreEditText;
    private EditText mRemarksEditText;
    private Button mSubmissionDateButton;
    private AlertDialog mCurrentDialog;
    private boolean mDialogShown = false;

    public static ClassItemRecordNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassItemDbAdapter.ClassItem classItem, ClassItemRecordDbAdapter.ClassItemRecord itemRecord, String fragmentTag) {
        ClassItemRecordNewEditDialogFragment f = new ClassItemRecordNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(CLASS_ITEM_ARG, classItem);
        args.putSerializable(CLASS_ITEM_RECORD_ARG, itemRecord);
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

    @Override
    public void onDestroyView() {
        if(mCurrentDialog != null && mCurrentDialog.isShowing())
            mCurrentDialog.dismiss();
        super.onDestroyView();
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);
        mClassItem = (ClassItemDbAdapter.ClassItem) args.getSerializable(CLASS_ITEM_ARG);
        mClassItemRecord = (ClassItemRecordDbAdapter.ClassItemRecord) args.getSerializable(CLASS_ITEM_RECORD_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_item_record_new_edit_dialog, null);
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);

        ((TextView) view.findViewById(R.id.description_text_view)).setText(mClassItem.getDescription());
        Date itemDate = mClassItem.getItemDate();
        final SimpleDateFormat sdfi = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        if(itemDate != null)
            ((TextView) view.findViewById(R.id.item_date_text_view)).setText(sdfi.format(itemDate));

        TextView itemTypeTextView = (TextView) view.findViewById(R.id.item_type_text_view);
        ClassItemTypeDbAdapter.ItemType itemType = mClassItem.getItemType();
        if(itemType != null) {
            itemTypeTextView.setText(itemType.getDescription());
            itemTypeTextView.setVisibility(View.VISIBLE);
        }
        else
            itemTypeTextView.setVisibility(View.GONE);
        TextView perfectScoreTextView = (TextView) view.findViewById(R.id.perfect_score_text_view);
        if(mClassItem.getRecordScores()) {
            Float perfectScore = mClassItem.getPerfectScore();
            if(perfectScore != null)
                perfectScoreTextView.setText(getString(R.string.perfect_score_label) + " " + String.valueOf(mClassItem.getPerfectScore()));
            perfectScoreTextView.setVisibility(View.VISIBLE);
        }
        else
            perfectScoreTextView.setVisibility(View.GONE);
        TextView dueDateTextView = (TextView) view.findViewById(R.id.due_date_text_view);
        if(mClassItem.getRecordSubmissions()) {
            Date dueDate = mClassItem.getSubmissionDueDate();
            if(dueDate != null)
                dueDateTextView.setText(getString(R.string.submission_due_date_label) + " " + sdfi.format(dueDate));
            dueDateTextView.setVisibility(View.VISIBLE);
        }
        else
            dueDateTextView.setVisibility(View.GONE);

        mPresentRadio = (RadioButton) view.findViewById(R.id.present_radio);
        mAbsentRadio = (RadioButton) view.findViewById(R.id.absent_radio);
        mScoreEditText = (EditText) view.findViewById(R.id.score_edit_text);
        final SimpleDateFormat sdf = new SimpleDateFormat(ClassItemRecordDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        mSubmissionDateButton = (Button) view.findViewById(R.id.submission_date_button);
        mSubmissionDateButton.setOnClickListener(new View.OnClickListener() {
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
        });
        mRemarksEditText = (EditText) view.findViewById(R.id.remarks_edit_text);
        RadioGroup attendanceRadioGroup = (RadioGroup) view.findViewById(R.id.attendance_radio_group);
        if(mClassItem.getCheckAttendance())
            attendanceRadioGroup.setVisibility(View.VISIBLE);
        else
            attendanceRadioGroup.setVisibility(View.GONE);
        if(mClassItem.getRecordScores())
            mScoreEditText.setVisibility(View.VISIBLE);
        else
            mScoreEditText.setVisibility(View.GONE);
        if(mClassItem.getRecordSubmissions())
            mSubmissionDateButton.setVisibility(View.VISIBLE);
        else
            mSubmissionDateButton.setVisibility(View.GONE);

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
                Boolean attendance = null;
                if(mPresentRadio.isChecked())
                    attendance = true;
                if(mAbsentRadio.isChecked())
                    attendance = false;
                Float score;
                try {
                    score = Float.parseFloat(mScoreEditText.getText().toString());
                }
                catch(Exception e) {
                    score = null;
                }
                Date submissionDate = (Date) mSubmissionDateButton.getTag();
                String remarks = mRemarksEditText.getText().toString();
                mClassItemRecord.setAttendance(attendance);
                mClassItemRecord.setScore(score);
                mClassItemRecord.setSubmissionDate(submissionDate);
                mClassItemRecord.setRemarks(remarks);
                try {
                    ((NewEditListener) mActivity).onNewEditItemRecord(mClassItemRecord, mFragmentTag);
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

        if(mClassItemRecord != null) {
            ((TextView) view.findViewById(R.id.student_name_text_view)).setText(mClassItemRecord.getClassStudent().getName());
            Boolean attendance = mClassItemRecord.getAttendance();
            if(attendance != null) {
                if(attendance)
                    mPresentRadio.setChecked(true);
                else
                    mAbsentRadio.setChecked(true);
            }
            Date submissionDate = mClassItemRecord.getSubmissionDate();
            if(submissionDate != null) {
                mSubmissionDateButton.setText(sdf.format(submissionDate));
                mSubmissionDateButton.setTag(submissionDate);
            }
            Float score = mClassItemRecord.getScore();
            if(score != null)
                mScoreEditText.setText(String.valueOf(score));
            mRemarksEditText.setText(mClassItemRecord.getRemarks());
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_SUBMISSION_DATE)) {
                Date submissionDate = (Date) savedInstanceState.getSerializable(STATE_SUBMISSION_DATE);
                mSubmissionDateButton.setText(sdf.format(submissionDate));
                mSubmissionDateButton.setTag(submissionDate);
            }
            boolean bPresent = savedInstanceState.getBoolean(STATE_ATTENDANCE);
            mPresentRadio.setChecked(bPresent);
            mAbsentRadio.setChecked(!bPresent);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        Date submissionDate = (Date) mSubmissionDateButton.getTag();
        if(submissionDate != null)
            outState.putSerializable(STATE_SUBMISSION_DATE, submissionDate);
        outState.putBoolean(STATE_ATTENDANCE, mPresentRadio.isChecked());
        super.onSaveInstanceState(outState);
    }
}
