package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.polybugger.apollot.SQLiteTableActivity;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassItemTypeDbAdapter {

    public static String TABLE_NAME = "ClassItemTypes";
    public static DbColumn ITEM_TYPE_ID = new DbColumn("ItemTypeId", DbColumn.TYPE_INTEGER);
    public static DbColumn DESCRIPTION = new DbColumn("Description", DbColumn.TYPE_TEXT);
    public static DbColumn COLOR = new DbColumn("Color", DbColumn.TYPE_TEXT);

    private ClassItemTypeDbAdapter() { }

    public static long _insert(SQLiteDatabase db, String description, String color) {
        ContentValues values = new ContentValues();
        values.put(DESCRIPTION.getName(), description);
        values.put(COLOR.getName(), color);
        return db.insert(TABLE_NAME, null, values);
    }

    public static void setTableName(String tableName) {
        TABLE_NAME = tableName;
    }

    public static void setIdColumnName(String idColumnName) {
        ITEM_TYPE_ID = new DbColumn(idColumnName, DbColumn.TYPE_INTEGER);
    }

    public static void setDataColumnName(String dataColumnName, String colorColumnName) {
        DESCRIPTION = new DbColumn(dataColumnName, DbColumn.TYPE_TEXT);
        COLOR = new DbColumn(colorColumnName, DbColumn.TYPE_TEXT);
    }

    public static ArrayList<ItemType> getList() {
        ArrayList<ItemType> list = new ArrayList<ItemType>();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            list.add(new ItemType(cursor.getLong(0), cursor.getString(1), cursor.getString(2)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return list;
    }

    @SuppressWarnings("serial")
    public static class ItemType implements Serializable {
        private long mId;
        private String mDescription;
        private String mColor;

        public ItemType(long id, String description, String color) {
            mId = id;
            mDescription = description;
            mColor = color;
        }

        public long getId() {
            return mId;
        }

        public String getDescription() {
            return mDescription;
        }

        public String getColor() {
            return mColor;
        }

        public int getColorInt() {
            return (int) Long.parseLong(mColor != null ? mColor : SQLiteTableActivity.WHITE_COLOR, 16);
        }

        @Override
        public String toString() {
            return mDescription;
        }

        @Override
        public boolean equals(Object object) {
            ItemType itemType;
            if(object != null) {
                try {
                    itemType = (ItemType) object;
                    if(itemType.mId == mId)
                        return true;
                }
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + ItemType.class.toString());
                }
            }
            return false;
        }
    }
}
