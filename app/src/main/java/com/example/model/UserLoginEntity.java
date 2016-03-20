package com.example.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by frank on 2016/3/1.
 */
public class UserLoginEntity implements Serializable{

    private String username;
    private String password;

    public UserLoginEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String[] getValues() {
        try {
            Class c = Class.forName(this.getClass().getName());
            String[] args = new String[c.getDeclaredFields().length];
            args[0] = getUsername();
            args[1] = getPassword();
            return args;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String[] reflectValues() {
        try {
            Class c = Class.forName(this.getClass().getName());
            Method[] method = c.getDeclaredMethods();
            String[] args = new String[c.getDeclaredFields().length];
            int j = 0;
            for (int i = 0; i < method.length;i++) {
                if (method[i].getName().startsWith("get")) {
                    args[j++] = method[i].invoke(this).toString();
                }
            }
            return args;
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
