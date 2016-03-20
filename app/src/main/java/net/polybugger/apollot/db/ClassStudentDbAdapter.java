package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import net.polybugger.apollot.R;

public class ClassStudentDbAdapter {
    
    public static final String TABLE_NAME = "ClassStudents_";

    public static final DbColumn ROW_ID = new DbColumn("RowId", DbColumn.TYPE_INTEGER);
    public static final DbColumn STUDENT_ID = new DbColumn("StudentId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn DATE_CREATED = new DbColumn("DateCreated", DbColumn.TYPE_DATETIME);

    public static final String SDF_DB_TEMPLATE = "yyyy-MM-dd";
    public static final String SDF_DISPLAY_TEMPLATE = "MMM d, yyyy";
    
    private static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_SQL2 = " (" +
            ROW_ID.getName() + " " + ROW_ID.getType() + " PRIMARY KEY, " +
            STUDENT_ID.getName() + " " + STUDENT_ID.getType() + " NOT NULL UNIQUE, " +
            CLASS_ID.getName() + " " + CLASS_ID.getType() + " NOT NULL REFERENCES " +
            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
            DATE_CREATED.getName() + " " + DATE_CREATED.getType() +
            " NOT NULL, FOREIGN KEY (" + STUDENT_ID.getName() + ") REFERENCES " +
            StudentDbAdapter.TABLE_NAME + " (" + StudentDbAdapter.STUDENT_ID.getName() + "))";

    private ClassStudentDbAdapter() { }
    
    public static long _insert(SQLiteDatabase db, long classId, long studentId, Date dateCreated) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        ContentValues values = new ContentValues();
        values.put(STUDENT_ID.getName(), studentId);
        values.put(CLASS_ID.getName(), classId);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fDateCreated = null;
        if(dateCreated != null)
            fDateCreated = sdf.format(dateCreated);
        values.put(DATE_CREATED.getName(), fDateCreated);
        return db.insert(tableName, null, values); 
    }
    
    
    public static long insert(long classId, long studentId, Date dateCreated) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, studentId, dateCreated);
        ApolloDbAdapter.close();
        return id;
    }

    public static int delete(long classId, long studentId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + STUDENT_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(studentId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static ArrayList<ClassStudent> getClassStudents(long classId) {
        ArrayList<ClassStudent> classStudents = new ArrayList<ClassStudent>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date dateCreated; ClassStudent classStudent;
        long rowId, prevRowId = -1;

        String classStudentsTable = TABLE_NAME + String.valueOf(classId);
        String classItemsTable = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        String classItemRecordsTable = ClassItemRecordDbAdapter.TABLE_NAME + String.valueOf(classId);

        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + classStudentsTable + CREATE_TABLE_SQL2);
        db.execSQL(ClassItemDbAdapter.CREATE_TABLE_SQL1 + classItemsTable + ClassItemDbAdapter.CREATE_TABLE_SQL2);
        db.execSQL(ClassItemRecordDbAdapter.CREATE_TABLE_SQL1 + classItemRecordsTable + ClassItemRecordDbAdapter.CREATE_TABLE_SQL2 + classStudentsTable + ClassItemRecordDbAdapter.CREATE_TABLE_SQL3 + classItemsTable + ClassItemRecordDbAdapter.CREATE_TABLE_SQL4);

        // TODO Please reference check attendance from class item table for accurately counting absences

        final String sql = "SELECT " + ROW_ID.getName() + // 0
                ", s." + STUDENT_ID.getName() + // 1
                ", " + StudentDbAdapter.LAST_NAME.getName() + // 2 
                ", " + StudentDbAdapter.FIRST_NAME.getName() + // 3
                ", " + StudentDbAdapter.MIDDLE_NAME.getName() + // 4
                ", " + StudentDbAdapter.GENDER.getName() + // 5
                ", " + StudentDbAdapter.EMAIL_ADDRESS.getName() + // 6 
                ", " + StudentDbAdapter.CONTACT_NO.getName() + // 7
                ", " + DATE_CREATED.getName() + // 8
                ", cir." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + // 9
                ", cir." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + // 10
                ", cir." + ClassItemRecordDbAdapter.ATTENDANCE.getName() + // 11
                ", cir." + ClassItemRecordDbAdapter.SCORE.getName() + // 12
                ", cir." + ClassItemTypeDbAdapter.COLOR.getName() + // 13
                ", cir." + ClassItemDbAdapter.CHECK_ATTENDANCE.getName() + // 14
                " FROM " + classStudentsTable +
                " AS cs INNER JOIN " + StudentDbAdapter.TABLE_NAME + 
                " AS s ON cs." + STUDENT_ID.getName() + "=s." + StudentDbAdapter.STUDENT_ID.getName() +
                " LEFT OUTER JOIN (SELECT cit." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + ", cit." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + ", cit." + ClassItemTypeDbAdapter.COLOR.getName() +
                ", " + ClassItemRecordDbAdapter.STUDENT_ID.getName() + ", " + ClassItemRecordDbAdapter.ATTENDANCE.getName() + ", " + ClassItemRecordDbAdapter.SCORE.getName() + ", " + ClassItemDbAdapter.CHECK_ATTENDANCE.getName() +
                " FROM " + classItemsTable + " AS ci INNER JOIN " + classItemRecordsTable + " AS cr ON ci." + ClassItemDbAdapter.ITEM_ID.getName() + "=cr." + ClassItemRecordDbAdapter.ITEM_ID.getName() +
                " LEFT OUTER JOIN " + ClassItemTypeDbAdapter.TABLE_NAME + " AS cit ON ci." + ClassItemDbAdapter.ITEM_TYPE_ID.getName() + "=cit." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + 
                ") AS cir ON cs." + STUDENT_ID.getName() + "=cir." + ClassItemRecordDbAdapter.STUDENT_ID.getName() + 
                " ORDER BY " + StudentDbAdapter.LAST_NAME.getName() + " COLLATE NOCASE ASC, " + StudentDbAdapter.FIRST_NAME.getName() + " COLLATE NOCASE ASC";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            rowId = cursor.getLong(0);
            
            if(rowId != prevRowId) {
                try {
                    dateCreated = sdf.parse(cursor.getString(8));
                } 
                catch(Exception e) {
                    dateCreated = null;
                }
                classStudent = new ClassStudent(cursor.getLong(0), classId,
                        new StudentDbAdapter.Student(cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7)),
                        dateCreated);
                if(!cursor.isNull(11) && (cursor.getInt(14) == 1) && (cursor.getInt(11) == 0))
                    classStudent.addAbsences();
                if(!cursor.isNull(12) && !cursor.isNull(9))
                    classStudent.addItemType(new ClassItemTypeDbAdapter.ItemType(cursor.getLong(9), cursor.getString(10), cursor.getString(13)));
                classStudents.add(classStudent);
            }
            else {
                classStudent = classStudents.get(classStudents.size() - 1);
                if(!cursor.isNull(11) && (cursor.getInt(14) == 1) && (cursor.getInt(11) == 0))
                    classStudent.addAbsences();
                if(!cursor.isNull(12) && !cursor.isNull(9))
                    classStudent.addItemType(new ClassItemTypeDbAdapter.ItemType(cursor.getLong(9), cursor.getString(10), cursor.getString(13)));
            }
            prevRowId = rowId;
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return classStudents;
    }
    
    public static long[] getClassStudentIds(long classId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ArrayList<Long> studentsIds = new ArrayList<Long>();
        final String sql = "SELECT cs." + STUDENT_ID.getName() + // 0
                " FROM " + tableName +
                " AS cs INNER JOIN " + StudentDbAdapter.TABLE_NAME + 
                " AS s ON cs." + STUDENT_ID.getName() + "=s." + StudentDbAdapter.STUDENT_ID.getName();
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            studentsIds.add(cursor.getLong(0));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        int size = studentsIds.size(); 
        long[] ids = new long[size];
        for(int i = 0; i < size; ++i)
            ids[i] = studentsIds.get(i);
        return ids;
    }
    
    public static int getClassStudentCount(long classId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        int count = 0;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        Cursor cursor = db.query(tableName, new String[] {"COUNT(" + ROW_ID.getName() + ")"}, 
                null, null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            count = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return count;
    }
    
    @SuppressWarnings("serial")
    public static class ClassStudent implements Serializable {
        
        private long mRowId;
        private long mClassId;
        private StudentDbAdapter.Student mStudent;
        private Date mDateCreated;
        private int mAbsencesCount;
        private ArrayList<ClassStudentItemCount> mItemSummary;
        
        public ClassStudent(long rowId, long classId, StudentDbAdapter.Student student, Date dateCreated) {
            mRowId = rowId;
            mClassId = classId;
            mStudent = student;
            mDateCreated = dateCreated;
            mAbsencesCount = 0;
            mItemSummary = new ArrayList<ClassStudentItemCount>();
        }

        public long getRowId() {
            return mRowId;
        }
        public void setRowId(long rowId) {
            if(mRowId == -1)
                mRowId = rowId;
        }

        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }

        public StudentDbAdapter.Student getStudent() {
            return mStudent;
        }
        public void setStudent(StudentDbAdapter.Student student) {
            mStudent = student;
        }
        public long getStudentId() {
            return mStudent.getStudentId();
        }
        public Date getDateCreated() {
            return mDateCreated;
        }
        
        public String getName() {
            return mStudent.getName();
        }
        public void addAbsences() {
            mAbsencesCount = mAbsencesCount + 1;
        }
        public int getAbsences() {
            return mAbsencesCount;
        }
        public void addItemType(ClassItemTypeDbAdapter.ItemType itemType) {
            for(ClassStudentItemCount itemCount : mItemSummary) {
                if(itemCount.getItemType().equals(itemType)) {
                    itemCount.addCount();
                    return;
                }
            }
            mItemSummary.add(new ClassStudentItemCount(itemType));
        }
        public String getSummary() {
            StringBuilder summary = new StringBuilder();
            if(mAbsencesCount > 0) {
                summary.append(ApolloDbAdapter.getAppContext().getString(R.string.absent_label) + " ");
                summary.append(mAbsencesCount);
            }
            int size = mItemSummary.size();
            if(size > 0) {
                if(summary.length() > 0)
                    summary.append(", ");
                for(ClassStudentItemCount itemCount : mItemSummary) {
                    summary.append(itemCount.getItemType().getDescription() + ": ");
                    summary.append(itemCount.getCount());
                    summary.append(", ");
                }
                int length = summary.length(); 
                summary.delete(length - 2, length);
            }
            return summary.toString();
        }
    }
    
    @SuppressWarnings("serial")
    public static class ClassStudentItemCount implements Serializable {
        
        private ClassItemTypeDbAdapter.ItemType mItemType;
        private int mCount;

        public ClassStudentItemCount(ClassItemTypeDbAdapter.ItemType itemType) {
            mItemType = itemType;
            mCount = 1;
        }
        public ClassItemTypeDbAdapter.ItemType getItemType() {
            return mItemType;
        }
        public int getCount() {
            return mCount;
        }
        public void addCount() {
            mCount = mCount + 1;
        }
    }
}
