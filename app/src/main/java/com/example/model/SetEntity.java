package com.example.model;

import android.content.ContentValues;
import android.database.Cursor;
import com.example.persist.sqlite.SetSQLiteHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by frank on 2016/3/1.
 */
public class SetEntity {
    private boolean soundOn = true;
    private boolean effectOn = true;
    private boolean viberateOn = true;
    private boolean loginOn = false;

    public boolean isLoginOn() {
        return loginOn;
    }

    public boolean isEffectOn() {
        return effectOn;
    }

    public boolean isSoundOn() {
        return soundOn;
    }

    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
    }

    public void setLoginOn(boolean loginOn) {
        this.loginOn = loginOn;
    }

    public void setEffectOn(boolean effectOn) {
        this.effectOn = effectOn;
    }

    public boolean isViberateOn() {
        return viberateOn;
    }

    public void setViberateOn(boolean viberateOn) {
        this.viberateOn = viberateOn;
    }

    public void writeToObject(Cursor cursor) {
        if (cursor != null) {
            this.setSoundOn(cursor.getInt(cursor.getColumnIndex(SetSQLiteHelper.MUSIC)) > 0);
            this.setEffectOn(cursor.getInt(cursor.getColumnIndex(SetSQLiteHelper.EFFECT)) > 0);
            this.setViberateOn(cursor.getInt(cursor.getColumnIndex(SetSQLiteHelper.VIBERATE)) > 0);
            this.setLoginOn(cursor.getInt(cursor.getColumnIndex(SetSQLiteHelper.LOGIN)) > 0);
        }
    }

    public String[] getValues() {
        try {
            Class c = Class.forName(this.getClass().getName());
            String[] args = new String[c.getDeclaredFields().length];
            Method[] methods = c.getMethods();
            int j = 0;
            for (Method method : methods) {
                if (method.getName().startsWith("is"))
                    args[j++] = (String) method.invoke(this);
                return args;
            }
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    public ContentValues returnValues() {
        ContentValues cv = new ContentValues();
        cv.put(SetSQLiteHelper.MUSIC, soundOn ? 1 : 0);
        cv.put(SetSQLiteHelper.EFFECT, effectOn ? 1 : 0);
        cv.put(SetSQLiteHelper.VIBERATE, viberateOn ? 1 : 0);
        cv.put(SetSQLiteHelper.LOGIN, loginOn ? 1 : 0);
        return cv;
    }
}
