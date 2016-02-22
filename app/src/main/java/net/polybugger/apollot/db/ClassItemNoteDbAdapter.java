package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassItemNoteDbAdapter {

    public static final String TABLE_NAME = "ClassItemNotes_";

    public static final DbColumn NOTE_ID = new DbColumn("NoteAttachmentId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn ITEM_ID = new DbColumn("ItemId", DbColumn.TYPE_INTEGER);
    public static final DbColumn NOTE = new DbColumn("Description", DbColumn.TYPE_TEXT);
    public static final DbColumn DATE_CREATED = new DbColumn("DateCreated", DbColumn.TYPE_DATETIME);

    public static final String SDF_DB_TEMPLATE = "yyyy-MM-dd";
    public static final String SDF_DISPLAY_TEMPLATE = "MMM d, yyyy";

    public static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS ";
    public static final String CREATE_TABLE_SQL2 = " (" +
            NOTE_ID.getName() + " " + NOTE_ID.getType() + " PRIMARY KEY, " +
            CLASS_ID.getName() + " " + CLASS_ID.getType() + " NOT NULL REFERENCES " +
            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
            ITEM_ID.getName() + " " + ITEM_ID.getType() + " NOT NULL REFERENCES ";
    public static final String CREATE_TABLE_SQL3 = " (" + ClassItemDbAdapter.ITEM_ID.getName() + "), " +
            NOTE.getName() + " " + NOTE.getType() + " NOT NULL, " +
            DATE_CREATED.getName() + " " + DATE_CREATED.getType() + " NOT NULL)";

    private ClassItemNoteDbAdapter() { }

    public static long _insert(SQLiteDatabase db, long classId, long itemId, String note, Date dateCreated) {
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + CREATE_TABLE_SQL3);
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(ITEM_ID.getName(), itemId);
        values.put(NOTE.getName(), note);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fDateCreated = null;
        if(dateCreated != null)
            fDateCreated = sdf.format(dateCreated);
        values.put(DATE_CREATED.getName(), fDateCreated);
        return db.insert(tableName1, null, values);
    }

    public static long insert(long classId, long itemId, String note, Date dateCreated) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, itemId, note, dateCreated);
        ApolloDbAdapter.close();
        return id;
    }


    public static int delete(long classId, long itemId, long noteId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=? AND " + NOTE_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(itemId), String.valueOf(noteId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int delete(long classId, long itemId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(itemId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int update(long classId, long itemId, long noteId, String note, Date dateCreated) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ContentValues values = new ContentValues();
        values.put(NOTE.getName(), note);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fDateCreated = null;
        if(dateCreated != null)
            fDateCreated = sdf.format(dateCreated);
        values.put(DATE_CREATED.getName(), fDateCreated);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(tableName, values, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=? AND " + NOTE_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(itemId), String.valueOf(noteId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static ArrayList<ClassItemNote> getClassItemNotes(long classId, long itemId) {
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        ArrayList<ClassItemNote> list = new ArrayList<ClassItemNote>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date dateCreated;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + CREATE_TABLE_SQL3);
        Cursor cursor = db.query(tableName1, new String[] {NOTE_ID.getName(), // 0
                        NOTE.getName(), // 1
                        DATE_CREATED.getName()}, // 2
                CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=?", new String[] {String.valueOf(classId), String.valueOf(itemId)}, null, null, "date(" + DATE_CREATED.getName() + ") DESC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            try {
                dateCreated = sdf.parse(cursor.getString(2));
            }
            catch(Exception e) {
                dateCreated = null;
            }
            list.add(new ClassItemNote(classId, itemId, cursor.getLong(0), cursor.getString(1), dateCreated));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    @SuppressWarnings("serial")
    public static class ClassItemNote extends ClassNoteDbAdapter.ClassNote implements Serializable {

        protected long mItemId;

        public ClassItemNote(ClassNoteDbAdapter.ClassNote classNote, long classId, long itemId) {
            super(classNote.getClassId(), classNote.getNoteId(), classNote.getNote(), classNote.getDateCreated());
            mClassId = classId;
            mItemId = itemId;
        }

        public ClassItemNote(long classId, long itemId, long noteId, String note, Date dateCreated) {
            super(classId, noteId, note, dateCreated);
            mItemId = itemId;
        }
        public long getItemId() {
            return mItemId;
        }
        public void setItemId(long itemId) {
            if(mItemId == -1)
                mItemId = itemId;
        }

        @Override
        public boolean equals(Object object) {
            ClassItemNote classItemNote;
            if(object != null) {
                try {
                    classItemNote = (ClassItemNote) object;
                    if((classItemNote.mClassId == mClassId) && (classItemNote.mItemId == mItemId) && (classItemNote.mNoteId == mNoteId))
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ClassItemNote.class.toString());
                }
            }
            return false;
        }
    }
}
