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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassStudentsFragment extends Fragment implements StudentNewEditDialogFragment.NewEditListener {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;

    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;

    public ClassStudentsFragment() { }

    public static ClassStudentsFragment newInstance(ClassDbAdapter.Class class_) {
        ClassStudentsFragment f = new ClassStudentsFragment();
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
        super.onDestroyView();
    }

    public void requeryStudents() {
        mTask = new DbQueryTask();
        mTask.execute(mClass.getClassId());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);

        View view = inflater.inflate(R.layout.fragment_class_students, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<ClassStudentDbAdapter.ClassStudent>());
        mListView.setAdapter(mListAdapter);

        requeryStudents();

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();
        switch(id) {
            case R.id.action_new_student:
                StudentNewEditDialogFragment sdf = (StudentNewEditDialogFragment) fm.findFragmentByTag(StudentNewEditDialogFragment.TAG);
                if(sdf == null) {
                    sdf = StudentNewEditDialogFragment.newInstance(getString(R.string.new_class_details), getString(R.string.add_button), null, getTag());
                    sdf.show(fm, StudentNewEditDialogFragment.TAG);
                }
                return true;
            case R.id.action_existing_students:
                /*
                if(mDialogFragmentShown)
                    return true;
                mDialogFragmentShown = true;
                int count = mListAdapter.getCount();
                long[] studentIds = new long[count];
                for(int i = 0; i < count; ++i)
                    studentIds[i] = mListAdapter.getItem(i).getStudentId();
                ExistingStudentsDialogFragment esf = ExistingStudentsDialogFragment.newInstance(studentIds, getTag());
                esf.show(getFragmentManager(), NewEditStudentDialogFragment.TAG);
                */
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_students, menu);
    }

    @Override
    public void onNewEditStudent(StudentDbAdapter.Student student, String fragmentTag) {
        long classId = mClass.getClassId();
        long studentId = StudentDbAdapter.insert(student.getLastName(), student.getFirstName(), student.getMiddleName(), student.getGender(), student.getEmailAddress(), student.getContactNo());
        if(studentId != -1) {
            student.setStudentId(studentId);
            Date dateCreated = new Date();
            long rowId = ClassStudentDbAdapter.insert(classId, studentId, dateCreated);
            if(rowId != -1) {
                ClassStudentDbAdapter.ClassStudent classStudent = new ClassStudentDbAdapter.ClassStudent(rowId, classId, student, dateCreated);
                mListAdapter.add(classStudent);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

    private class DbQueryTask extends AsyncTask<Long, Integer, ArrayList<ClassStudentDbAdapter.ClassStudent>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassStudentDbAdapter.ClassStudent> doInBackground(Long... classId) {
            ArrayList<ClassStudentDbAdapter.ClassStudent> classStudents = ClassStudentDbAdapter.getClassStudents(classId[0]);
            // TODO dbquery for student count and class items summary
            return classStudents;
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
        protected void onPostExecute(ArrayList<ClassStudentDbAdapter.ClassStudent> list) {
            // TODO cleanup loader on finish
            mListAdapter.clear();
            mListAdapter.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassStudentDbAdapter.ClassStudent> {

        private class ViewHolder {
            TextView name;
            TextView summary;
        }

        public ListArrayAdapter(Context context, List<ClassStudentDbAdapter.ClassStudent> objects) {
            super(context, R.layout.fragment_class_students_row, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassStudentDbAdapter.ClassStudent classStudent = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_students_row, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name_text_view);
                viewHolder.summary = (TextView) convertView.findViewById(R.id.summary_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(classStudent.getName());
            viewHolder.name.setTag(classStudent);
            String summary = classStudent.getSummary();
            if(!TextUtils.isEmpty(summary)) {
                viewHolder.summary.setText(summary);
                viewHolder.summary.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.summary.setVisibility(View.GONE);
            return convertView;
        }
    }

}
