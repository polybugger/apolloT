package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassGradeBreakdownDbAdapter;
import net.polybugger.apollot.db.ClassNoteDbAdapter;
import net.polybugger.apollot.db.ClassScheduleDbAdapter;

import java.util.ArrayList;

public class ClassInfoFragment extends Fragment implements ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener,
        ClassScheduleRemoveDialogFragment.RemoveListener,
        ClassNoteRemoveDialogFragment.RemoveListener,
        ClassScheduleNewEditDialogFragment.NewEditListener,
        ClassNoteNewEditDialogFragment.NewEditListener,
        ClassGradeBreakdownNewEditDialogFragment.NewEditListener,
        ClassGradeBreakdownRemoveDialogFragment.RemoveListener {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private TextView mClassTitleTextView;
    private TextView mAcademicTermTextView;
    private TextView mCurrentTextView;

    private LinearLayout mScheduleLinearLayout;
    private ArrayList<ClassScheduleDbAdapter.ClassSchedule> mScheduleList;
    private DbQuerySchedulesTask mScheduleTask;
    private View.OnClickListener mEditScheduleClickListener;
    private View.OnClickListener mRemoveScheduleClickListener;

    private LinearLayout mNoteLinearLayout;
    private ArrayList<ClassNoteDbAdapter.ClassNote> mNoteList;
    private DbQueryNotesTask mNoteTask;
    private View.OnClickListener mEditNoteClickListener;
    private View.OnClickListener mRemoveNoteClickListener;

    private LinearLayout mGradeBreakdownLinearLayout;
    private ArrayList<ClassGradeBreakdownDbAdapter.ClassGradeBreakdown> mGradeBreakdownList;
    private DbQueryGradeBreakdownsTask mGradeBreakdownTask;
    private View.OnClickListener mEditGradeBreakdownClickListener;
    private View.OnClickListener mRemoveGradeBreakdownClickListener;
    private TextView mTotalPercentageTextView;

    public ClassInfoFragment() { }

    public static ClassInfoFragment newInstance(ClassDbAdapter.Class class_) {
        ClassInfoFragment f = new ClassInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ARG, class_);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Activity){
            mActivity = (Activity) context;
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        mScheduleTask.cancel(true);
        mNoteTask.cancel(true);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_info, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();
        switch(id) {
            case R.id.action_new_schedule:
                ClassScheduleNewEditDialogFragment sdf = (ClassScheduleNewEditDialogFragment) fm.findFragmentByTag(ClassScheduleNewEditDialogFragment.TAG);
                if(sdf == null) {
                    sdf = ClassScheduleNewEditDialogFragment.newInstance(getString(R.string.new_class_schedule), getString(R.string.add_button), null, getTag());
                    sdf.show(fm, ClassScheduleNewEditDialogFragment.TAG);
                }
                return true;
            case R.id.action_new_grade_breakdown:
                ClassGradeBreakdownNewEditDialogFragment gbdf = (ClassGradeBreakdownNewEditDialogFragment) fm.findFragmentByTag(ClassGradeBreakdownNewEditDialogFragment.TAG);
                if(gbdf == null) {
                    gbdf = ClassGradeBreakdownNewEditDialogFragment.newInstance(getString(R.string.new_class_grade_breakdown), getString(R.string.add_button), mClass, null, getTag());
                    gbdf.show(fm, ClassGradeBreakdownNewEditDialogFragment.TAG);
                }
                return true;
            case R.id.action_new_note:
                ClassNoteNewEditDialogFragment ndf = (ClassNoteNewEditDialogFragment) fm.findFragmentByTag(ClassNoteNewEditDialogFragment.TAG);
                if(ndf == null) {
                    ndf = ClassNoteNewEditDialogFragment.newInstance(getString(R.string.new_class_note), getString(R.string.add_button), null, getTag());
                    ndf.show(fm, ClassNoteNewEditDialogFragment.TAG);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);

        View view = inflater.inflate(R.layout.fragment_class_info, container, false);
        mClassTitleTextView = (TextView) view.findViewById(R.id.class_title_text_view);
        mAcademicTermTextView = (TextView) view.findViewById(R.id.academic_term_text_view);
        mCurrentTextView = (TextView) view.findViewById(R.id.current_text_view);

        view.findViewById(R.id.edit_class_details_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassDetailsNewEditDialogFragment df = (ClassDetailsNewEditDialogFragment) fm.findFragmentByTag(ClassDetailsNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassDetailsNewEditDialogFragment.newInstance(getString(R.string.edit_class_details), getString(R.string.save_button), mClass, getTag());
                    df.show(fm, ClassDetailsNewEditDialogFragment.TAG);
                }
            }
        });

        mScheduleLinearLayout = (LinearLayout) view.findViewById(R.id.schedule_linear_layout);
        mEditScheduleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassScheduleNewEditDialogFragment df = (ClassScheduleNewEditDialogFragment) fm.findFragmentByTag(ClassScheduleNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassScheduleNewEditDialogFragment.newInstance(getString(R.string.edit_class_schedule), getString(R.string.save_button), (ClassScheduleDbAdapter.ClassSchedule) view.getTag(), getTag());
                    df.show(fm, ClassScheduleNewEditDialogFragment.TAG);
                }
            }
        };
        mRemoveScheduleClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassScheduleRemoveDialogFragment df = (ClassScheduleRemoveDialogFragment) fm.findFragmentByTag(ClassScheduleRemoveDialogFragment.TAG);
                if(df == null) {
                    df = ClassScheduleRemoveDialogFragment.newInstance(getString(R.string.remove_class_schedule), (ClassScheduleDbAdapter.ClassSchedule) view.getTag(), getTag());
                    df.show(fm, ClassScheduleRemoveDialogFragment.TAG);
                }
            }
        };

        mNoteLinearLayout = (LinearLayout) view.findViewById(R.id.note_linear_layout);
        mEditNoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassNoteNewEditDialogFragment df = (ClassNoteNewEditDialogFragment) fm.findFragmentByTag(ClassNoteNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassNoteNewEditDialogFragment.newInstance(getString(R.string.edit_class_note), getString(R.string.save_button), (ClassNoteDbAdapter.ClassNote) view.getTag(), getTag());
                    df.show(fm, ClassNoteNewEditDialogFragment.TAG);
                }
            }
        };
        mRemoveNoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassNoteRemoveDialogFragment df = (ClassNoteRemoveDialogFragment) fm.findFragmentByTag(ClassNoteRemoveDialogFragment.TAG);
                if(df == null) {
                    df = ClassNoteRemoveDialogFragment.newInstance(getString(R.string.remove_class_note), (ClassNoteDbAdapter.ClassNote) view.getTag(), getTag());
                    df.show(fm, ClassNoteRemoveDialogFragment.TAG);
                }
            }
        };

        mGradeBreakdownLinearLayout = (LinearLayout) view.findViewById(R.id.grade_breakdown_linear_layout);
        mEditGradeBreakdownClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassGradeBreakdownNewEditDialogFragment df = (ClassGradeBreakdownNewEditDialogFragment) fm.findFragmentByTag(ClassGradeBreakdownNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassGradeBreakdownNewEditDialogFragment.newInstance(getString(R.string.edit_class_grade_breakdown), getString(R.string.save_button), mClass, (ClassGradeBreakdownDbAdapter.ClassGradeBreakdown) view.getTag(), getTag());
                    df.show(fm, ClassNoteNewEditDialogFragment.TAG);
                }
            }
        };
        mRemoveGradeBreakdownClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassGradeBreakdownRemoveDialogFragment df = (ClassGradeBreakdownRemoveDialogFragment) fm.findFragmentByTag(ClassGradeBreakdownRemoveDialogFragment.TAG);
                if(df == null) {
                    df = ClassGradeBreakdownRemoveDialogFragment.newInstance(getString(R.string.remove_class_grade_breakdown), (ClassGradeBreakdownDbAdapter.ClassGradeBreakdown) view.getTag(), getTag());
                    df.show(fm, ClassGradeBreakdownRemoveDialogFragment.TAG);
                }
            }
        };
        mTotalPercentageTextView = (TextView) view.findViewById(R.id.total_percentage_text_view);

        mScheduleTask = new DbQuerySchedulesTask();
        mScheduleTask.execute(mClass.getClassId());

        mNoteTask = new DbQueryNotesTask();
        mNoteTask.execute(mClass.getClassId());

        mGradeBreakdownTask = new DbQueryGradeBreakdownsTask();
        mGradeBreakdownTask.execute(mClass.getClassId());

        populateClassDetailsViews();

        return view;
    }

    private void populateClassDetailsViews() {
        mClassTitleTextView.setText(mClass.getTitle());
        String academicTerm = mClass.getAcademicTermYear();
        if(!TextUtils.isEmpty(academicTerm)) {
            mAcademicTermTextView.setText(academicTerm);
            mAcademicTermTextView.setVisibility(View.VISIBLE);
        }
        else
            mAcademicTermTextView.setVisibility(View.GONE);
        mCurrentTextView.setText(mClass.getCurrent());
    }

    public void requeryClassInfo() {
        mClass = ClassDbAdapter.getClass(mClass.getClassId());
        if(mGradeBreakdownLinearLayout.getChildCount() > 0)
            mGradeBreakdownLinearLayout.removeAllViews();
        mGradeBreakdownTask = new DbQueryGradeBreakdownsTask();
        mGradeBreakdownTask.execute(mClass.getClassId());
        populateClassDetailsViews();
    }

    @Override
    public void onClassDetailsDialogSubmit(ClassDbAdapter.Class class_, String fragmentTag) {
        if(ClassDbAdapter.update(class_.getClassId(), class_.getCode(), class_.getDescription(), class_.getAcademicTerm().getId(), class_.getYear(), class_.isCurrent()) >= 1) {
            mClass = class_;
            populateClassDetailsViews();
        }
    }

    @Override
    public void onRemoveSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        if(ClassScheduleDbAdapter.delete(schedule.getClassId(), schedule.getScheduleId()) >= 1) {
            int childPosition = mScheduleList.indexOf(schedule);
            if(childPosition != -1) {
                mScheduleList.remove(childPosition);
                mScheduleLinearLayout.removeViewAt(childPosition);
            }
        }
    }

    @Override
    public void onRemoveNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        if(ClassNoteDbAdapter.delete(note.getClassId(), note.getNoteId()) >= 1) {
            int childPosition = mNoteList.indexOf(note);
            if(childPosition != -1) {
                mNoteList.remove(childPosition);
                mNoteLinearLayout.removeViewAt(childPosition);
            }
        }
    }

    @Override
    public void onNewEditSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        long classId = mClass.getClassId();
        if(schedule.getScheduleId() != -1) {
            if(ClassScheduleDbAdapter.update(classId, schedule.getScheduleId(), schedule.getTimeStart(), schedule.getTimeEnd(), schedule.getDays(), schedule.getRoom(), schedule.getBuilding(), schedule.getCampus()) >= 1) {
                int childPosition = mScheduleList.indexOf(schedule);
                if(childPosition != -1) {
                    mScheduleList.set(childPosition, schedule);
                    View rowView = mScheduleLinearLayout.getChildAt(childPosition);
                    if(rowView != null)
                        _getScheduleView(rowView, schedule, null, null);
                }
            }
        }
        else {
            long scheduleId = ClassScheduleDbAdapter.insert(classId, schedule.getTimeStart(), schedule.getTimeEnd(), schedule.getDays(), schedule.getRoom(), schedule.getBuilding(), schedule.getCampus());
            if(scheduleId != -1) {
                schedule.setClassId(classId);
                schedule.setScheduleId(scheduleId);
                mScheduleList.add(schedule);
                mScheduleLinearLayout.addView(getScheduleView(mActivity.getLayoutInflater(), schedule, mEditScheduleClickListener, mRemoveScheduleClickListener));
            }
        }
    }

    @Override
    public void onNewEditNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        long classId = mClass.getClassId();
        if(note.getNoteId() != -1) {
            if(ClassNoteDbAdapter.update(classId, note.getNoteId(), note.getNote(), note.getDateCreated()) >= 1) {
                int childPosition = mNoteList.indexOf(note);
                if(childPosition != -1) {
                    mNoteList.set(childPosition, note);
                    View rowView = mNoteLinearLayout.getChildAt(childPosition);
                    if(rowView != null)
                        _getNoteView(rowView, note, null, null);
                }
            }
        }
        else {
            long noteId = ClassNoteDbAdapter.insert(classId, note.getNote(), note.getDateCreated());
            if(noteId != -1) {
                note.setClassId(classId);
                note.setNoteId(noteId);
                mNoteList.add(note);
                mNoteLinearLayout.addView(getNoteView(mActivity.getLayoutInflater(), note, mEditNoteClickListener, mRemoveNoteClickListener));
            }
        }
    }

    @Override
    public void onNewEditClassGradeBreakdown(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, String fragmentTag) {
        long classId = mClass.getClassId();
        if(gradeBreakdown.getClassId() != -1) {
            gradeBreakdown.setClassId(classId);
            if(ClassGradeBreakdownDbAdapter.update(classId, gradeBreakdown.getItemType().getId(), gradeBreakdown.getPercentage()) >= 1) {
                int childPosition = mGradeBreakdownList.indexOf(gradeBreakdown);
                if(childPosition != -1) {
                    mGradeBreakdownList.set(childPosition, gradeBreakdown);
                    View rowView = mGradeBreakdownLinearLayout.getChildAt(childPosition);
                    if(rowView != null)
                        _getGradeBreakdownView(rowView, gradeBreakdown, null, null);
                }
            }
        }
        else {
            long gradeBreakdownId = ClassGradeBreakdownDbAdapter.insert(classId, gradeBreakdown.getItemType().getId(), gradeBreakdown.getPercentage());
            if(gradeBreakdownId != -1) {
                gradeBreakdown.setClassId(classId);
                mGradeBreakdownList.add(gradeBreakdown);
                mGradeBreakdownLinearLayout.addView(getGradeBreakdownView(mActivity.getLayoutInflater(), gradeBreakdown, mEditGradeBreakdownClickListener, mRemoveGradeBreakdownClickListener));
            }
        }
        float totalPercentage = (float) 0.0;
        for(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown_ : mGradeBreakdownList) {
            totalPercentage = totalPercentage + gradeBreakdown_.getPercentage();
        }
        mTotalPercentageTextView.setText(String.valueOf(totalPercentage) + "%");
    }

    @Override
    public void onRemoveGradeBreakdown(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, String fragmentTag) {
        if(ClassGradeBreakdownDbAdapter.delete(gradeBreakdown.getClassId(), gradeBreakdown.getItemType().getId()) >= 1) {
            int childPosition = mGradeBreakdownList.indexOf(gradeBreakdown);
            if(childPosition != -1) {
                mGradeBreakdownList.remove(childPosition);
                mGradeBreakdownLinearLayout.removeViewAt(childPosition);
            }
            float totalPercentage = (float) 0.0;
            for(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown_ : mGradeBreakdownList) {
                totalPercentage = totalPercentage + gradeBreakdown_.getPercentage();
            }
            mTotalPercentageTextView.setText(String.valueOf(totalPercentage) + "%");
        }

    }

    private class DbQuerySchedulesTask extends AsyncTask<Long, Integer, ArrayList<ClassScheduleDbAdapter.ClassSchedule>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassScheduleDbAdapter.ClassSchedule> doInBackground(Long... classId) {
            return ClassScheduleDbAdapter.getClassSchedules(classId[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // TODO updates loader
        }

        @Override
        protected void onCancelled() {
            // TODO cleanup loader on cancel
        }

        @Override
        protected void onPostExecute(ArrayList<ClassScheduleDbAdapter.ClassSchedule> scheduleList) {
            mScheduleList = scheduleList;
            for(ClassScheduleDbAdapter.ClassSchedule schedule : mScheduleList) {
                mScheduleLinearLayout.addView(getScheduleView(mActivity.getLayoutInflater(), schedule, mEditScheduleClickListener, mRemoveScheduleClickListener));
            }
        }
    }

    @SuppressLint("InflateParams")
    private View getScheduleView(LayoutInflater inflater, ClassScheduleDbAdapter.ClassSchedule schedule, View.OnClickListener editScheduleClickListener, View.OnClickListener removeScheduleClickListener) {
        return _getScheduleView(inflater.inflate(R.layout.class_schedule_row, null), schedule, editScheduleClickListener, removeScheduleClickListener);
    }

    private View _getScheduleView(View rowView, ClassScheduleDbAdapter.ClassSchedule schedule, View.OnClickListener editScheduleClickListener, View.OnClickListener removeScheduleClickListener) {
        TextView scheduleTextView = (TextView) rowView.findViewById(R.id.schedule_text_view);
        scheduleTextView.setTag(schedule);
        if(editScheduleClickListener != null) {
            scheduleTextView.setOnClickListener(editScheduleClickListener);
        }
        scheduleTextView.setText(schedule.getTime() + "\r\n" + schedule.getLocation());

        ImageButton removeButton = (ImageButton) rowView.findViewById(R.id.remove_button);
        removeButton.setTag(schedule);
        if(removeScheduleClickListener != null)
            removeButton.setOnClickListener(removeScheduleClickListener);
        return rowView;
    }

    private class DbQueryNotesTask extends AsyncTask<Long, Integer, ArrayList<ClassNoteDbAdapter.ClassNote>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassNoteDbAdapter.ClassNote> doInBackground(Long... classId) {
            return ClassNoteDbAdapter.getClassNotes(classId[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // TODO updates loader
        }

        @Override
        protected void onCancelled() {
            // TODO cleanup loader on cancel
        }

        @Override
        protected void onPostExecute(ArrayList<ClassNoteDbAdapter.ClassNote> noteList) {
            mNoteList = noteList;
            for(ClassNoteDbAdapter.ClassNote note : mNoteList) {
                mNoteLinearLayout.addView(getNoteView(mActivity.getLayoutInflater(), note, mEditNoteClickListener, mRemoveNoteClickListener));
            }
        }
    }

    private View _getNoteView(View rowView, ClassNoteDbAdapter.ClassNote note, View.OnClickListener editNoteClickListener, View.OnClickListener removeNoteClickListener) {
        TextView noteTextView = (TextView) rowView.findViewById(R.id.note_text_view);
        noteTextView.setTag(note);
        if(editNoteClickListener != null) {
            noteTextView.setOnClickListener(editNoteClickListener);
        }
        noteTextView.setText(note.getDateNoteText());

        ImageButton removeButton = (ImageButton) rowView.findViewById(R.id.remove_button);
        removeButton.setTag(note);
        if(removeNoteClickListener != null)
            removeButton.setOnClickListener(removeNoteClickListener);
        return rowView;
    }

    @SuppressLint("InflateParams")
    private View getNoteView(LayoutInflater inflater, ClassNoteDbAdapter.ClassNote note, View.OnClickListener editNoteClickListener, View.OnClickListener removeNoteClickListener) {
        return _getNoteView(inflater.inflate(R.layout.class_note_row, null), note, editNoteClickListener, removeNoteClickListener);
    }

    private class DbQueryGradeBreakdownsTask extends AsyncTask<Long, Integer, ArrayList<ClassGradeBreakdownDbAdapter.ClassGradeBreakdown>> {

        @Override
        protected void onPreExecute() {
            // TODO initialize loader
        }

        @Override
        protected ArrayList<ClassGradeBreakdownDbAdapter.ClassGradeBreakdown> doInBackground(Long... classId) {
            return ClassGradeBreakdownDbAdapter.getClassGradeBreakdowns(classId[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... percent) {
            // TODO updates loader
        }

        @Override
        protected void onCancelled() {
            // TODO cleanup loader on cancel
        }

        @Override
        protected void onPostExecute(ArrayList<ClassGradeBreakdownDbAdapter.ClassGradeBreakdown> gradeBreakdownList) {
            float totalPercentage = (float) 0.0;
            mGradeBreakdownList = gradeBreakdownList;
            for(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown : mGradeBreakdownList) {
                totalPercentage = totalPercentage + gradeBreakdown.getPercentage();
                mGradeBreakdownLinearLayout.addView(getGradeBreakdownView(mActivity.getLayoutInflater(), gradeBreakdown, mEditGradeBreakdownClickListener, mRemoveGradeBreakdownClickListener));
            }
            mTotalPercentageTextView.setText(String.valueOf(totalPercentage) + "%");
        }
    }

    private View _getGradeBreakdownView(View rowView, ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, View.OnClickListener editGradeBreakdownClickListener, View.OnClickListener removeGradeBreakdownClickListener) {
        LinearLayoutCompat linearLayoutCompat = (LinearLayoutCompat) rowView.findViewById(R.id.grade_breakdown_linear_layout_row);
        linearLayoutCompat.setBackgroundColor(gradeBreakdown.getItemType().getColorInt());

        TextView gradeBreakdownTextView = (TextView) rowView.findViewById(R.id.grade_breakdown_text_view);
        gradeBreakdownTextView.setText(gradeBreakdown.getItemType().getDescription());
        TextView percentageTextView = (TextView) rowView.findViewById(R.id.percentage_text_view);
        percentageTextView.setText(Float.toString(gradeBreakdown.getPercentage()) + "%");

        if(editGradeBreakdownClickListener != null) {
            RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.grade_breakdown_relative_layout_row);
            relativeLayout.setTag(gradeBreakdown);
            relativeLayout.setOnClickListener(editGradeBreakdownClickListener);
        }

        ImageButton removeButton = (ImageButton) rowView.findViewById(R.id.remove_button);
        removeButton.setTag(gradeBreakdown);
        if(removeGradeBreakdownClickListener != null)
            removeButton.setOnClickListener(removeGradeBreakdownClickListener);
        return rowView;
    }

    @SuppressLint("InflateParams")
    private View getGradeBreakdownView(LayoutInflater inflater, ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, View.OnClickListener editGradeBreakdownClickListener, View.OnClickListener removeGradeBreakdownClickListener) {
        return _getGradeBreakdownView(inflater.inflate(R.layout.class_grade_breakdown_row, null), gradeBreakdown, editGradeBreakdownClickListener, removeGradeBreakdownClickListener);
    }

}
