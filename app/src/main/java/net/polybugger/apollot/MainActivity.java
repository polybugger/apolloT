package net.polybugger.apollot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.polybugger.apollot.db.AcademicTermDbAdapter;
import net.polybugger.apollot.db.ApolloDbAdapter;
import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemTypeDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

public class MainActivity extends AppCompatActivity implements UnlockPasswordDialogFragment.UnlockPasswordDialogListener,
        ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener,
        ClassPasswordDialogFragment.ClassPasswordDialogListener,
        StartupTaskFragment.StartupListener {

    // for backpress from class activity, to requery data on affected class, a hack
    public static boolean CLASS_REQUERY_CALLBACK = false;
    public static boolean CLASS_REMOVE_CALLBACK = false;
    public static ClassDbAdapter.Class CLASS_REQUERY = null;
    public static boolean REQUERY_CALLBACK = false;

    private static boolean lockAuthenticated = false;
    private static final int CURRENT_TAB = 0;
    private static final int PAST_TAB = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        ApolloDbAdapter.setAppContext(getApplicationContext());
        AcademicTermDbAdapter.setTableName(getString(R.string.db_academic_terms_table));
        AcademicTermDbAdapter.setIdColumnName(getString(R.string.db_academic_terms_id_column));
        AcademicTermDbAdapter.setDataColumnName(getString(R.string.db_academic_terms_data_column), getString(R.string.db_academic_terms_color_column));
        ClassItemTypeDbAdapter.setTableName(getString(R.string.db_class_item_types_table));
        ClassItemTypeDbAdapter.setIdColumnName(getString(R.string.db_class_item_types_id_column));
        ClassItemTypeDbAdapter.setDataColumnName(getString(R.string.db_class_item_types_data_column), getString(R.string.db_class_item_types_color_column));
        ApolloDbAdapter.open();
        ApolloDbAdapter.close();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean lockEnabled = sharedPref.getBoolean(getString(R.string.pref_lock_enabled_key), false);
        int studentNameDisplayFormat = sharedPref.getInt(getString(R.string.pref_student_name_display_format_key), 0);
        StudentDbAdapter.DISPLAY_FORMAT = StudentDbAdapter.NameDisplayFormat.fromInteger(studentNameDisplayFormat);

        if(lockEnabled && !lockAuthenticated) {
            FragmentManager fm = getSupportFragmentManager();
            UnlockPasswordDialogFragment df = (UnlockPasswordDialogFragment) fm.findFragmentByTag(UnlockPasswordDialogFragment.TAG);
            if(df == null) {
                df = UnlockPasswordDialogFragment.newInstance();
                df.show(fm, UnlockPasswordDialogFragment.TAG);
            }
        }
        else {
            onUnlockPassword();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_new_class:
                showNewClassDialog();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FragmentManager fm = getSupportFragmentManager();
        ClassesFragment currentFragment = null, pastFragment = null;
        try {
            currentFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(CURRENT_TAB));
            pastFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(PAST_TAB));
        }
        catch(Exception e) { }
        if(CLASS_REQUERY_CALLBACK) {
            ClassDbAdapter.Class class_ = CLASS_REQUERY;
            if(class_ != null) {
                if(currentFragment != null && pastFragment != null) {
                    int currentPos = currentFragment.getClassPosition(class_);
                    int pastPos = pastFragment.getClassPosition(class_);
                    if(class_.isCurrent() && currentPos == -1) {
                        pastFragment.removeClass(class_);
                        currentFragment.addClass(class_);
                    }
                    else if(!class_.isCurrent() && pastPos == -1) {
                        currentFragment.removeClass(class_);
                        pastFragment.addClass(class_);
                    }
                    if(class_.isCurrent())
                        currentFragment.requeryClass(class_);
                    else
                        pastFragment.requeryClass(class_);
                }
                CLASS_REQUERY = null;
            }
            CLASS_REQUERY_CALLBACK = false;
        }
        if(REQUERY_CALLBACK) {
            currentFragment.requeryClasses();
            pastFragment.requeryClasses();
            REQUERY_CALLBACK = false;
        }
        if(CLASS_REMOVE_CALLBACK) {
            ClassDbAdapter.Class class_ = CLASS_REQUERY;
            if(class_ != null) {
                if(currentFragment != null && pastFragment != null) {
                    currentFragment.removeClass(class_);
                    pastFragment.removeClass(class_);
                }
                CLASS_REQUERY = null;
            }
            CLASS_REMOVE_CALLBACK = false;
        }
    }

    private void showNewClassDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ClassDetailsNewEditDialogFragment df = (ClassDetailsNewEditDialogFragment) fm.findFragmentByTag(ClassDetailsNewEditDialogFragment.TAG);
        if(df == null) {
            df = ClassDetailsNewEditDialogFragment.newInstance(getString(R.string.new_class_details), getString(R.string.add_button), null, null);
            df.show(fm, ClassDetailsNewEditDialogFragment.TAG);
        }
    }

    @Override
    public void onUnlockPassword() {
        lockAuthenticated = true;
        setTitle(R.string.activity_classes_title);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FragmentManager fm = getSupportFragmentManager();

        if(fm.findFragmentByTag(StartupTaskFragment.TAG) == null) {
            fm.beginTransaction().add(new StartupTaskFragment(), StartupTaskFragment.TAG).commit();
        }
    }

    @Override
    public void onClassDetailsDialogSubmit(ClassDbAdapter.Class class_, String fragmentTag) {
        AcademicTermDbAdapter.AcademicTerm academicTerm = class_.getAcademicTerm();
        long academicTermId = academicTerm == null ? -1 : academicTerm.getId();
        long classId = ClassDbAdapter.insert(class_.getCode(), class_.getDescription(), academicTermId, class_.getYear(), class_.isCurrent());
        if(classId != -1) {
            class_.setClassId(classId);
            FragmentManager fm = getSupportFragmentManager();
            ClassesFragment classesFragment;
            try {
                classesFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(class_.isCurrent() ? CURRENT_TAB : PAST_TAB));
                classesFragment.addClass(class_);
            }
            catch(Exception e) { }
            Intent intent = new Intent(this, ClassActivity.class);
            intent.putExtra(ClassActivity.CLASS_ARG, class_);
            startActivity(intent);
        }

    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

    @Override
    public void onClassPasswordDialogSubmit(ClassDbAdapter.Class class_, ClassPasswordDialogFragment.ClassPasswordOption option, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassPasswordDialogFragment.ClassPasswordDialogListener) f).onClassPasswordDialogSubmit(class_, option, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassPasswordDialogFragment.ClassPasswordDialogListener.class.toString());
            }
        }
    }

    @Override
    public void onStartup(StartupTaskFragment.StartupOption startupOption) {
        switch(startupOption) {
            case SHOW_CURRENT_CLASSES:
                mViewPager.setCurrentItem(CURRENT_TAB, true);
                break;
            case SHOW_PAST_CLASSES:
                mViewPager.setCurrentItem(PAST_TAB, true);
                break;
            case SHOW_NEW_CLASS_DIALOG:
                showNewClassDialog();
                break;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case CURRENT_TAB:
                    return ClassesFragment.newInstance(true);
                case PAST_TAB:
                    return ClassesFragment.newInstance(false);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case CURRENT_TAB:
                    return getString(R.string.current_tab);
                case PAST_TAB:
                    return getString(R.string.past_tab);
            }
            return null;
        }
    }
}
