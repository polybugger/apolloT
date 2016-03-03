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

import net.polybugger.apollot.db.ApolloDbAdapter;
import net.polybugger.apollot.db.ClassDbAdapter;
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassNoteDbAdapter;
import net.polybugger.apollot.db.ClassScheduleDbAdapter;
import net.polybugger.apollot.db.StudentDbAdapter;

public class ClassActivity extends AppCompatActivity implements ClassPasswordDialogFragment.ClassPasswordDialogListener,
        ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener,
        ClassScheduleRemoveDialogFragment.RemoveListener,
        ClassNoteRemoveDialogFragment.RemoveListener,
        ClassScheduleNewEditDialogFragment.NewEditListener,
        ClassNoteNewEditDialogFragment.NewEditListener,
        ClassItemNewEditDialogFragment.NewEditListener,
        StudentNewEditDialogFragment.NewEditListener {

    public static boolean CLASS_ITEM_REQUERY_CALLBACK = false;
    public static ClassItemDbAdapter.ClassItem CLASS_ITEM_REQUERY = null;

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";

    private static final int INFO_TAB = 0;
    private static final int ITEMS_TAB = 1;
    private static final int STUDENTS_TAB = 2;

    private ClassDbAdapter.Class mClass;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private MenuItem mLockMenuItem;
    private MenuItem mUnlockMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        Bundle args = getIntent().getExtras();
        if(args == null || !args.containsKey(CLASS_ARG)) {
            super.onBackPressed();
            return;
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int studentNameDisplayFormat = sharedPref.getInt(getString(R.string.pref_student_name_display_format_key), 0);
        StudentDbAdapter.DISPLAY_FORMAT = StudentDbAdapter.NameDisplayFormat.fromInteger(studentNameDisplayFormat);

        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);

        setTitle(mClass.getTitle());
        setContentView(R.layout.activity_class);

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
        getMenuInflater().inflate(R.menu.menu_class, menu);
        mUnlockMenuItem = menu.findItem(R.id.action_unlock);
        mLockMenuItem = menu.findItem(R.id.action_lock);
        if(mClass.isLocked()) {
            mUnlockMenuItem.setVisible(true);
            mLockMenuItem.setVisible(false);
        }
        else {
            mUnlockMenuItem.setVisible(false);
            mLockMenuItem.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        ClassPasswordDialogFragment df;
        int id = item.getItemId();
        switch(id) {
            case R.id.action_lock:
                if(!mClass.isLocked()) {
                    df = (ClassPasswordDialogFragment) fm.findFragmentByTag(ClassPasswordDialogFragment.TAG);
                    if(df == null) {
                        ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.lock_class), getString(R.string.apply_lock_button), ClassPasswordDialogFragment.ClassPasswordOption.APPLY_LOCK);
                        df = ClassPasswordDialogFragment.newInstance(dialogArgs, mClass, null);
                        df.show(fm, ClassPasswordDialogFragment.TAG);
                    }
                }
                return true;
            case R.id.action_unlock:
                if(mClass.isLocked()) {
                    df = (ClassPasswordDialogFragment) fm.findFragmentByTag(ClassPasswordDialogFragment.TAG);
                    if(df == null) {
                        ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.unlock_class), getString(R.string.remove_lock_button), ClassPasswordDialogFragment.ClassPasswordOption.REMOVE_LOCK);
                        df = ClassPasswordDialogFragment.newInstance(dialogArgs, mClass, null);
                        df.show(fm, ClassPasswordDialogFragment.TAG);
                    }
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
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
    protected void onResume() {
        super.onResume();
        if(CLASS_ITEM_REQUERY_CALLBACK) {
            ClassItemDbAdapter.ClassItem classItem = (ClassItemDbAdapter.ClassItem) CLASS_ITEM_REQUERY;
            if(CLASS_ITEM_REQUERY != null) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment cif = fm.findFragmentByTag(getFragmentTag(ITEMS_TAB));
                if(cif != null) {
                    try {
                        ((ClassItemsFragment) cif).requeryClassItem(classItem);
                    }
                    catch(ClassCastException e) {
                        throw new ClassCastException(cif.toString() + " must implement " + ClassItemsFragment.class.toString());
                    }
                }
                Fragment csf = fm.findFragmentByTag(getFragmentTag(STUDENTS_TAB));
                if(csf != null) {
                    try {
                        ((ClassStudentsFragment) csf).requeryStudents();
                    }
                    catch(ClassCastException e) {
                        throw new ClassCastException(csf.toString() + " must implement " + ClassStudentsFragment.class.toString());
                    }
                }
                CLASS_ITEM_REQUERY = null;
            }
            CLASS_ITEM_REQUERY_CALLBACK = false;
        }
    }

    @Override
    public void onClassPasswordDialogSubmit(ClassDbAdapter.Class class_, ClassPasswordDialogFragment.ClassPasswordOption option, String fragmentTag) {
        mClass = class_;
        if(mClass.isLocked()) {
            mUnlockMenuItem.setVisible(true);
            mLockMenuItem.setVisible(false);
        }
        else {
            mUnlockMenuItem.setVisible(false);
            mLockMenuItem.setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.CLASS_REQUERY_CALLBACK = true;
        MainActivity.CLASS_REQUERY = mClass;
        super.onBackPressed();
    }

    @Override
    public void onClassDetailsDialogSubmit(ClassDbAdapter.Class class_, String fragmentTag) {
        mClass = class_;
        setTitle(mClass.getTitle());
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener) f).onClassDetailsDialogSubmit(mClass, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassDetailsNewEditDialogFragment.ClassDetailsDialogListener.class.toString());
            }
        }
    }

    @Override
    public void onRemoveSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassScheduleRemoveDialogFragment.RemoveListener) f).onRemoveSchedule(schedule, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassScheduleRemoveDialogFragment.RemoveListener.class.toString());
            }
        }
    }

    @Override
    public void onRemoveNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassNoteRemoveDialogFragment.RemoveListener) f).onRemoveNote(note, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassNoteRemoveDialogFragment.RemoveListener.class.toString());
            }
        }
    }

    @Override
    public void onNewEditSchedule(ClassScheduleDbAdapter.ClassSchedule schedule, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassScheduleNewEditDialogFragment.NewEditListener) f).onNewEditSchedule(schedule, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassScheduleNewEditDialogFragment.NewEditListener.class.toString());
            }
        }
    }

    @Override
    public void onNewEditNote(ClassNoteDbAdapter.ClassNote note, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassNoteNewEditDialogFragment.NewEditListener) f).onNewEditNote(note, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassNoteNewEditDialogFragment.NewEditListener.class.toString());
            }
        }
    }

    @Override
    public void onNewEditItem(ClassItemDbAdapter.ClassItem item, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassItemNewEditDialogFragment.NewEditListener) f).onNewEditItem(item, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassItemNewEditDialogFragment.NewEditListener.class.toString());
            }
        }
    }

    @Override
    public void onNewEditStudent(StudentDbAdapter.Student student, String fragmentTag) {
        Fragment f = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((StudentNewEditDialogFragment.NewEditListener) f).onNewEditStudent(student, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + StudentNewEditDialogFragment.NewEditListener.class.toString());
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
                case INFO_TAB:
                    return ClassInfoFragment.newInstance(mClass);
                case ITEMS_TAB:
                    return ClassItemsFragment.newInstance(mClass);
                case STUDENTS_TAB:
                    return ClassStudentsFragment.newInstance(mClass);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case INFO_TAB:
                    return getString(R.string.info_tab);
                case ITEMS_TAB:
                    return getString(R.string.items_tab);
                case STUDENTS_TAB:
                    return getString(R.string.students_tab);
            }
            return null;
        }
    }
}
