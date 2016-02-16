package net.polybugger.apollot;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassDbAdapter;

public class ClassInfoFragment extends Fragment {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private TextView mClassTitleTextView;
    private TextView mAcademicTermTextView;
    private TextView mCurrentTextView;

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_class_info, menu);
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
        ImageButton editClassDetailsButton = (ImageButton) view.findViewById(R.id.edit_class_details_button);
        editClassDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassDetailsDialogFragment.DialogArgs dialogArgs = new NewEditClassDetailsDialogFragment.DialogArgs(getString(R.string.edit_class_details), getString(R.string.save_button));
                NewEditClassDetailsDialogFragment f = NewEditClassDetailsDialogFragment.newInstance(dialogArgs, mClass, getTag());
                f.show(getFragmentManager(), NewEditClassDetailsDialogFragment.TAG);
                */
            }
        });

        /*
        mScheduleLinearLayout = (LinearLayout) view.findViewById(R.id.schedule_linear_layout);
        mNoteAttachmentLinearLayout = (LinearLayout) view.findViewById(R.id.note_attachment_linear_layout);
        mEditScheduleClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassScheduleDialogFragment.DialogArgs dialogArgs = new NewEditClassScheduleDialogFragment.DialogArgs(getString(R.string.edit_class_schedule), getString(R.string.save_button));
                NewEditClassScheduleDialogFragment f = NewEditClassScheduleDialogFragment.newInstance(dialogArgs, (ClassSchedule) view.getTag(), getTag());
                f.show(getFragmentManager(), NewEditClassScheduleDialogFragment.TAG);
            }
        };
        mRemoveScheduleClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                final ClassSchedule schedule = (ClassSchedule) view.getTag();
                RemoveClassScheduleDialog removeDialog = new RemoveClassScheduleDialog(mActivity, schedule,
                        new RemoveClassScheduleDialog.RemoveClassScheduleListener() {
                            @Override
                            public void onRemove() {
                                if(ClassScheduleDbAdapter.delete(schedule.getClassId(), schedule.getScheduleId()) >= 1) {
                                    int childPosition = mScheduleList.indexOf(schedule);
                                    if(childPosition != -1) {
                                        mScheduleList.remove(childPosition);
                                        mScheduleLinearLayout.removeViewAt(childPosition);
                                    }
                                }
                            }
                        });
                removeDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogFragmentShown = false;
                    }
                });
                mCurrentDialog = removeDialog.show();
            }
        };

        mEditNoteClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                NewEditClassNoteAttachmentDialogFragment.DialogArgs noteDialogArgs = new NewEditClassNoteAttachmentDialogFragment.DialogArgs(getString(R.string.edit_class_note_attachment), getString(R.string.save_button));
                NewEditClassNoteAttachmentDialogFragment f = NewEditClassNoteAttachmentDialogFragment.newInstance(noteDialogArgs, (ClassNoteAttachment) view.getTag(), getTag());
                f.show(getFragmentManager(), NewEditClassNoteAttachmentDialogFragment.TAG);
            }
        };
        mLinkClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = String.valueOf(view.getTag());
                if(!StringUtils.isBlank(link)) {
                    if(link.startsWith("http://") || link.startsWith("https://")) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                        startActivity(browserIntent);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.setAction(android.content.Intent.ACTION_VIEW);
                        File file = new File(link);
                        MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String ext = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                        intent.setDataAndType(Uri.fromFile(file), mime.getMimeTypeFromExtension(ext));
                        startActivity(intent);
                    }
                }
            }
        };
        mRemoveNoteClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialogFragmentShown)
                    return;
                mDialogFragmentShown = true;
                final ClassNoteAttachment note = (ClassNoteAttachment) view.getTag();
                RemoveClassNoteAttachmentDialog removeDialog = new RemoveClassNoteAttachmentDialog(mActivity, note, getString(R.string.remove_class_note),
                        new RemoveClassNoteAttachmentDialog.RemoveClassNoteAttachmentListener() {
                            @Override
                            public void onRemove() {
                                if(ClassNoteAttachmentDbAdapter.delete(note.getClassId(), note.getNoteId()) >= 1) {
                                    int childPosition = mNoteList.indexOf(note);
                                    if(childPosition != -1) {
                                        mNoteList.remove(childPosition);
                                        mNoteAttachmentLinearLayout.removeViewAt(childPosition);
                                    }
                                }
                            }
                        });
                removeDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogFragmentShown = false;
                    }
                });
                mCurrentDialog = removeDialog.show();
            }
        };


        mScheduleTask = new DbQuerySchedulesTask();
        mScheduleTask.execute(mClass.getClassId());

        mNoteTask = new DbQueryNotesTask();
        mNoteTask.execute(mClass.getClassId());

        */
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
}
