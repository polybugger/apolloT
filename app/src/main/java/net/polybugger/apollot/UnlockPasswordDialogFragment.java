package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class UnlockPasswordDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.unlock_password_dialog_fragment";

    public interface UnlockPasswordDialogListener {
        void onUnlockPassword();
    }

    public static UnlockPasswordDialogFragment newInstance() {
        UnlockPasswordDialogFragment fragment = new UnlockPasswordDialogFragment();
        fragment.setCancelable(false);
        return fragment;
    }

    public UnlockPasswordDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View view = activity.getLayoutInflater().inflate(R.layout.fragment_unlock_password_dialog, null);

        final EditText unlockPasswordEditText = (EditText) view.findViewById(R.id.edit_text);
        view.findViewById(R.id.unlock_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String savedPassword = PreferenceManager.getDefaultSharedPreferences(activity).getString(getString(R.string.pref_unlock_password_key), getString(R.string.default_unlock_password));
                String unlockPassword = unlockPasswordEditText.getText().toString();
                if (savedPassword.equals(unlockPassword)) {
                    try {
                        ((UnlockPasswordDialogListener) activity).onUnlockPassword();
                    } catch (ClassCastException e) {
                        throw new ClassCastException(activity.toString() + " must implement " + UnlockPasswordDialogListener.class.toString());
                    }
                    dismiss();
                } else {
                    Toast toast = Toast.makeText(activity, R.string.fragment_status_incorrect_password, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }

}
