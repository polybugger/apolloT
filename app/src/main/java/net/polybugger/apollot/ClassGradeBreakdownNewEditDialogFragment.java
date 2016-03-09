package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassGradeBreakdownDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;

public class ClassGradeBreakdownNewEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_class_grade_breakdown_dialog_fragment";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";
    public static final String BUTTON_TEXT_ARG = "net.polybugger.apollot.button_text_arg";
    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String GRADE_BREAKDOWN_ARG = "net.polybugger.apollot.grade_breakdown_arg";
    public static final String FRAGMENT_TAG_ARG = "net.polybugger.apollot.fragment_tag_arg";

    private Activity mActivity;
    private ClassDbAdapter.Class mClass;
    private ClassGradeBreakdownDbAdapter.ClassGradeBreakdown mGradeBreakdown;
    private String mFragmentTag;
    private LinearLayoutCompat mLinearLayout;
    private TextView mItemTypeTextView;
    private Spinner mItemTypeSpinner;
    private ArrayAdapter<ClassItemTypeDbAdapter.ItemType> mSpinnerAdapter;
    private EditText mPercentageEditText;

    public ClassGradeBreakdownNewEditDialogFragment() {}

    public interface NewEditListener {
        void onNewEditClassGradeBreakdown(ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, String fragmentTag);
    }

    public static ClassGradeBreakdownNewEditDialogFragment newInstance(String dialogTitle, String buttonText, ClassDbAdapter.Class class_, ClassGradeBreakdownDbAdapter.ClassGradeBreakdown gradeBreakdown, String fragmentTag) {
        ClassGradeBreakdownNewEditDialogFragment f = new ClassGradeBreakdownNewEditDialogFragment();
        Bundle args = new Bundle();
        args.putString(DIALOG_TITLE_ARG, dialogTitle);
        args.putString(BUTTON_TEXT_ARG, buttonText);
        args.putSerializable(CLASS_ARG, class_);
        args.putSerializable(GRADE_BREAKDOWN_ARG, gradeBreakdown);
        args.putString(FRAGMENT_TAG_ARG, fragmentTag);
        f.setArguments(args);
        return f;
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

    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        String dialogTitle = args.getString(DIALOG_TITLE_ARG);
        String buttonText = args.getString(BUTTON_TEXT_ARG);
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mGradeBreakdown = (ClassGradeBreakdownDbAdapter.ClassGradeBreakdown) args.getSerializable(GRADE_BREAKDOWN_ARG);
        mFragmentTag = args.getString(FRAGMENT_TAG_ARG);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_class_grade_breakdown_new_edit_dialog, null);

        mLinearLayout = (LinearLayoutCompat) view.findViewById(R.id.linear_layout);
        ((TextView) view.findViewById(R.id.title_text_view)).setText(dialogTitle);
        // http://stackoverflow.com/questions/6602339/android-spinner-hint
        mSpinnerAdapter = new ArrayAdapter<ClassItemTypeDbAdapter.ItemType>(mActivity, android.R.layout.simple_spinner_item, ClassItemTypeDbAdapter.getList(true, mClass.getClassId()));
        mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mItemTypeSpinner = (Spinner) view.findViewById(R.id.item_type_spinner);
        mItemTypeSpinner.setAdapter(mSpinnerAdapter);
        mItemTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLinearLayout.setBackgroundColor(mSpinnerAdapter.getItem(position).getColorInt());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mItemTypeTextView = (TextView) view.findViewById(R.id.item_type_text_view);
        mPercentageEditText = (EditText) view.findViewById(R.id.percentage_edit_text);

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
            public void onClick(View view) {
                String strPercentage = mPercentageEditText.getText().toString();
                if (TextUtils.isEmpty(strPercentage)) {
                    mPercentageEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_percentage_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Float percentage = Float.parseFloat(strPercentage);
                if (mGradeBreakdown == null) {
                    ClassItemTypeDbAdapter.ItemType itemType = (mItemTypeSpinner.getSelectedItemPosition() == mItemTypeSpinner.getCount()) ? null : (ClassItemTypeDbAdapter.ItemType) mItemTypeSpinner.getSelectedItem();
                    mGradeBreakdown = new ClassGradeBreakdownDbAdapter.ClassGradeBreakdown(-1, itemType, percentage);
                } else {
                    mGradeBreakdown.setPercentage(percentage);
                }
                try {
                    ((NewEditListener) mActivity).onNewEditClassGradeBreakdown(mGradeBreakdown, mFragmentTag);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + NewEditListener.class.toString());
                }
                dismiss();
            }
        });

        if(mGradeBreakdown != null) {
            ClassItemTypeDbAdapter.ItemType itemType = mGradeBreakdown.getItemType();
            if(itemType != null) {
                mItemTypeTextView.setText(itemType.getDescription());
                mLinearLayout.setBackgroundColor(itemType.getColorInt());
            }
            mItemTypeTextView.setVisibility(View.VISIBLE);
            mItemTypeSpinner.setVisibility(View.GONE);
            mPercentageEditText.setText(String.valueOf(mGradeBreakdown.getPercentage()));
        }
        else {
            mItemTypeTextView.setVisibility(View.GONE);
            mItemTypeSpinner.setVisibility(View.VISIBLE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }

}
