package net.polybugger.apollot;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import net.polybugger.apollot.db.ClassDbAdapter;

public class ClassActivity extends AppCompatActivity implements ClassPasswordDialogFragment.ClassPasswordDialogListener {

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

        Bundle args = getIntent().getExtras();
        if(args == null || !args.containsKey(CLASS_ARG)) {
            super.onBackPressed();
            return;
        }

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
        int id = item.getItemId();

        switch(id) {
            case R.id.action_lock:
                if(!mClass.isLocked()) {
                    ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.lock_class), getString(R.string.apply_lock_button), ClassPasswordDialogFragment.ClassPasswordOption.APPLY_LOCK);
                    ClassPasswordDialogFragment f = ClassPasswordDialogFragment.newInstance(dialogArgs, mClass, null);
                    f.show(getSupportFragmentManager(), ClassPasswordDialogFragment.TAG);
                }
                return true;
            case R.id.action_unlock:
                if(mClass.isLocked()) {
                    ClassPasswordDialogFragment.DialogArgs dialogArgs = new ClassPasswordDialogFragment.DialogArgs(getString(R.string.unlock_class), getString(R.string.remove_lock_button), ClassPasswordDialogFragment.ClassPasswordOption.REMOVE_LOCK);
                    ClassPasswordDialogFragment f = ClassPasswordDialogFragment.newInstance(dialogArgs, mClass, null);
                    f.show(getSupportFragmentManager(), ClassPasswordDialogFragment.TAG);
                }
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
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
                    return ClassInfoFragment.newInstance(mClass);
                case STUDENTS_TAB:
                    return ClassInfoFragment.newInstance(mClass);
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
