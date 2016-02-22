package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ClassItemsFragment extends Fragment {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;

    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;
    private DbRequeryTask mRequeryTask;

    public ClassItemsFragment() { }

    public static ClassItemsFragment newInstance(ClassDbAdapter.Class class_) {
        ClassItemsFragment f = new ClassItemsFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ARG, class_);
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
        if(mRequeryTask != null)
            mRequeryTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);

        View view = inflater.inflate(R.layout.fragment_class_items, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<ClassItemDbAdapter.ClassItem>());
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassItemDbAdapter.ClassItem classItem = (ClassItemDbAdapter.ClassItem) view.findViewById(R.id.description_text_view).getTag();
                startClassItemActivity(classItem);
            }
        });

        mTask = new DbQueryTask();
        mTask.execute(mClass.getClassId());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_new_item:
                /*
                if(mDialogFragmentShown)
                    return true;
                mDialogFragmentShown = true;
                NewEditClassItemDialogFragment.DialogArgs itemDialogArgs = new NewEditClassItemDialogFragment.DialogArgs(getString(R.string.new_class_item), getString(R.string.add_button));
                NewEditClassItemDialogFragment f = NewEditClassItemDialogFragment.newInstance(itemDialogArgs, null, getTag());
                f.show(getFragmentManager(), NewEditClassItemDialogFragment.TAG);
                */
                return true;
            case R.id.action_sort_description:
                mListAdapter.sortBy(R.id.action_sort_description);
                return true;
            case R.id.action_sort_date:
                mListAdapter.sortBy(R.id.action_sort_date);
                return true;
            case R.id.action_sort_activity:
                mListAdapter.sortBy(R.id.action_sort_activity);
                return true;
            case R.id.action_sort_check_attendance:
                mListAdapter.sortBy(R.id.action_sort_check_attendance);
                return true;
            case R.id.action_sort_perfect_score:
                mListAdapter.sortBy(R.id.action_sort_perfect_score);
                return true;
            case R.id.action_sort_submission_due_date:
                mListAdapter.sortBy(R.id.action_sort_submission_due_date);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_items, menu);
    }

    private void startClassItemActivity(ClassItemDbAdapter.ClassItem classItem) {
        /*
        Intent intent = new Intent(mActivity, ClassItemActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ClassItemActivity.CLASS_ARG, mClass);
        args.putSerializable(ClassItemActivity.CLASS_ITEM_ARG, classItem);
        intent.putExtras(args);
        startActivity(intent);
        */
    }

    public void requeryClassItem(ClassItemDbAdapter.ClassItem classItem) {
        mRequeryTask = new DbRequeryTask(classItem);
        mRequeryTask.execute(classItem);
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassItemDbAdapter.ClassItem> {

        final private SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, getResources().getConfiguration().locale);

        public void sortBy(int id) {
            sortId = (sortId == id) ? -id : id;
            sort(comp);
        }

        private int sortId;

        final private Comparator<ClassItemDbAdapter.ClassItem> comp = new Comparator<ClassItemDbAdapter.ClassItem>() {

            @Override
            public int compare(ClassItemDbAdapter.ClassItem arg0, ClassItemDbAdapter.ClassItem arg1) {
                if(sortId == R.id.action_sort_description) {
                    return arg0.getDescription().compareToIgnoreCase(arg1.getDescription());
                }
                else if(-sortId == R.id.action_sort_description) {
                    return -arg0.getDescription().compareToIgnoreCase(arg1.getDescription());
                }
                else if(sortId == R.id.action_sort_date) {
                    Date d0 = arg0.getItemDate();
                    Date d1 = arg1.getItemDate();
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
                    Date d0 = arg0.getItemDate();
                    Date d1 = arg1.getItemDate();
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
                    ClassItemTypeDbAdapter.ItemType i0 = arg0.getItemType();
                    ClassItemTypeDbAdapter.ItemType i1 = arg1.getItemType();
                    if(i0 == null)
                        return 1;
                    if(i1 == null)
                        return -1;
                    return i0.getDescription().compareToIgnoreCase(i1.getDescription());
                }
                else if(-sortId == R.id.action_sort_activity) {
                    ClassItemTypeDbAdapter.ItemType i0 = arg0.getItemType();
                    ClassItemTypeDbAdapter.ItemType i1 = arg1.getItemType();
                    if(i0 == null)
                        return 1;
                    if(i1 == null)
                        return -1;
                    return -i0.getDescription().compareToIgnoreCase(i1.getDescription());
                }
                else if(sortId == R.id.action_sort_check_attendance) {
                    Boolean b0 = arg0.getCheckAttendance();
                    Boolean b1 = arg1.getCheckAttendance();
                    if(b0 == null)
                        return 1;
                    if(b1 == null)
                        return -1;
                    return (b0 ? -1 : b1 ? 1 : 0);
                }
                else if(-sortId == R.id.action_sort_check_attendance) {
                    Boolean b0 = arg0.getCheckAttendance();
                    Boolean b1 = arg1.getCheckAttendance();
                    if(b0 == null)
                        return 1;
                    if(b1 == null)
                        return -1;
                    return (b0 ? 1 : b1 ? -1 : 0);
                }
                else if(sortId == R.id.action_sort_perfect_score) {
                    Float f0 = arg0.getPerfectScore();
                    Float f1 = arg1.getPerfectScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? -1 : 1);
                }
                else if(-sortId == R.id.action_sort_perfect_score) {
                    Float f0 = arg0.getPerfectScore();
                    Float f1 = arg1.getPerfectScore();
                    if(f0 == null)
                        return 1;
                    if(f1 == null)
                        return -1;
                    return (f0 < f1 ? 1 : -1);
                }
                else if(sortId == R.id.action_sort_submission_due_date) {
                    Date d0 = arg0.getSubmissionDueDate();
                    Date d1 = arg1.getSubmissionDueDate();
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
                    Date d0 = arg0.getSubmissionDueDate();
                    Date d1 = arg1.getSubmissionDueDate();
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
            TextView description;
            TextView itemDate;
            TextView itemType;
            TextView checkAttendance;
            TextView perfectScore;
            TextView dueDate;
        }

        private String perfectScoreLabel;
        private String dueDateLabel;

        public ListArrayAdapter(Context context, List<ClassItemDbAdapter.ClassItem> objects) {
            super(context, R.layout.fragment_class_items_row, objects);
            perfectScoreLabel = getString(R.string.perfect_score_label);
            dueDateLabel = getString(R.string.submission_due_date_label);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassItemDbAdapter.ClassItem classItem = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_items_row, parent, false);
                viewHolder.description = (TextView) convertView.findViewById(R.id.description_text_view);
                viewHolder.itemDate = (TextView) convertView.findViewById(R.id.item_date_text_view);
                viewHolder.itemType = (TextView) convertView.findViewById(R.id.item_type_text_view);
                viewHolder.checkAttendance = (TextView) convertView.findViewById(R.id.check_attendance_text_view);
                viewHolder.perfectScore = (TextView) convertView.findViewById(R.id.perfect_score_text_view);
                viewHolder.dueDate = (TextView) convertView.findViewById(R.id.submission_due_date_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.description.setText(classItem.getDescription());
            viewHolder.description.setTag(classItem);
            Date itemDate = classItem.getItemDate();
            viewHolder.itemDate.setText(itemDate == null ? null : sdf.format(itemDate));
            ClassItemTypeDbAdapter.ItemType itemType = classItem.getItemType();
            if(itemType != null) {
                viewHolder.itemType.setText(itemType.getDescription());
                viewHolder.itemType.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.itemType.setVisibility(View.GONE);
            if(classItem.getCheckAttendance())
                viewHolder.checkAttendance.setVisibility(View.VISIBLE);
            else
                viewHolder.checkAttendance.setVisibility(View.GONE);
            if(classItem.getRecordScores()) {
                Float perfectScore = classItem.getPerfectScore();
                viewHolder.perfectScore.setText(perfectScore == null ? null : perfectScoreLabel + " " + String.valueOf(perfectScore));
                viewHolder.perfectScore.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.perfectScore.setVisibility(View.GONE);
            if(classItem.getRecordSubmissions()) {
                Date dueDate = classItem.getSubmissionDueDate();
                viewHolder.dueDate.setText(dueDate == null ? null : dueDateLabel + " " + sdf.format(dueDate));
                viewHolder.dueDate.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.dueDate.setVisibility(View.GONE);
            return convertView;
        }
    }

    private class DbQueryTask extends AsyncTask<Long, Integer, ArrayList<ClassItemDbAdapter.ClassItem>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassItemDbAdapter.ClassItem> doInBackground(Long... classId) {
            ArrayList<ClassItemDbAdapter.ClassItem> classItems = ClassItemDbAdapter.getClassItems(classId[0]);
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
        protected void onPostExecute(ArrayList<ClassItemDbAdapter.ClassItem> list) {
            // TODO cleanup loader on finish
            mListAdapter.clear();
            mListAdapter.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class DbRequeryTask extends AsyncTask<ClassItemDbAdapter.ClassItem, Integer, ClassItemDbAdapter.ClassItem> {

        private ClassItemDbAdapter.ClassItem oldClassItem;

        public DbRequeryTask(ClassItemDbAdapter.ClassItem oldItem) {
            oldClassItem = oldItem;
        }

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ClassItemDbAdapter.ClassItem doInBackground(ClassItemDbAdapter.ClassItem... classItem) {
            ClassItemDbAdapter.ClassItem classItem_ = classItem[0];
            ClassItemDbAdapter.ClassItem requeriedClassItem = ClassItemDbAdapter.getClassItem(classItem_.getClassId(), classItem_.getItemId());
            // TODO dbquery for student count and class items summary
            return requeriedClassItem;
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
            // TODO cleanup loader on finish
            int currentPosition = mListAdapter.getPosition(oldClassItem);
            if(currentPosition != -1) {
                mListAdapter.remove(oldClassItem);
                if(classItem != null)
                    mListAdapter.insert(classItem, currentPosition);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

}
