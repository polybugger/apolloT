package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClassPasswordDbAdapter {

    public static final String TABLE_NAME = "ClassPasswords";

    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn PASSWORD = new DbColumn("Password", DbColumn.TYPE_TEXT);

    private ClassPasswordDbAdapter() { }

    public static long _insert(SQLiteDatabase db, long classId, String password) {
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(PASSWORD.getName(), password);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long insert(long classId, String password) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long rowId = _insert(db, classId, password);
        ApolloDbAdapter.close();
        return rowId;
    }

    public static int update(long classId, String password) {
        ContentValues values = new ContentValues();
        values.put(PASSWORD.getName(), password);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(TABLE_NAME, values, CLASS_ID.getName() + "=?", new String[] { String.valueOf(classId) });
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static int delete(long classId) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(TABLE_NAME, CLASS_ID.getName() + "=?", new String[] { String.valueOf(classId) });
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static String getPassword(long classId) {
        String password = null;
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(TABLE_NAME, new String[] { PASSWORD.getName() }, CLASS_ID.getName() + "=?", new String[] { String.valueOf(classId) }, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast())
            password = cursor.getString(0);
        cursor.close();
        ApolloDbAdapter.close();
        return password;
    }
}
