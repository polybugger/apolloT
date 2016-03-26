package net.polybugger.apollot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AToFPercentageEditDialogFragment extends DialogFragment {

    public static final String TAG = "net.polybugger.apollot.new_edit_a_to_f_percentage_dialog_fragment";
    public static final String TAG_DUMMY_ARG = "net.polybugger.apollot.tag_dummy_arg";

    public interface EditListener {
        void onEditPercentage(float percentage, AToFActivity.TagDummy tagDummy);
    }

    private Activity mActivity;
    private EditText mPercentageEditText;
    private AToFActivity.TagDummy mTagDummy;

    public AToFPercentageEditDialogFragment() {}

    public static AToFPercentageEditDialogFragment newInstance(AToFActivity.TagDummy tagDummy) {
        AToFPercentageEditDialogFragment f = new AToFPercentageEditDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(TAG_DUMMY_ARG, tagDummy);
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
        mTagDummy = (AToFActivity.TagDummy) args.getSerializable(TAG_DUMMY_ARG);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_a_to_f_percentage_edit_dialog, null);

        ((TextView) view.findViewById(R.id.a_to_f_percentage_text_view)).setText(mTagDummy.mGradeMark.getGradeMark());
        mPercentageEditText = (EditText) view.findViewById(R.id.a_to_f_percentage_edit_text);
        mPercentageEditText.setText(String.format("%.2f", mTagDummy.mGradeMark.getPercentage()));

        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        Button saveButton = (Button) view.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strPercentage = mPercentageEditText.getText().toString();
                if (TextUtils.isEmpty(strPercentage)) {
                    mPercentageEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_a_to_f_percentage_required, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                Float percentage = Float.parseFloat(strPercentage);

                if(!(percentage > 0.0 && percentage < 100.0)) {
                    mPercentageEditText.requestFocus();
                    Toast toast = Toast.makeText(mActivity, R.string.fragment_status_a_to_f_percentage_error, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                try {
                    ((EditListener) mActivity).onEditPercentage(percentage, mTagDummy);
                } catch (ClassCastException e) {
                    throw new ClassCastException(mActivity.toString() + " must implement " + EditListener.class.toString());
                }
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(null).setView(view).setPositiveButton(null, null).setNegativeButton(null, null);
        return builder.create();
    }
}
