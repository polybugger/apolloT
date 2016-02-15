package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.polybugger.apollot.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassDbAdapter {

    public static final String TABLE_NAME = "Classes";

    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CODE = new DbColumn("Code", DbColumn.TYPE_TEXT);
    public static final DbColumn DESCRIPTION = new DbColumn("Description", DbColumn.TYPE_TEXT);
    public static final DbColumn ACADEMIC_TERM_ID = new DbColumn("AcademicTermId", DbColumn.TYPE_INTEGER);
    public static final DbColumn YEAR = new DbColumn("Year", DbColumn.TYPE_TEXT);
    public static final DbColumn CURRENT = new DbColumn("Current", DbColumn.TYPE_BOOLEAN);

    private ClassDbAdapter() { }

    public static long _insert(SQLiteDatabase db, String code, String description, long academicTermId, String year, boolean current) {
        ContentValues values = new ContentValues();
        values.put(CODE.getName(), code);
        values.put(DESCRIPTION.getName(), description);
        values.put(ACADEMIC_TERM_ID.getName(), academicTermId);
        values.put(YEAR.getName(), year);
        values.put(CURRENT.getName(), (current ? 1 : 0));
        return db.insert(TABLE_NAME, null, values);
    }

    public static long insert(String code, String description, long academicTermId, String year, boolean current) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long classId = _insert(db, code, description, academicTermId, year, current);
        ApolloDbAdapter.close();
        return classId;
    }

    public static int update(long classId, String code, String description, long academicTermId, String year, boolean current) {
        ContentValues values = new ContentValues();
        values.put(CODE.getName(), code);
        values.put(DESCRIPTION.getName(), description);
        values.put(ACADEMIC_TERM_ID.getName(), academicTermId);
        values.put(YEAR.getName(), year);
        values.put(CURRENT.getName(), current ? 1 : 0);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(TABLE_NAME, values, CLASS_ID.getName() + "=?", new String[]{String.valueOf(classId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static int getClassesCount(boolean current) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(TABLE_NAME, new String[]{"COUNT(" + CLASS_ID.getName() + ")"}, CURRENT.getName() + "=?", new String[]{(current ? "1" : "0")}, null, null, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        ApolloDbAdapter.close();
        return count;
    }

    public static ArrayList<Class> getClasses(boolean current) {
        long classId, prevClassId = -1; boolean locked = false;
        Date timeStart, timeEnd; AcademicTermDbAdapter.AcademicTerm academicTerm; ClassScheduleDbAdapter.ClassSchedule classSchedule;
        SimpleDateFormat sdf = new SimpleDateFormat(ClassScheduleDbAdapter.SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        ArrayList<Class> classes = new ArrayList<Class>();
        final String sql = "SELECT c." + CLASS_ID.getName() + // 0
                ", " + CODE.getName() + // 1
                ", c." + DESCRIPTION.getName() + // 2
                ", at." + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() + // 3
                ", at." + AcademicTermDbAdapter.DESCRIPTION.getName() + // 4
                " AS AcademicTerm, " + YEAR.getName() + // 5
                ", " + ClassScheduleDbAdapter.SCHEDULE_ID.getName() + // 6
                ", " + ClassScheduleDbAdapter.TIME_START.getName() + // 7
                ", " + ClassScheduleDbAdapter.TIME_END.getName() + // 8
                ", " + ClassScheduleDbAdapter.DAYS.getName() + // 9
                ", " + ClassScheduleDbAdapter.ROOM.getName() + // 10
                ", " + ClassScheduleDbAdapter.BUILDING.getName() + // 11
                ", " + ClassScheduleDbAdapter.CAMPUS.getName() + // 12
                ", " + ClassPasswordDbAdapter.PASSWORD.getName() + // 13
                " FROM " + TABLE_NAME + " AS c LEFT OUTER JOIN " + AcademicTermDbAdapter.TABLE_NAME +
                " AS at ON c." + ACADEMIC_TERM_ID.getName() + "=at." + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() +
                " LEFT OUTER JOIN " + ClassScheduleDbAdapter.TABLE_NAME +
                " AS cs ON c." + CLASS_ID.getName() + "=cs." + ClassScheduleDbAdapter.CLASS_ID.getName() +
                " LEFT OUTER JOIN " + ClassPasswordDbAdapter.TABLE_NAME +
                " AS cp ON c." + CLASS_ID.getName() + "=cp." + ClassPasswordDbAdapter.CLASS_ID.getName() +
                " WHERE " + CURRENT.getName() + "=" + (current ? "1" : "0") +
                " ORDER BY c." + CLASS_ID.getName() + ", cs." + ClassScheduleDbAdapter.SCHEDULE_ID.getName();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            classId = cursor.getLong(0);
            if(classId != prevClassId) {
                try {
                    timeStart = sdf.parse(cursor.getString(7));
                }
                catch(Exception e) {
                    timeStart = null;
                }
                try {
                    timeEnd = sdf.parse(cursor.getString(8));
                }
                catch(Exception e) {
                    timeEnd = null;
                }
                if(cursor.isNull(3))
                    academicTerm = null;
                else
                    academicTerm = new AcademicTermDbAdapter.AcademicTerm(cursor.getLong(3), cursor.getString(4));
                if(cursor.isNull(6))
                    classSchedule = null;
                else
                    classSchedule = new ClassScheduleDbAdapter.ClassSchedule(classId, cursor.getLong(6), timeStart, timeEnd, cursor.getInt(9), cursor.getString(10), cursor.getString(11), cursor.getString(12));
                locked = cursor.isNull(13) ? false : true;
                classes.add(new Class(classId, cursor.getString(1), cursor.getString(2), academicTerm, cursor.getString(5), current, classSchedule, locked));
            }
            else {
                classes.get(classes.size() - 1).setMultiSchedule(true);
            }
            prevClassId = classId;
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return classes;
    }

    public static Class getClass(long classId) {
        long prevClassId = -1; boolean locked = false;
        Date timeStart, timeEnd; AcademicTermDbAdapter.AcademicTerm academicTerm; ClassScheduleDbAdapter.ClassSchedule classSchedule;
        SimpleDateFormat sdf = new SimpleDateFormat(ClassScheduleDbAdapter.SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Class class_ = null;
        final String sql = "SELECT c." + CLASS_ID.getName() + // 0
                ", " + CODE.getName() + // 1
                ", c." + DESCRIPTION.getName() + // 2
                ", at." + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() + // 3
                ", at." + AcademicTermDbAdapter.DESCRIPTION.getName() + // 4
                " AS AcademicTerm, " + YEAR.getName() + // 5
                ", " + CURRENT.getName() + // 6
                ", " + ClassScheduleDbAdapter.SCHEDULE_ID.getName() + // 7
                ", " + ClassScheduleDbAdapter.TIME_START.getName() + // 8
                ", " + ClassScheduleDbAdapter.TIME_END.getName() + // 9
                ", " + ClassScheduleDbAdapter.DAYS.getName() + // 10
                ", " + ClassScheduleDbAdapter.ROOM.getName() + // 11
                ", " + ClassScheduleDbAdapter.BUILDING.getName() + // 12
                ", " + ClassScheduleDbAdapter.CAMPUS.getName() + // 13
                ", " + ClassPasswordDbAdapter.PASSWORD.getName() + // 14
                " FROM " + TABLE_NAME + " AS c LEFT OUTER JOIN " + AcademicTermDbAdapter.TABLE_NAME +
                " AS at ON c." + ACADEMIC_TERM_ID.getName() + "=at." + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() +
                " LEFT OUTER JOIN " + ClassScheduleDbAdapter.TABLE_NAME +
                " AS cs ON c." + CLASS_ID.getName() + "=cs." + ClassScheduleDbAdapter.CLASS_ID.getName() +
                " LEFT OUTER JOIN " + ClassPasswordDbAdapter.TABLE_NAME +
                " AS cp ON c." + CLASS_ID.getName() + "=cp." + ClassPasswordDbAdapter.CLASS_ID.getName() +
                " WHERE c." + CLASS_ID.getName() + "=" + String.valueOf(classId) +
                " ORDER BY c." + CLASS_ID.getName() + ", cs." + ClassScheduleDbAdapter.SCHEDULE_ID.getName();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            classId = cursor.getLong(0);
            if(classId != prevClassId) {
                try {
                    timeStart = sdf.parse(cursor.getString(8));
                }
                catch(Exception e) {
                    timeStart = null;
                }
                try {
                    timeEnd = sdf.parse(cursor.getString(9));
                }
                catch(Exception e) {
                    timeEnd = null;
                }
                if(cursor.isNull(3))
                    academicTerm = null;
                else
                    academicTerm = new AcademicTermDbAdapter.AcademicTerm(cursor.getLong(3), cursor.getString(4));
                if(cursor.isNull(7))
                    classSchedule = null;
                else
                    classSchedule = new ClassScheduleDbAdapter.ClassSchedule(classId, cursor.getLong(7), timeStart, timeEnd, cursor.getInt(10), cursor.getString(11), cursor.getString(12), cursor.getString(13));
                locked = cursor.isNull(14) ? false : true;
                class_ = new Class(classId, cursor.getString(1), cursor.getString(2), academicTerm, cursor.getString(5), cursor.getInt(6) == 0 ? false : true, classSchedule, locked);
            }
            else {
                class_.setMultiSchedule(true);
            }
            prevClassId = classId;
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return class_;
    }

    @SuppressWarnings("serial")
    public static class Class implements Serializable {

        private long mClassId;
        private String mCode;
        private String mDescription;
        private AcademicTermDbAdapter.AcademicTerm mAcademicTerm;
        private String mYear;
        private boolean mCurrent;
        private ClassScheduleDbAdapter.ClassSchedule mFirstClassSchedule;
        private boolean mMultiSchedule = false;
        private boolean mLocked = false;

        public Class(long classId, String code, String description, AcademicTermDbAdapter.AcademicTerm academicTerm, String year, boolean current) {
            mClassId = classId;
            mCode = code;
            mDescription = description;
            mAcademicTerm = academicTerm;
            mYear = year;
            mCurrent = current;
        }

        public Class(long classId, String code, String description, AcademicTermDbAdapter.AcademicTerm academicTerm, String year, boolean current, ClassScheduleDbAdapter.ClassSchedule classSchedule, boolean locked) {
            mClassId = classId;
            mCode = code;
            mDescription = description;
            mAcademicTerm = academicTerm;
            mYear = year;
            mCurrent = current;
            mFirstClassSchedule = classSchedule;
            mLocked = locked;
        }

        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }

        public String getCode() {
            return mCode;
        }
        public void setCode(String code) {
            mCode = code;
        }

        public String getDescription() {
            return mDescription;
        }
        public void setDescription(String description) {
            mDescription = description;
        }

        public AcademicTermDbAdapter.AcademicTerm getAcademicTerm() {
            return mAcademicTerm;
        }
        public void setAcademicTerm(AcademicTermDbAdapter.AcademicTerm academicTerm) {
            mAcademicTerm = academicTerm;
        }

        public String getYear() {
            return mYear;
        }
        public void setYear(String year) {
            mYear = year;
        }

        public boolean isCurrent() {
            return mCurrent;
        }
        public void setCurrent(boolean current) {
            mCurrent = current;
        }

        public String getTitle() {
            if(TextUtils.isEmpty(mDescription))
                return mCode;
            else
                return mCode + " - " + mDescription;
        }

        public String getAcademicTermYear() {
            String academicTerm = "";
            if(mAcademicTerm != null)
                academicTerm = mAcademicTerm.getDescription();
            boolean inbA = !TextUtils.isEmpty(academicTerm);
            boolean inbY = !TextUtils.isEmpty(mYear);
            if(inbA && inbY) {
                return (academicTerm + " " + mYear);
            }
            else if(inbY) {
                return mYear;
            }
            return academicTerm;
        }

        public void setMultiSchedule(boolean multiSchedule) {
            mMultiSchedule = multiSchedule;
        }

        public String getSchedule() {
            if(mFirstClassSchedule != null) {
                String schedule = mFirstClassSchedule.getTime();
                String location = mFirstClassSchedule.getLocation();
                boolean inbS = !TextUtils.isEmpty(schedule);
                boolean inbL = !TextUtils.isEmpty(location);
                if(inbS && inbL) {
                    schedule = schedule + " " + location;
                }
                else if(inbL) {
                    schedule = location;
                }

                return (schedule + (mMultiSchedule ? " ..." : ""));
            }
            return null;
        }

        public String getCurrent() {
            Context context = ApolloDbAdapter.getAppContext();
            return (mCurrent ? context.getString(R.string.current) : context.getString(R.string.past));
        }

        public boolean isLocked() {
            return mLocked;
        }
        public void setLocked(boolean locked) {
            mLocked = locked;
        }

        @Override
        public boolean equals(Object object) {
            Class class_;
            if(object != null) {
                try {
                    class_ = (Class) object;
                    if(class_.mClassId == mClassId)
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + Class.class.toString());
                }
            }
            return false;
        }
    }

}
