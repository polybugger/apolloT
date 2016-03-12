package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassStudentInfoFragment extends Fragment {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private ClassStudentDbAdapter.ClassStudent mStudent;
    private TextView mStudentNameTextView;
    private TextView mGenderTextView;
    private TextView mEmailAddressTextView;
    private TextView mContactNoTextView;


    public ClassStudentInfoFragment() { }

    public static ClassStudentInfoFragment newInstance(ClassDbAdapter.Class class_, ClassStudentDbAdapter.ClassStudent student) {
        ClassStudentInfoFragment f = new ClassStudentInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(CLASS_ARG, class_);
        args.putSerializable(STUDENT_ARG, student);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mStudent = (ClassStudentDbAdapter.ClassStudent) args.getSerializable(STUDENT_ARG);

        View view = inflater.inflate(R.layout.fragment_class_student_info, container, false);
        //view.findViewById(R.id.scroll_view).setBackgroundColor(mClassItem.getItemType().getColorInt());

        mStudentNameTextView = (TextView) view.findViewById(R.id.student_name_text_view);
        mGenderTextView = (TextView) view.findViewById(R.id.gender_text_view);
        mEmailAddressTextView = (TextView) view.findViewById(R.id.email_address_text_view);
        mContactNoTextView = (TextView) view.findViewById(R.id.contact_no_text_view);

        view.findViewById(R.id.edit_class_student_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                FragmentManager fm = getFragmentManager();
                ClassItemNewEditDialogFragment df = (ClassItemNewEditDialogFragment) fm.findFragmentByTag(ClassItemNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassItemNewEditDialogFragment.newInstance(getString(R.string.edit_class_item), getString(R.string.save_button), mClassItem, getTag());
                    df.show(fm, ClassItemNewEditDialogFragment.TAG);
                }
                */
            }
        });
        view.findViewById(R.id.remove_class_student_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                RemoveDialog d = new RemoveDialog(mActivity);
                mCurrentDialog = d.show();
                */
            }
        });

        /*
        mNoteLinearLayout = (LinearLayout) view.findViewById(R.id.note_linear_layout);
        mEditNoteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                ClassNoteNewEditDialogFragment df = (ClassNoteNewEditDialogFragment) fm.findFragmentByTag(ClassNoteNewEditDialogFragment.TAG);
                if(df == null) {
                    df = ClassNoteNewEditDialogFragment.newInstance(getString(R.string.edit_class_item_note), getString(R.string.save_button), (ClassItemNoteDbAdapter.ClassItemNote) view.getTag(), getTag());
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
                    df = ClassNoteRemoveDialogFragment.newInstance(getString(R.string.remove_class_item_note), (ClassItemNoteDbAdapter.ClassItemNote) view.getTag(), getTag());
                    df.show(fm, ClassNoteRemoveDialogFragment.TAG);
                }
            }
        };

        mAttendanceSummaryTextView = (TextView) view.findViewById(R.id.attendance_summary_text_view);
        mScoresSummaryTextView = (TextView) view.findViewById(R.id.scores_summary_text_view);
        mSubmissionsSummaryTextView = (TextView) view.findViewById(R.id.submissions_summary_text_view);


        mNoteTask = new DbQueryNotesTask();
        mNoteTask.execute(mClassItem);

        updateSummary();

        */
        populateClassStudentViews();

        return view;
    }

    private void populateClassStudentViews() {
        StudentDbAdapter.Student student = mStudent.getStudent();
        mStudentNameTextView.setText(student.getName());

        // TODO replace gender with localized text
        mGenderTextView.setText(student.getGender());

        String emailAddress = student.getEmailAddress();
        if(TextUtils.isEmpty(emailAddress)) {
            mEmailAddressTextView.setVisibility(View.GONE);
        }
        else {
            mEmailAddressTextView.setText(emailAddress);
            mEmailAddressTextView.setVisibility(View.VISIBLE);
        }

        String contactNo = student.getContactNo();
        if(TextUtils.isEmpty(contactNo)) {
            mContactNoTextView.setVisibility(View.GONE);
        }
        else {
            mContactNoTextView.setText(emailAddress);
            mContactNoTextView.setVisibility(View.VISIBLE);
        }
    }


}
