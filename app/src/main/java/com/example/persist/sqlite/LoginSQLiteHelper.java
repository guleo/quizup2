package com.example.persist.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by frank on 2016/3/1.
 */
public class LoginSQLiteHelper extends SQLiteOpenHelper {

    public static final String tableName = "user_login";
    public static final String columnName[] = {"username", "password"};
    public static final String DELETE_USER_LOGIN = "DELETE FROM " + tableName + ";";
    public static final String FIND_USER_LOGIN = "SELECT * FROM " + tableName + ";";
    public static final String INSERT_USER_LOGIN = "INSERT INTO " + tableName + " VALUES (?,?);";
    public static final String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + " ( " +
            columnName[0] + " VARCHAR , " + columnName[1] + " CHAR(32))";

    public LoginSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public int getColumnCount() {
        return columnName.length;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_USER_LOGIN);
        onCreate(db);
    }
}
