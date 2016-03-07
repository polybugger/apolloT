package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
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

public class ClassesFragment extends Fragment implements ClassPasswordDialogFragment.ClassPasswordDialogListener {

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
                if(class_.isLocked()) {
                    FragmentManager fm = getFragmentManager();
                    ClassPasswordDialogFragment df = (ClassPasswordDialogFragment) fm.findFragmentByTag(ClassPasswordDialogFragment.TAG);
                    if(df == null) {
                        ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.unlock_class), getString(R.string.unlock_button), ClassPasswordDialogFragment.ClassPasswordOption.UNLOCK_CLASS);
                        df = ClassPasswordDialogFragment.newInstance(dialogArgs, class_, getTag());
                        df.show(fm, ClassPasswordDialogFragment.TAG);
                    }
                }
                else {
                    startClassActivity(class_);
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

    private void startClassActivity(ClassDbAdapter.Class class_) {
        Intent intent = new Intent(mActivity, ClassActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ClassActivity.CLASS_ARG, class_);
        intent.putExtras(args);
        startActivity(intent);
    }

    public void addClass(ClassDbAdapter.Class class_) {
        mListAdapter.add(class_);
        mListAdapter.notifyDataSetChanged();
    }

    public void removeClass(ClassDbAdapter.Class class_) {
        mListAdapter.remove(class_);
        mListAdapter.notifyDataSetChanged();
    }

    public void requeryClass(ClassDbAdapter.Class class_) {
        if(mListAdapter.getPosition(class_) != -1) {
            mRequeryTask = new DbRequeryTask();
            mRequeryTask.execute(class_.getClassId());
        }
    }

    public void requeryClasses() {
        mTask = new DbQueryTask();
        mTask.execute(mCurrent);
    }

    public int getClassPosition(ClassDbAdapter.Class class_) {
        return mListAdapter.getPosition(class_);
    }

    @Override
    public void onClassPasswordDialogSubmit(ClassDbAdapter.Class class_, ClassPasswordDialogFragment.ClassPasswordOption option, String fragmentTag) {
        startClassActivity(class_);
    }

    private class ListArrayAdapter extends ArrayAdapter<ClassDbAdapter.Class> {

        private class ViewHolder {
            LinearLayoutCompat linearLayout;
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
                viewHolder.linearLayout = (LinearLayoutCompat) convertView.findViewById(R.id.linear_layout);
                viewHolder.locked = (ImageView) convertView.findViewById(R.id.locked_image_view);
                viewHolder.title = (TextView) convertView.findViewById(R.id.class_title_text_view);
                viewHolder.academicTerm = (TextView) convertView.findViewById(R.id.academic_term_text_view);
                viewHolder.schedule = (TextView) convertView.findViewById(R.id.schedule_text_view);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.linearLayout.setBackgroundColor(class_.getAcademicTerm().getColorInt());
            viewHolder.title.setTag(class_);
            viewHolder.academicTerm.setText(class_.getAcademicTermYear());

            if (class_.isLocked()) {
                viewHolder.title.setText(class_.getCode() + " ...");
                viewHolder.schedule.setVisibility(View.GONE);
                viewHolder.locked.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.title.setText(class_.getTitle());
                String schedule = class_.getSchedule();
                if(schedule != null) {
                    viewHolder.schedule.setText(schedule);
                    viewHolder.schedule.setVisibility(View.VISIBLE);
                }
                else
                    viewHolder.schedule.setVisibility(View.GONE);
                viewHolder.locked.setVisibility(View.GONE);
            }
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
