package net.polybugger.apollot;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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
import net.polybugger.apollot.db.ClassItemDbAdapter;
import net.polybugger.apollot.db.ClassItemRecordDbAdapter;
import net.polybugger.apollot.db.ClassNoteDbAdapter;

public class ClassItemActivity extends AppCompatActivity implements ClassItemNewEditDialogFragment.NewEditListener,
        ClassNoteRemoveDialogFragment.RemoveListener,
        ClassNoteNewEditDialogFragment.NewEditListener,
        ClassItemRecordNewEditDialogFragment.NewEditListener {

    public static final String CLASS_ARG = "net.polybugger.apollot.class_arg";
    public static final String CLASS_ITEM_ARG = "net.polybugger.apollot.class_item_arg";

    private static final int INFO_TAB = 0;
    private static final int RECORDS_TAB = 1;

    private ClassDbAdapter.Class mClass;
    private ClassItemDbAdapter.ClassItem mClassItem;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        Bundle args = getIntent().getExtras();
        if(args == null || !args.containsKey(CLASS_ARG) || !args.containsKey(CLASS_ITEM_ARG)) {
            super.onBackPressed();
            return;
        }
        mClass = (ClassDbAdapter.Class) args.getSerializable(CLASS_ARG);
        mClassItem = (ClassItemDbAdapter.ClassItem) args.getSerializable(CLASS_ITEM_ARG);

        setTitle(mClass.getTitle());
        setContentView(R.layout.activity_class_item);

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
        getMenuInflater().inflate(R.menu.menu_class_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ClassActivity.CLASS_ITEM_REQUERY_CALLBACK = true;
        ClassActivity.CLASS_ITEM_REQUERY = mClassItem;
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
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

        Fragment rf = getSupportFragmentManager().findFragmentByTag(getFragmentTag(RECORDS_TAB));
        if(rf != null) {
            ((ClassItemRecordsFragment) rf).notifyUpdate(item);
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
    public void onNewEditItemRecord(ClassItemRecordDbAdapter.ClassItemRecord itemRecord, String fragmentTag) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentByTag(fragmentTag);
        if(f != null) {
            try {
                ((ClassItemRecordNewEditDialogFragment.NewEditListener) f).onNewEditItemRecord(itemRecord, fragmentTag);
            }
            catch(ClassCastException e) {
                throw new ClassCastException(f.toString() + " must implement " + ClassItemRecordNewEditDialogFragment.NewEditListener.class.toString());
            }
        }
        Fragment iif = fm.findFragmentByTag(getFragmentTag(INFO_TAB));
        if(iif != null) {
            try {
                ((ClassItemInfoFragment) iif).updateSummary();
            }
            catch(ClassCastException e) {
                throw new ClassCastException(iif.toString() + " must implement " + ClassItemInfoFragment.class.toString());
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
                    return ClassItemInfoFragment.newInstance(mClassItem);
                case RECORDS_TAB:
                    return ClassItemRecordsFragment.newInstance(mClassItem);
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
                case INFO_TAB:
                    return getString(R.string.item_info_tab);
                case RECORDS_TAB:
                    return getString(R.string.student_records_tab);
            }
            return null;
        }
    }
}
