package net.polybugger.apollot.db;

public class DbColumn {

    public static final String TYPE_TEXT = "TEXT";
    public static final String TYPE_DATETIME = "TEXT";
    public static final String TYPE_INTEGER = "INTEGER";
    public static final String TYPE_REAL = "REAL";
    public static final String TYPE_BOOLEAN = "INTEGER";
    public static final String TYPE_BITS = "INTEGER";

    private final String mName;
    private final String mType;

    public DbColumn(String name, String type) {
        mName = name;
        mType = type;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
