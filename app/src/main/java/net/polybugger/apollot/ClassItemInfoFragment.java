package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemNoteDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;

public class ClassItemInfoFragment extends Fragment {

    public static final String CLASS_ITEM_ARG = "net.polybugger.apollot.class_item_arg";

    private Activity mActivity;
    private ClassItemDbAdapter.ClassItem mClassItem;
    private TextView mItemDateTextView;
    private TextView mItemTypeTextView;
    private TextView mCheckAttendanceTextView;
    private TextView mDescriptionTextView;
    private TextView mPerfectScoreTextView;
    private TextView mDueDateTextView;

    private TextView mAttendanceSummaryTextView;
    private TextView mScoresSummaryTextView;
    private TextView mSubmissionsSummaryTextView;
    private DbQuerySummaryTask mSummaryTask;

    private LinearLayoutCompat mNoteLinearLayout;
    private ArrayList<ClassItemNoteDbAdapter.ClassItemNote> mNoteList;
    private DbQueryNotesTask mNoteTask;
    private View.OnClickListener mEditNoteClickListener;
    private View.OnClickListener mRemoveNoteClickListener;

    public ClassItemInfoFragment() { }

    public static ClassItemInfoFragment newInstance(ClassItemDbAdapter.ClassItem classItem) {
        ClassItemInfoFragment f = new ClassItemInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ITEM_ARG, classItem);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClassItem = (ClassItemDbAdapter.ClassItem) args.getSerializable(CLASS_ITEM_ARG);

