package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassGradeBreakdownDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.ClassScheduleDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

import java.util.ArrayList;

public class ClassStudentInfoFragment extends Fragment implements StudentNewEditDialogFragment.NewEditListener {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private ClassStudentDbAdapter.ClassStudent mClassStudent;
    private TextView mStudentNameTextView;
    private TextView mGenderTextView;
    private TextView mEmailAddressTextView;
    private TextView mContactNoTextView;
    private LinearLayout mSummaryLinearLayout;
    private DbQueryTask mSummaryTask;

    private ArrayList<SummaryItem> mItemList;

    public ClassStudentInfoFragment() { }

    public static ClassStudentInfoFragment newInstance(ClassDbAdapter.Class class_, ClassStudentDbAdapter.ClassStudent student) {
        ClassStudentInfoFragment f = new ClassStudentInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ARG, class_);
        args.putSerializable(STUDENT_ARG, student);
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
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mClassStudent = (ClassStudentDbAdapter.ClassStudent) args.getSerializable(STUDENT_ARG);

        View view = inflater.inflate(R.layout.fragment_class_student_info, container, false);
        //view.findViewById(R.id.scroll_view).setBackgroundColor(mClassItem.getItemType().getColorInt());

        mStudentNameTextView = (TextView) view.findViewById(R.id.student_name_text_view);
        mGenderTextView = (TextView) view.findViewById(R.id.gender_text_view);
        mEmailAddressTextView = (TextView) view.findViewById(R.id.email_address_text_view);
        mContactNoTextView = (TextView) view.findViewById(R.id.contact_no_text_view);

