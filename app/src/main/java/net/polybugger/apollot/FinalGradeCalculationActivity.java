package net.polybugger.apollot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import net.polybugger.apollot.db.ApolloDbAdapter;
import net.polybugger.apollot.db.ClassGradeBreakdownDbAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FinalGradeCalculationActivity extends AppCompatActivity implements PassingGradePercentageDialogFragment.EditListener,
        PassingGradeMarkEditDialogFragment.EditListener {

    public static float DEFAULT_PASSING_GRADE_PERCENTAGE = (float) 75.0;
    public static float DEFAULT_ONE_TO_FIVE_PASSING_GRADE_MARK = (float) 3.0;
    public static float DEFAULT_FOUR_TO_ONE_PASSING_GRADE_MARK = (float) 1.0;

    private TextView mPassingGradePercentageTextView;
    private ListArrayAdapter mListAdapter;
    private ListView mListView;
    private ArrayList<GradeSystem> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        setContentView(R.layout.activity_final_grade_calculation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPassingGradePercentageTextView = (TextView) findViewById(R.id.passing_grade_percentage_text_view);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        float passingGradePercentage = sharedPref.getFloat(getString(R.string.pref_passing_grade_percentage_key), DEFAULT_PASSING_GRADE_PERCENTAGE);
        mPassingGradePercentageTextView.setText(String.format("%.2f%%", passingGradePercentage));

        findViewById(R.id.passing_grade_percentage_relative_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                PassingGradePercentageDialogFragment df = (PassingGradePercentageDialogFragment) fm.findFragmentByTag(PassingGradePercentageDialogFragment.TAG);
                if (df == null) {
                    df = PassingGradePercentageDialogFragment.newInstance();
                    df.show(fm, PassingGradePercentageDialogFragment.TAG);
                }
            }
        });

        mList.add(new GradeSystem(getString(R.string.a_to_f), GradeSystemType.A_TO_F, sharedPref.getBoolean(getString(R.string.pref_a_to_f_selected_key), true)));
        mList.add(new GradeSystem(getString(R.string.one_to_five), GradeSystemType.ONE_TO_FIVE, sharedPref.getBoolean(getString(R.string.pref_one_to_five_selected_key), false)));
        mList.add(new GradeSystem(getString(R.string.four_to_one), GradeSystemType.FOUR_TO_ONE, sharedPref.getBoolean(getString(R.string.pref_four_to_one_selected_key), false)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListAdapter = new ListArrayAdapter(this, mList);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onEditPassingGradePercentage(float passingGradePercentage) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.edit().putFloat(getString(R.string.pref_passing_grade_percentage_key), passingGradePercentage).apply();
        mPassingGradePercentageTextView.setText(String.format("%.2f%%", passingGradePercentage));
    }

    @Override
    public void onEditPassingGradeMark(float passingGradeMark, GradeSystemType type) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        switch(type) {
            case ONE_TO_FIVE:
                sharedPref.edit().putFloat(getString(R.string.pref_one_to_five_passing_grade_mark_key), passingGradeMark).apply();
                break;
            case FOUR_TO_ONE:
                sharedPref.edit().putFloat(getString(R.string.pref_four_to_one_passing_grade_mark_key), passingGradeMark).apply();
                break;
        }
    }


    private class ListArrayAdapter extends ArrayAdapter<GradeSystem> {

        private View.OnClickListener mEditClickListener;
        private View.OnClickListener mCheckBoxListener;

        private class ViewHolder {
            CheckBox checkBox;
            ImageButton imageButton;
        }

        public ListArrayAdapter(Context context, List<GradeSystem> objects) {
            super(context, R.layout.listview_grade_system_row, R.id.check_box, objects);
            mCheckBoxListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox checkBox = (CheckBox) view;
                    Context context = getContext();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
                    GradeSystem gradeSystem = (GradeSystem) checkBox.getTag();
                    boolean selected = checkBox.isChecked();
                    gradeSystem.setSelected(selected);
                    switch(gradeSystem.getType()) {
                        case A_TO_F:
                            sharedPref.edit().putBoolean(getString(R.string.pref_a_to_f_selected_key), selected).apply();
                            break;
                        case ONE_TO_FIVE:
                            sharedPref.edit().putBoolean(getString(R.string.pref_one_to_five_selected_key), selected).apply();
                            break;
                        case FOUR_TO_ONE:
                            sharedPref.edit().putBoolean(getString(R.string.pref_four_to_one_selected_key), selected).apply();
                            break;
                        default:
                            sharedPref.edit().putBoolean(getString(R.string.pref_a_to_f_selected_key), selected).apply();
                    }
                }
            };
            mEditClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    GradeSystem gradeSystem = (GradeSystem) view.getTag();
                    Intent intent;
                    FragmentManager fm = getSupportFragmentManager();
                    PassingGradeMarkEditDialogFragment df;
                    switch(gradeSystem.getType()) {
                        case A_TO_F:
                            intent = new Intent(getContext(), AToFActivity.class);
                            startActivity(intent);
                            break;
                        case ONE_TO_FIVE:
                            df = (PassingGradeMarkEditDialogFragment) fm.findFragmentByTag(PassingGradeMarkEditDialogFragment.TAG);
                            if(df == null) {
                                df = PassingGradeMarkEditDialogFragment.newInstance(gradeSystem.getType());
                                df.show(fm, PassingGradeMarkEditDialogFragment.TAG);
                            }
                            break;
                        case FOUR_TO_ONE:
                            df = (PassingGradeMarkEditDialogFragment) fm.findFragmentByTag(PassingGradeMarkEditDialogFragment.TAG);
                            if(df == null) {
                                df = PassingGradeMarkEditDialogFragment.newInstance(gradeSystem.getType());
                                df.show(fm, PassingGradeMarkEditDialogFragment.TAG);
                            }
                            break;
                        default:
                    }
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.listview_grade_system_row, parent, false);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
                viewHolder.checkBox.setOnClickListener(mCheckBoxListener);
                viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.edit_button);
                viewHolder.imageButton.setOnClickListener(mEditClickListener);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            GradeSystem gradeSystem = getItem(position);
            viewHolder.checkBox.setText(gradeSystem.getDescription());
            viewHolder.checkBox.setTag(gradeSystem);
            viewHolder.checkBox.setChecked(gradeSystem.isSelected());
            viewHolder.imageButton.setTag(gradeSystem);
            return convertView;
        }

    }

    public static class GradeSystem implements Serializable {
        private String mDescription;
        private GradeSystemType mType;
        private boolean mSelected = true;

        public GradeSystem(String description, GradeSystemType type, boolean selected) {
            mDescription = description;
            mType = type;
            mSelected = selected;
        }
        public String getDescription() {
            return mDescription;
        }
        public GradeSystemType getType() {
            return mType;
        }
        public boolean isSelected() {
            return mSelected;
        }
        public void setSelected(boolean selected) {
            this.mSelected = selected;
        }
    }

    public enum GradeSystemType {
        ONE_TO_FIVE(0),
        A_TO_F(1),
        FOUR_TO_ONE(2);

        private static final GradeSystemType[] sFinalGradeCalculationTypeValues = GradeSystemType.values();

        public static GradeSystemType fromInteger(int x) {
            if(x < 0 || x > 2)
                return ONE_TO_FIVE;
            return sFinalGradeCalculationTypeValues[x];
        }

        private int mValue;

        private GradeSystemType(int value) {
            mValue = value;
        }

        public int getValue() {
            return mValue;
        }
    }
}
