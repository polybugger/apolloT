package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassItemDbAdapter {

    public static final String TABLE_NAME = "ClassItems_";

    public static final DbColumn ITEM_ID = new DbColumn("ItemId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn DESCRIPTION = new DbColumn("Description", DbColumn.TYPE_TEXT);
    public static final DbColumn ITEM_TYPE_ID = new DbColumn("ItemTypeId", DbColumn.TYPE_INTEGER);
    public static final DbColumn ITEM_DATE = new DbColumn("ItemDate", DbColumn.TYPE_DATETIME);
    public static final DbColumn CHECK_ATTENDANCE = new DbColumn("CheckAttendance", DbColumn.TYPE_BOOLEAN);
    public static final DbColumn RECORD_SCORES = new DbColumn("RecordScores", DbColumn.TYPE_BOOLEAN);
    public static final DbColumn PERFECT_SCORE = new DbColumn("PerfectScore", DbColumn.TYPE_REAL);
    public static final DbColumn RECORD_SUBMISSIONS = new DbColumn("RecordSubmissions", DbColumn.TYPE_BOOLEAN);
    public static final DbColumn SUBMISSION_DUE_DATE = new DbColumn("SubmissionDueDate", DbColumn.TYPE_DATETIME);

    public static final String SDF_DB_TEMPLATE = "yyyy-MM-dd";
    public static final String SDF_DISPLAY_TEMPLATE = "MMM d, yyyy";

    public static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS ";
    public static final String CREATE_TABLE_SQL2 = " (" +
            ITEM_ID.getName() + " " + ITEM_ID.getType() + " PRIMARY KEY, " +
            CLASS_ID.getName() + " " + CLASS_ID.getType() + " NOT NULL REFERENCES " +
            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
            DESCRIPTION.getName() + " " + DESCRIPTION.getType() + " NOT NULL, " +
            ITEM_TYPE_ID.getName() + " " + ITEM_TYPE_ID.getType() + " NULL REFERENCES " +
            ClassItemTypeDbAdapter.TABLE_NAME + " (" + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + "), " +
            ITEM_DATE.getName() + " " + ITEM_DATE.getType() + " NULL, " +
            CHECK_ATTENDANCE.getName() + " " + CHECK_ATTENDANCE.getType() + " NOT NULL DEFAULT 0, " +
            RECORD_SCORES.getName() + " " + RECORD_SCORES.getType() + " NOT NULL DEFAULT 0, " +
            PERFECT_SCORE.getName() + " " + PERFECT_SCORE.getType() + " NULL, " +
            RECORD_SUBMISSIONS.getName() + " " + RECORD_SUBMISSIONS.getType() + " NOT NULL DEFAULT 0, " +
            SUBMISSION_DUE_DATE.getName() + " " + SUBMISSION_DUE_DATE.getType() + " NULL)";

    private ClassItemDbAdapter() { }

    public static long _insert(SQLiteDatabase db, long classId, String description, long itemTypeId, Date itemDate, boolean checkAttendance, boolean recordScores, Float perfectScore, boolean recordSubmissions, Date submissionDueDate) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(DESCRIPTION.getName(), description);
        values.put(ITEM_TYPE_ID.getName(), itemTypeId);
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fItemDate = null;
        if(itemDate != null)
            fItemDate = sdf.format(itemDate);
        values.put(ITEM_DATE.getName(), fItemDate);
        values.put(CHECK_ATTENDANCE.getName(), checkAttendance ? 1 : 0);
        values.put(RECORD_SCORES.getName(), recordScores ? 1 : 0);
        values.put(PERFECT_SCORE.getName(), perfectScore);
        values.put(RECORD_SUBMISSIONS.getName(), recordSubmissions ? 1 : 0);
        String fSubmissionDueDate = null;
        if(submissionDueDate != null)
            fSubmissionDueDate = sdf.format(submissionDueDate);
        values.put(SUBMISSION_DUE_DATE.getName(), fSubmissionDueDate);
        return db.insert(tableName, null, values);
    }

    public static long insert(long classId, String description, long itemTypeId, Date itemDate, boolean checkAttendance, boolean recordScores, Float perfectScore, boolean recordSubmissions, Date submissionDueDate) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, description, itemTypeId, itemDate, checkAttendance, recordScores, perfectScore, recordSubmissions, submissionDueDate);
        ApolloDbAdapter.close();
        return id;
    }

    public static int delete(long classId, long itemId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(itemId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int update(long classId, long itemId, String description, long itemTypeId, Date itemDate, boolean checkAttendance, boolean recordScores, Float perfectScore, boolean recordSubmissions, Date submissionDueDate) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ContentValues values = new ContentValues();
        values.put(DESCRIPTION.getName(), description);
        values.put(ITEM_TYPE_ID.getName(), itemTypeId);
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fItemDate = null;
        if(itemDate != null)
            fItemDate = sdf.format(itemDate);
        values.put(ITEM_DATE.getName(), fItemDate);
        values.put(CHECK_ATTENDANCE.getName(), checkAttendance ? 1 : 0);
        values.put(RECORD_SCORES.getName(), recordScores ? 1 : 0);
        values.put(PERFECT_SCORE.getName(), perfectScore);
        values.put(RECORD_SUBMISSIONS.getName(), recordSubmissions ? 1 : 0);
        String fSubmissionDueDate = null;
        if(submissionDueDate != null)
            fSubmissionDueDate = sdf.format(submissionDueDate);
        values.put(SUBMISSION_DUE_DATE.getName(), fSubmissionDueDate);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(tableName, values, CLASS_ID.getName() + "=? AND " + ITEM_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(itemId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static ArrayList<ClassItem> getClassItems(long classId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ArrayList<ClassItem> classItems = new ArrayList<ClassItem>();
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date itemDate, submissionDueDate; ClassItemTypeDbAdapter.ItemType itemType;
        final String sql = "SELECT " + ITEM_ID.getName() + // 0
                ", ci." + DESCRIPTION.getName() + // 1
                ", it." + ITEM_TYPE_ID.getName() + // 2
                ", it." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + // 3
                " AS ItemType, " + ITEM_DATE.getName() + // 4
                ", " + CHECK_ATTENDANCE.getName() + // 5
                ", " + RECORD_SCORES.getName() + // 6
                ", " + PERFECT_SCORE.getName() + // 7
                ", " + RECORD_SUBMISSIONS.getName() + // 8
                ", " + SUBMISSION_DUE_DATE.getName() + // 9
                " FROM " + tableName +
                " AS ci LEFT OUTER JOIN " + ClassItemTypeDbAdapter.TABLE_NAME +
                " AS it ON ci." + ITEM_TYPE_ID.getName() + "=it." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName();
        //+ " ORDER BY date(" + ITEM_DATE.getName() + ") ASC, ci." + DESCRIPTION.getName() + ", it." + ClassItemTypeDbAdapter.DESCRIPTION.getName();
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if(cursor.isNull(2))
                itemType = null;
            else
                itemType = new ClassItemTypeDbAdapter.ItemType(cursor.getLong(2), cursor.getString(3));
            try {
                itemDate = sdf.parse(cursor.getString(4));
            }
            catch(Exception e) {
                itemDate = null;
            }
            try {
                submissionDueDate = sdf.parse(cursor.getString(9));
            }
            catch(Exception e) {
                submissionDueDate = null;

            }
            classItems.add(new ClassItem(classId, cursor.getLong(0), cursor.getString(1), itemType, itemDate, cursor.getInt(5) == 0 ? false : true,
                    cursor.getInt(6) == 0 ? false : true, cursor.isNull(7) ? null : cursor.getFloat(7), cursor.getInt(8) == 0 ? false : true, submissionDueDate));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return classItems;
    }

    public static ClassItem getClassItem(long classId, long itemId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ClassItem classItem = null;
        final SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date itemDate, submissionDueDate; ClassItemTypeDbAdapter.ItemType itemType;
        final String sql = "SELECT ci." + DESCRIPTION.getName() + // 0
                ", it." + ITEM_TYPE_ID.getName() + // 1
                ", it." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + // 2
                " AS ItemType, " + ITEM_DATE.getName() + // 3
                ", " + CHECK_ATTENDANCE.getName() + // 4
                ", " + RECORD_SCORES.getName() + // 5
                ", " + PERFECT_SCORE.getName() + // 6
                ", " + RECORD_SUBMISSIONS.getName() + // 7
                ", " + SUBMISSION_DUE_DATE.getName() + // 8
                " FROM " + tableName +
                " AS ci LEFT OUTER JOIN " + ClassItemTypeDbAdapter.TABLE_NAME +
                " AS it ON ci." + ITEM_TYPE_ID.getName() + "=it." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() +
                " WHERE " + ITEM_ID.getName() + "=" + String.valueOf(itemId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        if(!cursor.isAfterLast()) {
            if(cursor.isNull(1))
                itemType = null;
            else
                itemType = new ClassItemTypeDbAdapter.ItemType(cursor.getLong(1), cursor.getString(2));
            try {
                itemDate = sdf.parse(cursor.getString(3));
            }
            catch(Exception e) {
                itemDate = null;
            }
            try {
                submissionDueDate = sdf.parse(cursor.getString(8));
            }
            catch(Exception e) {
                submissionDueDate = null;

            }
            classItem = new ClassItem(classId, itemId, cursor.getString(0), itemType, itemDate, cursor.getInt(4) == 0 ? false : true,
                    cursor.getInt(5) == 0 ? false : true, cursor.isNull(6) ? null : cursor.getFloat(6), cursor.getInt(7) == 0 ? false : true, submissionDueDate);
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return classItem;
    }

    @SuppressWarnings("serial")
    public static class ClassItem implements Serializable {

        private long mClassId;
        private long mItemId;
        private String mDescription;
        private ClassItemTypeDbAdapter.ItemType mItemType;
        private Date mItemDate;
        private boolean mCheckAttendance;
        private boolean mRecordScores;
        private Float mPerfectScore;
        private boolean mRecordSubmissions;
        private Date mSubmissionDueDate;

        private int mPresentCount;
        private int mAbsentCount;
        private int mNullAttendanceCount;
        private int mScoresCount;
        private int mNullScoresCount;
        private int mSubmissionsCount;
        private int mLateCount;
        private int mNullSubmissionsCount;

        public ClassItem(long classId, long itemId, String description, ClassItemTypeDbAdapter.ItemType itemType, Date itemDate, boolean checkAttendance, boolean recordScores, Float perfectScore, boolean recordSubmissions, Date submissionDueDate) {
            mClassId = classId;
            mItemId = itemId;
            mDescription = description;
            mItemType = itemType;
            mItemDate = itemDate;
            mCheckAttendance = checkAttendance;
            mRecordScores = recordScores;
            mPerfectScore = perfectScore;
            mRecordSubmissions = recordSubmissions;
            mSubmissionDueDate = submissionDueDate;
        }
        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }
        public long getItemId() {
            return mItemId;
        }
        public void setItemId(long itemId) {
            if(mItemId == -1)
                mItemId = itemId;
        }
        public String getDescription() {
            return mDescription;
        }
        public void setDescription(String description) {
            mDescription = description;
        }
        public ClassItemTypeDbAdapter.ItemType getItemType() {
            return mItemType;
        }
        public void setItemType(ClassItemTypeDbAdapter.ItemType itemType) {
            mItemType = itemType;
        }
        public Date getItemDate() {
            return mItemDate;
        }
        public void setItemDate(Date itemDate) {
            mItemDate = itemDate;
        }

        public boolean getCheckAttendance() {
            return mCheckAttendance;
        }
        public void setCheckAttendance(boolean checkAttendance) {
            mCheckAttendance = checkAttendance;
        }
        public boolean getRecordScores() {
            return mRecordScores;
        }
        public void setRecordScores(boolean recordScores) {
            mRecordScores = recordScores;
        }
        public Float getPerfectScore() {
            return mPerfectScore;
        }
        public void setPerfectScore(Float perfectScore) {
            mPerfectScore = perfectScore;
        }
        public boolean getRecordSubmissions() {
            return mRecordSubmissions;
        }
        public void setRecordSubmissions(boolean recordSubmissions) {
            mRecordSubmissions = recordSubmissions;
        }
        public Date getSubmissionDueDate() {
            return mSubmissionDueDate;
        }
        public void setSubmissionDueDate(Date submissionDueDate) {
            mSubmissionDueDate = submissionDueDate;
        }
        public void setAttendanceSummary(int presentCount, int absentCount) {
            mPresentCount = presentCount;
            mAbsentCount = absentCount;
        }
        public void setNullAttendanceCount(int nullCount) {
            mNullAttendanceCount = nullCount;
        }
        public int getPresentCount() {
            return mPresentCount;
        }
        public int getAbsentCount() {
            return mAbsentCount;
        }
        public int getNullAttendanceCount() {
            return mNullAttendanceCount;
        }
        public void setScoresCount(int scoresCount) {
            mScoresCount = scoresCount;
        }
        public void setNullScoresCount(int nullCount) {
            mNullScoresCount = nullCount;
        }
        public int getScoresCount() {
            return mScoresCount;
        }
        public int getNullScoresCount() {
            return mNullScoresCount;
        }
        public void setSubmissionsSummary(int submissionsCount, int lateCount) {
            mSubmissionsCount = submissionsCount;
            mLateCount = lateCount;
        }
        public void setNullSubmissionsCount(int nullCount) {
            mNullSubmissionsCount = nullCount;
        }
        public int getSubmissionsCount() {
            return mSubmissionsCount;
        }
        public int getLateCount() {
            return mLateCount;
        }
        public int getNullSubmissionsCount() {
            return mNullSubmissionsCount;
        }
        @Override
        public boolean equals(Object object) {
            ClassItem classItem;
            if(object != null) {
                try {
                    classItem = (ClassItem) object;
                    if((classItem.mClassId == mClassId) && (classItem.mItemId == mItemId))
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ClassItem.class.toString());
                }
            }
            return false;
        }
    }

}
