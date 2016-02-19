package net.polybugger.apollot;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class TimeStartDialogPreference extends DialogPreference {

    public static final String SDF_PREF_TEMPLATE = "HH:mm:ss";

    public TimeStartDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true);
        setDialogLayoutResource(R.layout.pref_time_start);
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
            defaultValue = getContext().getString(R.string.default_time_start);
        return defaultValue;
    }
}
