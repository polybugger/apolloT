package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassPasswordDbAdapter;

import java.io.Serializable;

public class ClassPasswordDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.class_password_dialog_fragment";
    public static final String DIALOG_ARG = "net.polybugger.apollot.dialog_arg";
    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private String mFragmentTag;
    private EditText mPasswordEditText;
    private ClassPasswordOption mOption;

    public interface ClassPasswordDialogListener {
        void onClassPasswordDialogSubmit(ClassDbAdapter.Class class_, ClassPasswordOption option, String fragmentTag);
    }

    public static ClassPasswordDialogFragment newInstance(DialogArgs dialogArgs, ClassDbAdapter.Class class_, String fragmentTag) {
        ClassPasswordDialogFragment f = new ClassPasswordDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(DIALOG_ARG, dialogArgs);
        args.putSerializable(CLASS_ARG, class_);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
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

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        DialogArgs dialogArgs = (DialogArgs) args.getSerializable(DIALOG_ARG);
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);
        mOption = dialogArgs.getOption();

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_password_dialog, null);
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogArgs.getTitle());
        mPasswordEditText = (EditText) view.findViewById(R.id.password_edit_text);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button unlockButton = (Button) view.findViewById(R.id.unlock_button);
        unlockButton.setText(dialogArgs.getButtonText());
        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast;
                String savedPassword = ClassPasswordDbAdapter.getPassword(mClass.getClassId());
                String unlockPassword = mPasswordEditText.getText().toString();
                int row = 0; long rowId = -1;
                switch(mOption) {
                    case APPLY_LOCK:
                        if(savedPassword != null)
                            row = ClassPasswordDbAdapter.update(mClass.getClassId(), unlockPassword);
                        else
                            rowId = ClassPasswordDbAdapter.insert(mClass.getClassId(), unlockPassword);
                        if(row > 0 || rowId > 0)
                            mClass.setLocked(true);
                        try {
                            ((ClassPasswordDialogListener) mActivity).onClassPasswordDialogSubmit(mClass, mOption, mFragmentTag);
                        }
                        catch(ClassCastException e) {
                            throw new ClassCastException(mActivity.toString() + " must implement " + ClassPasswordDialogListener.class.toString());
                        }
                        toast = Toast.makeText(mActivity, R.string.fragment_status_class_password_protection_applied, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        dismiss();
                        break;
                    case REMOVE_LOCK:
                        if(unlockPassword.equals(savedPassword)) {
                            if(savedPassword != null)
                                row = ClassPasswordDbAdapter.delete(mClass.getClassId());
                            if(row > 0)
                                mClass.setLocked(false);
                            try {
                                ((ClassPasswordDialogListener) mActivity).onClassPasswordDialogSubmit(mClass, mOption, mFragmentTag);
                            }
                            catch(ClassCastException e) {
                                throw new ClassCastException(mActivity.toString() + " must implement " + ClassPasswordDialogListener.class.toString());
                            }
                            toast = Toast.makeText(mActivity, R.string.fragment_status_class_password_protection_removed, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            dismiss();
                        }
                        else {
                            toast = Toast.makeText(mActivity, R.string.fragment_status_incorrect_password, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        break;
                    case UNLOCK_CLASS:
                        if(unlockPassword.equals(savedPassword)) {
                            try {
                                ((ClassPasswordDialogListener) mActivity).onClassPasswordDialogSubmit(mClass, mOption, mFragmentTag);
                            }
                            catch(ClassCastException e) {
                                throw new ClassCastException(mActivity.toString() + " must implement " + ClassPasswordDialogListener.class.toString());
                            }
                            dismiss();
                        }
                        else {
                            toast = Toast.makeText(mActivity, R.string.fragment_status_incorrect_password, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }

    @SuppressWarnings("serial")
    public static class DialogArgs implements Serializable {

        private String mTitle;
        private String mButtonText;
        private ClassPasswordOption mOption;

        public DialogArgs(String title, String buttonText, ClassPasswordOption option) {
            mTitle = title;
            mButtonText = buttonText;
            mOption = option;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getButtonText() {
            return mButtonText;
        }

        public ClassPasswordOption getOption() {
            return mOption;
        }
    }

    public enum ClassPasswordOption {
        UNLOCK_CLASS(0),
        REMOVE_LOCK(1),
        APPLY_LOCK(2);

        private int mValue;

        private ClassPasswordOption(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }
}
