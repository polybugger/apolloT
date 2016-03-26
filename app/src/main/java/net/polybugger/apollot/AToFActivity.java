package net.polybugger.apollot;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import net.polybugger.apollot.db.AToFCalculation;
import net.polybugger.apollot.db.AToFCalculation.GradeMark;
import net.polybugger.apollot.db.ApolloDbAdapter;

import java.io.Serializable;

public class AToFActivity extends AppCompatActivity implements AToFPercentageEditDialogFragment.EditListener {

    private View.OnClickListener mEditClickListener;
    private View.OnClickListener mCheckBoxListener;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        setContentView(R.layout.activity_a_to_f);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        mCheckBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                TagDummy tagDummy = (TagDummy) checkBox.getTag();
                boolean selected = !checkBox.isChecked();
                tagDummy.mGradeMark.setHidden(selected);
                mSharedPref.edit().putBoolean(tagDummy.mHiddenKey, selected).apply();
            }
        };

        mEditClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TagDummy tagDummy = (TagDummy) view.getTag();
                FragmentManager fm = getSupportFragmentManager();
                AToFPercentageEditDialogFragment df = (AToFPercentageEditDialogFragment) fm.findFragmentByTag(AToFPercentageEditDialogFragment.TAG);
                if (df == null) {
                    df = AToFPercentageEditDialogFragment.newInstance(tagDummy);
                    df.show(fm, AToFPercentageEditDialogFragment.TAG);
                }
            }
        };

        TagDummy aPlusGradeMark = new TagDummy(getString(R.string.pref_a_plus_hidden_key), getString(R.string.pref_a_plus_percentage_key), R.id.a_plus_text_view, R.id.a_plus_check_box, R.id.a_plus_edit_button,
                new GradeMark(AToFCalculation.A_PLUS, mSharedPref.getFloat(getString(R.string.pref_a_plus_percentage_key), AToFCalculation.A_PLUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_a_plus_hidden_key), false)));
        TagDummy aGradeMark = new TagDummy(getString(R.string.pref_a_hidden_key), getString(R.string.pref_a_percentage_key), R.id.a_text_view, R.id.a_check_box, R.id.a_edit_button,
                new GradeMark(AToFCalculation.A, mSharedPref.getFloat(getString(R.string.pref_a_percentage_key), AToFCalculation.A_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_a_hidden_key), false)));
        TagDummy aMinusGradeMark = new TagDummy(getString(R.string.pref_a_minus_hidden_key), getString(R.string.pref_a_minus_percentage_key), R.id.a_minus_text_view, R.id.a_minus_check_box, R.id.a_minus_edit_button,
                new GradeMark(AToFCalculation.A_MINUS, mSharedPref.getFloat(getString(R.string.pref_a_minus_percentage_key), AToFCalculation.A_MINUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_a_minus_hidden_key), false)));

        CheckBox aPlusCheckBox = (CheckBox) findViewById(R.id.a_plus_check_box);
        aPlusCheckBox.setOnClickListener(mCheckBoxListener);
        aPlusCheckBox.setTag(aPlusGradeMark);
        aPlusCheckBox.setChecked(!aPlusGradeMark.mGradeMark.getHidden());
        TextView aPlusTextView = (TextView) findViewById(R.id.a_plus_text_view);
        aPlusTextView.setText(String.format(" ≥ %.2f%%", aPlusGradeMark.mGradeMark.getPercentage()));
        CheckBox aCheckBox = (CheckBox) findViewById(R.id.a_check_box);
        aCheckBox.setOnClickListener(mCheckBoxListener);
        aCheckBox.setTag(aGradeMark);
        aCheckBox.setChecked(!aGradeMark.mGradeMark.getHidden());
        TextView aTextView = (TextView) findViewById(R.id.a_text_view);
        aTextView.setText(String.format(" ≥ %.2f%%", aGradeMark.mGradeMark.getPercentage()));
        CheckBox aMinusCheckBox = (CheckBox) findViewById(R.id.a_minus_check_box);
        aMinusCheckBox.setOnClickListener(mCheckBoxListener);
        aMinusCheckBox.setTag(aMinusGradeMark);
        aMinusCheckBox.setChecked(!aMinusGradeMark.mGradeMark.getHidden());
        TextView aMinusTextView = (TextView) findViewById(R.id.a_minus_text_view);
        aMinusTextView.setText(String.format(" ≥ %.2f%%", aMinusGradeMark.mGradeMark.getPercentage()));

        ImageButton aPlusEditButton = (ImageButton) findViewById(R.id.a_plus_edit_button);
        aPlusEditButton.setOnClickListener(mEditClickListener);
        aPlusEditButton.setTag(aPlusGradeMark);
        ImageButton aEditButton = (ImageButton) findViewById(R.id.a_edit_button);
        aEditButton.setOnClickListener(mEditClickListener);
        aEditButton.setTag(aGradeMark);
        ImageButton aMinusEditButton = (ImageButton) findViewById(R.id.a_minus_edit_button);
        aMinusEditButton.setOnClickListener(mEditClickListener);
        aMinusEditButton.setTag(aMinusGradeMark);

        TagDummy bPlusGradeMark = new TagDummy(getString(R.string.pref_b_plus_hidden_key), getString(R.string.pref_b_plus_percentage_key), R.id.b_plus_text_view, R.id.b_plus_check_box, R.id.b_plus_edit_button,
                new GradeMark(AToFCalculation.B_PLUS, mSharedPref.getFloat(getString(R.string.pref_b_plus_percentage_key), AToFCalculation.B_PLUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_b_plus_hidden_key), false)));
        TagDummy bGradeMark = new TagDummy(getString(R.string.pref_b_hidden_key), getString(R.string.pref_b_percentage_key), R.id.b_text_view, R.id.b_check_box, R.id.b_edit_button,
                new GradeMark(AToFCalculation.B, mSharedPref.getFloat(getString(R.string.pref_b_percentage_key), AToFCalculation.B_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_b_hidden_key), false)));
        TagDummy bMinusGradeMark = new TagDummy(getString(R.string.pref_b_minus_hidden_key), getString(R.string.pref_b_minus_percentage_key), R.id.b_minus_text_view, R.id.b_minus_check_box, R.id.b_minus_edit_button,
                new GradeMark(AToFCalculation.B_MINUS, mSharedPref.getFloat(getString(R.string.pref_b_minus_percentage_key), AToFCalculation.B_MINUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_b_minus_hidden_key), false)));

        CheckBox bPlusCheckBox = (CheckBox) findViewById(R.id.b_plus_check_box);
        bPlusCheckBox.setOnClickListener(mCheckBoxListener);
        bPlusCheckBox.setTag(bPlusGradeMark);
        bPlusCheckBox.setChecked(!bPlusGradeMark.mGradeMark.getHidden());
        TextView bPlusTextView = (TextView) findViewById(R.id.b_plus_text_view);
        bPlusTextView.setText(String.format(" ≥ %.2f%%", bPlusGradeMark.mGradeMark.getPercentage()));
        CheckBox bCheckBox = (CheckBox) findViewById(R.id.b_check_box);
        bCheckBox.setOnClickListener(mCheckBoxListener);
        bCheckBox.setTag(bGradeMark);
        bCheckBox.setChecked(!bGradeMark.mGradeMark.getHidden());
        TextView bTextView = (TextView) findViewById(R.id.b_text_view);
        bTextView.setText(String.format(" ≥ %.2f%%", bGradeMark.mGradeMark.getPercentage()));
        CheckBox bMinusCheckBox = (CheckBox) findViewById(R.id.b_minus_check_box);
        bMinusCheckBox.setOnClickListener(mCheckBoxListener);
        bMinusCheckBox.setTag(bMinusGradeMark);
        bMinusCheckBox.setChecked(!bMinusGradeMark.mGradeMark.getHidden());
        TextView bMinusTextView = (TextView) findViewById(R.id.b_minus_text_view);
        bMinusTextView.setText(String.format(" ≥ %.2f%%", bMinusGradeMark.mGradeMark.getPercentage()));

        ImageButton bPlusEditButton = (ImageButton) findViewById(R.id.b_plus_edit_button);
        bPlusEditButton.setOnClickListener(mEditClickListener);
        bPlusEditButton.setTag(bPlusGradeMark);
        ImageButton bEditButton = (ImageButton) findViewById(R.id.b_edit_button);
        bEditButton.setOnClickListener(mEditClickListener);
        bEditButton.setTag(bGradeMark);
        ImageButton bMinusEditButton = (ImageButton) findViewById(R.id.b_minus_edit_button);
        bMinusEditButton.setOnClickListener(mEditClickListener);
        bMinusEditButton.setTag(bMinusGradeMark);

        TagDummy cPlusGradeMark = new TagDummy(getString(R.string.pref_c_plus_hidden_key), getString(R.string.pref_c_plus_percentage_key), R.id.c_plus_text_view, R.id.c_plus_check_box, R.id.c_plus_edit_button,
                new GradeMark(AToFCalculation.C_PLUS, mSharedPref.getFloat(getString(R.string.pref_c_plus_percentage_key), AToFCalculation.C_PLUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_c_plus_hidden_key), false)));
        TagDummy cGradeMark = new TagDummy(getString(R.string.pref_c_hidden_key), getString(R.string.pref_c_percentage_key), R.id.c_text_view, R.id.c_check_box, R.id.c_edit_button,
                new GradeMark(AToFCalculation.C, mSharedPref.getFloat(getString(R.string.pref_c_percentage_key), AToFCalculation.C_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_c_hidden_key), false)));
        TagDummy cMinusGradeMark = new TagDummy(getString(R.string.pref_c_minus_hidden_key), getString(R.string.pref_c_minus_percentage_key), R.id.c_minus_text_view, R.id.c_minus_check_box, R.id.c_minus_edit_button,
                new GradeMark(AToFCalculation.C_MINUS, mSharedPref.getFloat(getString(R.string.pref_c_minus_percentage_key), AToFCalculation.C_MINUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_c_minus_hidden_key), false)));

        CheckBox cPlusCheckBox = (CheckBox) findViewById(R.id.c_plus_check_box);
        cPlusCheckBox.setOnClickListener(mCheckBoxListener);
        cPlusCheckBox.setTag(cPlusGradeMark);
        cPlusCheckBox.setChecked(!cPlusGradeMark.mGradeMark.getHidden());
        TextView cPlusTextView = (TextView) findViewById(R.id.c_plus_text_view);
        cPlusTextView.setText(String.format(" ≥ %.2f%%", cPlusGradeMark.mGradeMark.getPercentage()));
        CheckBox cCheckBox = (CheckBox) findViewById(R.id.c_check_box);
        cCheckBox.setOnClickListener(mCheckBoxListener);
        cCheckBox.setTag(cGradeMark);
        cCheckBox.setChecked(!cGradeMark.mGradeMark.getHidden());
        TextView cTextView = (TextView) findViewById(R.id.c_text_view);
        cTextView.setText(String.format(" ≥ %.2f%%", cGradeMark.mGradeMark.getPercentage()));
        CheckBox cMinusCheckBox = (CheckBox) findViewById(R.id.c_minus_check_box);
        cMinusCheckBox.setOnClickListener(mCheckBoxListener);
        cMinusCheckBox.setTag(cMinusGradeMark);
        cMinusCheckBox.setChecked(!cMinusGradeMark.mGradeMark.getHidden());
        TextView cMinusTextView = (TextView) findViewById(R.id.c_minus_text_view);
        cMinusTextView.setText(String.format(" ≥ %.2f%%", cMinusGradeMark.mGradeMark.getPercentage()));

        ImageButton cPlusEditButton = (ImageButton) findViewById(R.id.c_plus_edit_button);
        cPlusEditButton.setOnClickListener(mEditClickListener);
        cPlusEditButton.setTag(cPlusGradeMark);
        ImageButton cEditButton = (ImageButton) findViewById(R.id.c_edit_button);
        cEditButton.setOnClickListener(mEditClickListener);
        cEditButton.setTag(cGradeMark);
        ImageButton cMinusEditButton = (ImageButton) findViewById(R.id.c_minus_edit_button);
        cMinusEditButton.setOnClickListener(mEditClickListener);
        cMinusEditButton.setTag(cMinusGradeMark);

        TagDummy dPlusGradeMark = new TagDummy(getString(R.string.pref_d_plus_hidden_key), getString(R.string.pref_d_plus_percentage_key), R.id.d_plus_text_view, R.id.d_plus_check_box, R.id.d_plus_edit_button,
                new GradeMark(AToFCalculation.D_PLUS, mSharedPref.getFloat(getString(R.string.pref_d_plus_percentage_key), AToFCalculation.D_PLUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_d_plus_hidden_key), false)));
        TagDummy dGradeMark = new TagDummy(getString(R.string.pref_d_hidden_key), getString(R.string.pref_d_percentage_key), R.id.d_text_view, R.id.d_check_box, R.id.d_edit_button,
                new GradeMark(AToFCalculation.D, mSharedPref.getFloat(getString(R.string.pref_d_percentage_key), AToFCalculation.D_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_d_hidden_key), false)));
        TagDummy dMinusGradeMark = new TagDummy(getString(R.string.pref_d_minus_hidden_key), getString(R.string.pref_d_minus_percentage_key), R.id.d_minus_text_view, R.id.d_minus_check_box, R.id.d_minus_edit_button,
                new GradeMark(AToFCalculation.D_MINUS, mSharedPref.getFloat(getString(R.string.pref_d_minus_percentage_key), AToFCalculation.D_MINUS_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_d_minus_hidden_key), false)));

        CheckBox dPlusCheckBox = (CheckBox) findViewById(R.id.d_plus_check_box);
        dPlusCheckBox.setOnClickListener(mCheckBoxListener);
        dPlusCheckBox.setTag(dPlusGradeMark);
        dPlusCheckBox.setChecked(!dPlusGradeMark.mGradeMark.getHidden());
        TextView dPlusTextView = (TextView) findViewById(R.id.d_plus_text_view);
        dPlusTextView.setText(String.format(" ≥ %.2f%%", dPlusGradeMark.mGradeMark.getPercentage()));

        CheckBox dCheckBox = (CheckBox) findViewById(R.id.d_check_box);
        dCheckBox.setOnClickListener(mCheckBoxListener);
        dCheckBox.setTag(dGradeMark);
        dCheckBox.setChecked(!dGradeMark.mGradeMark.getHidden());
        TextView dTextView = (TextView) findViewById(R.id.d_text_view);
        dTextView.setText(String.format(" ≥ %.2f%%", dGradeMark.mGradeMark.getPercentage()));

        CheckBox dMinusCheckBox = (CheckBox) findViewById(R.id.d_minus_check_box);
        dMinusCheckBox.setOnClickListener(mCheckBoxListener);
        dMinusCheckBox.setTag(dMinusGradeMark);
        dMinusCheckBox.setChecked(!dMinusGradeMark.mGradeMark.getHidden());
        TextView dMinusTextView = (TextView) findViewById(R.id.d_minus_text_view);
        dMinusTextView.setText(String.format(" ≥ %.2f%%", dMinusGradeMark.mGradeMark.getPercentage()));

        ImageButton dPlusEditButton = (ImageButton) findViewById(R.id.d_plus_edit_button);
        dPlusEditButton.setOnClickListener(mEditClickListener);
        dPlusEditButton.setTag(dPlusGradeMark);
        ImageButton dEditButton = (ImageButton) findViewById(R.id.d_edit_button);
        dEditButton.setOnClickListener(mEditClickListener);
        dEditButton.setTag(dGradeMark);
        ImageButton dMinusEditButton = (ImageButton) findViewById(R.id.d_minus_edit_button);
        dMinusEditButton.setOnClickListener(mEditClickListener);
        dMinusEditButton.setTag(dMinusGradeMark);

        TagDummy fGradeMark = new TagDummy(getString(R.string.pref_f_hidden_key), getString(R.string.pref_f_percentage_key), R.id.f_text_view, R.id.f_check_box, R.id.f_edit_button,
                new GradeMark(AToFCalculation.F, mSharedPref.getFloat(getString(R.string.pref_f_percentage_key), AToFCalculation.F_PERCENTAGE), mSharedPref.getBoolean(getString(R.string.pref_f_hidden_key), false)));

        CheckBox fCheckBox = (CheckBox) findViewById(R.id.f_check_box);
        fCheckBox.setOnClickListener(mCheckBoxListener);
        fCheckBox.setTag(fGradeMark);
        fCheckBox.setChecked(!fGradeMark.mGradeMark.getHidden());
        TextView fTextView = (TextView) findViewById(R.id.f_text_view);
        fTextView.setText(String.format(" ≥ %.2f%%", fGradeMark.mGradeMark.getPercentage()));

    }

    @Override
    public void onEditPercentage(float percentage, TagDummy tagDummy) {
        TextView textView = (TextView) findViewById(tagDummy.mTextViewId);
        textView.setText(String.format(" ≥ %.2f%%", percentage));
        mSharedPref.edit().putFloat(tagDummy.mPercentageKey, percentage).apply();
        tagDummy.mGradeMark.setPercentage(percentage);
        findViewById(tagDummy.mCheckBoxId).setTag(tagDummy);
        findViewById(tagDummy.mEditButtonId).setTag(tagDummy);
    }

    public static class TagDummy implements Serializable {
        public String mHiddenKey;
        public String mPercentageKey;
        public int mTextViewId;
        public int mCheckBoxId;
        public int mEditButtonId;
        public GradeMark mGradeMark;

        public TagDummy(String hiddenKey, String percentageKey, int textViewId, int checkBoxId, int editButtonId, GradeMark gradeMark) {
            mHiddenKey = hiddenKey;
            mPercentageKey = percentageKey;
            mTextViewId = textViewId;
            mCheckBoxId = checkBoxId;
            mEditButtonId = editButtonId;
            mGradeMark = gradeMark;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
