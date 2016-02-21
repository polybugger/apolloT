package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import net.polybugger.apollot.db.ClassDbAdapter;

public class StartupTaskFragment extends Fragment {

    public static final String TAG = "net.polybugger.apollot.startup_task_fragment";

    private Activity mActivity;

    public interface StartupListener {
        void onStartup(StartupOption startupOption);
    }

    public enum StartupOption {
        SHOW_CURRENT_CLASSES (0),
        SHOW_PAST_CLASSES    (1),
        SHOW_NEW_CLASS_DIALOG(2);
        private final int mValue;
        private StartupOption(int value) {
            mValue = value;
        }
        public int getValue() {
            return mValue;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new StartupTask().execute();
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

    private class StartupTask extends AsyncTask<Void, Integer, StartupOption> {

        @Override
        protected StartupOption doInBackground(Void... ignore) {
            if(ClassDbAdapter.getClassesCount(true) > 0)
                return StartupOption.SHOW_CURRENT_CLASSES;
            else if(ClassDbAdapter.getClassesCount(false) > 0)
                return StartupOption.SHOW_PAST_CLASSES;
            return StartupOption.SHOW_NEW_CLASS_DIALOG;
        }

        @Override
        protected void onPostExecute(StartupOption startupOption) {
            if(mActivity != null) {
                try {
                    ((StartupListener) mActivity).onStartup(startupOption);
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + StartupListener.class.toString());
                }
            }
        }
    }
}
