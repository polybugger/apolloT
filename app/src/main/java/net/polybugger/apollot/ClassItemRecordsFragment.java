package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;

public class ClassItemRecordsFragment extends Fragment {

    public static final String CLASS_ITEM_ARG = "net.polybugger.apollot.class_item_arg";

    private Activity mActivity;
    private ClassItemDbAdapter.ClassItem mClassItem;

    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;

    public ClassItemRecordsFragment() { }

    public static ClassItemRecordsFragment newInstance(ClassItemDbAdapter.ClassItem classItem) {
        ClassItemRecordsFragment f = new ClassItemRecordsFragment();
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

        View view = inflater.inflate(R.layout.fragment_class_item_records, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<ClassItemRecordDbAdapter.ClassItemRecord>());
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                ClassItemRecordDbAdapter.ClassItemRecord record = (ClassItemRecordDbAdapter.ClassItemRecord) view.findViewById(R.id.student_name_text_view).getTag();

                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassItemRecordDialogFragment.DialogArgs dialogArgs = new NewEditClassItemRecordDialogFragment.DialogArgs(getString(R.string.edit_class_item_record), getString(R.string.save_button));
                NewEditClassItemRecordDialogFragment f = NewEditClassItemRecordDialogFragment.newInstance(dialogArgs, mClassItem, record, getTag());
                f.show(getFragmentManager(), NewEditClassItemRecordDialogFragment.TAG);
                */
            }
        });

        mTask = new DbQueryTask();
        mTask.execute(mClassItem);

        return view;
    }

    @Override
    public void onDestroyView() {
        mTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_new_student:
                /*
                if(mDialogFragmentShown)
                    return true;
                mDialogFragmentShown = true;
                NewEditStudentDialogFragment.DialogArgs dialogArgs = new NewEditStudentDialogFragment.DialogArgs(getString(R.string.new_student), getString(R.string.add_button));
                NewEditStudentDialogFragment sf = NewEditStudentDialogFragment.newInstance(dialogArgs, null, getTag());
                sf.show(getFragmentManager(), NewEditStudentDialogFragment.TAG);
                */
                return true;
            case R.id.action_existing_students:
                /*
                if(mDialogFragmentShown)
                    return true;
                mDialogFragmentShown = true;
                long[] studentIds = ClassStudentDbAdapter.getClassStudentIds(mClassItem.getClassId());
                ExistingStudentsDialogFragment esf = ExistingStudentsDialogFragment.newInstance(studentIds, getTag());
                esf.show(getFragmentManager(), NewEditStudentDialogFragment.TAG);
                */
                return true;
            case R.id.action_sort_last_name:
                mListAdapter.sortBy(R.id.action_sort_last_name);
                return true;
            case R.id.action_sort_first_name:
                mListAdapter.sortBy(R.id.action_sort_first_name);
                return true;
            case R.id.action_sort_attendance:
                mListAdapter.sortBy(R.id.action_sort_attendance);
                return true;
            case R.id.action_sort_score:
                mListAdapter.sortBy(R.id.action_sort_score);
                return true;
            case R.id.action_sort_submission_date:
                mListAdapter.sortBy(R.id.action_sort_submission_date);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void notifyUpdate(ClassItemDbAdapter.ClassItem classItem) {
        mClassItem = classItem;
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_item_records, menu);
        menu.findItem(R.id.action_sort_attendance).setVisible(mClassItem.getCheckAttendance());
        menu.findItem(R.id.action_sort_score).setVisible(mClassItem.getRecordScores());
        menu.findItem(R.id.action_sort_submission_date).setVisible(mClassItem.getRecordSubmissions());
    }

    private class DbQueryTask extends AsyncTask<ClassItemDbAdapter.ClassItem, Integer, ArrayList<ClassItemRecordDbAdapter.ClassItemRecord>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> doInBackground(ClassItemDbAdapter.ClassItem... classItem) {
            ClassItemDbAdapter.ClassItem classItem_ = classItem[0];
            ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> records = ClassItemRecordDbAdapter.getClassItemRecords(classItem_.getClassId(), classItem_.getItemId());
            // TODO dbquery for student count and class items summary
            return records;
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
        protected void onPostExecute(ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> list) {
            // TODO cleanup loader on finish
            mListAdapter.clear();
            mListAdapter.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassItemRecordDbAdapter.ClassItemRecord> {

        final private SimpleDateFormat sdf = new SimpleDateFormat(ClassItemRecordDbAdapter.SDF_DISPLAY_TEMPLATE, getResources().getConfiguration().locale);

        public void sortBy(int id) {
            sortId = (sortId == id) ? -id : id;
            sort(comp);
        }

        private int sortId;

        final private Comparator<ClassItemRecordDbAdapter.ClassItemRecord> comp = new Comparator<ClassItemRecordDbAdapter.ClassItemRecord>() {

            @Override
            public int compare(ClassItemRecordDbAdapter.ClassItemRecord arg0, ClassItemRecordDbAdapter.ClassItemRecord arg1) {
                if(sortId == R.id.action_sort_last_name) {
                    String s0 = arg0.getClassStudent().getStudent().getLastName();
                    String s1 = arg1.getClassStudent().getStudent().getLastName();
                    if(s0 == null)
                        return 1;
                    if(s1 == null)
                        return -1;
                    return s0.compareToIgnoreCase(s1);
                }
                else if(-sortId == R.id.action_sort_last_name) {
                    String s0 = arg0.getClassStudent().getStudent().getLastName();
                    String s1 = arg1.getClassStudent().getStudent().getLastName();
                    if(s0 == null)
                        return 1;
                    if(s1 == null)
                        return -1;
                    return -s0.compareToIgnoreCase(s1);
                }
                else if(sortId == R.id.action_sort_first_name) {
                    String s0 = arg0.getClassStudent().getStudent().getFirstName();
                    String s1 = arg1.getClassStudent().getStudent().getFirstName();
                    if(s0 == null)
                        return 1;
                    if(s1 == null)
                        return -1;
                    return s0.compareToIgnoreCase(s1);
                }
                else if(-sortId == R.id.action_sort_first_name) {
                    String s0 = arg0.getClassStudent().getStudent().getFirstName();
                    String s1 = arg1.getClassStudent().getStudent().getFirstName();
                    if(s0 == null)
                        return 1;
                    if(s1 == null)
                        return -1;
                    return -s0.compareToIgnoreCase(s1);
                }
                else if(sortId == R.id.action_sort_attendance) {
                    Boolean b0 = arg0.getAttendance();
                    Boolean b1 = arg1.getAttendance();
                    if(b0 == null)
                        return 1;
                    if(b1 == null)
                        return -1;
                    return (b0 ? -1 : b1 ? 1 : 0);
                }
                else if(-sortId == R.id.action_sort_attendance) {
                    Boolean b0 = arg0.getAttendance();
                    Boolean b1 = arg1.getAttendance();
                    if(b0 == null)
                        return 1;
                    if(b1 == null)
                        return -1;
                    return (b0 ? 1 : b1 ? -1 : 0);
                }
                else if(sortId == R.id.action_sort_score) {
                    Float f0 = arg0.getScore();
                    Float f1 = arg1.getScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? -1 : 1);
                }
                else if(-sortId == R.id.action_sort_score) {
                    Float f0 = arg0.getScore();
                    Float f1 = arg1.getScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? 1 : -1);
                }
                else if(sortId == R.id.action_sort_submission_date) {
                    Date d0 = arg0.getSubmissionDate();
                    Date d1 = arg1.getSubmissionDate();
                    if(d0 == null)
                        return 1;
                    if(d1 == null)
                        return -1;
                    Calendar cal0 = Calendar.getInstance();
                    cal0.setTime(d0);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(d1);
                    return cal0.compareTo(cal1);
                }
                else if(-sortId == R.id.action_sort_submission_date) {
                    Date d0 = arg0.getSubmissionDate();
                    Date d1 = arg1.getSubmissionDate();
                    if(d0 == null)
                        return 1;
                    if(d1 == null)
                        return -1;
                    Calendar cal0 = Calendar.getInstance();
                    cal0.setTime(d0);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(d1);
                    return -cal0.compareTo(cal1);
                }
                return 0;
            }
        };

        private class ViewHolder {
            TextView studentName;
            TextView attendance;
            TextView score;
            TextView submissionDate;
            LinearLayoutCompat recordData;
            TextView remarks;
        }

        public ListArrayAdapter(Context context, List<ClassItemRecordDbAdapter.ClassItemRecord> objects) {
            super(context, R.layout.fragment_class_item_records_row, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassItemRecordDbAdapter.ClassItemRecord record = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_item_records_row, parent, false);
                viewHolder.studentName = (TextView) convertView.findViewById(R.id.student_name_text_view);
                viewHolder.attendance = (TextView) convertView.findViewById(R.id.attendance_text_view);
                viewHolder.score = (TextView) convertView.findViewById(R.id.score_text_view);
                viewHolder.submissionDate = (TextView) convertView.findViewById(R.id.submission_date_text_view);
                viewHolder.recordData = (LinearLayoutCompat) convertView.findViewById(R.id.record_data_layout);
                viewHolder.remarks = (TextView) convertView.findViewById(R.id.remarks_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.studentName.setText(record.getClassStudent().getName());
            viewHolder.studentName.setTag(record);

            if(mClassItem.getCheckAttendance()) {
                Boolean attendance = record.getAttendance();
                viewHolder.attendance.setText(attendance == null ? null : attendance ? getString(R.string.present) : getString(R.string.absent));
                viewHolder.attendance.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.attendance.setVisibility(View.GONE);

            boolean recordScores = mClassItem.getRecordScores();
            if(recordScores) {
                Float score = record.getScore();
                Float perfectScore = mClassItem.getPerfectScore();
                viewHolder.score.setText(score == null ? null : String.valueOf(record.getScore()) + " / " + (perfectScore != null ? String.valueOf(perfectScore) : ""));
                viewHolder.score.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.score.setVisibility(View.GONE);

            boolean recordSubmissions = mClassItem.getRecordSubmissions();
            if(recordSubmissions) {
                String late = "";
                Date dueDate = mClassItem.getSubmissionDueDate();
                Date submissionDate = record.getSubmissionDate();
                if(dueDate != null && submissionDate != null) {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(submissionDate);
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(dueDate);
                    if(cal1.compareTo(cal2) == 1)
                        late = " " + getString(R.string.late_hint);
                }
                viewHolder.submissionDate.setText(submissionDate == null ? null : sdf.format(submissionDate) + late);
                viewHolder.submissionDate.setGravity(recordScores ? Gravity.RIGHT : Gravity.LEFT);
                viewHolder.submissionDate.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.submissionDate.setVisibility(View.GONE);

            if(!recordScores && !recordSubmissions)
                viewHolder.recordData.setVisibility(View.GONE);
            else
                viewHolder.recordData.setVisibility(View.VISIBLE);

            String remarks = record.getRemarks();
            if(record.getRecordId() != null) {
                if(TextUtils.isEmpty(remarks))
                    viewHolder.remarks.setVisibility(View.GONE);
                else {
                    viewHolder.remarks.setText(remarks);
                    viewHolder.remarks.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }
    }


}
