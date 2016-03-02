package net.polybugger.apollot;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

public class StudentNameDisplayFormatDialogPreference extends DialogPreference {

    public StudentNameDisplayFormatDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true);
        setDialogLayoutResource(R.layout.pref_student_name_display_format);
    }

    @Override
    public int getPersistedInt(int defaultReturnValue) {
        return super.getPersistedInt(defaultReturnValue);
    }

    @Override
    public boolean persistInt(int value) {
        return super.persistInt(value);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        int defaultValue = a.getInt(index, 0);
        if(defaultValue == 0)
            defaultValue = getContext().getResources().getInteger(R.integer.default_student_name_display_format);
        return defaultValue;
    }
}
