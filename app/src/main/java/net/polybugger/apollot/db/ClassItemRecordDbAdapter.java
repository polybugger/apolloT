package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassItemRecordDbAdapter {
    
    public static final String TABLE_NAME = "ClassItemRecords_";
    
    public static final DbColumn RECORD_ID = new DbColumn("RecordId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn STUDENT_ID = new DbColumn("StudentId", DbColumn.TYPE_INTEGER);
    public static final DbColumn ITEM_ID = new DbColumn("ItemId", DbColumn.TYPE_INTEGER);
    public static final DbColumn ATTENDANCE = new DbColumn("Attendance", DbColumn.TYPE_BOOLEAN);
    public static final DbColumn SCORE = new DbColumn("Score", DbColumn.TYPE_REAL);
    public static final DbColumn SUBMISSION_DATE = new DbColumn("SubmissionDate", DbColumn.TYPE_DATETIME);
    public static final DbColumn REMARKS = new DbColumn("Remarks", DbColumn.TYPE_TEXT);
    
    public static final String SDF_DB_TEMPLATE = "yyyy-MM-dd";
    public static final String SDF_DISPLAY_TEMPLATE = "MMM d, yyyy";
    
    public static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS ";
    public static final String CREATE_TABLE_SQL2 = " (" + 
            RECORD_ID.getName() + " " + RECORD_ID.getType() + " PRIMARY KEY, " +
            CLASS_ID.getName() + " " + CLASS_ID.getType() + " NOT NULL REFERENCES " +
            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
            STUDENT_ID.getName() + " " + STUDENT_ID.getType() + " NOT NULL REFERENCES ";
    public static final String CREATE_TABLE_SQL3 = " (" + ClassStudentDbAdapter.STUDENT_ID.getName() + "), " +
            ITEM_ID.getName() + " " + ITEM_ID.getType() + " NOT NULL REFERENCES ";
    public static final String CREATE_TABLE_SQL4 = " (" + ClassItemDbAdapter.ITEM_ID.getName() + "), " +
            ATTENDANCE.getName() + " " + ATTENDANCE.getType() + " NULL, " +
            SCORE.getName() + " " + SCORE.getType() + " NULL, " +
            SUBMISSION_DATE.getName() + " " + SUBMISSION_DATE.getType() + " NULL, " +
            REMARKS.getName() + " " + REMARKS.getType() + " NULL, " +
            "UNIQUE (" + STUDENT_ID.getName() + ", " + ITEM_ID.getName() + "))";
    
    private ClassItemRecordDbAdapter() { } 
    
    public static long _insert(SQLiteDatabase db, long classId, long studentId, long itemId, Boolean attendance, Float score, Date submissionDate, String remarks) {
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(STUDENT_ID.getName(), studentId);
        values.put(ITEM_ID.getName(), itemId);
        values.put(ATTENDANCE.getName(), attendance == null ? null : attendance ? 1 : 0);
        values.put(SCORE.getName(), score);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fSubmissionDate = null;
        if(submissionDate != null)
            fSubmissionDate = sdf.format(submissionDate);
        values.put(SUBMISSION_DATE.getName(), fSubmissionDate);
        values.put(REMARKS.getName(), remarks);
        return db.insert(tableName1, null, values); 
    }

    public static long insert(long classId, long studentId, long itemId, Boolean attendance, Float score, Date submissionDate, String remarks) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, studentId, itemId, attendance, score, submissionDate, remarks);
        ApolloDbAdapter.close();
        return id;
    }

    public static int delete(long classId, long studentId, long itemId, long recordId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + STUDENT_ID.getName() + "=? AND " + ITEM_ID.getName() + "=? AND " + RECORD_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(studentId), String.valueOf(itemId), String.valueOf(recordId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int delete(long classId, long itemId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(itemId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }
    
    public static int update(long classId, long studentId, long itemId, long recordId, Boolean attendance, Float score, Date submissionDate, String remarks) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ContentValues values = new ContentValues();
        values.put(ATTENDANCE.getName(), attendance == null ? null : attendance ? 1 : 0);
        values.put(SCORE.getName(), score);
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fSubmissionDate = null;
        if(submissionDate != null)
            fSubmissionDate = sdf.format(submissionDate);
        values.put(SUBMISSION_DATE.getName(), fSubmissionDate);
        values.put(REMARKS.getName(), remarks);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(tableName, values, CLASS_ID.getName() + "=? AND " + STUDENT_ID.getName() + "=? AND " + ITEM_ID.getName() + "=? AND " + RECORD_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(studentId), String.valueOf(itemId), String.valueOf(recordId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }
    
    public static ArrayList<ClassItemRecord> getClassItemRecords(long classId, long itemId) {
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        ArrayList<ClassItemRecord> list = new ArrayList<ClassItemRecord>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date submissionDate; Long recordId; Boolean attendance; Float score;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        final String sql = "SELECT cir." + RECORD_ID.getName() + // 0
                ", " + ClassStudentDbAdapter.ROW_ID.getName() + // 1
                ", s." + StudentDbAdapter.STUDENT_ID.getName() + // 2
                ", " + StudentDbAdapter.LAST_NAME.getName() + // 3
                ", " + StudentDbAdapter.FIRST_NAME.getName() + // 4
                ", " + StudentDbAdapter.MIDDLE_NAME.getName() + // 5
                ", cir." + ATTENDANCE.getName() + // 6
                ", cir." + SCORE.getName() + // 7
                ", cir." + SUBMISSION_DATE.getName() + // 8
                ", cir." + REMARKS.getName() + // 9
                " FROM " + tableName2  +
                " AS cs INNER JOIN " + StudentDbAdapter.TABLE_NAME +
                " AS s ON cs." + ClassStudentDbAdapter.STUDENT_ID.getName() + "=s." + StudentDbAdapter.STUDENT_ID.getName() +
                " LEFT OUTER JOIN (SELECT ci." + ITEM_ID.getName() + 
                ", " + RECORD_ID.getName() + ", " + STUDENT_ID.getName() + ", " + ATTENDANCE.getName() + 
                ", " + SCORE.getName() + ", " + SUBMISSION_DATE.getName() + ", " + REMARKS.getName() +
                " FROM " + tableName3 + " AS ci INNER JOIN " + tableName1 +
                " AS cr ON ci." + ClassItemDbAdapter.ITEM_ID.getName() + "=cr." + ITEM_ID.getName() + 
                " WHERE ci." + ClassItemDbAdapter.ITEM_ID.getName() + "=" + String.valueOf(itemId) +
                ") AS cir ON cs." + ClassStudentDbAdapter.STUDENT_ID.getName() + "=cir." + STUDENT_ID.getName() +
                " ORDER BY " + StudentDbAdapter.LAST_NAME.getName() + " COLLATE NOCASE ASC, " + StudentDbAdapter.FIRST_NAME.getName() + " COLLATE NOCASE ASC";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            recordId = cursor.isNull(0) ? null : cursor.getLong(0);
            attendance = cursor.isNull(6) ? null : cursor.getInt(6) == 0 ? false : true;
            score = cursor.isNull(7) ? null : cursor.getFloat(7);
            try {
                submissionDate = sdf.parse(cursor.getString(8));
            } 
            catch(Exception e) {
                submissionDate = null;
            }
            list.add(new ClassItemRecord(recordId, 
                    new ClassStudentDbAdapter.ClassStudent(cursor.getLong(1), classId,
                            new StudentDbAdapter.Student(cursor.getLong(2), cursor.getString(3), cursor.getString(4), cursor.getString(5)), null),
                    null, attendance, score, submissionDate, cursor.getString(9)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    public static ArrayList<ClassItemRecord> getClassStudentRecords(long classId, long studentId) {
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        ArrayList<ClassItemRecord> list = new ArrayList<ClassItemRecord>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date submissionDate, submissionDueDate, itemDate; Long recordId, studentId_; Boolean attendance; Float score, perfectScore;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        final String sql = "SELECT cir." + RECORD_ID.getName() + // 0
                ", cit." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + // 1
                ", cit." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + // 2
                ", ci." + ClassItemDbAdapter.ITEM_ID.getName() + // 3
                ", ci." + ClassItemDbAdapter.DESCRIPTION.getName() + // 4
                ", ci." + ClassItemDbAdapter.ITEM_DATE.getName() + // 5
                ", ci." + ClassItemDbAdapter.CHECK_ATTENDANCE.getName() + // 6
                ", ci." + ClassItemDbAdapter.RECORD_SCORES.getName() + // 7
                ", ci." + ClassItemDbAdapter.PERFECT_SCORE.getName() + // 8
                ", ci." + ClassItemDbAdapter.RECORD_SUBMISSIONS.getName() + // 9
                ", ci." + ClassItemDbAdapter.SUBMISSION_DUE_DATE.getName() + // 10
                ", cir." + ATTENDANCE.getName() + // 11
                ", cir." + SCORE.getName() + // 12
                ", cir." + SUBMISSION_DATE.getName() + // 13
                ", cir." + REMARKS.getName() + // 14
                ", cit." + ClassItemTypeDbAdapter.COLOR.getName() + // 15
                ", cir." + STUDENT_ID.getName() + // 16
                " FROM " + tableName2  +
                " AS cs INNER JOIN " + tableName3 +
                " AS ci ON cs." + ClassStudentDbAdapter.CLASS_ID.getName() + "=ci." + ClassItemDbAdapter.CLASS_ID.getName() +
                " INNER JOIN " + ClassItemTypeDbAdapter.TABLE_NAME +
                " AS cit ON ci." + ClassItemDbAdapter.ITEM_TYPE_ID.getName() + "=cit." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() +
                " LEFT OUTER JOIN (SELECT * FROM " + tableName1 + " WHERE " + ClassStudentDbAdapter.STUDENT_ID.getName() + "=" + String.valueOf(studentId) +
                ") AS cir ON ci." + ClassItemDbAdapter.ITEM_ID.getName() + "=cir." + ITEM_ID.getName() +
                " WHERE cs." + StudentDbAdapter.STUDENT_ID.getName() + "=" + String.valueOf(studentId);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            recordId = cursor.isNull(0) ? null : cursor.getLong(0);
            attendance = cursor.isNull(11) ? null : cursor.getInt(11) == 0 ? false : true;
            score = cursor.isNull(12) ? null : cursor.getFloat(12);
            perfectScore = cursor.isNull(8) ? null : cursor.getFloat(8);
            try {
                submissionDate = sdf.parse(cursor.getString(13));
            }
            catch(Exception e) {
                submissionDate = null;
            }
            try {
                submissionDueDate = sdf.parse(cursor.getString(10));
            }
            catch(Exception e) {
                submissionDueDate = null;
            }
            try {
                itemDate = sdf.parse(cursor.getString(5));
            }
            catch(Exception e) {
                itemDate = null;
            }
            list.add(new ClassItemRecord(recordId,
                    new ClassStudentDbAdapter.ClassStudent(-1, classId,
                            new StudentDbAdapter.Student(studentId, null, null, null), null),
                    new ClassItemDbAdapter.ClassItem(classId, cursor.getLong(3), cursor.getString(4),
                            new ClassItemTypeDbAdapter.ItemType(cursor.getLong(1), cursor.getString(2), cursor.getString(15)),
                            itemDate, cursor.getInt(6) == 0 ? false : true, cursor.getInt(7) == 0 ? false : true, perfectScore,
                            cursor.getInt(9) == 0 ? false : true, submissionDueDate),
                    attendance, score, submissionDate, cursor.getString(14)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    public static void getAttendanceSummary(ClassItemDbAdapter.ClassItem classItem) {
        long classId = classItem.getClassId(), itemId = classItem.getItemId();
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        int presentCount = 0, absentCount = 0;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + 
                CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        Cursor cursor = db.query(tableName1, new String[] {"COUNT(" + ATTENDANCE.getName() + ")"}, 
                ATTENDANCE.getName() + "=1 AND " + ITEM_ID.getName() + "=" + String.valueOf(itemId), 
                null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            presentCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = db.query(tableName1, new String[] {"COUNT(" + ATTENDANCE.getName() + ")"}, 
                ATTENDANCE.getName() + "=0 AND " + ITEM_ID.getName() + "=" + String.valueOf(itemId), 
                null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            absentCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        classItem.setAttendanceSummary(presentCount, absentCount);
    }

    public static void getScoresCount(ClassItemDbAdapter.ClassItem classItem) {
        long classId = classItem.getClassId(), itemId = classItem.getItemId();
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        int scoresCount = 0;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + 
                CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        Cursor cursor = db.query(tableName1, new String[] {"COUNT(" + SCORE.getName() + ")"}, 
                ITEM_ID.getName() + "=" + String.valueOf(itemId), null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            scoresCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        classItem.setScoresCount(scoresCount);
    }

    public static void getSubmissionsSummary(ClassItemDbAdapter.ClassItem classItem) {
        long classId = classItem.getClassId(), itemId = classItem.getItemId();
        String tableName1 = TABLE_NAME + String.valueOf(classId);
        String tableName2 = ClassStudentDbAdapter.TABLE_NAME + String.valueOf(classId);
        String tableName3 = ClassItemDbAdapter.TABLE_NAME + String.valueOf(classId);
        int submissionsCount = 0, lateCount = 0;
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName1 + CREATE_TABLE_SQL2 + tableName2 + 
                CREATE_TABLE_SQL3 + tableName3 + CREATE_TABLE_SQL4);
        SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date dueDate = classItem.getSubmissionDueDate();
        Cursor cursor = db.query(tableName1, new String[] {"COUNT(" + SUBMISSION_DATE.getName() + ")"}, 
                (dueDate != null ? SUBMISSION_DATE.getName() + "<='" + sdf.format(dueDate) + "' AND " : "") + ITEM_ID.getName() + "=" + String.valueOf(itemId),
                null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            submissionsCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        cursor = db.query(tableName1, new String[] {"COUNT(" + SUBMISSION_DATE.getName() + ")"}, 
                (dueDate != null ? SUBMISSION_DATE.getName() + ">'" + sdf.format(dueDate) + "' AND " : "") + ITEM_ID.getName() + "=" + String.valueOf(itemId),
                null, null, null, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            lateCount = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        classItem.setSubmissionsSummary(submissionsCount, lateCount);
    }
    
    @SuppressWarnings("serial")
    public static class ClassItemRecord implements Serializable {
        
        private Long mRecordId;
        private ClassStudentDbAdapter.ClassStudent mClassStudent;
        private ClassItemDbAdapter.ClassItem mClassItem;
        private Boolean mAttendance;
        private Float mScore;
        private Date mSubmissionDate;
        private String mRemarks;
        
        public ClassItemRecord(Long recordId, ClassStudentDbAdapter.ClassStudent classStudent, ClassItemDbAdapter.ClassItem classItem, Boolean attendance, Float score, Date submissionDate, String remarks) {
            mRecordId = recordId;
            mClassStudent = classStudent;
            mClassItem = classItem;
            mAttendance = attendance;
            mScore = score;
            mSubmissionDate = submissionDate;
            mRemarks = remarks;
        }
        public Long getRecordId() {
            return mRecordId;
        }
        public void setRecordId(Long recordId) {
            if(mRecordId == null)
                mRecordId = recordId;
        }
        public ClassStudentDbAdapter.ClassStudent getClassStudent() {
            return mClassStudent;
        }
        public void setStudent(StudentDbAdapter.Student student) {
            mClassStudent.setStudent(student);
        }
        public ClassItemDbAdapter.ClassItem getClassItem() {
            return mClassItem;
        }
        public Boolean getAttendance() {
            return mAttendance;
        }
        public void setAttendance(Boolean attendance) {
            mAttendance = attendance;
        }
        public Float getScore() {
            return mScore;
        }
        public void setScore(Float score) {
            mScore = score;
        }
        public Date getSubmissionDate() {
            return mSubmissionDate;
        }
        public void setSubmissionDate(Date submissionDate) {
            mSubmissionDate = submissionDate;
        }
        public String getRemarks() {
            return mRemarks;
        }
        public void setRemarks(String remarks) {
            mRemarks = remarks;
        }
        
        @Override
        public boolean equals(Object object) {
            ClassItemRecord classItemRecord;
            if(object != null) {
                try {
                    classItemRecord = (ClassItemRecord) object;
                    if((classItemRecord.getClassItem().getItemId() == getClassItem().getItemId()) && (classItemRecord.mClassStudent.getRowId() == mClassStudent.getRowId()))
                        return true;
                } 
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ClassItemRecord.class.toString());
                }
            }
            return false;
        }
    }
}
