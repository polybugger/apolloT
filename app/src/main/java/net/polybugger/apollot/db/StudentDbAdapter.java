package net.polybugger.apollot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class StudentDbAdapter {

    public static final String TABLE_NAME = "Students";

    public static final DbColumn STUDENT_ID = new DbColumn("StudentId", DbColumn.TYPE_INTEGER);
    public static final DbColumn LAST_NAME = new DbColumn("LastName", DbColumn.TYPE_TEXT);
    public static final DbColumn FIRST_NAME = new DbColumn("FirstName", DbColumn.TYPE_TEXT);
    public static final DbColumn MIDDLE_NAME = new DbColumn("MiddleName", DbColumn.TYPE_TEXT);
    public static final DbColumn GENDER = new DbColumn("Gender", DbColumn.TYPE_TEXT);
    public static final DbColumn EMAIL_ADDRESS = new DbColumn("EmailAddress", DbColumn.TYPE_TEXT);
    public static final DbColumn CONTACT_NO = new DbColumn("ContactNumber", DbColumn.TYPE_TEXT);
    
    private StudentDbAdapter() { }
    
    public static long _insert(SQLiteDatabase db, String lastName, String firstName, String middleName, String gender, String emailAddress, String contactNo) {
        ContentValues values = new ContentValues();
        values.put(LAST_NAME.getName(), lastName);
        values.put(FIRST_NAME.getName(), firstName);
        values.put(MIDDLE_NAME.getName(), middleName);
        values.put(GENDER.getName(), gender);
        values.put(EMAIL_ADDRESS.getName(), emailAddress);
        values.put(CONTACT_NO.getName(), contactNo);
        return db.insert(TABLE_NAME, null, values); 
    }
    
    public static long insert(String lastName, String firstName, String middleName, String gender, String emailAddress, String contactNo) {
        SQLiteDatabase db = ApolloDbAdapter.open();
        long classId = _insert(db, lastName, firstName, middleName, gender, emailAddress, contactNo);
        ApolloDbAdapter.close();
        return classId; 
    }
    
    public static int update(long studentId, String lastName, String firstName, String middleName, String gender, String emailAddress, String contactNo) {
        ContentValues values = new ContentValues();
        values.put(LAST_NAME.getName(), lastName);
        values.put(FIRST_NAME.getName(), firstName);
        values.put(MIDDLE_NAME.getName(), middleName);
        values.put(GENDER.getName(), gender);
        values.put(EMAIL_ADDRESS.getName(), emailAddress);
        values.put(CONTACT_NO.getName(), contactNo);
        SQLiteDatabase db = ApolloDbAdapter.open();
        int rowsUpdated = db.update(TABLE_NAME, values, STUDENT_ID.getName() + "=?", new String[] { String.valueOf(studentId) });
        ApolloDbAdapter.close();
        return rowsUpdated;
    }
    
    public static ArrayList<Student> getStudents(long[] filterIds) {
        StringBuilder idSb = new StringBuilder();
        int idCount = 0;
        if(filterIds != null) 
            idCount = filterIds.length;
        for(int i = 0; i < idCount; ++i)
            idSb.append(filterIds[i] + ",");
        String notInIds = null;
        if(idSb.length() > 0) {
            notInIds = STUDENT_ID.getName() + " NOT IN(" + idSb.substring(0, idSb.length() - 1) + ")";
        }
        ArrayList<Student> students = new ArrayList<Student>();
        SQLiteDatabase db = ApolloDbAdapter.open();
        Cursor cursor = db.query(TABLE_NAME, new String[] {STUDENT_ID.getName(), // 0 
                LAST_NAME.getName(), // 1
                FIRST_NAME.getName(), // 2
                MIDDLE_NAME.getName(), // 3
                GENDER.getName(), // 4 
                EMAIL_ADDRESS.getName(), // 5 
                CONTACT_NO.getName()}, // 6
                notInIds, null, null, null, LAST_NAME.getName() + " COLLATE NOCASE ASC, " + FIRST_NAME.getName() + " COLLATE NOCASE ASC");
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            students.add(new Student(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)));
            cursor.moveToNext();
        }
        cursor.close();
        ApolloDbAdapter.close();
        return students;
    }
    
    @SuppressWarnings("serial")
    public static class Student implements Serializable {

        private long mStudentId;
        private String mLastName;
        private String mFirstName;
        private String mMiddleName;
        private String mGender;
        private String mEmailAddress;
        private String mContactNo;
        
        public Student(long studentId, String lastName, String firstName, String middleName) {
            mStudentId = studentId;
            mLastName = lastName;
            mFirstName = firstName;
            mMiddleName = middleName;
        }
        
        public Student(long studentId, String lastName, String firstName, String middleName, String gender, String emailAddress, String contactNo) {
            mStudentId = studentId;
            mLastName = lastName;
            mFirstName = firstName;
            mMiddleName = middleName;
            mGender = gender;
            mEmailAddress = emailAddress;
            mContactNo = contactNo;
        }

        public long getStudentId() {
            return mStudentId;
        }
        public void setStudentId(long studentId) {
            if(mStudentId == -1)
                mStudentId = studentId;
        }
        
        public String getLastName() {
            return mLastName;
        }
        public void setLastName(String lastName) {
            mLastName = lastName;
        }
        public String getFirstName() {
            return mFirstName;
        }
        public void setFirstName(String firstName) {
            mFirstName = firstName;
        }
        public String getMiddleName() {
            return mMiddleName;
        }
        public void setMiddleName(String middleName) {
            mMiddleName = middleName;
        }
        
        public String getGender() {
            return mGender;
        }
        public void setGender(String gender) {
            mGender = gender;
        }
        
        public String getEmailAddress() {
            return mEmailAddress;
        }
        public void setEmailAddress(String emailAddress) {
            mEmailAddress = emailAddress;
        }
        
        public String getContactNo() {
            return mContactNo;
        }
        public void setContactNo(String contactNo) {
            mContactNo = contactNo;
        }
        
        public String getName() {
            String name1, name;
            boolean inbFn = !TextUtils.isEmpty(mFirstName);
            boolean inbMn = !TextUtils.isEmpty(mMiddleName);
            if(inbMn) {
                if(inbFn)
                    name1 = mFirstName + " " + mMiddleName.substring(0, 1) + ".";
                else
                    name1 = mMiddleName.substring(0, 1) + ".";
            }
            else 
                name1 = mFirstName;
            
            if(TextUtils.isEmpty(name1))
                name = mLastName;
            else {
                switch(DISPLAY_FORMAT) {
                case LAST_NAME_FIRST_NAME:
                    name = mLastName + ", " + name1;
                    break;
                case FIRST_NAME_LAST_NAME:
                    name = name1 + " " + mLastName;
                    break;
                default:
                    name = mLastName + ", " + name1;
                    break;
                }
            }
            return name;
        }
        
        @Override
        public boolean equals(Object object) {
            Student student;
            if(object != null) {
                try {
                    student = (Student) object;
                    if(student.mStudentId == mStudentId)
                        return true;
                } 
                catch(ClassCastException e) {
                    throw new ClassCastException(object.toString() + " must be an instance of " + Student.class.toString());
                }
            }
            return false;
        }
    }
    
    public static NameDisplayFormat DISPLAY_FORMAT = NameDisplayFormat.LAST_NAME_FIRST_NAME;
    public static boolean ITEM_RECORDS_CHANGED_CALLBACK = false;
    public static boolean CLASS_STUDENTS_CHANGED_CALLBACK = false;
    
    public enum NameDisplayFormat {
        LAST_NAME_FIRST_NAME(0), 
        FIRST_NAME_LAST_NAME(1);
        
        private static final NameDisplayFormat[] sNameDisplayFormatValues = NameDisplayFormat.values();
        
        public static NameDisplayFormat fromInteger(int x) 
        {
          if(x < 0 || x > 1)
            return LAST_NAME_FIRST_NAME;
          return sNameDisplayFormatValues[x];
        }
        
        private int mValue;

        private NameDisplayFormat(int value) {
            mValue = value;
        }
        
        public int getValue() {
            return mValue;
        }
    }
}
