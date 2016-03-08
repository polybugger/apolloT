package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassGradeBreakdownDbAdapter {

    public static final String TABLE_NAME = "ClassGradeBreakdown_";

    public static final DbColumn ROW_ID = new DbColumn("RowId", DbColumn.TYPE_INTEGER);
    public static final DbColumn ITEM_TYPE_ID = new DbColumn("ItemTypeId", DbColumn.TYPE_INTEGER);
    public static final DbColumn CLASS_ID = new DbColumn("ClassId", DbColumn.TYPE_INTEGER);
    public static final DbColumn PERCENTAGE = new DbColumn("Percentage", DbColumn.TYPE_REAL);

    private static final String CREATE_TABLE_SQL1 = "CREATE TABLE IF NOT EXISTS ";
    private static final String CREATE_TABLE_SQL2 = " (" +
            ROW_ID.getName() + " " + ROW_ID.getType() + " PRIMARY KEY, " +
            PERCENTAGE.getName() + " " + PERCENTAGE.getType() + " NULL, " +
            ITEM_TYPE_ID.getName() + " " + ITEM_TYPE_ID.getType() + " NOT NULL UNIQUE, " +
            CLASS_ID.getName() + " " + CLASS_ID.getType() + " NOT NULL REFERENCES " +
            ClassDbAdapter.TABLE_NAME + " (" + ClassDbAdapter.CLASS_ID.getName() +
            "), FOREIGN KEY (" + ITEM_TYPE_ID.getName() + ") REFERENCES " +
            ClassItemTypeDbAdapter.TABLE_NAME + " (" + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName() + "))";

    private ClassGradeBreakdownDbAdapter() {}

    public static long _insert(SQLiteDatabase db, long classId, long itemTypeId, Float percentage) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        ContentValues values = new ContentValues();
        values.put(CLASS_ID.getName(), classId);
        values.put(ITEM_TYPE_ID.getName(), itemTypeId);
        values.put(PERCENTAGE.getName(), percentage);
        return db.insert(tableName, null, values);
    }

    public static long insert(long classId, long itemTypeId, Float percentage) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long id = _insert(db, classId, itemTypeId, percentage);
        ApolloDbAdapter.close();
        return id;
    }

    public static int delete(long classId, long itemTypeId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsDeleted = db.delete(tableName, CLASS_ID.getName() + "=? AND " + ITEM_TYPE_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(itemTypeId)});
        ApolloDbAdapter.close();
        return rowsDeleted;
    }

    public static int update(long classId, long itemTypeId, Float percentage) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ContentValues values = new ContentValues();
        values.put(PERCENTAGE.getName(), percentage);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(tableName, values, CLASS_ID.getName() + "=? AND " + ITEM_TYPE_ID.getName() + "=?",
                new String[]{String.valueOf(classId), String.valueOf(itemTypeId)});
        ApolloDbAdapter.close();
        return rowsUpdated;
    }

    public static ArrayList<ClassGradeBreakdown> getClassGradeBreakdowns(long classId) {
        String tableName = TABLE_NAME + String.valueOf(classId);
        ArrayList<ClassGradeBreakdown> classGradeBreakdowns = new ArrayList<ClassGradeBreakdown>();
        ClassItemTypeDbAdapter.ItemType itemType;
        final String sql = "SELECT it." + ITEM_TYPE_ID.getName() + // 0
                ", it." + ClassItemTypeDbAdapter.DESCRIPTION.getName() + // 1
                ", it." + ClassItemTypeDbAdapter.COLOR.getName() + // 2
                ", gb." + PERCENTAGE.getName() + // 3
                " FROM " + tableName +
                " AS gb LEFT OUTER JOIN " + ClassItemTypeDbAdapter.TABLE_NAME +
                " AS it ON gb." + ITEM_TYPE_ID.getName() + "=it." + ClassItemTypeDbAdapter.ITEM_TYPE_ID.getName();
        //+ " ORDER BY date(" + ITEM_DATE.getName() + ") ASC, ci." + DESCRIPTION.getName() + ", it." + ClassItemTypeDbAdapter.DESCRIPTION.getName();
        SQLiteDatabase db = ApolloDbAdapter.open();
        db.execSQL(CREATE_TABLE_SQL1 + tableName + CREATE_TABLE_SQL2);
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            if(cursor.isNull(2))
                itemType = null;
            else
                itemType = new ClassItemTypeDbAdapter.ItemType(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            classGradeBreakdowns.add(new ClassGradeBreakdown(classId, itemType, cursor.isNull(3) ? null : cursor.getFloat(3)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return classGradeBreakdowns;
    }

    @SuppressWarnings("serial")
    public static class ClassGradeBreakdown implements Serializable {

        private long mClassId;
        private ClassItemTypeDbAdapter.ItemType mItemType;
        private Float mPercentage;

        public ClassGradeBreakdown(long classId, ClassItemTypeDbAdapter.ItemType itemType, Float percentage) {
            mClassId = classId;
            mItemType = itemType;
            mPercentage = percentage;
        }
        public long getClassId() {
            return mClassId;
        }
        public void setClassId(long classId) {
            if(mClassId == -1)
                mClassId = classId;
        }
        public ClassItemTypeDbAdapter.ItemType getItemType() {
            return mItemType;
        }
        public void setItemType(ClassItemTypeDbAdapter.ItemType itemType) {
            mItemType = itemType;
        }
        public Float getPercentage() {
            return mPercentage;
        }
        public void setPercentage(Float percentage) {
            mPercentage = percentage;
        }
        public String getDescription() {
            StringBuilder sb = new StringBuilder();
            sb.append(mItemType.getDescription());
            sb.append(" - ");
            sb.append(mPercentage);
            sb.append("%");
            return sb.toString();
        }
        @Override
        public boolean equals(Object object) {
            ClassGradeBreakdown classGradeBreakdown;
            if(object != null) {
                try {
                    classGradeBreakdown = (ClassGradeBreakdown) object;
                    if((classGradeBreakdown.mClassId == mClassId) && (classGradeBreakdown.mItemType.getId() == mItemType.getId()))
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ClassGradeBreakdown.class.toString());
                }
            }
            return false;
        }
    }

}
