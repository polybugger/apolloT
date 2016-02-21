package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class DatePickerDialog extends AlertDialog.Builder {

    private Context mContext;
    private Button mSourceButton;
    private DatePicker mDatePicker;
    private AlertDialog mAlertDialog;
    private DatePickerDialogCallback mCallback;

    public interface DatePickerDialogCallback {
        void onSet(Date date, Button sourceButton);
    }

    public DatePickerDialog(Context context) {
        super(context);
        mContext = context;
    }

    public DatePickerDialog(Context context, Button sourceButton, DatePickerDialogCallback callback) {
        super(context);
        mContext = context;
        mSourceButton = sourceButton;
        mCallback = callback;
    }

    public void setSourceButton(Button sourceButton) {
        mSourceButton = sourceButton;
    }

    public void setCallback(DatePickerDialogCallback callback) {
        mCallback = callback;
    }

    @SuppressLint("InflateParams")
    @Override
    public AlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.date_picker_dialog, null);
        mDatePicker = (DatePicker) view.findViewById(R.id.date_picker);
        if(mSourceButton != null) {
            ((TextView) view.findViewById(R.id.title_text_view)).setText(mSourceButton.getHint());
            Date date = (Date) mSourceButton.getTag();
            if(date != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                mDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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
                if (mSourceButton != null) {
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
                    calendar.set(Calendar.YEAR, mDatePicker.getYear());
                    calendar.set(Calendar.MONTH, mDatePicker.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
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
