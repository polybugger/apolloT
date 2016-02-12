package net.polybugger.apollot;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UnlockPasswordPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private EditText mCurrentPasswordEditText;
    private EditText mNewPasswordEditText;

    @Override
    public void onDialogClosed(boolean b) {

    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setTitle(null).setPositiveButton(null, null).setNegativeButton(null, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        final UnlockPasswordDialogPreference dp = (UnlockPasswordDialogPreference) getPreference();
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dp.getTitle());
        mCurrentPasswordEditText = (EditText) view.findViewById(R.id.current_password_edit_text);
        mNewPasswordEditText = (EditText) view.findViewById(R.id.new_password_edit_text);
        view.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        view.findViewById(R.id.change_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int textResId;
                String savedPassword = dp.getPersistedString(getContext().getString(R.string.default_unlock_password));
                String currentPassword = mCurrentPasswordEditText.getText().toString();
                String newPassword = mNewPasswordEditText.getText().toString();
                if (!savedPassword.equals(currentPassword))
                    textResId = R.string.pref_status_incorrect_password;
                else {
                    dp.persistString(newPassword);
                    if (newPassword.equals(""))
                        textResId = R.string.pref_status_password_cleared;
                    else
                        textResId = R.string.pref_status_password_changed;
                    mNewPasswordEditText.setText("");
                    mCurrentPasswordEditText.setText("");
                }
                mCurrentPasswordEditText.requestFocus();
                Toast toast = Toast.makeText(getContext(), textResId, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    public static UnlockPasswordPreferenceDialogFragmentCompat newInstance(Preference preference) {
        UnlockPasswordPreferenceDialogFragmentCompat fragment = new UnlockPasswordPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}
