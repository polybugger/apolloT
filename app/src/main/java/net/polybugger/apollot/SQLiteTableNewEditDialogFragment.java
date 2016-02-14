package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SQLiteTableNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String SQLITE_ROW_ARG = "net.polybugger.apollot.sqlite_row_arg";

    private String mDialogTitle;
    private String mButtonText;
    private SQLiteTableActivity.SQLiteRow mSQLiteRow;
    private EditText mEditText;

    public interface NewEditListener {
        void onNewEdit(SQLiteTableActivity.SQLiteRow sqLiteRow);
    }

    public static SQLiteTableNewEditDialogFragment newInstance(String dialogTitle, String buttonText, SQLiteTableActivity.SQLiteRow sqliteRow) {
        SQLiteTableNewEditDialogFragment fragment = new SQLiteTableNewEditDialogFragment();

        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(SQLITE_ROW_ARG, sqliteRow);
        fragment.setArguments(args);
        return fragment;
    }

    public SQLiteTableNewEditDialogFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mDialogTitle = args.getString(DIALOG_TITLE_ARG);
        mButtonText = args.getString(BUTTON_TEXT_ARG);
        mSQLiteRow = (SQLiteTableActivity.SQLiteRow) args.getSerializable(SQLITE_ROW_ARG);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_sqlite_table_new_edit_dialog, null);

        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(mDialogTitle);

        mEditText = (EditText) view.findViewById(R.id.edit_text);
        if(mSQLiteRow.getId() != -1) {
            mEditText.setHint("");
            mEditText.setText(mSQLiteRow.getData());
            mEditText.setSelection(mEditText.getText().length());
        }

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button addButton = (Button) view.findViewById(R.id.add_save_button);
        addButton.setText(mButtonText);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String academicTerm = mEditText.getText().toString();
                if (TextUtils.isEmpty(academicTerm)) {
                    Toast toast = Toast.makeText(activity, R.string.fragment_status_blank_entry, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    try {
                        mSQLiteRow.setData(academicTerm);
                        ((NewEditListener) activity).onNewEdit(mSQLiteRow);
                    } catch (ClassCastException e) {
                        throw new ClassCastException(activity.toString() + " must implement " + NewEditListener.class.toString());
                    }
                    dismiss();

                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }
}
