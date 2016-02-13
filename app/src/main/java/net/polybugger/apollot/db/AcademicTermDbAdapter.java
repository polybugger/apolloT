package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class AcademicTermDbAdapter {

    public static String TABLE_NAME = "AcademicTerms";
    public static DbColumn ACADEMIC_TERM_ID = new DbColumn("AcademicTermId", DbColumn.TYPE_INTEGER);
    public static DbColumn DESCRIPTION = new DbColumn("Description", DbColumn.TYPE_TEXT);

    public static long _insert(SQLiteDatabase db, String description) {
        ContentValues values = new ContentValues();
        values.put(DESCRIPTION.getName(), description);
        return db.insert(TABLE_NAME, null, values);
    }

    public static void setTableName(String tableName) {
        TABLE_NAME = tableName;
    }

    public static void setIdColumnName(String idColumnName) {
        ACADEMIC_TERM_ID = new DbColumn(idColumnName, DbColumn.TYPE_INTEGER);
    }

    public static void setDataColumnName(String dataColumnName) {
        DESCRIPTION = new DbColumn(dataColumnName, DbColumn.TYPE_TEXT);
    }

    private AcademicTermDbAdapter() { }

    public static ArrayList<AcademicTerm> getList() {
        ArrayList<AcademicTerm> list = new ArrayList<>();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            list.add(new AcademicTerm(cursor.getLong(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    @SuppressWarnings("serial")
    public static class AcademicTerm implements Serializable {

        private long mId;
        private String mDescription;

        public AcademicTerm(long id, String description) {
            mId = id;
            mDescription = description;
        }

        public long getId() {
            return mId;
        }

        public String getDescription() {
            return mDescription;
        }

        @Override
        public String toString() {
            return mDescription;
        }

        public boolean equals(AcademicTerm academicTerm) {
            if(academicTerm != null && academicTerm.mId == mId)
                return true;
            return false;
        }

        @Override
        public boolean equals(Object object) {
            AcademicTerm academicTerm;
            if(object != null) {
                try {
                    academicTerm = (AcademicTerm) object;
                    if(academicTerm.mId == mId)
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + AcademicTerm.class.toString());
                }
            }
            return false;
        }
    }
}
