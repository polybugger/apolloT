package net.polybugger.apollot.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.polybugger.apollot.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ApolloDbAdapter {

    public static final String DATABASE_NAME = "ApolloDb.sqlite3";
    public static final int DATABASE_VERSION = 1;

    private static Context sAppContext;
    private static ApolloDbHelper mDbHelper;
    private static int mOpenCounter;
    private static SQLiteDatabase mDb;

    private ApolloDbAdapter() {
    }

    public static void setAppContext(Context context) {
        if (mDbHelper == null) {
            sAppContext = context;
            mDbHelper = new ApolloDbHelper(sAppContext);
        }
    }

    public static Context getAppContext() {
        return sAppContext;
    }

    public static synchronized SQLiteDatabase open() throws SQLException {
        mOpenCounter++;
        if(mOpenCounter == 1) {
            mDb = mDbHelper.getWritableDatabase();
        }
        return mDb;
    }

    public static synchronized void close() {
        mOpenCounter--;
        if(mOpenCounter == 0) {
            mDb.close();
        }
    }

    private static class ApolloDbHelper extends SQLiteOpenHelper {

        public ApolloDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String CREATE_TABLE_ACADEMIC_TERMS =
                    "CREATE TABLE " + AcademicTermDbAdapter.TABLE_NAME + " (" +
                            AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() + " " + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getType() + " PRIMARY KEY, " +
                            AcademicTermDbAdapter.DESCRIPTION.getName() + " " + AcademicTermDbAdapter.DESCRIPTION.getType() + " NOT NULL)";
            db.execSQL(CREATE_TABLE_ACADEMIC_TERMS);

            final String CREATE_TABLE_CLASS_ITEM_TYPES =
                    "CREATE TABLE " + ClassItemTypeDbAdapter.TABLE_NAME + " (" +
                            ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + " " + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getType() + " PRIMARY KEY, " +
                            ClassItemTypeDbAdapter.DESCRIPTION.getName() + " " + ClassItemTypeDbAdapter.DESCRIPTION.getType() + " NOT NULL)";
            db.execSQL(CREATE_TABLE_CLASS_ITEM_TYPES);

            final String CREATE_TABLE_CLASSES =
                    "CREATE TABLE " + ClassDbAdapter.TABLE_NAME + " (" +
                            ClassDbAdapter.CLASS_ID.getName() + " " + ClassDbAdapter.CLASS_ID.getType() + " PRIMARY KEY, " +
                            ClassDbAdapter.CODE.getName() + " " + ClassDbAdapter.CODE.getType() + " NOT NULL, " +
                            ClassDbAdapter.DESCRIPTION.getName() + " " + ClassDbAdapter.DESCRIPTION.getType() + " NULL, " +
                            ClassDbAdapter.ACADEMIC_TERM_ID.getName() + " " + ClassDbAdapter.ACADEMIC_TERM_ID.getType() + " NULL REFERENCES " +
                            AcademicTermDbAdapter.TABLE_NAME + " (" + AcademicTermDbAdapter.ACADEMIC_TERM_ID.getName() + "), " +
                            ClassDbAdapter.YEAR.getName() + " " + ClassDbAdapter.YEAR.getType() + " NULL, " +
                            ClassDbAdapter.CURRENT.getName() + " " + ClassDbAdapter.CURRENT.getType() + " NOT NULL DEFAULT 1)";
            db.execSQL(CREATE_TABLE_CLASSES);

            final String CREATE_TABLE_CLASS_PASSWORDS =
                    "CREATE TABLE " + ClassPasswordDbAdapter.TABLE_NAME + " (" +
                            ClassPasswordDbAdapter.CLASS_ID.getName() + " " + ClassPasswordDbAdapter.CLASS_ID.getType() + " NOT NULL REFERENCES " +
                            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
                            ClassPasswordDbAdapter.PASSWORD.getName() + " " + ClassPasswordDbAdapter.PASSWORD.getType() + " NOT NULL, " +
                            "UNIQUE (" + ClassPasswordDbAdapter.CLASS_ID.getName() + "))";
            db.execSQL(CREATE_TABLE_CLASS_PASSWORDS);

            final String CREATE_TABLE_CLASS_SCHEDULES =
                    "CREATE TABLE " + ClassScheduleDbAdapter.TABLE_NAME + " (" +
                            ClassScheduleDbAdapter.SCHEDULE_ID.getName() + " " + ClassScheduleDbAdapter.SCHEDULE_ID.getType() + " PRIMARY KEY, " +
                            ClassScheduleDbAdapter.CLASS_ID.getName() + " " + ClassScheduleDbAdapter.CLASS_ID.getType() + " NOT NULL REFERENCES " +
                            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
                            ClassScheduleDbAdapter.TIME_START.getName() + " " + ClassScheduleDbAdapter.TIME_START.getType() + " NOT NULL, " +
                            ClassScheduleDbAdapter.TIME_END.getName() + " " + ClassScheduleDbAdapter.TIME_END.getType() + " NULL, " +
                            ClassScheduleDbAdapter.DAYS.getName() + " " + ClassScheduleDbAdapter.DAYS.getType() + " NOT NULL DEFAULT 0, " +
                            ClassScheduleDbAdapter.ROOM.getName() + " " + ClassScheduleDbAdapter.ROOM.getType() + " NULL, " +
                            ClassScheduleDbAdapter.BUILDING.getName() + " " + ClassScheduleDbAdapter.BUILDING.getType() + " NULL, " +
                            ClassScheduleDbAdapter.CAMPUS.getName() + " " + ClassScheduleDbAdapter.CAMPUS.getType() + " NULL)";
            db.execSQL(CREATE_TABLE_CLASS_SCHEDULES);

            final String CREATE_TABLE_CLASS_NOTES =
                    "CREATE TABLE " + ClassNoteDbAdapter.TABLE_NAME + " (" +
                            ClassNoteDbAdapter.NOTE_ID.getName() + " " + ClassNoteDbAdapter.NOTE_ID.getType() + " PRIMARY KEY, " +
                            ClassNoteDbAdapter.CLASS_ID.getName() + " " + ClassNoteDbAdapter.CLASS_ID.getType() + " NOT NULL REFERENCES " +
                            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() + "), " +
                            ClassNoteDbAdapter.NOTE.getName() + " " + ClassNoteDbAdapter.NOTE.getType() + " NOT NULL, " +
                            ClassNoteDbAdapter.DATE_CREATED.getName() + " " + ClassNoteDbAdapter.DATE_CREATED.getType() + " NOT NULL)";
            db.execSQL(CREATE_TABLE_CLASS_NOTES);

            _insertDefaultAcademicTerms(db);
            _insertDefaultClassItemTypes(db);

            long classId = _insertDummyClass1(db);
            _insertDummyClassPassword(db, classId, "test");
            _insertDummyNotes(db, classId);

            _insertDummyClass2(db);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static void _insertDefaultAcademicTerms(SQLiteDatabase db) {
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_0));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_1));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_2));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_3));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_4));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_5));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_6));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_7));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_8));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_9));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_10));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_11));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_12));
    }

    private static void _insertDefaultClassItemTypes(SQLiteDatabase db) {
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_0));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_1));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_2));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_3));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_4));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_5));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_6));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_7));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_8));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_9));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_10));
    }

    private static long _insertDummyClass1(SQLiteDatabase db) {
        long id = ClassDbAdapter._insert(db, "Math 311", "Abstract Algebra", 1, "2014", true);
        Calendar calTimeStart = Calendar.getInstance();
        calTimeStart.set(Calendar.HOUR, 8);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.AM);
        Calendar calTimeEnd = Calendar.getInstance();
        calTimeEnd.set(Calendar.HOUR, 9);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.AM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.MWF.getValue(), "214", "Arts & Sciences", "North");
        return id;
    }

    private static void _insertDummyNotes(SQLiteDatabase db, long classId) {
        final SimpleDateFormat sdf = new SimpleDateFormat(ClassNoteDbAdapter.SDF_DISPLAY_TEMPLATE, sAppContext.getResources().getConfiguration().locale);
        Date noteDate;
        try {
            noteDate = sdf.parse("Oct 11, 2014");
        }
        catch(Exception e) {
            noteDate = new Date();
        }
        ClassNoteDbAdapter._insert(db, classId, "Textbook - A First Course in Abstract Algebra, 7th Edition by John B. Fraleigh", noteDate);
        ClassNoteDbAdapter._insert(db, classId, "Syllabus - http://www.extension.harvard.edu/sites/default/files/openlearning/math222/files/OLI_MathE222_Syllabus.pdf", noteDate);
    }

    private static void _insertDummyClass2(SQLiteDatabase db) {
        long id = ClassDbAdapter._insert(db, "IT 116", "Advanced Programming", 1, "2014", true);
        Calendar calTimeStart = Calendar.getInstance();
        calTimeStart.set(Calendar.HOUR, 7);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.AM);
        Calendar calTimeEnd = Calendar.getInstance();
        calTimeEnd.set(Calendar.HOUR, 8);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.AM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.MWF.getValue(), "202", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 126", "Data Structures II", 1, "2014", false);
        calTimeStart.set(Calendar.HOUR, 2);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.PM);
        calTimeEnd.set(Calendar.HOUR, 3);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.PM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.MWF.getValue(), "204", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 128", "Multimedia Systems", 1, "2014", false);
        calTimeStart.set(Calendar.HOUR, 2);
        calTimeStart.set(Calendar.MINUTE, 15);
        calTimeStart.set(Calendar.AM_PM, Calendar.PM);
        calTimeEnd.set(Calendar.HOUR, 3);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.PM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.TTh.getValue(), "304", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 134", "Object-Oriented Technology", 1, "2014", true);
        calTimeStart.set(Calendar.HOUR, 3);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.PM);
        calTimeEnd.set(Calendar.HOUR, 5);
        calTimeEnd.set(Calendar.MINUTE, 15);
        calTimeEnd.set(Calendar.AM_PM, Calendar.PM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.M.getValue(), "302", "AS", "TC");
        calTimeStart.set(Calendar.HOUR, 9);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.AM);
        calTimeEnd.set(Calendar.HOUR, 11);
        calTimeEnd.set(Calendar.MINUTE, 15);
        calTimeEnd.set(Calendar.AM_PM, Calendar.AM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.W.getValue(), "302", "AS", "TC");
    }

    private static void _insertDummyClassPassword(SQLiteDatabase db, long classId, String password) {
        ClassPasswordDbAdapter._insert(db, classId, password);
    }
}
