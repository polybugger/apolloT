package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ClassItemRemoveDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.class_item_remove_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String ITEM_ARG = "net.polybugger.apollot.item_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassItemDbAdapter.ClassItem mClassItem;
    private String mFragmentTag;

    public interface RemoveListener {
        void onRemoveItem(ClassItemDbAdapter.ClassItem classItem, String fragmentTag);
    }

    public ClassItemRemoveDialogFragment() {}

    public static ClassItemRemoveDialogFragment newInstance(String dialogTitle, ClassItemDbAdapter.ClassItem classItem, String fragmentTag) {
        ClassItemRemoveDialogFragment f = new ClassItemRemoveDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putSerializable(ITEM_ARG, classItem);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
    }

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        mClassItem = (ClassItemDbAdapter.ClassItem) args.getSerializable(ITEM_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = mActivity.getLayoutInflater().inflate(R.layout.fragment_class_item_remove_dialog, null);

        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        ((TextView) view.findViewById(R.id.description_text_view)).setText(mClassItem.getDescription());
        Date itemDate = mClassItem.getItemDate();
        final SimpleDateFormat sdfi = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, mActivity.getResources().getConfiguration().locale);
        if(itemDate != null)
            ((TextView) view.findViewById(R.id.item_date_text_view)).setText(sdfi.format(itemDate));
        TextView itemTypeTextView = (TextView) view.findViewById(R.id.item_type_text_view);
        ClassItemTypeDbAdapter.ItemType itemType = mClassItem.getItemType();
        if(itemType != null) {
            itemTypeTextView.setText(itemType.getDescription());
            itemTypeTextView.setVisibility(View.VISIBLE);
        }
        else
            itemTypeTextView.setVisibility(View.GONE);
        TextView perfectScoreTextView = (TextView) view.findViewById(R.id.perfect_score_text_view);
        if(mClassItem.getRecordScores()) {
            Float perfectScore = mClassItem.getPerfectScore();
            if(perfectScore != null)
                perfectScoreTextView.setText(getString(R.string.perfect_score_label) + " " + String.valueOf(mClassItem.getPerfectScore()));
            perfectScoreTextView.setVisibility(View.VISIBLE);
        }
        else
            perfectScoreTextView.setVisibility(View.GONE);
        TextView dueDateTextView = (TextView) view.findViewById(R.id.due_date_text_view);
        if(mClassItem.getRecordSubmissions()) {
            Date dueDate = mClassItem.getSubmissionDueDate();
            if(dueDate != null)
                dueDateTextView.setText(getString(R.string.submission_due_date_label) + " " + sdfi.format(dueDate));
            dueDateTextView.setVisibility(View.VISIBLE);
        }
        else
            dueDateTextView.setVisibility(View.GONE);

        view.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        view.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ((RemoveListener) mActivity).onRemoveItem(mClassItem, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + RemoveListener.class.toString());
                }
                dismiss();
            }
        });

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
}
