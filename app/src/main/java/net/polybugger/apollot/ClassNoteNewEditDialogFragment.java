package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.ClassNoteDbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassNoteNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_note_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String NOTE_ARG = "net.polybugger.apollot.note_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    public static final String STATE_NOTE_DATE = "net.polybugger.apollot.note_date";

    private Activity mActivity;
    private ClassNoteDbAdapter.ClassNote mNote;
    private String mFragmentTag;

    private Button mNoteDateButton;
    private EditText mNoteEditText;

    private AlertDialog mCurrentDialog;
    private boolean mDialogShown = false;

    public interface NewEditListener {
        void onNewEditNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag);
    }

    public ClassNoteNewEditDialogFragment() {}

    public static ClassNoteNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        ClassNoteNewEditDialogFragment f = new ClassNoteNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(NOTE_ARG, note);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);
        mNote = (ClassNoteDbAdapter.ClassNote) args.getSerializable(NOTE_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_note_new_edit_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);

        final SimpleDateFormat sdf = new SimpleDateFormat(ClassNoteDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        mNoteDateButton = (Button) view.findViewById(R.id.note_date_button);
        Date dateNow = new Date();
        mNoteDateButton.setText(sdf.format(dateNow));
        mNoteDateButton.setTag(dateNow);
        mNoteDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogShown)
                    return;
                mDialogShown = true;
                DatePickerDialog datePickerDialog = new DatePickerDialog(mActivity, (Button) view, new DatePickerDialog.DatePickerDialogCallback() {
                    @Override
                    public void onSet(Date date, Button sourceButton) {
                        sourceButton.setText(sdf.format(date));
                        sourceButton.setTag(date);
                    }
                });
                datePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogShown = false;
                    }
                });
                mCurrentDialog = datePickerDialog.show();
            }
        });
        mNoteEditText = (EditText) view.findViewById(R.id.note_edit_text);
        mNoteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String charsLeft = String.valueOf(getResources().getInteger(R.integer.note_char_limit) - s.toString().length()) + " " + getString(R.string.characters_left);
                final Toast toast = Toast.makeText(mActivity, charsLeft, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 500);
                return;
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button addSaveButton = (Button) view.findViewById(R.id.add_save_button);
        addSaveButton.setText(buttonText);
        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date noteDate = (Date) mNoteDateButton.getTag();
                String note = mNoteEditText.getText().toString();
                boolean ie = TextUtils.isEmpty(note);
                if(noteDate == null || ie) {
                    int textResId = 0;
                    if(noteDate == null) {
                        textResId = R.string.fragment_status_note_date_required;
                    }
                    else {
                        textResId = R.string.fragment_status_note_required;
                        mNoteEditText.requestFocus();
                    }
                    Toast toast = Toast.makeText(mActivity, textResId, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(mNote == null) {
                    mNote = new ClassNoteDbAdapter.ClassNote(-1, -1, note, noteDate);
                }
                else {
                    mNote.setNote(note);
                    mNote.setDateCreated(noteDate);
                }
                try {
                    ((NewEditListener) mActivity).onNewEditNote(mNote, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

        if(mNote != null) {
            Date noteDate = mNote.getDateCreated();
            if(noteDate != null) {
                mNoteDateButton.setText(sdf.format(noteDate));
                mNoteDateButton.setTag(noteDate);
            }
            mNoteEditText.setText(mNote.getNote());
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_NOTE_DATE)) {
                Date noteDate = (Date) savedInstanceState.getSerializable(STATE_NOTE_DATE);
                mNoteDateButton.setText(sdf.format(noteDate));
                mNoteDateButton.setTag(noteDate);
            }
        }

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
        if(mCurrentDialog != null && mCurrentDialog.isShowing())
            mCurrentDialog.dismiss();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Date noteDate = (Date) mNoteDateButton.getTag();
        if(noteDate != null)
            outState.putSerializable(STATE_NOTE_DATE, noteDate);
        super.onSaveInstanceState(outState);
    }
}
