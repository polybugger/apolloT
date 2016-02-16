package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import net.polybugger.apollot.db.ClassDbAdapter;

public class ClassesFragment extends Fragment {

    public static final String CURRENT_PAST_ARG = "net.polybugger.apollot.current_past_arg";

    private Activity mActivity;
    private boolean mCurrent;
    private ListView mListView;
    private ListArrayAdapter mListAdapter;
    private DbQueryTask mTask;
    private DbRequeryTask mRequeryTask;

    public ClassesFragment() { }

    public static ClassesFragment newInstance(boolean current) {
        ClassesFragment f = new ClassesFragment();
        Bundle args = new Bundle();
        args.putBoolean(CURRENT_PAST_ARG, current);
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
        mCurrent = args.getBoolean(CURRENT_PAST_ARG);

        View view = inflater.inflate(R.layout.fragment_classes, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListAdapter = new ListArrayAdapter(mActivity, new ArrayList<ClassDbAdapter.Class>());
        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassDbAdapter.Class class_ = (ClassDbAdapter.Class) view.findViewById(R.id.class_title_text_view).getTag();
                if (class_.isLocked()) {
                    /*
                    if(mDialogFragmentShown)
                        return;
                    mDialogFragmentShown = true;
                    ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.unlock_class), getString(R.string.unlock_button), ClassPasswordDialogFragment.ClassPasswordOption.UNLOCK_CLASS);
                    ClassPasswordDialogFragment f = ClassPasswordDialogFragment.newInstance(dialogArgs, class_, getTag());
                    f.show(getFragmentManager(), ClassPasswordDialogFragment.TAG);
                    */
                } else {
                    //startClassActivity(class_);
                }
            }
        });

        mTask = new DbQueryTask();
        mTask.execute(mCurrent);

        return view;
    }

    @Override
    public void onDestroyView() {
        mTask.cancel(true);
        if(mRequeryTask != null)
            mRequeryTask.cancel(true);
        super.onDestroyView();
    }

    public void addClass(ClassDbAdapter.Class class_) {
        mListAdapter.add(class_);
        mListAdapter.notifyDataSetChanged();
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassDbAdapter.Class> {

        private class ViewHolder {
            ImageView locked;
            TextView title;
            TextView academicTerm;
            TextView schedule;
        }

        public ListArrayAdapter(Context context, List<ClassDbAdapter.Class> objects) {
            super(context, R.layout.fragment_classes_row, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ClassDbAdapter.Class class_ = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.fragment_classes_row, parent, false);
                viewHolder.locked = (ImageView) convertView.findViewById(R.id.locked_image_view);
                viewHolder.title = (TextView) convertView.findViewById(R.id.class_title_text_view);
                viewHolder.academicTerm = (TextView) convertView.findViewById(R.id.academic_term_text_view);
                viewHolder.schedule = (TextView) convertView.findViewById(R.id.schedule_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if(class_.isLocked())
                viewHolder.locked.setVisibility(View.VISIBLE);
            else
                viewHolder.locked.setVisibility(View.GONE);
            viewHolder.title.setText(class_.getTitle());
            viewHolder.title.setTag(class_);
            viewHolder.academicTerm.setText(class_.getAcademicTermYear());
            String schedule = class_.getSchedule();
            if(schedule != null) {
                viewHolder.schedule.setText(schedule);
                viewHolder.schedule.setVisibility(View.VISIBLE);
            }
            else
                viewHolder.schedule.setVisibility(View.GONE);
            return convertView;
        }
    }

    private class DbQueryTask extends AsyncTask<Boolean, Integer, ArrayList<ClassDbAdapter.Class>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassDbAdapter.Class> doInBackground(Boolean... current) {
            ArrayList<ClassDbAdapter.Class> classes = ClassDbAdapter.getClasses(current[0]);
            // TODO dbquery for student count and class items summary
            return classes;
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
        protected void onPostExecute(ArrayList<ClassDbAdapter.Class> list) {
            // TODO cleanup loader on finish
            mListAdapter.clear();
            mListAdapter.addAll(list);
            mListAdapter.notifyDataSetChanged();
        }
    }

    private class DbRequeryTask extends AsyncTask<Long, Integer, ClassDbAdapter.Class> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ClassDbAdapter.Class doInBackground(Long... classId) {
            ClassDbAdapter.Class class_ = ClassDbAdapter.getClass(classId[0]);
            // TODO dbquery for student count and class items summary
            return class_;
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
        protected void onPostExecute(ClassDbAdapter.Class class_) {
            // TODO cleanup loader on finish
            int currentPosition = mListAdapter.getPosition(class_);
            if(currentPosition != -1) {
                mListAdapter.remove(class_);
                mListAdapter.insert(class_, currentPosition);
                mListAdapter.notifyDataSetChanged();
            }
        }
    }

}