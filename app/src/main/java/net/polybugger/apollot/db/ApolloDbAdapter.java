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
                            AcademicTermDbAdapter.DESCRIPTION.getName() + " " + AcademicTermDbAdapter.DESCRIPTION.getType() + " NOT NULL, " +
                            AcademicTermDbAdapter.COLOR.getName() + " " + AcademicTermDbAdapter.COLOR.getType() + " NULL)";
            db.execSQL(CREATE_TABLE_ACADEMIC_TERMS);

            final String CREATE_TABLE_CLASS_ITEM_TYPES =
                    "CREATE TABLE " + ClassItemTypeDbAdapter.TABLE_NAME + " (" +
                            ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + " " + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getType() + " PRIMARY KEY, " +
                            ClassItemTypeDbAdapter.DESCRIPTION.getName() + " " + ClassItemTypeDbAdapter.DESCRIPTION.getType() + " NOT NULL, " +
                            ClassItemTypeDbAdapter.COLOR.getName() + " " + ClassItemTypeDbAdapter.COLOR.getType() + " NULL)";
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

            final String CREATE_TABLE_STUDENTS =
                    "CREATE TABLE " + StudentDbAdapter.TABLE_NAME + " (" +
                            StudentDbAdapter.STUDENT_ID.getName() + " " + StudentDbAdapter.STUDENT_ID.getType() + " PRIMARY KEY, " +
                            StudentDbAdapter.LAST_NAME.getName() + " " + StudentDbAdapter.LAST_NAME.getType() + " NOT NULL, " +
                            StudentDbAdapter.FIRST_NAME.getName() + " " + StudentDbAdapter.FIRST_NAME.getType() + " NULL, " +
                            StudentDbAdapter.MIDDLE_NAME.getName() + " " + StudentDbAdapter.MIDDLE_NAME.getType() + " NULL, " +
                            StudentDbAdapter.GENDER.getName() + " " + StudentDbAdapter.GENDER.getType() + " NULL, " +
                            StudentDbAdapter.EMAIL_ADDRESS.getName() + " " + StudentDbAdapter.EMAIL_ADDRESS.getType() + " NULL, " +
                            StudentDbAdapter.CONTACT_NO.getName() + " " + StudentDbAdapter.CONTACT_NO.getType() + " NULL)";
            db.execSQL(CREATE_TABLE_STUDENTS);

            _insertDefaultAcademicTerms(db);
            _insertDefaultClassItemTypes(db);

            long classId = _insertDummyClass1(db);
            _insertDummyNotes(db, classId);
            _insertDummyItems(db, classId);
            _insertDummyStudents(db, classId);
            _insertDummyGradeBreakdowns(db, classId);

            //classId = _insertDummyClass2(db);
            //_insertDummyClassPassword(db, classId, "test");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    private static void _insertDefaultAcademicTerms(SQLiteDatabase db) {
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_0), sAppContext.getString(R.string.default_academic_term_color_0));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_1), sAppContext.getString(R.string.default_academic_term_color_1));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_2), sAppContext.getString(R.string.default_academic_term_color_2));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_3), sAppContext.getString(R.string.default_academic_term_color_3));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_4), sAppContext.getString(R.string.default_academic_term_color_4));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_5), sAppContext.getString(R.string.default_academic_term_color_5));
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_6), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_7), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_8), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_9), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_10), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_11), null);
        AcademicTermDbAdapter._insert(db, sAppContext.getString(R.string.default_academic_term_12), null);
    }

    private static void _insertDefaultClassItemTypes(SQLiteDatabase db) {
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_0), sAppContext.getString(R.string.default_class_item_type_color_0));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_1), sAppContext.getString(R.string.default_class_item_type_color_1));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_2), sAppContext.getString(R.string.default_class_item_type_color_2));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_3), sAppContext.getString(R.string.default_class_item_type_color_3));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_4), sAppContext.getString(R.string.default_class_item_type_color_4));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_5), sAppContext.getString(R.string.default_class_item_type_color_5));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_6), sAppContext.getString(R.string.default_class_item_type_color_6));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_7), sAppContext.getString(R.string.default_class_item_type_color_7));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_8), sAppContext.getString(R.string.default_class_item_type_color_8));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_9), sAppContext.getString(R.string.default_class_item_type_color_9));
        ClassItemTypeDbAdapter._insert(db, sAppContext.getString(R.string.default_class_item_type_10), sAppContext.getString(R.string.default_class_item_type_color_10));
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

    private static long _insertDummyClass2(SQLiteDatabase db) {
        long id = ClassDbAdapter._insert(db, "IT 116", "Advanced Programming", 2, "2014", true);
        Calendar calTimeStart = Calendar.getInstance();
        calTimeStart.set(Calendar.HOUR, 7);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.AM);
        Calendar calTimeEnd = Calendar.getInstance();
        calTimeEnd.set(Calendar.HOUR, 8);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.AM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.MWF.getValue(), "202", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 126", "Data Structures II", 3, "2014", false);
        calTimeStart.set(Calendar.HOUR, 2);
        calTimeStart.set(Calendar.MINUTE, 45);
        calTimeStart.set(Calendar.AM_PM, Calendar.PM);
        calTimeEnd.set(Calendar.HOUR, 3);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.PM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.MWF.getValue(), "204", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 128", "Multimedia Systems", 4, "2014", false);
        calTimeStart.set(Calendar.HOUR, 2);
        calTimeStart.set(Calendar.MINUTE, 15);
        calTimeStart.set(Calendar.AM_PM, Calendar.PM);
        calTimeEnd.set(Calendar.HOUR, 3);
        calTimeEnd.set(Calendar.MINUTE, 45);
        calTimeEnd.set(Calendar.AM_PM, Calendar.PM);
        ClassScheduleDbAdapter._insert(db, id, calTimeStart.getTime(), calTimeEnd.getTime(), DaysBits.TTh.getValue(), "304", "AS", "TC");

        id = ClassDbAdapter._insert(db, "IT 134", "Object-Oriented Technology", 5, "2014", true);
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

        return id;
    }

    private static void _insertDummyClassPassword(SQLiteDatabase db, long classId, String password) {
        ClassPasswordDbAdapter._insert(db, classId, password);
    }

    private static void _insertDummyItems(SQLiteDatabase db, long classId) {
        final SimpleDateFormat sdf = new SimpleDateFormat(ClassItemDbAdapter.SDF_DISPLAY_TEMPLATE, sAppContext.getResources().getConfiguration().locale);
        Date itemDate;
        try {
            itemDate = sdf.parse("Oct 13, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        long itemId = ClassItemDbAdapter._insert(db, classId, "Review of Linear Algebra", 1, itemDate, true, true, 10.0f, false, null);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "10.0 points is class participation", itemDate);

        Date dueDate;
        try {
            dueDate = sdf.parse("Oct 17, 2014");
        }
        catch(Exception e) {
            dueDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Arithmetic in Z", 2, itemDate, false, true, 100.0f, true, dueDate);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "Exercises pp. 8 (odd numbers), 14 (even numbers), 22 (#'s 1-3, 4, 5, 8-10)", itemDate);

        try {
            itemDate = sdf.parse("Oct 17, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Permutations", 1, itemDate, true, true, 10.0f, false, null);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "10.0 points is class participation", itemDate);

        try {
            itemDate = sdf.parse("Oct 22, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Quotient Groups, First Isomorphism Theorem", 1, itemDate, true, false, null, false, null);
        try {
            dueDate = sdf.parse("Oct 27, 2014");
        }
        catch(Exception e) {
            dueDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Groups 1", 2, itemDate, false, true, 100.0f, true, dueDate);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "Exercises pp. 180-181 (odd numbers), 182-183 (even numbers), 201 (odd numbers)", itemDate);

        try {
            itemDate = sdf.parse("Oct 27, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Abstract Linear Operators and How to Calculate with Them", 1, itemDate, true, false, null, false, null);
        try {
            itemDate = sdf.parse("Nov 5, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Orthogonal Groups", 1, itemDate, true, true, 10.0f, false, null);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "10.0 points is class participation", itemDate);

        try {
            dueDate = sdf.parse("Nov 10, 2014");
        }
        catch(Exception e) {
            dueDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Groups 2", 2, itemDate, false, true, 100.0f, true, dueDate);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "Exercises pp. 211 (odd numbers), 212 (odd numbers), 213 (even numbers)", itemDate);

        try {
            itemDate = sdf.parse("Nov 10, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Midterm Exam", 10, itemDate, false, true, 200.0f, false, null);

        try {
            itemDate = sdf.parse("Nov 12, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        try {
            dueDate = sdf.parse("Dec 19, 2014");
        }
        catch(Exception e) {
            dueDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Practical Applications", 8, itemDate, false, true, 200.0f, true, dueDate);
        ClassItemDbAdapter._insert(db, classId, "Isometrics of Plane Figures", 1, itemDate, true, false, null, false, null);

        try {
            itemDate = sdf.parse("Nov 17, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Group Actions", 1, itemDate, true, true, 10.0f, false, null);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "10.0 points is class participation", itemDate);

        try {
            itemDate = sdf.parse("Nov 21, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "A5 and the Symmetries of an Icosahedron", 1, itemDate, true, false, null, false, null);
        try {
            itemDate = sdf.parse("Nov 26, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Rings", 1, itemDate, true, true, 10.0f, false, null);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "10.0 points is class participation", itemDate);

        try {
            dueDate = sdf.parse("Dec 1, 2014");
        }
        catch(Exception e) {
            dueDate = new Date();
        }
        itemId = ClassItemDbAdapter._insert(db, classId, "Rings", 2, itemDate, false, true, 100.0f, true, dueDate);
        ClassItemNoteDbAdapter._insert(db, classId, itemId, "Exercises pp. 53-59 (odd numbers), 66-70 (even numbers), 80-83 (odd numbers)", itemDate);

        try {
            itemDate = sdf.parse("Dec 1, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Extensions of Rings", 1, itemDate, true, false, null, false, null);
        try {
            itemDate = sdf.parse("Dec 5, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Special Lecture", 1, itemDate, true, false, null, false, null);
        try {
            itemDate = sdf.parse("Dec 10, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Euclidean Domains, PIDs, UFDs", 1, itemDate, true, false, null, false, null);
        try {
            itemDate = sdf.parse("Dec 15, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Structure of Ring of Integers in a Quadratic Field", 1, itemDate, true, false, null, false, null);

        try {
            itemDate = sdf.parse("Dec 19, 2014");
        }
        catch(Exception e) {
            itemDate = new Date();
        }
        ClassItemDbAdapter._insert(db, classId, "Final Exam", 11, itemDate, false, true, 200.0f, false, null);
    }

    private static void _insertDummyStudents(SQLiteDatabase db, long classId) {
        String m = sAppContext.getString(R.string.male);
        String f = sAppContext.getString(R.string.female);
        Date dateCreated = new Date();

        long studentId = StudentDbAdapter._insert(db, "Knutes", "Deunan", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Ibanez", "Carmen", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Jenkins", "Carl", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Durer", "Tia \"Trig\"", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Rico", "Johnny", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Kusanagi", "Motoko", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Cage", "William", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Vrataski", "Rita", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Wayne", "Bruce", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Kent", "Clark", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Allen", "Barry", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Stark", "Tony", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Rogers", "Steve", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Romanoff", "Natasha", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Banner", "Bruce", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Barton", "Clint", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Coulson", "Phil", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Fury", "Nick", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Parker", "Peter", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Hattori", "Hanzo", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Tachibana", "Ukyo", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Nakoruru", null, null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Haohmaru", null, null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Weller", "Galford", "D", m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "de Colde", "Charlotte Christine", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Neinhalt", "Sieger", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Cham Cham", null, null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Descartes", "René", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Euclid", null, null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Leibniz", "Gottfried Wilhelm", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Gauss", "Carl Friedrich", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Boole", "George", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Pythagoras", null, null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Kepler", "Johannes", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Pascal", "Blaise", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Dedekind", "Julius Wilhelm Richard", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Cantor", "Georg", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);

        studentId = StudentDbAdapter._insert(db, "Wesker", "Albert", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Redfield", "Chris", null, m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Wong", "Ada", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Valentine", "Jill", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Redfield", "Claire", null, f, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
        studentId = StudentDbAdapter._insert(db, "Kennedy", "Leon", "S", m, null, null);
        ClassStudentDbAdapter._insert(db, classId, studentId, dateCreated);
    }

    private static void _insertDummyGradeBreakdowns(SQLiteDatabase db, long classId) {
        ClassGradeBreakdownDbAdapter._insert(db, classId, 1, (float) 20.00); // lesson
        ClassGradeBreakdownDbAdapter._insert(db, classId, 2, (float) 20.00); // homework
        ClassGradeBreakdownDbAdapter._insert(db, classId, 8, (float) 10.00); // report
        ClassGradeBreakdownDbAdapter._insert(db, classId, 10, (float) 25.00); // midterm
        ClassGradeBreakdownDbAdapter._insert(db, classId, 11, (float) 25.00); // final


    }

}
