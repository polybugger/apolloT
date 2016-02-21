package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import net.polybugger.apollot.db.DaysBits;

public class DaysPickerDialog extends AlertDialog.Builder {

    private Context mContext;
    private Button mSourceButton;
    private CheckBox mMonCheckBox;
    private CheckBox mTueCheckBox;
    private CheckBox mWedCheckBox;
    private CheckBox mThuCheckBox;
    private CheckBox mFriCheckBox;
    private CheckBox mSatCheckBox;
    private CheckBox mSunCheckBox;
    private AlertDialog mAlertDialog;

    public DaysPickerDialog(Context context) {
        super(context);
        mContext = context;
    }

    public DaysPickerDialog(Context context, Button sourceButton) {
        super(context);
        mContext = context;
        mSourceButton = sourceButton;
    }

    public void setSourceButton(Button sourceButton) {
        mSourceButton = sourceButton;
    }

    @SuppressLint("InflateParams")
    @Override
    public AlertDialog show() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.days_picker_dialog, null);
        mMonCheckBox = (CheckBox) view.findViewById(R.id.m_checkbox);
        mTueCheckBox = (CheckBox) view.findViewById(R.id.t_checkbox);
        mWedCheckBox = (CheckBox) view.findViewById(R.id.w_checkbox);
        mThuCheckBox = (CheckBox) view.findViewById(R.id.th_checkbox);
        mFriCheckBox = (CheckBox) view.findViewById(R.id.f_checkbox);
        mSatCheckBox = (CheckBox) view.findViewById(R.id.s_checkbox);
        mSunCheckBox = (CheckBox) view.findViewById(R.id.su_checkbox);
        if(mSourceButton != null) {
            int days = 0;
            try {
                days = (Integer) mSourceButton.getTag();
            }
            catch(Exception e) { }
            mMonCheckBox.setChecked((days & DaysBits.M.getValue()) == DaysBits.M.getValue());
            mTueCheckBox.setChecked((days & DaysBits.T.getValue()) == DaysBits.T.getValue());
            mWedCheckBox.setChecked((days & DaysBits.W.getValue()) == DaysBits.W.getValue());
            mThuCheckBox.setChecked((days & DaysBits.Th.getValue()) == DaysBits.Th.getValue());
            mFriCheckBox.setChecked((days & DaysBits.F.getValue()) == DaysBits.F.getValue());
            mSatCheckBox.setChecked((days & DaysBits.S.getValue()) == DaysBits.S.getValue());
            mSunCheckBox.setChecked((days & DaysBits.Su.getValue()) == DaysBits.Su.getValue());
        }
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlertDialog.dismiss();
            }
        });
        view.findViewById(R.id.set_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSourceButton != null) {
                    int days = (mMonCheckBox.isChecked() ? DaysBits.M.getValue() : 0) +
                            (mTueCheckBox.isChecked() ? DaysBits.T.getValue() : 0) +
                            (mWedCheckBox.isChecked() ? DaysBits.W.getValue() : 0) +
                            (mThuCheckBox.isChecked() ? DaysBits.Th.getValue() : 0) +
                            (mFriCheckBox.isChecked() ? DaysBits.F.getValue() : 0) +
                            (mSatCheckBox.isChecked() ? DaysBits.S.getValue() : 0) +
                            (mSunCheckBox.isChecked() ? DaysBits.Su.getValue() : 0);
                    mSourceButton.setText(DaysBits.intToString(mContext, days));
                    mSourceButton.setTag(days);
                    mAlertDialog.dismiss();
                }
            }
        });
        setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        mAlertDialog = super.show();
        return mAlertDialog;
    }
}
