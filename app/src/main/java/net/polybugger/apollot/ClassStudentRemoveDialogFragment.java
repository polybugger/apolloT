package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassStudentDbAdapter;

public class ClassStudentRemoveDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.class_student_remove_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassStudentDbAdapter.ClassStudent mClassStudent;
    private String mFragmentTag;

    public interface RemoveListener {
        void onRemoveStudent(ClassStudentDbAdapter.ClassStudent classStudent, String fragmentTag);
    }

    public ClassStudentRemoveDialogFragment() {}

    public static ClassStudentRemoveDialogFragment newInstance(String dialogTitle, ClassStudentDbAdapter.ClassStudent classStudent, String fragmentTag) {
        ClassStudentRemoveDialogFragment f = new ClassStudentRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putSerializable(STUDENT_ARG, classStudent);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        mClassStudent = (ClassStudentDbAdapter.ClassStudent) args.getSerializable(STUDENT_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_student_remove_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.class_student_text_view)).setText(mClassStudent.getName());

        view.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((RemoveListener) mActivity).onRemoveStudent(mClassStudent, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + RemoveListener.class.toString());
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
