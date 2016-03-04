package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import net.polybugger.apollot.db.StudentDbAdapter;

public class StudentExistingDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.existing_students_dialog_fragment";
    public static final String STUDENT_IDS_ARG = "net.polybugger.apollot.student_ids_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";
    public static final String STATE_SELECTED_STUDENT_IDS = "net.polybugger.apollot.selected_student_ids";

    public interface ExistingListener {
        void onExisting(ArrayList<StudentDbAdapter.Student> students, String fragmentTag);
    }

    private Activity mActivity;
    private long[] mStudentIds;
    private String mFragmentTag;
    private ArrayList<Long> mSelectedStudentIds;
    private ArrayList<StudentDbAdapter.Student> mSelectedStudents;
    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;

    public StudentExistingDialogFragment() {}

    public static StudentExistingDialogFragment newInstance(long[] studentIds, String fragmentTag) {
        StudentExistingDialogFragment f = new StudentExistingDialogFragment();
        Bundle args = new Bundle();
        args.putLongArray(STUDENT_IDS_ARG, studentIds);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mStudentIds = args.getLongArray(STUDENT_IDS_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);
        mSelectedStudentIds = new ArrayList<Long>();
        mSelectedStudents = new ArrayList<StudentDbAdapter.Student>();

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_student_existing_dialog, null);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setEmptyView(view.findViewById(R.id.empty_row));
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<StudentDbAdapter.Student>());
        mListView.setAdapter(mListAdapter);
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox addCheckBox = (CheckBox) view.findViewById(R.id.add_checkbox);
                boolean checked = !addCheckBox.isChecked();
                StudentDbAdapter.Student student = (StudentDbAdapter.Student) addCheckBox.getTag();
                if (checked) {
                    mSelectedStudentIds.add(student.getStudentId());
                    mSelectedStudents.add(student);
                } else {
                    mSelectedStudentIds.remove(student.getStudentId());
                    mSelectedStudents.remove(student);
                }
                addCheckBox.setChecked(checked);
            }
        });
        view.findViewById(R.id.add_save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedStudents.size() == 0) {
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_no_student_selected, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                try {
                    ((ExistingListener) mActivity).onExisting(mSelectedStudents, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + ExistingListener.class.toString());
                }
                dismiss();
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(STATE_SELECTED_STUDENT_IDS)) {
            long[] selectedStudentIds = savedInstanceState.getLongArray(STATE_SELECTED_STUDENT_IDS);
            for(long id : selectedStudentIds)
                mSelectedStudentIds.add(id);
        }

        mTask = new DbQueryTask();
        mTask.execute(mStudentIds);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
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
        mTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int size = mSelectedStudentIds.size();
        if(size > 0) {
            long[] selectedStudentIds = new long[size];
            for(int i = 0; i < size; ++i)
                selectedStudentIds[i] = mSelectedStudentIds.get(i);
            outState.putLongArray(STATE_SELECTED_STUDENT_IDS, selectedStudentIds);
        }
        super.onSaveInstanceState(outState);
    }

    private class DbQueryTask extends AsyncTask<long[], Integer, ArrayList<StudentDbAdapter.Student>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<StudentDbAdapter.Student> doInBackground(long[]... studentIds) {
            ArrayList<StudentDbAdapter.Student> students = StudentDbAdapter.getStudents(studentIds[0]);
            // TODO dbquery for student count and class items summary
            return students;
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
        protected void onPostExecute(ArrayList<StudentDbAdapter.Student> list) {
            // TODO cleanup loader on finish
            mListAdapter.clear();
            mListAdapter.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class ListArrayAdapter extends ArrayAdapter<StudentDbAdapter.Student> {

        private class ViewHolder {
            TextView name;
            CheckBox addCheckBox;
        }

        public ListArrayAdapter(Context context, List<StudentDbAdapter.Student> objects) {
            super(context, R.layout.fragment_student_existing_dialog_row, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            StudentDbAdapter.Student student = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_student_existing_dialog_row, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name_text_view);
                viewHolder.addCheckBox = (CheckBox) convertView.findViewById(R.id.add_checkbox);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(student.getName());
            boolean selected = mSelectedStudentIds.contains(student.getStudentId());
            viewHolder.addCheckBox.setTag(student);
            viewHolder.addCheckBox.setChecked(selected);
            if(selected) {
                if(!mSelectedStudents.contains(student))
                    mSelectedStudents.add(student);
            }
            return convertView;
        }
    }

}
