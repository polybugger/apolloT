package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ClassStudentItemsFragment extends Fragment implements ClassItemRecordNewEditDialogFragment.NewEditListener {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private ClassStudentDbAdapter.ClassStudent mClassStudent;

    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;

    public ClassStudentItemsFragment() { }

    public static ClassStudentItemsFragment newInstance(ClassDbAdapter.Class class_, ClassStudentDbAdapter.ClassStudent student) {
        ClassStudentItemsFragment f = new ClassStudentItemsFragment();
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
    public void onDestroyView() {
        mTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mClassStudent = (ClassStudentDbAdapter.ClassStudent) args.getSerializable(STUDENT_ARG);

        View view = inflater.inflate(R.layout.fragment_class_student_items, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<ClassItemRecordDbAdapter.ClassItemRecord>());
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fm = getFragmentManager();
                ClassItemRecordNewEditDialogFragment df = (ClassItemRecordNewEditDialogFragment) fm.findFragmentByTag(ClassItemRecordNewEditDialogFragment.TAG);
                if (df == null) {
                    ClassItemRecordDbAdapter.ClassItemRecord record = (ClassItemRecordDbAdapter.ClassItemRecord) view.findViewById(R.id.description_text_view).getTag();
                    record.setStudent(mClassStudent.getStudent());
                    df = ClassItemRecordNewEditDialogFragment.newInstance(getString(R.string.edit_class_item_record), getString(R.string.save_button), record.getClassItem(), record, getTag());
                    df.show(fm, ClassItemRecordNewEditDialogFragment.TAG);
                }
            }
        });

        requeryClassStudentItems();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_student_items, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_sort_description:
                mListAdapter.sortBy(R.id.action_sort_description);
                return true;
            case R.id.action_sort_date:
                mListAdapter.sortBy(R.id.action_sort_date);
                return true;
            case R.id.action_sort_activity:
                mListAdapter.sortBy(R.id.action_sort_activity);
                return true;
            case R.id.action_sort_perfect_score:
                mListAdapter.sortBy(R.id.action_sort_perfect_score);
                return true;
            case R.id.action_sort_submission_due_date:
                mListAdapter.sortBy(R.id.action_sort_submission_due_date);
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

    public void requeryClassStudentItems() {
        mTask = new DbQueryTask();
        mTask.execute(new ClassStudentActivity.TaskParams(mClass.getClassId(), mClassStudent.getStudentId()));
    }

    @Override
    public void onNewEditItemRecord(ClassItemRecordDbAdapter.ClassItemRecord itemRecord, String fragmentTag) {
        ClassStudentDbAdapter.ClassStudent student = itemRecord.getClassStudent();
        ClassItemDbAdapter.ClassItem classItem = itemRecord.getClassItem();
        if(itemRecord.getRecordId() == null) {
            long recordId = ClassItemRecordDbAdapter.insert(classItem.getClassId(), student.getStudentId(), classItem.getItemId(), itemRecord.getAttendance(), itemRecord.getScore(), itemRecord.getSubmissionDate(), itemRecord.getRemarks());
            if(recordId != -1) {
                itemRecord.setRecordId(recordId);
                int position = mListAdapter.getPosition(itemRecord);
                if(position != -1) {
                    mListAdapter.remove(itemRecord);
                    mListAdapter.insert(itemRecord, position);
                }
            }
        }
        else {
            if(ClassItemRecordDbAdapter.update(classItem.getClassId(), student.getStudentId(), classItem.getItemId(), itemRecord.getRecordId(), itemRecord.getAttendance(), itemRecord.getScore(), itemRecord.getSubmissionDate(), itemRecord.getRemarks()) > 0) {
                int position = mListAdapter.getPosition(itemRecord);
                if(position != -1) {
                    mListAdapter.remove(itemRecord);
                    mListAdapter.insert(itemRecord, position);
                }
            }
        }
        mListAdapter.notifyDataSetChanged();
        try {
            ((ClassStudentActivity) mActivity).requerySummaryItems();
        }
        catch(ClassCastException e) {
            throw new ClassCastException(mActivity.toString() + " must implement " + ClassStudentActivity.class.toString());
        }
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassItemRecordDbAdapter.ClassItemRecord> {

        final private SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, getResources().getConfiguration().locale);

        public void sortBy(int id) {
            sortId = (sortId == id) ? -id : id;
            sort(comp);
        }

        private int sortId;

        final private Comparator<ClassItemRecordDbAdapter.ClassItemRecord> comp = new Comparator<ClassItemRecordDbAdapter.ClassItemRecord>() {

            @Override
            public int compare(ClassItemRecordDbAdapter.ClassItemRecord arg0, ClassItemRecordDbAdapter.ClassItemRecord arg1) {
                if(sortId == R.id.action_sort_description) {
                    return arg0.getClassItem().getDescription().compareToIgnoreCase(arg1.getClassItem().getDescription());
                }
                else if(-sortId == R.id.action_sort_description) {
                    return -arg0.getClassItem().getDescription().compareToIgnoreCase(arg1.getClassItem().getDescription());
                }
                else if(sortId == R.id.action_sort_date) {
                    Date d0 = arg0.getClassItem().getItemDate();
                    Date d1 = arg1.getClassItem().getItemDate();
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
                else if(-sortId == R.id.action_sort_date) {
                    Date d0 = arg0.getClassItem().getItemDate();
                    Date d1 = arg1.getClassItem().getItemDate();
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
                else if(sortId == R.id.action_sort_activity) {
                    ClassItemTypeDbAdapter.ItemType i0 = arg0.getClassItem().getItemType();
                    ClassItemTypeDbAdapter.ItemType i1 = arg1.getClassItem().getItemType();
                    if(i0 == null)
                        return 1;
                    if(i1 == null)
                        return -1;
                    return i0.getDescription().compareToIgnoreCase(i1.getDescription());
                }
                else if(-sortId == R.id.action_sort_activity) {
                    ClassItemTypeDbAdapter.ItemType i0 = arg0.getClassItem().getItemType();
                    ClassItemTypeDbAdapter.ItemType i1 = arg1.getClassItem().getItemType();
                    if(i0 == null)
                        return 1;
                    if(i1 == null)
                        return -1;
                    return -i0.getDescription().compareToIgnoreCase(i1.getDescription());
                }
                else if(sortId == R.id.action_sort_perfect_score) {
                    Float f0 = arg0.getClassItem().getPerfectScore();
                    Float f1 = arg1.getClassItem().getPerfectScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? -1 : 1);
                }
                else if(-sortId == R.id.action_sort_perfect_score) {
                    Float f0 = arg0.getClassItem().getPerfectScore();
                    Float f1 = arg1.getClassItem().getPerfectScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? 1 : -1);
                }
                else if(sortId == R.id.action_sort_submission_due_date) {
                    Date d0 = arg0.getClassItem().getSubmissionDueDate();
                    Date d1 = arg1.getClassItem().getSubmissionDueDate();
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
                else if(-sortId == R.id.action_sort_submission_due_date) {
                    Date d0 = arg0.getClassItem().getSubmissionDueDate();
                    Date d1 = arg1.getClassItem().getSubmissionDueDate();
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
            RelativeLayout relativeLayout;
            TextView description;
            TextView itemDate;
            TextView itemType;
            TextView perfectScore;
            TextView dueDate;
            TextView score;
            TextView submissionDate;
            TextView attendance;
            TextView remarks;
        }

        private String perfectScoreLabel;
        private String dueDateLabel;
        private String scoreLabel;
        private String submissionDateLabel;

        public ListArrayAdapter(Context context, List<ClassItemRecordDbAdapter.ClassItemRecord> objects) {
            super(context, R.layout.fragment_class_student_items_row, objects);
            perfectScoreLabel = getString(R.string.perfect_score_label);
            dueDateLabel = getString(R.string.submission_due_date_label);
            scoreLabel = getString(R.string.score_label);
            submissionDateLabel = getString(R.string.submission_date_label);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassItemRecordDbAdapter.ClassItemRecord classStudentRecord = getItem(position);
            ClassItemDbAdapter.ClassItem classItem = classStudentRecord.getClassItem();
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_student_items_row, parent, false);
                viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_layout);
                viewHolder.description = (TextView) convertView.findViewById(R.id.description_text_view);
                viewHolder.itemDate = (TextView) convertView.findViewById(R.id.item_date_text_view);
                viewHolder.itemType = (TextView) convertView.findViewById(R.id.item_type_text_view);
                viewHolder.perfectScore = (TextView) convertView.findViewById(R.id.perfect_score_text_view);
                viewHolder.dueDate = (TextView) convertView.findViewById(R.id.submission_due_date_text_view);
                viewHolder.score = (TextView) convertView.findViewById(R.id.score_text_view);
                viewHolder.submissionDate = (TextView) convertView.findViewById(R.id.submission_date_text_view);
                viewHolder.attendance = (TextView) convertView.findViewById(R.id.attendance_text_view);
                viewHolder.remarks = (TextView) convertView.findViewById(R.id.remarks_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.description.setText(classItem.getDescription());
            viewHolder.description.setTag(classStudentRecord);
            Date itemDate = classItem.getItemDate();
            viewHolder.itemDate.setText(itemDate == null ? null : sdf.format(itemDate));
            ClassItemTypeDbAdapter.ItemType itemType = classItem.getItemType();
            viewHolder.relativeLayout.setBackgroundColor(itemType.getColorInt());
            if(itemType != null) {
                viewHolder.itemType.setText(itemType.getDescription());
                viewHolder.itemType.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.itemType.setVisibility(View.GONE);
            if(classItem.getRecordScores()) {
                Float perfectScore = classItem.getPerfectScore();
                Float score = classStudentRecord.getScore();
                viewHolder.perfectScore.setText(perfectScore == null ? null : perfectScoreLabel + " " + String.valueOf(perfectScore));
                viewHolder.perfectScore.setVisibility(View.VISIBLE);
                if(score == null)
                    viewHolder.score.setVisibility(View.GONE);
                else {
                    viewHolder.score.setText(score == null ? null : scoreLabel + " " + String.valueOf(score));
                    viewHolder.score.setVisibility(View.VISIBLE);
                }
            }
            else {
                viewHolder.perfectScore.setVisibility(View.GONE);
                viewHolder.score.setVisibility(View.GONE);
            }
            if(classItem.getRecordSubmissions()) {
                Date dueDate = classItem.getSubmissionDueDate();
                Date submissionDate = classStudentRecord.getSubmissionDate();
                viewHolder.dueDate.setText(dueDate == null ? null : dueDateLabel + " " + sdf.format(dueDate));
                viewHolder.dueDate.setVisibility(View.VISIBLE);
                if(submissionDate == null)
                    viewHolder.submissionDate.setVisibility(View.GONE);
                else {
                    viewHolder.submissionDate.setText(submissionDateLabel + " " + sdf.format(submissionDate));
                    viewHolder.submissionDate.setVisibility(View.VISIBLE);
                }
            }
            else {
                viewHolder.dueDate.setVisibility(View.GONE);
                viewHolder.submissionDate.setVisibility(View.GONE);
            }
            if(classItem.getCheckAttendance()) {
                Boolean attendance = classStudentRecord.getAttendance();
                if(attendance == null) {
                    viewHolder.attendance.setVisibility(View.GONE);
                }
                else {
                    viewHolder.attendance.setText(attendance ? getString(R.string.present) : getString(R.string.absent));
                    viewHolder.attendance.setVisibility(View.VISIBLE);
                }
            }
            else
                viewHolder.attendance.setVisibility(View.GONE);
            String remarks = classStudentRecord.getRemarks();
            if(TextUtils.isEmpty(remarks)) {
                viewHolder.remarks.setVisibility(View.GONE);
            }
            else {
                viewHolder.remarks.setText(remarks);
                viewHolder.remarks.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }

    private class DbQueryTask extends AsyncTask<ClassStudentActivity.TaskParams, Integer, ArrayList<ClassItemRecordDbAdapter.ClassItemRecord>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> doInBackground(ClassStudentActivity.TaskParams... taskParams) {
            ArrayList<ClassItemRecordDbAdapter.ClassItemRecord> classItems = ClassItemRecordDbAdapter.getClassStudentRecords(taskParams[0].mClassId, taskParams[0].mStudentId);
            // TODO dbquery for student count and class items summary
            return classItems;
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
}
