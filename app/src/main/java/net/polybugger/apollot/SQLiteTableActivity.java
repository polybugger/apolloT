package net.polybugger.apollot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.polybugger.apollot.db.ApolloDbAdapter;

public class SQLiteTableActivity extends AppCompatActivity implements SQLiteTableNewEditDialogFragment.NewEditListener,
        SQLiteTableRemoveDialogFragment.RemoveListener {

    public static final String WHITE_COLOR = "00ffffff";

    public static final String TABLE_NAME_ARG = "net.polybugger.apollot.table_name_arg";
    public static final String ID_COLUMN_ARG = "net.polybugger.apollot.id_column_arg";
    public static final String DATA_COLUMN_ARG = "net.polybugger.apollot.data_column_arg";
    public static final String COLOR_COLUMN_ARG = "net.polybugger.apollot.color_column_arg";
    public static final String TITLE_ARG = "net.polybugger.apollot.title_arg";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";

    private String mTableName;
    private String mIdColumn;
    private String mDataColumn;
    private String mColorColumn;
    private String mTitle;
    private String mDialogTitle;
    private ListArrayAdapter mListAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApolloDbAdapter.setAppContext(getApplicationContext());

        setContentView(R.layout.activity_sqlite_table);

        Bundle args = getIntent().getExtras();
        mTableName = args.getString(TABLE_NAME_ARG);
        mIdColumn = args.getString(ID_COLUMN_ARG);
        mDataColumn = args.getString(DATA_COLUMN_ARG);
        mColorColumn = args.getString(COLOR_COLUMN_ARG);
        mTitle = args.getString(TITLE_ARG);
        mDialogTitle = args.getString(DIALOG_TITLE_ARG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mTitle);

        findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                SQLiteTableNewEditDialogFragment df = (SQLiteTableNewEditDialogFragment) fm.findFragmentByTag(SQLiteTableNewEditDialogFragment.TAG);
                if(df == null) {
                    df = SQLiteTableNewEditDialogFragment.newInstance(getString(R.string.new_) + " " + mDialogTitle, getString(R.string.add_button), new SQLiteRow(-1, null, null));
                    df.show(fm, SQLiteTableNewEditDialogFragment.TAG);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mListAdapter = new ListArrayAdapter(this, getList());
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mListAdapter);
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

    @Override
    public void onBackPressed() {
        if(TextUtils.equals(mTableName, getString(R.string.db_class_item_types_table))) {
            ClassActivity.REQUERY_CALLBACK = true;
            ClassItemActivity.REQUERY_CALLBACK = true;
        }
        if(TextUtils.equals(mTableName, getString(R.string.db_academic_terms_table))) {
            MainActivity.REQUERY_CALLBACK = true;
            ClassActivity.REQUERY_CALLBACK = true;
        }
        super.onBackPressed();
    }

    private ArrayList<SQLiteRow> getList() {
        ArrayList<SQLiteRow> list = new ArrayList<>();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(mTableName, new String[] { mIdColumn, mDataColumn, mColorColumn }, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            list.add(new SQLiteRow(cursor.getLong(0), cursor.getString(1), cursor.isNull(2) ? null : cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    private long insert(String tableName, String dataColumn, String data, String colorColumn, String color) {
        ContentValues values = new ContentValues();
        values.put(dataColumn, data);
        values.put(colorColumn, color);
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = db.insert(tableName, null, values);
        ApolloDbAdapter.close();
        return id;
    }

    private int update(String tableName, String idColumn, long id, String dataColumn, String data, String colorColumn, String color) {
        ContentValues values = new ContentValues();
        values.put(dataColumn, data);
        values.put(colorColumn, color);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(tableName, values, idColumn + "=?", new String[]{String.valueOf(id)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public int delete(String tableName, String idColumn, long id) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, idColumn + "=?", new String[]{String.valueOf(id)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    private void scrollListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                mListView.smoothScrollToPosition(mListAdapter.getCount() - 1);
            }
        });
    }

    @Override
    public void onNewEdit(SQLiteRow sqliteRow) {
        long id = sqliteRow.getId();
        String data = sqliteRow.getData();
        String color = sqliteRow.getColor();
        if(id == -1) {
            id = insert(mTableName, mDataColumn, data, mColorColumn, color);
            if(id != -1) {
                mListAdapter.add(new SQLiteRow(id, data, color));
                scrollListViewToBottom();
            }
        }
        else {
            SQLiteRow item;
            int i, count = mListAdapter.getCount();
            for(i = 0; i < count; ++i) {
                item = mListAdapter.getItem(i);
                if(item.getId() == id) {
                    if(update(mTableName, mIdColumn, id, mDataColumn, data, mColorColumn, color) >= 1)
                        item.setData(data);
                    break;
                }
            }
        }
        mListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemove(SQLiteRow sqliteRow) {
        long id = sqliteRow.getId();
        SQLiteRow item;
        int i, count = mListAdapter.getCount();
        for(i = 0; i < count; ++i) {
            item = mListAdapter.getItem(i);
            if(item.getId() == id) {
                if(delete(mTableName, mIdColumn, id) >= 1) {
                    mListAdapter.remove(item);
                    mListAdapter.notifyDataSetChanged();
                }
                break;
            }
        }
    }

    private class ListArrayAdapter extends ArrayAdapter<SQLiteRow> {

        private View.OnClickListener mEditClickListener;
        private View.OnClickListener mRemoveClickListener;

        private class ViewHolder {
            LinearLayoutCompat linearLayoutCompat;
            TextView textView;
            ImageButton imageButton;
        }

        public ListArrayAdapter(Context context, List<SQLiteRow> objects) {
            super(context, R.layout.listview_sqlite_table_row, R.id.text_view, objects);
            mEditClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SQLiteTableNewEditDialogFragment df = (SQLiteTableNewEditDialogFragment) fm.findFragmentByTag(SQLiteTableNewEditDialogFragment.TAG);
                    if(df == null) {
                        df = SQLiteTableNewEditDialogFragment.newInstance(getString(R.string.edit) + " " + mDialogTitle, getString(R.string.save_button), (SQLiteRow) view.getTag());
                        df.show(fm, SQLiteTableNewEditDialogFragment.TAG);
                    }
                }
            };
            mRemoveClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getSupportFragmentManager();
                    SQLiteTableRemoveDialogFragment df = (SQLiteTableRemoveDialogFragment) fm.findFragmentByTag(SQLiteTableRemoveDialogFragment.TAG);
                    if(df == null) {
                        df = SQLiteTableRemoveDialogFragment.newInstance(getString(R.string.remove) + " " + mDialogTitle, (SQLiteRow) view.getTag());
                        df.show(fm, SQLiteTableRemoveDialogFragment.TAG);
                    }
                }
            };

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SQLiteRow sqliteRow = getItem(position);
            String color = sqliteRow.getColor();
            if(TextUtils.isEmpty(color))
                color = WHITE_COLOR;
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.listview_sqlite_table_row, parent, false);
                viewHolder.linearLayoutCompat = (LinearLayoutCompat) convertView.findViewById(R.id.linear_layout);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.textView.setOnClickListener(mEditClickListener);
                viewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.remove_button);
                viewHolder.imageButton.setOnClickListener(mRemoveClickListener);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.linearLayoutCompat.setBackgroundColor((int) Long.parseLong(color, 16));
            viewHolder.textView.setText(sqliteRow.getData());
            viewHolder.textView.setTag(sqliteRow);
            viewHolder.imageButton.setTag(sqliteRow);
            return convertView;
        }
    }

    public static class SQLiteRow implements Serializable {
        private long mId;
        private String mData;
        private String mColor;
        public SQLiteRow(long id, String data, String color) {
            mId = id;
            mData = data;
            mColor = color;
        }
        public long getId() {
            return mId;
        }
        public String getData() {
            return mData;
        }
        public void setData(String data) {
            mData = data;
        }
        public String getColor() {
            return mColor;
        }
        public void setColor(String color) {
            mColor = color;
        }
    }
}
