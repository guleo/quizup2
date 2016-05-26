package com.example.persist.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by frank on 2016/5/24.
 * 存储服务器的数据操作
 */
public class TestSQLiteHelper extends SQLiteOpenHelper{
    public static final String tableName = "user_test";
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + " ( server_ip " +
              " VARCHAR )";
    public static final String SELECT_SERVER_IP = "SELECT * FROM " + tableName + ";";
    public static final String DELETE_SERVER_IP = "DELETE FROM " + tableName + ";";
    public static final String INSERT_SERVER_IP = "INSERT INTO " + tableName + " VALUES (?);";
    public static final String DELETE_USER_TEST = "DROP TABLE IF EXISTS " + tableName + ";";
    public TestSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_USER_TEST);
        onCreate(db);
    }
}