        View view = inflater.inflate(R.layout.fragment_class_item_info, container, false);
        mDescriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        mItemDateTextView = (TextView) view.findViewById(R.id.item_date_text_view);
        mItemTypeTextView = (TextView) view.findViewById(R.id.item_type_text_view);
        mCheckAttendanceTextView = (TextView) view.findViewById(R.id.check_attendance_text_view);
        mPerfectScoreTextView = (TextView) view.findViewById(R.id.perfect_score_text_view);
        mDueDateTextView = (TextView) view.findViewById(R.id.due_date_text_view);
        view.findViewById(R.id.edit_class_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassItemDialogFragment.DialogArgs dialogArgs = new NewEditClassItemDialogFragment.DialogArgs(getString(R.string.edit_class_item), getString(R.string.save_button));
                NewEditClassItemDialogFragment f = NewEditClassItemDialogFragment.newInstance(dialogArgs, mClassItem, getTag());
                f.show(getFragmentManager(), NewEditClassItemDialogFragment.TAG);
                */
            }
        });
        view.findViewById(R.id.remove_class_item_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                RemoveDialog d = new RemoveDialog(mActivity);
                mCurrentDialog = d.show();
                */
            }
        });

        mNoteLinearLayout = (LinearLayoutCompat) view.findViewById(R.id.note_linear_layout);
        mEditNoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassNoteAttachmentDialogFragment.DialogArgs noteDialogArgs = new NewEditClassNoteAttachmentDialogFragment.DialogArgs(getString(R.string.edit_class_item_note_attachment), getString(R.string.save_button));
                NewEditClassNoteAttachmentDialogFragment f = NewEditClassNoteAttachmentDialogFragment.newInstance(noteDialogArgs, (ClassItemNoteAttachment) view.getTag(), getTag());
                f.show(getFragmentManager(), NewEditClassNoteAttachmentDialogFragment.TAG);
                */
            }
        };
        mRemoveNoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                final ClassItemNoteAttachment itemNote = (ClassItemNoteAttachment) view.getTag();
                RemoveClassNoteAttachmentDialog removeDialog = new RemoveClassNoteAttachmentDialog(mActivity, itemNote, getString(R.string.remove_class_item_note),
                        new RemoveClassNoteAttachmentDialog.RemoveClassNoteAttachmentListener() {
                            @Override
                            public void onRemove() {
                                if(ClassItemNoteAttachmentDbAdapter.delete(itemNote.getClassId(), itemNote.getItemId(), itemNote.getNoteId()) >= 1) {
                                    int childPosition = mNoteList.indexOf(itemNote);
                                    if(childPosition != -1) {
                                        mNoteList.remove(childPosition);
                                        mNoteAttachmentLinearLayout.removeViewAt(childPosition);
                                    }
                                }
                            }
                        });
                removeDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogFragmentShown = false;
                    }
                });
                mCurrentDialog = removeDialog.show();
                */
            }
        };

        mAttendanceSummaryTextView = (TextView) view.findViewById(R.id.attendance_summary_text_view);
        mScoresSummaryTextView = (TextView) view.findViewById(R.id.scores_summary_text_view);
        mSubmissionsSummaryTextView = (TextView) view.findViewById(R.id.submissions_summary_text_view);

        populateClassItemViews();

        mNoteTask = new DbQueryNotesTask();
        mNoteTask.execute(mClassItem);

        updateSummary();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_new_note:
                /*
                if(mDialogFragmentShown)
                    return true;
                mDialogFragmentShown = true;
                NewEditClassNoteAttachmentDialogFragment.DialogArgs noteDialogArgs = new NewEditClassNoteAttachmentDialogFragment.DialogArgs(getString(R.string.new_class_item_note_attachment), getString(R.string.add_button));
                NewEditClassNoteAttachmentDialogFragment nf = NewEditClassNoteAttachmentDialogFragment.newInstance(noteDialogArgs, null, getTag());
                nf.show(getFragmentManager(), NewEditClassNoteAttachmentDialogFragment.TAG);
                return true;
                */
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_item_info, menu);
    }

    public void updateSummary() {
        mSummaryTask = new DbQuerySummaryTask();
        mSummaryTask.execute(mClassItem);
    }

    private void populateClassItemViews() {
        SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        Date itemDate = mClassItem.getItemDate();
        if(itemDate != null) {
            mItemDateTextView.setText(sdf.format(itemDate));
        }
        ClassItemTypeDbAdapter.ItemType itemType = mClassItem.getItemType();
        if(itemType != null) {
            mItemTypeTextView.setText(itemType.getDescription());
            mItemTypeTextView.setVisibility(View.VISIBLE);
        }
        else
            mItemTypeTextView.setVisibility(View.GONE);
        mDescriptionTextView.setText(mClassItem.getDescription());
        if(mClassItem.getCheckAttendance())
            mCheckAttendanceTextView.setVisibility(View.VISIBLE);
        else
            mCheckAttendanceTextView.setVisibility(View.GONE);
        if(mClassItem.getRecordScores()) {
            Float perfectScore = mClassItem.getPerfectScore();
            if(perfectScore != null)
                mPerfectScoreTextView.setText(getString(R.string.perfect_score_label) + " " + String.valueOf(mClassItem.getPerfectScore()));
            mPerfectScoreTextView.setVisibility(View.VISIBLE);
        }
        else
            mPerfectScoreTextView.setVisibility(View.GONE);
        if(mClassItem.getRecordSubmissions()) {
            Date dueDate = mClassItem.getSubmissionDueDate();
            if(dueDate != null)
                mDueDateTextView.setText(getString(R.string.submission_due_date_label) + " " + sdf.format(dueDate));
            mDueDateTextView.setVisibility(View.VISIBLE);
        }
        else
            mDueDateTextView.setVisibility(View.GONE);
    }

    private class DbQueryNotesTask extends AsyncTask<ClassItemDbAdapter.ClassItem, Integer, ArrayList<ClassItemNoteDbAdapter.ClassItemNote>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassItemNoteDbAdapter.ClassItemNote> doInBackground(ClassItemDbAdapter.ClassItem... classItem) {
            ClassItemDbAdapter.ClassItem classItem_ = classItem[0];
            return ClassItemNoteDbAdapter.getClassItemNotes(classItem_.getClassId(), classItem_.getItemId());
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // TODO updates loader
        }

        @Override
        protected void onCancelled() {
            // TODO cleanup loader on cancel
        }

        @Override
        protected void onPostExecute(ArrayList<ClassItemNoteDbAdapter.ClassItemNote> noteList) {
            mNoteList = noteList;
            for(ClassItemNoteDbAdapter.ClassItemNote itemNote : mNoteList) {
                mNoteLinearLayout.addView(getNoteView(mActivity.getLayoutInflater(), itemNote, mEditNoteClickListener, mRemoveNoteClickListener));
            }
        }
    }

    private class DbQuerySummaryTask extends AsyncTask<ClassItemDbAdapter.ClassItem, Integer, ClassItemDbAdapter.ClassItem> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ClassItemDbAdapter.ClassItem doInBackground(ClassItemDbAdapter.ClassItem... classItem) {
            ClassItemDbAdapter.ClassItem classItem_ = classItem[0];
            int studentCount = ClassStudentDbAdapter.getClassStudentCount(classItem_.getClassId());
            if(classItem_.getCheckAttendance()) {
                ClassItemRecordDbAdapter.getAttendanceSummary(classItem_);
                classItem_.setNullAttendanceCount(studentCount - classItem_.getPresentCount() - classItem_.getAbsentCount());
            }
            if(classItem_.getRecordScores()) {
                ClassItemRecordDbAdapter.getScoresCount(classItem_);
                classItem_.setNullScoresCount(studentCount - classItem_.getScoresCount());
            }
            if(classItem_.getRecordSubmissions()) {
                ClassItemRecordDbAdapter.getSubmissionsSummary(classItem_);
                classItem_.setNullSubmissionsCount(studentCount - classItem_.getSubmissionsCount() - classItem_.getLateCount());
            }

            return classItem_;
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // TODO updates loader
        }

        @Override
        protected void onCancelled() {
            // TODO cleanup loader on cancel
        }

        @Override
        protected void onPostExecute(ClassItemDbAdapter.ClassItem classItem) {
            if(mClassItem.getCheckAttendance()) {
                int presentCount = classItem.getPresentCount();
                int absentCount = classItem.getAbsentCount();
                int nullCount = classItem.getNullAttendanceCount();
                mClassItem.setAttendanceSummary(presentCount, absentCount);
                mClassItem.setNullAttendanceCount(nullCount);
                mAttendanceSummaryTextView.setText(getString(R.string.present_label) + " " + String.valueOf(presentCount) + ", " +
                        getString(R.string.absent_label) + " " + String.valueOf(absentCount) + ", " +
                        getString(R.string.null_attendance_label) + " " + String.valueOf(nullCount));
                mAttendanceSummaryTextView.setVisibility(View.VISIBLE);
            }
            else
                mAttendanceSummaryTextView.setVisibility(View.GONE);
            if(mClassItem.getRecordScores()) {
                int scoresCount = classItem.getScoresCount();
                int nullCount = classItem.getNullScoresCount();
                mClassItem.setScoresCount(scoresCount);
                mClassItem.setNullScoresCount(nullCount);
                mScoresSummaryTextView.setText(getString(R.string.scores_label) + " " + String.valueOf(scoresCount) + ", " +
                        getString(R.string.null_scores_label) + " " + String.valueOf(nullCount));
                mScoresSummaryTextView.setVisibility(View.VISIBLE);
            }
            else
                mScoresSummaryTextView.setVisibility(View.GONE);
            if(mClassItem.getRecordSubmissions()) {
                int submissionsCount = classItem.getSubmissionsCount();
                int lateCount = classItem.getLateCount();
                int nullCount = classItem.getNullSubmissionsCount();
                mClassItem.setSubmissionsSummary(submissionsCount, lateCount);
                mClassItem.setNullSubmissionsCount(nullCount);
                mSubmissionsSummaryTextView.setText(getString(R.string.submissions_label) + " " + String.valueOf(submissionsCount) + ", " +
                        getString(R.string.late_label) + " " + String.valueOf(lateCount) + ", " +
                        getString(R.string.null_submissions_label) + " " + String.valueOf(nullCount));
                mSubmissionsSummaryTextView.setVisibility(View.VISIBLE);
            }
            else
                mSubmissionsSummaryTextView.setVisibility(View.GONE);
        }
    }

    private View _getNoteView(View rowView, ClassItemNoteDbAdapter.ClassItemNote itemNote, View.OnClickListener editNoteClickListener, View.OnClickListener removeNoteClickListener) {
        TextView noteTextView = (TextView) rowView.findViewById(R.id.note_text_view);
        noteTextView.setText(itemNote.getNote());
        noteTextView.setTag(itemNote);
        if(editNoteClickListener != null) {
            noteTextView.setOnClickListener(editNoteClickListener);
        }
        ImageButton removeButton = (ImageButton) rowView.findViewById(R.id.remove_button);
        removeButton.setTag(itemNote);
        if(removeNoteClickListener != null)
            removeButton.setOnClickListener(removeNoteClickListener);
        return rowView;
    }

    @SuppressLint("InflateParams")
    private View getNoteView(LayoutInflater inflater, ClassItemNoteDbAdapter.ClassItemNote itemNote, View.OnClickListener editNoteClickListener, View.OnClickListener removeNoteClickListener) {
        return _getNoteView(inflater.inflate(R.layout.class_note_row, null), itemNote, editNoteClickListener, removeNoteClickListener);
    }

}
