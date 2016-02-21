package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

public class SQLiteTableRemoveDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.remove_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String SQLITE_ROW_ARG = "net.polybugger.apollot.sqlite_row_arg";

    private Activity mActivity;
    private SQLiteTableActivity.SQLiteRow mSQLiteRow;

    public interface RemoveListener {
        void onRemove(SQLiteTableActivity.SQLiteRow sqliteRow);
    }

    public static SQLiteTableRemoveDialogFragment newInstance(String dialogTitle, SQLiteTableActivity.SQLiteRow sqliteRow) {
        SQLiteTableRemoveDialogFragment fragment = new SQLiteTableRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putSerializable(SQLITE_ROW_ARG, sqliteRow);
        fragment.setArguments(args);
        return fragment;
    }

    public SQLiteTableRemoveDialogFragment() {}

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        mSQLiteRow = (SQLiteTableActivity.SQLiteRow) args.getSerializable(SQLITE_ROW_ARG);
        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_sqlite_table_remove_dialog, null);
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.text_view)).setText(mSQLiteRow.getData());
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
                    ((RemoveListener) mActivity).onRemove(mSQLiteRow);
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
