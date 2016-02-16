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

public class MainActivity extends AppCompatActivity implements UnlockPasswordDialogFragment.UnlockPasswordListener,
        ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener,
        ClassPasswordDialogFragment.ClassPasswordDialogListener {

    public static boolean CLASS_REQUERY_CALLBACK = false;
    public static ClassDbAdapter.Class CLASS_REQUERY = null;

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
        AcademicTermDbAdapter.setDataColumnName(getString(R.string.db_academic_terms_data_column));
        ClassItemTypeDbAdapter.setTableName(getString(R.string.db_class_item_types_table));
        ClassItemTypeDbAdapter.setIdColumnName(getString(R.string.db_class_item_types_id_column));
        ClassItemTypeDbAdapter.setDataColumnName(getString(R.string.db_class_item_types_data_column));
        ApolloDbAdapter.open();
        ApolloDbAdapter.close();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean lockEnabled = sharedPref.getBoolean(getString(R.string.pref_lock_enabled_key), false);

        if(lockEnabled && !lockAuthenticated) {
            FragmentManager fm = getSupportFragmentManager();
            UnlockPasswordDialogFragment df = (UnlockPasswordDialogFragment) fm.findFragmentByTag(UnlockPasswordDialogFragment.TAG);
            if (df == null) {
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
        if(CLASS_REQUERY_CALLBACK) {
            ClassDbAdapter.Class class_ = CLASS_REQUERY;
            if(class_ != null) {
                FragmentManager fm = getSupportFragmentManager();
                ClassesFragment currentFragment = null, pastFragment = null;
                try {
                    currentFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(CURRENT_TAB));
                    pastFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(PAST_TAB));
                }
                catch(Exception e) { }
                if(currentFragment != null && pastFragment != null) {
                    int currentPosition = currentFragment.getClassPosition(class_);
                    int pastPosition = pastFragment.getClassPosition(class_);

                    if(class_.isCurrent() && currentPosition == -1) {
                        pastFragment.removeClass(class_);
                        currentFragment.addClass(class_);
                    }
                    else if(!class_.isCurrent() && pastPosition == -1) {
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
    }

    private void showNewClassDialog() {
        ClassDetailsNewEditDialogFragment.DialogArgs dialogArgs = new ClassDetailsNewEditDialogFragment.DialogArgs(getString(R.string.new_class_details), getString(R.string.add_button));
        ClassDetailsNewEditDialogFragment f = ClassDetailsNewEditDialogFragment.newInstance(dialogArgs, null, null);
        f.show(getSupportFragmentManager(), ClassDetailsNewEditDialogFragment.TAG);
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

    }

    @Override
    public void onClassDetailsDialogSubmit(ClassDbAdapter.Class class_, String fragmentTag) {
        AcademicTermDbAdapter.AcademicTerm academicTerm = class_.getAcademicTerm();
        long academicTermId = academicTerm == null ? -1 : academicTerm.getId();
        long classId = ClassDbAdapter.insert(class_.getCode(), class_.getDescription(), academicTermId, class_.getYear(), class_.isCurrent());
        if(classId != -1) {
            FragmentManager fm = getSupportFragmentManager();
            ClassesFragment classesFragment;
            try {
                classesFragment = (ClassesFragment) fm.findFragmentByTag(getFragmentTag(class_.isCurrent() ? CURRENT_TAB : PAST_TAB));
                classesFragment.addClass(class_);
            }
            catch(Exception e) { }
            class_.setClassId(classId);
            //Intent intent = new Intent(this, ClassActivity.class);
            //intent.putExtra(ClassActivity.CLASS_ARG, class_);
            //startActivity(intent);
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
