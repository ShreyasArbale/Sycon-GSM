package com.nexzen.sycongsm;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 8;

    // Database Name

    private static final String DATABASE_NAME = "sycon";

    // Distributors table name

    private static final String REGISTER_MEMBER = "register_member";
    private static final String SCHEDULE = "schedule";


    private static final String CustomerId = "CustomerId";
    private static final String DeviceMobileNo = "DeviceMobileNo";
    private static final String UserMobileNo = "UserMobileNo";

    private static final String ScheduleId = "ScheduleId";
    private static final String ScheduleDate = "ScheduleDate";
    private static final String ScheduleEndDate = "ScheduleEndDate";
    private static final String StartTime = "StartTime";
    private static final String EndTime = "EndTime";
    private static final String Status = "Status";
    private static final String Roll = "Roll";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + REGISTER_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + REGISTER_MEMBER + "("
                + CustomerId + " INTEGER PRIMARY KEY," + DeviceMobileNo + " TEXT,"
                + UserMobileNo + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + SCHEDULE + "("
                + ScheduleId + " INTEGER PRIMARY KEY,"
                + ScheduleDate + " TEXT,"
                + ScheduleEndDate + " TEXT,"
                + StartTime + " TEXT,"
                + EndTime + " TEXT,"
                + Status + " TEXT,"
                + Roll + " TEXT)";
        db.execSQL(CREATE_SCHEDULE_TABLE);
        Log.e("On Update : ", "Database Created..!");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        // db.execSQL("DROP TABLE IF EXISTS " + DISTRIBUTOR_LIST);

        // Create tables again
        db.execSQL("DROP TABLE IF EXISTS " + REGISTER_MEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + SCHEDULE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + REGISTER_MEMBER + "("
                + CustomerId + " INTEGER PRIMARY KEY," + DeviceMobileNo + " TEXT,"
                + UserMobileNo + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + SCHEDULE + "("
                + ScheduleId + " INTEGER PRIMARY KEY,"
                + ScheduleDate + " TEXT,"
                + ScheduleEndDate + " TEXT,"
                + StartTime + " TEXT,"
                + EndTime + " TEXT,"
                + Status + " TEXT,"
                + Roll + " TEXT)";
        db.execSQL(CREATE_SCHEDULE_TABLE);
        Log.e("On Update : ", "Database Updated..!");
    }

    // Adding new contact
    void addUser(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeviceMobileNo, users.getDeviceMobileNo()); // Contact Name
        values.put(UserMobileNo, users.getUserMobileNo()); // City


        // Inserting Row
        db.insert(REGISTER_MEMBER, null, values);
        db.close(); // Closing database connection
    }

    // Adding new contact
    void addSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScheduleId, schedule.getScheduleId()); // Contact Name
        values.put(ScheduleDate, schedule.getScheduleDate());
        values.put(ScheduleEndDate, schedule.getScheduleEndDate());
        values.put(StartTime, schedule.getStartTime());
        values.put(EndTime, schedule.getEndTime());// City
        values.put(Status, schedule.getStatus());// City
        values.put(Roll, schedule.getRoll());// City

        // Inserting Row
        db.insert(SCHEDULE, null, values);
        db.close(); // Closing database connection
    }

    // Getting single contact
    Users getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(REGISTER_MEMBER, new String[]{CustomerId,
                        DeviceMobileNo, UserMobileNo}, CustomerId + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Users users = new Users(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        // return contact
        return users;
    }

    // Getting single contact
    Schedule getSchedule(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(SCHEDULE, new String[]{ScheduleId,
                        ScheduleDate, ScheduleEndDate, StartTime, EndTime, Status, Roll}, ScheduleId + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Schedule schedule = new Schedule(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5),
                cursor.getString(6));
        // return contact
        return schedule;
    }

    // Getting All Contacts
    public List<Users> getAllUsers() {
        List<Users> usersArrayList = new ArrayList<Users>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + REGISTER_MEMBER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Users users = new Users();
                users.setCustomerId(Integer.parseInt(cursor.getString(0)));
                users.setDeviceMobileNo(cursor.getString(1));
                users.setUserMobileNo(cursor.getString(2));

                // Adding contact to list
                usersArrayList.add(users);
            } while (cursor.moveToNext());
        }

        // return contact list
        return usersArrayList;
    }

    // Getting All Contacts
    public List<Schedule> getAllSchedule() {
        List<Schedule> usersArrayList = new ArrayList<Schedule>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + SCHEDULE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Schedule schedule = new Schedule();
                schedule.setScheduleId(Integer.parseInt(cursor.getString(0)));
                schedule.setScheduleDate(cursor.getString(1));
                schedule.setScheduleEndDate(cursor.getString(2));
                schedule.setStartTime(cursor.getString(3));
                schedule.setEndTime(cursor.getString(4));
                schedule.setStatus(cursor.getString(5));
                schedule.setRoll(cursor.getString(6));
                // Adding contact to list
                usersArrayList.add(schedule);
            } while (cursor.moveToNext());
        }

        // return contact list
        return usersArrayList;
    }

    // Updating single contact
    public int updateUser(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DeviceMobileNo, users.getDeviceMobileNo());
        values.put(UserMobileNo, users.getUserMobileNo());


        // updating row
        return db.update(REGISTER_MEMBER, values, CustomerId + " = ?",
                new String[]{String.valueOf(users.getCustomerId())});
    }

    // Updating single contact
    public int updateSchedule(Schedule schedule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ScheduleDate, schedule.getScheduleDate());
        values.put(ScheduleEndDate, schedule.getScheduleEndDate());
        values.put(StartTime, schedule.getStartTime());
        values.put(EndTime, schedule.getEndTime());
        values.put(Status, schedule.getStatus());
        values.put(Roll, schedule.getRoll());

        // updating row
        return db.update(SCHEDULE, values, ScheduleId + " = ?",
                new String[]{String.valueOf(schedule.getScheduleId())});
    }

    // Updating single contact
    public int updateScheduleStatus(int id, String _status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Status, _status);

        // updating row
        return db.update(SCHEDULE, values, ScheduleId + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Updating single contact
    public int updateScheduleStartTime(int id, String startT) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(StartTime, startT);

        // updating row
        return db.update(SCHEDULE, values, ScheduleId + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Updating single contact
    public int updateScheduleStartEndTime(int id, String startT, String endT) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(StartTime, startT);
        values.put(EndTime, endT);

        // updating row
        return db.update(SCHEDULE, values, ScheduleId + " = ?",
                new String[]{String.valueOf(id)});
    }

    // Deleting single contact
    public void deleteUser(Users users) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(REGISTER_MEMBER, CustomerId + " = ?",
                new String[]{String.valueOf(users.getCustomerId())});
        db.close();
    }

    // Deleting single contact
    public void deleteSchedule(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(SCHEDULE, ScheduleId + " = ?",
                new String[]{id});
        db.close();
    }


    // Deleting single contact
    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + REGISTER_MEMBER);
        db.close();
    }

    // Deleting single contact
    public void deleteAllSchedule() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + SCHEDULE);
        db.close();
    }

    // Getting contacts Count
    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + REGISTER_MEMBER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;

    }

    // Getting contacts Count
    public int getScheduleCount() {
        String countQuery = "SELECT  * FROM " + SCHEDULE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;

    }

}
