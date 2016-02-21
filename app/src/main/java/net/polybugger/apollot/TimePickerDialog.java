package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerDialog extends AlertDialog.Builder {

    private Context mContext;
    private Button mSourceButton;
    private Date mDefaultTime;
    private TimePicker mTimePicker;
    private AlertDialog mAlertDialog;
    private TimePickerDialogCallback mCallback;

    public interface TimePickerDialogCallback {
        void onSet(Date time, Button sourceButton);
    }

    public TimePickerDialog(Context context) {
        super(context);
    }

    public TimePickerDialog(Context context, Button sourceButton, Date defaultTime, TimePickerDialogCallback callback) {
        super(context);
        mContext = context;
        mSourceButton = sourceButton;
        mDefaultTime = defaultTime;
        mCallback = callback;
    }

    public void setSourceButton(Button sourceButton) {
        mSourceButton = sourceButton;
    }

    public void setDefaultTime(Date defaultTime) {
        mDefaultTime = defaultTime;
    }

    public void setCallback(TimePickerDialogCallback callback) {
        mCallback = callback;
    }

    @SuppressLint("InflateParams")
    @Override
    public AlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.time_picker_dialog, null);
        mTimePicker = (TimePicker) view.findViewById(R.id.time_picker);
        mTimePicker.setIs24HourView(false);
        if(mDefaultTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mDefaultTime);
            if(Build.VERSION.SDK_INT >= 23 ) {
                mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
            }
            else {
                mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
            }
        }
        if(mSourceButton != null) {
            ((TextView) view.findViewById(R.id.title_text_view)).setText(mSourceButton.getHint());
            Date date = (Date) mSourceButton.getTag();
            if(date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if(Build.VERSION.SDK_INT >= 23 ) {
                    mTimePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                    mTimePicker.setMinute(calendar.get(Calendar.MINUTE));
                }
                else {
                    mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
                    mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
                }
            }
        }
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        view.findViewById(R.id.clear_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSourceButton != null) {
                    mSourceButton.setTag(null);
                    mSourceButton.setText("");
                    mAlertDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.set_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSourceButton != null) {
                    Calendar calendar = Calendar.getInstance();
                    if(Build.VERSION.SDK_INT >= 23 ) {
                        calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getHour());
                        calendar.set(Calendar.MINUTE, mTimePicker.getMinute());
                    }
                    else {
                        calendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
                        calendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                    }
                    Date date = calendar.getTime();
                    if(mCallback != null)
                        mCallback.onSet(date, mSourceButton);
                    mAlertDialog.dismiss();
                }
            }
        });
        setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        mAlertDialog = super.show();
        return mAlertDialog;
    }
}