        view.findViewById(R.id.edit_class_student_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = getFragmentManager();
                StudentNewEditDialogFragment df = (StudentNewEditDialogFragment) fm.findFragmentByTag(StudentNewEditDialogFragment.TAG);
                if(df == null) {
                    df = StudentNewEditDialogFragment.newInstance(getString(R.string.edit_student), getString(R.string.save_button), mClassStudent.getStudent(), getTag());
                    df.show(fm, StudentNewEditDialogFragment.TAG);
                }

            }
        });
        view.findViewById(R.id.remove_class_student_button).setOnClickListener(new View.OnClickListener() {
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

        mSummaryLinearLayout = (LinearLayout) view.findViewById(R.id.summary_linear_layout);
        /*
        mAttendanceSummaryTextView = (TextView) view.findViewById(R.id.attendance_summary_text_view);
        mScoresSummaryTextView = (TextView) view.findViewById(R.id.scores_summary_text_view);
        mSubmissionsSummaryTextView = (TextView) view.findViewById(R.id.submissions_summary_text_view);
        */


        updateSummary();

        populateClassStudentViews();

        return view;
    }

    public void updateSummary() {
        if(mSummaryLinearLayout.getChildCount() > 0)
            mSummaryLinearLayout.removeAllViews();
        mSummaryTask = new DbQueryTask();
        mSummaryTask.execute(new ClassStudentActivity.TaskParams(mClass.getClassId(), mClassStudent.getStudentId()));
    }

    private void populateClassStudentViews() {
        StudentDbAdapter.Student student = mClassStudent.getStudent();
        mStudentNameTextView.setText(student.getName());

        // TODO replace gender with localized text
        mGenderTextView.setText(student.getGender());

        String emailAddress = student.getEmailAddress();
        if(TextUtils.isEmpty(emailAddress)) {
            mEmailAddressTextView.setVisibility(View.GONE);
        }
        else {
            mEmailAddressTextView.setText(emailAddress);
            mEmailAddressTextView.setVisibility(View.VISIBLE);
        }

        String contactNo = student.getContactNo();
        if(TextUtils.isEmpty(contactNo)) {
            mContactNoTextView.setVisibility(View.GONE);
        }
        else {
            mContactNoTextView.setText(emailAddress);
            mContactNoTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNewEditStudent(StudentDbAdapter.Student student, String fragmentTag) {

        long studentId = student.getStudentId();
        if(studentId != -1) {
            if(StudentDbAdapter.update(studentId, student.getLastName(), student.getFirstName(), student.getMiddleName(), student.getGender(), student.getEmailAddress(), student.getContactNo()) >= 1){
                mClassStudent.setStudent(student);
            }
            populateClassStudentViews();
        }
    }

    private class SummaryItem {
        public ClassGradeBreakdownDbAdapter.ClassGradeBreakdown mGradeBreakdown;
        public float mPercentage;
        public int mAttendanceCount;
        public int mAbsencesCount;
        public int mClassItemCount;
        public int mClassItemTotal;

        public SummaryItem(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown) {
            mGradeBreakdown = gradeBreakdown;
            mPercentage = 0;
            mClassItemCount = 0;
            mClassItemTotal = 0;
            mAttendanceCount = 0;
            mAbsencesCount = 0;
        }

        public void incrementItemCount() {
            mClassItemCount = mClassItemCount + 1;
        }
        public void incrementItemTotal() {
            mClassItemTotal = mClassItemTotal + 1;
        }
        public void incrementAttendanceCount() {
            mAttendanceCount = mAttendanceCount + 1;
        }
        public void incrementAbsencesCount() {
            mAbsencesCount = mAbsencesCount + 1;
        }
        public void addPercentage(float percentage) {
            mPercentage = mPercentage + percentage;
        }
        public float getPercentage() {
            return mPercentage / mClassItemTotal;
        }
        public boolean equals(SummaryItem summaryItem) {
            if(summaryItem != null && summaryItem.mGradeBreakdown.equals(mGradeBreakdown))
                return true;
            return false;
        }

        @Override
        public boolean equals(Object object) {
            SummaryItem summaryItem;
            if(object != null) {
                try {
                    summaryItem = (SummaryItem) object;
                    if(summaryItem.mGradeBreakdown.equals(mGradeBreakdown))
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + SummaryItem.class.toString());
                }
            }
            return false;
        }
    }

    private class DbQueryTask extends AsyncTask<ClassStudentActivity.TaskParams, Integer, ArrayList<SummaryItem>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<SummaryItem> doInBackground(ClassStudentActivity.TaskParams... taskParams) {
            ArrayList<ClassGradeBreakdownDbAdapter.ClassGradeBreakdown> gradeBreakdowns = ClassGradeBreakdownDbAdapter.getClassGradeBreakdowns(taskParams[0].mClassId);
            ArrayList<SummaryItem> summaryItems = new ArrayList<>();
            for(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown : gradeBreakdowns) {
                summaryItems.add(new SummaryItem(gradeBreakdown));
            }
            ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> classItems = ClassItemRecordDbAdapter.getClassStudentRecords(taskParams[0].mClassId, taskParams[0].mStudentId);
            int index; SummaryItem summaryItem; float percentage;
            ClassItemDbAdapter.ClassItem classItem;
            for(ClassItemRecordDbAdapter.ClassItemRecord classItemRecord : classItems) {
                classItem = classItemRecord.getClassItem();
                summaryItem = new SummaryItem(new ClassGradeBreakdownDbAdapter.ClassGradeBreakdown(taskParams[0].mClassId, classItem.getItemType(), 0));
                index = summaryItems.indexOf(summaryItem);
                if (index != -1) {
                    summaryItem = summaryItems.get(index);
                } else {
                    summaryItems.add(summaryItem);
                }
                summaryItem.incrementItemTotal();
                if(classItemRecord.getRecordId() != null) {
                    summaryItem.incrementItemCount();
                    boolean recordScores = classItem.getRecordScores();
                    if (recordScores) {
                        percentage = classItemRecord.getScore() / classItem.getPerfectScore();
                        summaryItem.addPercentage(percentage);
                    }
                    if (classItem.getCheckAttendance()) {
                        Boolean attendance = classItemRecord.getAttendance();
                        if (attendance != null) {
                            if (attendance) {
                                if (!recordScores) {
                                    summaryItem.addPercentage((float) 1.0);
                                }
                            } else {
                                summaryItem.incrementAbsencesCount();
                            }
                        }
                        summaryItem.incrementAttendanceCount();
                    }
                }
            }
            return summaryItems;
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
        protected void onPostExecute(ArrayList<SummaryItem> list) {
            mItemList = list;
            for(SummaryItem summaryItem : mItemList) {
                mSummaryLinearLayout.addView(getSummaryItemView(mActivity.getLayoutInflater(), summaryItem));
            }
        }
    }

    private View _getSummaryItemView(View rowView, SummaryItem summaryItem) {
        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.summary_item_relative_layout_row);
        relativeLayout.setBackgroundColor(summaryItem.mGradeBreakdown.getItemType().getColorInt());

        ((TextView) rowView.findViewById(R.id.summary_item_text_view)).setText(summaryItem.mGradeBreakdown.getItemType().getDescription());
        ((TextView) rowView.findViewById(R.id.count_text_view)).setText(mActivity.getString(R.string.count_label) + " " + summaryItem.mClassItemCount + "/" + summaryItem.mClassItemTotal);
        ((TextView) rowView.findViewById(R.id.attendance_count_text_view)).setText(mActivity.getString(R.string.attendance_count_label) + " " + (summaryItem.mAttendanceCount - summaryItem.mAbsencesCount) + "/" + summaryItem.mAttendanceCount);
        float breakdownPercentage = summaryItem.mGradeBreakdown.getPercentage();
        float summaryPercentage = summaryItem.getPercentage() * 100;
        ((TextView) rowView.findViewById(R.id.grade_breakdown_percentage_text_view)).setText(String.format("%s %.2f%%", mActivity.getString(R.string.percentage_label), breakdownPercentage));
        ((TextView) rowView.findViewById(R.id.percentage_text_view)).setText(String.format("%.2f%%", summaryPercentage));
        ((TextView) rowView.findViewById(R.id.subtotal_percentage_text_view)).setText(String.format("%s %.2f%%", mActivity.getString(R.string.subtotal_label), breakdownPercentage / 100 * summaryPercentage));

        return rowView;
    }

    @SuppressLint("InflateParams")
    private View getSummaryItemView(LayoutInflater inflater, SummaryItem summaryItem) {
        return _getSummaryItemView(inflater.inflate(R.layout.summary_item_row, null), summaryItem);
    }

}
