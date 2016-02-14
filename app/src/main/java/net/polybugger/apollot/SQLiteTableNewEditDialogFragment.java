package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SQLiteTableNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";

    private String mDialogTitle;
    private String mButtonText;

    public interface NewEditListener {
        void onNewEdit();
    }

    public static SQLiteTableNewEditDialogFragment newInstance(String dialogTitle, String buttonText) {
        SQLiteTableNewEditDialogFragment fragment = new SQLiteTableNewEditDialogFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        fragment.setArguments(args);
        return fragment;
    }

    public SQLiteTableNewEditDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_sqlite_table_new_edit_dialog, null);

        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);

        TextView titleTextView = (TextView) view.findViewById(R.id.title_text_view);
        titleTextView.setText(dialogTitle);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button addButton = (Button) view.findViewById(R.id.add_save_button);
        addButton.setText(buttonText);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }
}
