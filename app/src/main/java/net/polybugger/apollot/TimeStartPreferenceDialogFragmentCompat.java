package net.polybugger.apollot;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeStartPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private TimeStartDialogPreference mPreference;
    private TimePicker mTimePicker;

    @Override
    public void onDialogClosed(boolean b) {}

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setTitle(null).setPositiveButton(null, null).setNegativeButton(null, null);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mPreference = (TimeStartDialogPreference) getPreference();
        ((TextView) view.findViewById(R.id.title_text_view)).setText(mPreference.getTitle());

        mTimePicker = (TimePicker) view.findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(false);

        final SimpleDateFormat sdf = new SimpleDateFormat(mPreference.SDF_PREF_TEMPLATE, getContext().getResources().getConfiguration().locale);
        Date timeStart = new Date();
        try {
            timeStart = sdf.parse(mPreference.getPersistedString(getContext().getString(R.string.default_time_start)));
        }
        catch(Exception e) { }
        Calendar cal = Calendar.getInstance();
        cal.setTime(timeStart);
        if(Build.VERSION.SDK_INT >= 23 ) {
            mTimePicker.setHour(cal.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setMinute(cal.get(Calendar.MINUTE));
        }
        else {
            mTimePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            mTimePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
        }

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        view.findViewById(R.id.set_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                if(Build.VERSION.SDK_INT >= 23 ) {
                    cal.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                    cal.set(Calendar.MINUTE, mTimePicker.getMinute());
                }
                else {
                    cal.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                    cal.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                }
                mPreference.persistString(sdf.format(cal.getTime()));
                dismiss();
            }
        });

    }

    public static TimeStartPreferenceDialogFragmentCompat newInstance(Preference preference) {
        TimeStartPreferenceDialogFragmentCompat fragment = new TimeStartPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }
}
