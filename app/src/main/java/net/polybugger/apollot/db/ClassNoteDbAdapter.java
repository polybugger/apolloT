package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassNoteDbAdapter {

    public static final String TABLE_NAME = "ClassNotes";

    public static final DbColumn NOTE_ID = new DbColumn("NoteId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn NOTE = new DbColumn("Note", DbColumn.TYPE_TEXT);
    public static final DbColumn DATE_CREATED = new DbColumn("DateCreated", DbColumn.TYPE_DATETIME);

    public static final String SDF_DB_TEMPLATE = "yyyy-MM-dd";
    public static final String SDF_DISPLAY_TEMPLATE = "MMM d, yyyy";

    private ClassNoteDbAdapter() { }

    public static long _insert(SQLiteDatabase db, long classId, String note, Date dateCreated) {
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(NOTE.getName(), note);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fDateCreated = null;
        if(dateCreated != null)
            fDateCreated = sdf.format(dateCreated);
        values.put(DATE_CREATED.getName(), fDateCreated);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long insert(long classId, String note, Date dateCreated) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, note, dateCreated);
        ApolloDbAdapter.close();
        return id;
    }

    public static int delete(long classId, long noteId) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(TABLE_NAME, CLASS_ID.getName() + "=? AND " + NOTE_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(noteId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int update(long classId, long noteId, String note, Date dateCreated) {
        ContentValues values = new ContentValues();
        values.put(NOTE.getName(), note);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fDateCreated = null;
        if(dateCreated != null)
            fDateCreated = sdf.format(dateCreated);
        values.put(DATE_CREATED.getName(), fDateCreated);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(TABLE_NAME, values, CLASS_ID.getName() + "=? AND " + NOTE_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(noteId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static ArrayList<ClassNote> getClassNotes(long classId) {
        ArrayList<ClassNote> list = new ArrayList<ClassNote>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date dateCreated;
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(TABLE_NAME, new String[] {NOTE_ID.getName(), // 0
                        NOTE.getName(), // 1
                        DATE_CREATED.getName()}, // 2
                CLASS_ID.getName() + "=?", new String[] {String.valueOf(classId)}, null, null, "date(" + DATE_CREATED.getName() + ") DESC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            try {
                dateCreated = sdf.parse(cursor.getString(2));
            }
            catch(Exception e) {
                dateCreated = null;
            }
            list.add(new ClassNote(classId, cursor.getLong(0), cursor.getString(1), dateCreated));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    @SuppressWarnings("serial")
    public static class ClassNote implements Serializable {

        protected long mClassId;
        protected long mNoteId;
        protected String mNote;
        protected Date mDateCreated;

        public ClassNote(long classId, long noteId, String note, Date dateCreated) {
            mClassId = classId;
            mNoteId = noteId;
            mNote = note;
            mDateCreated = dateCreated;
        }
        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }
        public long getNoteId() {
            return mNoteId;
        }
        public void setNoteId(long noteId) {
            if(mNoteId == -1)
                mNoteId = noteId;
        }
        public String getNote() {
            return mNote;
        }
        public void setNote(String note) {
            mNote = note;
        }
        public Date getDateCreated() {
            return mDateCreated;
        }
        public void setDateCreated(Date dateCreated) {
            mDateCreated = dateCreated;
        }

        public String getDateNoteText() {
            final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DISPLAY_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
            String dateCreated = "";
            if(mDateCreated != null)
                dateCreated = sdf.format(mDateCreated);
            return (dateCreated + " - " + mNote);
        }
    }
}
