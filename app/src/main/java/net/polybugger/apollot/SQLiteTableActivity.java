package net.polybugger.apollot;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.polybugger.apollot.db.ApolloDbAdapter;

import java.util.ArrayList;
import java.util.List;

public class SQLiteTableActivity extends AppCompatActivity {

    public static final String TABLE_NAME_ARG = "net.polybugger.apollot.table_name_arg";
    public static final String ID_COLUMN_ARG = "net.polybugger.apollot.id_column_arg";
    public static final String DATA_COLUMN_ARG = "net.polybugger.apollot.data_column_arg";
    public static final String TITLE_ARG = "net.polybugger.apollot.title_arg";
    public static final String DIALOG_TITLE_ARG = "net.polybugger.apollot.dialog_title_arg";

    private String mTableName;
    private String mIdColumn;
    private String mDataColumn;
    private String mTitle;
    private String mDialogTitle;
    private ListArrayAdapter mListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_table);

        Bundle args = getIntent().getExtras();

        mTableName = args.getString(TABLE_NAME_ARG);
        mIdColumn = args.getString(ID_COLUMN_ARG);
        mDataColumn = args.getString(DATA_COLUMN_ARG);
        mTitle = args.getString(TITLE_ARG);
        mDialogTitle = args.getString(DIALOG_TITLE_ARG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(mTitle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mListAdapter = new ListArrayAdapter(this, getList());
        ((ListView) findViewById(R.id.list_view)).setAdapter(mListAdapter);

    }

    private ArrayList<SQLiteRow> getList() {
        ArrayList<SQLiteRow> list = new ArrayList<>();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(mTableName, new String[] { mIdColumn, mDataColumn }, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            list.add(new SQLiteRow(cursor.getLong(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    private class ListArrayAdapter extends ArrayAdapter<SQLiteRow> {

        private View.OnClickListener mEditClickListener;

        private class ViewHolder {
            TextView textView;
        }

        public ListArrayAdapter(Context context, List<SQLiteRow> objects) {
            super(context, R.layout.listview_sqlite_table_row, R.id.text_view, objects);
            mEditClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    if(mIsAlertDialogShown)
                        return;
                    mIsAlertDialogShown = true;
                    String title = mContext.getString(R.string.edit) + " " + mDialogTitle;
                    SQLiteRow sqliteRow = (SQLiteRow) view.getTag();
                    NewEditDialog d = new NewEditDialog(mContext);
                    d.setDialogArgs(title, "", mContext.getString(R.string.save_button));
                    d.setData(sqliteRow);
                    mCurrentAlertDialog = d.show();
                    */
                }
            };
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SQLiteRow sqliteRow = getItem(position);
            ViewHolder viewHolder;
            if(convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.listview_sqlite_table_row, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view);
                viewHolder.textView.setOnClickListener(mEditClickListener);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textView.setText(sqliteRow.getData());
            viewHolder.textView.setTag(sqliteRow);
            return convertView;
        }
    }


    private class SQLiteRow {
        private long mId;
        private String mData;
        public SQLiteRow(long id, String data) {
            mId = id;
            mData = data;
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
    }
}
