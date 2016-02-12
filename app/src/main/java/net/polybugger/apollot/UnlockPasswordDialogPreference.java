package net.polybugger.apollot;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class UnlockPasswordDialogPreference extends DialogPreference {

    public UnlockPasswordDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true);
        setDialogLayoutResource(R.layout.pref_unlock_password);
    }

    @Override
    public String getPersistedString(String defaultReturnValue) {
        return super.getPersistedString(defaultReturnValue);
    }

    @Override
    public boolean persistString(String value) {
        return super.persistString(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        String defaultValue = a.getString(index);
        if(defaultValue == null)
            defaultValue = getContext().getString(R.string.default_unlock_password);
        return defaultValue;
    }

}
