package com.example.persist.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by frank on 2016/3/1.
 */
public class SetSQLiteHelper extends SQLiteOpenHelper {
    public static final String EFFECT = "effect" ;
    public static final String LOGIN = "login";
    public static final String VIBERATE = "viberate";
    public static final String MUSIC = "music";
    public static final String TABLE_NAME = "user_set";
    public static final String FIND_SET_INFO = "SELECT * FROM " + TABLE_NAME;
    private static final String INSERT_SET_INFO = "INSERT INTO " + TABLE_NAME +
            " VALUES (1,1,1,0)";
    public static final String CREATE_SET_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
            " (effect Integer DEFAULT 1,viberate Integer DEFAULT 1,music Integer DEFAULT 1,login Integer DEFAULT 1);";
    private static final String DROP_SET_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    public static final String UPDATE_SET_INFO = "UPDATE TABLE " + TABLE_NAME +
            "SET effect = ?,login = ?,viberate = ?,music = ?";
    public SetSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SET_TABLE);
        if (db.rawQuery(FIND_SET_INFO,null).getCount() == 0)
            db.execSQL(INSERT_SET_INFO);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SET_TABLE);
        onCreate(db);
    }
}
