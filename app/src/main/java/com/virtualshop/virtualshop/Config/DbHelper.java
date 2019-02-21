package com.virtualshop.virtualshop.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Shaikh on 15-02-2018.
 */

public class DbHelper extends SQLiteOpenHelper {
    public static final String TAG = DbHelper.class.getSimpleName();
    public static final String DB_NAME = "notification.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_NOTIFICATION = "notification";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "tittle";
    public static final String COLUMN_MESSAGE = "message";
    public static final String COLUMN_DATE = "date";


    public  static  final String CREATE_TABLE_NOTIFICATION = "CREATE TABLE " + TABLE_NOTIFICATION + "("
            + COLUMN_ID + "  INTEGER PRIMARY KEY , "
            + COLUMN_TITLE + " TEXT , "+ COLUMN_DATE + " TEXT , " + COLUMN_MESSAGE + " TEXT);";




    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF Exist" + TABLE_NOTIFICATION);
        onCreate(db);
    }
    public void addNotification(String tittle, String message , String date)
    {

        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, tittle);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_DATE,date);

        long id1 = db.insert(TABLE_NOTIFICATION, null, values);
        db.close();

        Log.d(TAG, "User medicine history inserted " +id1);

    }

    public Cursor getNotificationlist(){
        //String selectQuery = "select * from " + USER_TABLE_ADDICON;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+ TABLE_NOTIFICATION, null);
        return cursor;
    }
    public void deleteData(String id)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(TABLE_NOTIFICATION,"id=?",new String[]{id});
        db.close();

        Log.d(TAG, "Delete medicine name" +i); } catch (SQLException s) {
        new Exception("Error with DB Open");
    }
    }

    public void SMSDelete() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_NOTIFICATION, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }


    // Add Icon Method
    public boolean updateData(String id, String date, String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TITLE,date);
        contentValues.put(COLUMN_MESSAGE,count);
        db.update(TABLE_NOTIFICATION, contentValues, "id = ?",new String[] { id });
        return true;
    }

}

