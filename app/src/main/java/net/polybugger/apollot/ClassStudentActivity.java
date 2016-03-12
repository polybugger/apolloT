package net.polybugger.apollot;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import net.polybugger.apollot.db.ApolloDbAdapter;
import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassStudentDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

public class ClassStudentActivity extends AppCompatActivity {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String STUDENT_ARG = "net.polybugger.apollot.student_arg";

    private static final int INFO_TAB = 0;
    private static final int ITEMS_TAB = 1;

    private ClassDbAdapter.Class mClass;
    private ClassStudentDbAdapter.ClassStudent mStudent;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        Bundle args = getIntent().getExtras();
        if(args == null || !args.containsKey(CLASS_ARG) || !args.containsKey(STUDENT_ARG)) {
            super.onBackPressed();
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int studentNameDisplayFormat = sharedPref.getInt(getString(R.string.pref_student_name_display_format_key), 0);
        StudentDbAdapter.DISPLAY_FORMAT = StudentDbAdapter.NameDisplayFormat.fromInteger(studentNameDisplayFormat);

        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mStudent = (ClassStudentDbAdapter.ClassStudent) args.getSerializable(STUDENT_ARG);

        setTitle(mStudent.getName());
        setContentView(R.layout.activity_class_students);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_class_student, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case INFO_TAB:
                    return ClassStudentInfoFragment.newInstance(mClass, mStudent);
                case ITEMS_TAB:
                    return ClassStudentInfoFragment.newInstance(mClass, mStudent);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case INFO_TAB:
                    return getString(R.string.student_info_tab);
                case ITEMS_TAB:
                    return getString(R.string.student_items_tab);
            }
            return null;
        }
    }
}
