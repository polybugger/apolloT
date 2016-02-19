package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassScheduleDbAdapter;

public class ClassScheduleNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_schedule_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String SCHEDULE_ARG = "net.polybugger.apollot.schedule_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private String mDialogTitle;
    private String mButtonText;
    private ClassScheduleDbAdapter.ClassSchedule mSchedule;
    private String mFragmentTag;

    public interface NewEditListener {
        void onNewEditSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag);
    }

    public ClassScheduleNewEditDialogFragment() {}

    public static ClassScheduleNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        ClassScheduleNewEditDialogFragment f = new ClassScheduleNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(SCHEDULE_ARG, schedule);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mDialogTitle = args.getString(DIALOG_TITLE_ARG);
        mButtonText = args.getString(BUTTON_TEXT_ARG);
        mSchedule = (ClassScheduleDbAdapter.ClassSchedule) args.getSerializable(SCHEDULE_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_schedule_new_edit_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(mDialogTitle);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button addSaveButton = (Button) view.findViewById(R.id.add_save_button);
        addSaveButton.setText(mButtonText);
        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((NewEditListener) mActivity).onNewEditSchedule(mSchedule, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

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
}
