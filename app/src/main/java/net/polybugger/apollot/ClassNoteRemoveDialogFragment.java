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

import net.polybugger.apollot.db.ClassNoteDbAdapter;

public class ClassNoteRemoveDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.class_note_remove_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String NOTE_ARG = "net.polybugger.apollot.note_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private String mDialogTitle;
    private ClassNoteDbAdapter.ClassNote mNote;
    private String mFragmentTag;

    public interface RemoveListener {
        void onRemoveNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag);
    }

    public ClassNoteRemoveDialogFragment() {}

    public static ClassNoteRemoveDialogFragment newInstance(String dialogTitle, ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        ClassNoteRemoveDialogFragment f = new ClassNoteRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(DIALOG_TITLE_ARG, dialogTitle);
        args.putSerializable(NOTE_ARG, note);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mDialogTitle = args.getString(DIALOG_TITLE_ARG);
        mNote = (ClassNoteDbAdapter.ClassNote) args.getSerializable(NOTE_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);
    }


    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_note_remove_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(mDialogTitle);
        ((TextView) view.findViewById(R.id.note_text_view)).setText(mNote.getDateNoteText());

        view.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button yesButton = (Button) view.findViewById(R.id.yes_button);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((RemoveListener) mActivity).onRemoveNote(mNote, mFragmentTag);
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
