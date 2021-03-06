package com.example.frank.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import com.example.persist.sqlite.TestSQLiteHelper;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by frank on 2016/1/31.
 * 常量控制
 */
public class Utils {
    private static String HTTP_URL = "http://192.168.191.3:8080/";
    public static final int VERSION = 1;
    public static final String ACTIVITY_PACKAGE_PATH = "com.example.frank.activity";
    public static final String GAME_TTF = "tu.ttf";
    public static final String SCORE_TTF = "SnapITC.TTF";
    public static final String SQLITE_DATABASE = "quiz.db";
    public static final int QUESTION_SERVLET_CLASS = 0;
    public static final int QUESTION_SERVLET_QUESTION = 1;
    public static final int FIRST_CLASS = 0;
    public static final int SUB_CLASS = 1;
    public static final int COLOR_BKG = Color.rgb(47, 47, 47);

    //获得当前的activity
    public static final String getTopActivity(Context context) {
        String contextString = context.toString();
        return contextString.substring(contextString.lastIndexOf(".") + 1, contextString.indexOf("@"));
    }

    /**
     * md5或者sha-1加密
     *
     * @param inputText     要加密的内容
     * @param algorithmName 加密算法名称：md5或者sha-1，不区分大小写
     * @return
     */
    public static String encrypt(String inputText, String algorithmName) {
        if (inputText == null || "".equals(inputText.trim())) {
            throw new IllegalArgumentException("请输入要加密的内容");
        }
        if (algorithmName == null || "".equals(algorithmName.trim())) {
            algorithmName = "md5";
        }
        String encryptText = null;
        try {
            MessageDigest m = MessageDigest.getInstance(algorithmName);
            m.update(inputText.getBytes("UTF-8"));
            byte s[] = m.digest();
            // m.digest(inputText.getBytes("UTF8"));
            return hex(s);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encryptText;
    }

    /**
     * 返回十六进制字符串
     *
     * @param arr
     * @return
     */
    private static String hex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private String getServerIp(Context context) {
        TestSQLiteHelper testHelper = new TestSQLiteHelper(context, TestSQLiteHelper.CREATE_USER_TABLE, null, Utils.VERSION);
        SQLiteDatabase db = testHelper.getReadableDatabase();
        Cursor c = db.rawQuery(TestSQLiteHelper.SELECT_SERVER_IP, null);
        if (c.getCount() != 0) {
            c.moveToFirst();
            int index = c.getColumnIndex("server_ip");
            HTTP_URL = c.getString(index);
        }
        db.close();
        return HTTP_URL;
    }

    public String getHttpUrl(Context context) {
            getServerIp(context);
        return HTTP_URL;
    }
}
