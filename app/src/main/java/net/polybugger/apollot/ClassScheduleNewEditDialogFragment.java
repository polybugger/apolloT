package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.ClassScheduleDbAdapter;
import net.polybugger.apollot.db.DaysBits;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassScheduleNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_schedule_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String SCHEDULE_ARG = "net.polybugger.apollot.schedule_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    public static final String STATE_TIME_START = "net.polybugger.apollot.time_start";
    public static final String STATE_TIME_END = "net.polybugger.apollot.time_end";
    public static final String STATE_DAYS = "net.polybugger.apollot.days";

    private Activity mActivity;
    private ClassScheduleDbAdapter.ClassSchedule mSchedule;
    private String mFragmentTag;

    private Button mTimeStartButton;
    private Button mTimeEndButton;
    private Button mDaysButton;
    private EditText mRoomEditText;
    private EditText mBuildingEditText;
    private EditText mCampusEditText;

    private AlertDialog mCurrentDialog;
    private boolean mDialogShown = false;

    public interface NewEditListener {
        void onNewEditSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag);
    }

    public ClassScheduleNewEditDialogFragment() {}

    public static ClassScheduleNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        ClassScheduleNewEditDialogFragment f = new ClassScheduleNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(SCHEDULE_ARG, schedule);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);
        mSchedule = (ClassScheduleDbAdapter.ClassSchedule) args.getSerializable(SCHEDULE_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_schedule_new_edit_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);

        final SimpleDateFormat sdf = new SimpleDateFormat(ClassScheduleDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        mTimeStartButton = (Button) view.findViewById(R.id.time_start_button);
        mTimeStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogShown)
                    return;
                mDialogShown = true;
                Date defaultTimeStart = null;
                SimpleDateFormat sdfDefault = new SimpleDateFormat(TimeStartDialogPreference.SDF_PREF_TEMPLATE, mActivity.getResources().getConfiguration().locale);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mActivity);
                try {
                    defaultTimeStart = sdfDefault.parse(sharedPref.getString(getString(R.string.pref_time_start_key), getString(R.string.default_time_start)));
                }
                catch(Exception e) { }
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, (Button) view, defaultTimeStart, new TimePickerDialog.TimePickerDialogCallback() {
                    @Override
                    public void onSet(Date time, Button sourceButton) {
                        sourceButton.setText(sdf.format(time));
                        sourceButton.setTag(time);
                    }
                });
                timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogShown = false;
                    }
                });
                mCurrentDialog = timePickerDialog.show();
            }
        });
        mTimeEndButton = (Button) view.findViewById(R.id.time_end_button);
        mTimeEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogShown)
                    return;
                mDialogShown = true;
                TimePickerDialog timePickerDialog = new TimePickerDialog(mActivity, (Button) view, (Date) mTimeStartButton.getTag(), new TimePickerDialog.TimePickerDialogCallback() {
                    @Override
                    public void onSet(Date time, Button sourceButton) {
                        sourceButton.setText(sdf.format(time));
                        sourceButton.setTag(time);
                    }
                });
                timePickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogShown = false;
                    }
                });
                mCurrentDialog = timePickerDialog.show();
            }
        });
        mDaysButton = (Button) view.findViewById(R.id.days_button);
        mDaysButton.setTag(0);
        mDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialogShown)
                    return;
                mDialogShown = true;
                DaysPickerDialog daysPickerDialog = new DaysPickerDialog(mActivity, (Button) view);
                daysPickerDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogShown = false;
                    }
                });
                mCurrentDialog = daysPickerDialog.show();
            }
        });
        mRoomEditText = (EditText) view.findViewById(R.id.room_edit_text);
        mBuildingEditText = (EditText) view.findViewById(R.id.building_edit_text);
        mCampusEditText = (EditText) view.findViewById(R.id.campus_edit_text);

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button addSaveButton = (Button) view.findViewById(R.id.add_save_button);
        addSaveButton.setText(buttonText);
        addSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date timeStart = (Date) mTimeStartButton.getTag();
                Date timeEnd = (Date) mTimeEndButton.getTag();
                int days = (Integer) mDaysButton.getTag();
                String room = mRoomEditText.getText().toString();
                String building = mBuildingEditText.getText().toString();
                String campus = mCampusEditText.getText().toString();
                if(timeStart == null || TextUtils.isEmpty(mTimeStartButton.getText())) {
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_time_start_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                if(mSchedule == null) {
                    mSchedule = new ClassScheduleDbAdapter.ClassSchedule(-1, -1, timeStart, timeEnd, days, room, building, campus);
                }
                else {
                    mSchedule.setTimeStart(timeStart);
                    mSchedule.setTimeEnd(timeEnd);
                    mSchedule.setDays(days);
                    mSchedule.setRoom(room);
                    mSchedule.setBuilding(building);
                    mSchedule.setCampus(campus);
                }
                try {
                    ((NewEditListener) mActivity).onNewEditSchedule(mSchedule, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

        if(mSchedule != null) {
            Date timeStart = mSchedule.getTimeStart();
            if(timeStart != null) {
                mTimeStartButton.setText(sdf.format(timeStart));
                mTimeStartButton.setTag(timeStart);
            }
            Date timeEnd = mSchedule.getTimeEnd();
            if(timeEnd != null) {
                mTimeEndButton.setText(sdf.format(timeEnd));
                mTimeEndButton.setTag(timeEnd);
            }
            int days = mSchedule.getDays();
            mDaysButton.setText(DaysBits.intToString(mActivity, days));
            mDaysButton.setTag(days);
            mRoomEditText.setText(mSchedule.getRoom());
            mBuildingEditText.setText(mSchedule.getBuilding());
            mCampusEditText.setText(mSchedule.getCampus());
        }

        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_DAYS)) {
                int days = savedInstanceState.getInt(STATE_DAYS);
                mDaysButton.setText(DaysBits.intToString(mActivity, days));
                mDaysButton.setTag(days);
            }
            if(savedInstanceState.containsKey(STATE_TIME_START)) {
                Date timeStart = (Date) savedInstanceState.getSerializable(STATE_TIME_START);
                mTimeStartButton.setText(sdf.format(timeStart));
                mTimeStartButton.setTag(timeStart);
            }
            if(savedInstanceState.containsKey(STATE_TIME_END)) {
                Date timeEnd = (Date) savedInstanceState.getSerializable(STATE_TIME_END);
                mTimeEndButton.setText(sdf.format(timeEnd));
                mTimeEndButton.setTag(timeEnd);
            }
        }

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

    @Override
    public void onDestroyView() {
        if(mCurrentDialog != null && mCurrentDialog.isShowing())
            mCurrentDialog.dismiss();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(mDaysButton.getTag() != null) {
            int days = (Integer) mDaysButton.getTag();
            outState.putInt(STATE_DAYS, days);
        }
        Date timeStart = (Date) mTimeStartButton.getTag();
        if(timeStart != null)
            outState.putSerializable(STATE_TIME_START, timeStart);
        Date timeEnd = (Date) mTimeEndButton.getTag();
        if(timeEnd != null)
            outState.putSerializable(STATE_TIME_END, timeEnd);
        super.onSaveInstanceState(outState);
    }
}
