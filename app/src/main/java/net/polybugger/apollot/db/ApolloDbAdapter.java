package net.polybugger.apollot.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.polybugger.apollot.R;

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

            _insertDefaultAcademicTerms(db);
            _insertDefaultClassItemTypes(db);

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
}
