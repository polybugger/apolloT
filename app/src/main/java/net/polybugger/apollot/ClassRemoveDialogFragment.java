package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassRemoveDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.class_remove_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private String mFragmentTag;

    public interface RemoveListener {
        void onRemoveClass(ClassDbAdapter.Class class_, String fragmentTag);
    }

    public ClassRemoveDialogFragment() {}

    public static ClassRemoveDialogFragment newInstance(String dialogTitle, ClassDbAdapter.Class class_, String fragmentTag) {
        ClassRemoveDialogFragment f = new ClassRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putSerializable(CLASS_ARG, class_);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_remove_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.code_description_text_view)).setText(mClass.getTitle());
        TextView academicTermTextView = (TextView) view.findViewById(R.id.academic_term_text_view);
        String academicTerm = mClass.getAcademicTermYear();

        if(!TextUtils.isEmpty(academicTerm)) {
            academicTermTextView.setText(academicTerm);
            academicTermTextView.setVisibility(View.VISIBLE);
        }
        else
            academicTermTextView.setVisibility(View.GONE);
        ((TextView) view.findViewById(R.id.current_text_view)).setText(mClass.getCurrent());

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
                    ((RemoveListener) mActivity).onRemoveClass(mClass, mFragmentTag);
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
