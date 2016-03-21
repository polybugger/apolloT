package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClassScheduleDbAdapter {

    public static final String TABLE_NAME = "ClassSchedules";

    public static final DbColumn SCHEDULE_ID = new DbColumn("ScheduleId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn TIME_START = new DbColumn("TimeStart", DbColumn.TYPE_DATETIME);
    public static final DbColumn TIME_END = new DbColumn("TimeEnd", DbColumn.TYPE_DATETIME);
    public static final DbColumn DAYS = new DbColumn("Days", DbColumn.TYPE_BITS);
    public static final DbColumn ROOM = new DbColumn("Room", DbColumn.TYPE_TEXT);
    public static final DbColumn BUILDING = new DbColumn("Building", DbColumn.TYPE_TEXT);
    public static final DbColumn CAMPUS = new DbColumn("Campus", DbColumn.TYPE_TEXT);

    public static final String SDF_DB_TEMPLATE = "HH:mm:ss";
    public static final String SDF_DISPLAY_TEMPLATE = "hh:mm aa";

    private ClassScheduleDbAdapter () { }

    public static long _insert(SQLiteDatabase db, long classId, Date timeStart, Date timeEnd, int days, String room, String building, String campus) {
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fTimeStart = null, fTimeEnd = null;
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        if(timeStart != null)
            fTimeStart = sdf.format(timeStart);
        values.put(TIME_START.getName(), fTimeStart);
        if(timeEnd != null)
            fTimeEnd = sdf.format(timeEnd);
        values.put(TIME_END.getName(), fTimeEnd);
        values.put(DAYS.getName(), days);
        values.put(ROOM.getName(), room);
        values.put(BUILDING.getName(), building);
        values.put(CAMPUS.getName(), campus);
        return db.insert(TABLE_NAME, null, values);
    }

    public static long insert(long classId, Date timeStart, Date timeEnd, int days, String room, String building, String campus) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long scheduleId = _insert(db, classId, timeStart, timeEnd, days, room, building, campus);
        ApolloDbAdapter.close();
        return scheduleId;
    }

    public static int update(long classId, long scheduleId, Date timeStart, Date timeEnd, int days, String room, String building, String campus) {
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        String fTimeStart = null, fTimeEnd = null;
        if(timeStart != null)
            fTimeStart = sdf.format(timeStart);
        if(timeEnd != null)
            fTimeEnd = sdf.format(timeEnd);
        ContentValues values = new ContentValues();
        values.put(TIME_START.getName(), fTimeStart);
        values.put(TIME_END.getName(), fTimeEnd);
        values.put(DAYS.getName(), days);
        values.put(ROOM.getName(), room);
        values.put(BUILDING.getName(), building);
        values.put(CAMPUS.getName(), campus);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(TABLE_NAME, values, CLASS_ID.getName() + "=? AND " + SCHEDULE_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(scheduleId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static int delete(long classId, long scheduleId) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(TABLE_NAME, CLASS_ID.getName() + "=? AND " + SCHEDULE_ID.getName() + "=?",
                new String[] { String.valueOf(classId), String.valueOf(scheduleId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int delete(long classId) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(TABLE_NAME, CLASS_ID.getName() + "=?",
                new String[]{String.valueOf(classId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static ArrayList<ClassSchedule> getClassSchedules(long classId) {
        ArrayList<ClassSchedule> list = new ArrayList<ClassSchedule>();
        SimpleDateFormat sdf = new SimpleDateFormat(SDF_DB_TEMPLATE, ApolloDbAdapter.getAppContext().getResources().getConfiguration().locale);
        Date timeStart, timeEnd;
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(TABLE_NAME, new String[] {SCHEDULE_ID.getName(), // 0
                        TIME_START.getName(), // 1
                        TIME_END.getName(), // 2
                        DAYS.getName(), // 3
                        ROOM.getName(), // 4
                        BUILDING.getName(), // 5
                        CAMPUS.getName()}, // 6
                CLASS_ID.getName() + "=?", new String[] {String.valueOf(classId)}, null, null, SCHEDULE_ID.getName());
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            try {
                timeStart = sdf.parse(cursor.getString(1));
            }
            catch(Exception e) {
                timeStart = null;
            }
            try {
                timeEnd = sdf.parse(cursor.getString(2));
            }
            catch(Exception e) {
                timeEnd = null;
            }
            list.add(new ClassSchedule(classId, cursor.getLong(0), timeStart, timeEnd, cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    @SuppressWarnings("serial")
    public static class ClassSchedule implements Serializable {

        private long mClassId;
        private long mScheduleId;
        private Date mTimeStart;
        private Date mTimeEnd;
        private int mDays;
        private String mRoom;
        private String mBuilding;
        private String mCampus;

        public ClassSchedule(long classId, long scheduleId, Date timeStart, Date timeEnd, int days, String room, String building, String campus) {
            mClassId = classId;
            mScheduleId = scheduleId;
            mTimeStart = timeStart;
            mTimeEnd = timeEnd;
            mDays = days;
            mRoom = room;
            mBuilding = building;
            mCampus = campus;
        }
        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }
        public long getScheduleId() {
            return mScheduleId;
        }
        public void setScheduleId(long scheduleId) {
            if(mScheduleId == -1)
                mScheduleId = scheduleId;
        }
        public Date getTimeStart() {
            return mTimeStart;
        }
        public void setTimeStart(Date timeStart) {
            mTimeStart = timeStart;
        }
        public Date getTimeEnd() {
            return mTimeEnd;
        }
        public void setTimeEnd(Date timeEnd) {
            mTimeEnd = timeEnd;
        }
        public int getDays() {
            return mDays;
        }
        public void setDays(int days) {
            mDays = days;
        }
        public String getRoom() {
            return mRoom;
        }
        public void setRoom(String room) {
            mRoom = room;
        }
        public String getBuilding() {
            return mBuilding;
        }
        public void setBuilding(String building) {
            mBuilding = building;
        }
        public String getCampus() {
            return mCampus;
        }
        public void setCampus(String campus) {
            mCampus = campus;
        }

        public boolean equals(ClassSchedule schedule) {
            if((schedule != null) && (schedule.mClassId == mClassId) && (schedule.mScheduleId == mScheduleId))
                return true;
            return false;
        }

        @Override
        public boolean equals(Object object) {
            ClassSchedule schedule;
            if(object != null) {
                try {
                    schedule = (ClassSchedule) object;
                    if((schedule.mClassId == mClassId) && (schedule.mScheduleId == mScheduleId))
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ClassSchedule.class.toString());
                }
            }
            return false;
        }

        public String getTime() {
            Context context = ApolloDbAdapter.getAppContext();
            SimpleDateFormat sdf = new SimpleDateFormat(ClassScheduleDbAdapter.SDF_DISPLAY_TEMPLATE, context.getResources().getConfiguration().locale);
            String timeStart = "", timeEnd = "";
            if(mTimeStart != null)
                timeStart = sdf.format(mTimeStart);
            if(mTimeEnd != null)
                timeEnd = sdf.format(mTimeEnd);
            return (timeStart + " - " + timeEnd + " " + DaysBits.intToString(context, mDays));
        }

        public String getLocation() {
            return (mRoom + " " + mBuilding + " (" + mCampus + ")");
        }
    }
}
