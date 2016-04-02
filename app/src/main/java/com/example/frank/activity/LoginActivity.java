package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.frank.dialog.RegisterDialog;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import com.example.model.SetEntity;
import com.example.model.UserLoginEntity;
import com.example.persist.sqlite.LoginSQLiteHelper;
import com.example.persist.sqlite.SetSQLiteHelper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by frank on 2016/1/23.
 * 用户登录界面
 */


public class LoginActivity extends Activity implements View.OnClickListener, SweetAlertDialog.OnSweetClickListener {

    private static final String httpServlet = Utils.HTTP_URL + "login";
    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_ERROR = 1;
    private static final int LOGIN_FAIL = 2;
    private static final int LOGIN_CON = 3;
    private static final String LOGIN_CON_HINT = "登录超时!";
    private static final String LOGIN_FAIL_HINT = "登录失败，请重新登录!";
    private static final String LOGIN_SUC_HINT = "登录成功";
    private static final String LOGIN_PROCESS_HINT = "登录中...";
    private static final String LOGIN_ERROR_HINT = "用户名或密码不能为空!";
    public static BitmapDrawable HeadDrawabale;
    public static SetEntity setEntity;
    private static SweetAlertDialog dialog;
    public static UserLoginEntity entity;
    private Handler loginHandler;
    //判断是否使用了快速登录
    private static boolean isSwiftLog;
    private LoginSQLiteHelper dbHelper;
    private SetSQLiteHelper setHelper;
    private CheckBox mSwiftLogin;
    private EditText mUsername, mPwd;
    private static boolean isFirstLogin = true;


    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        isFirstLogin = true;
        isSwiftLog = false;
        dialog.hide();
    }

    private void showLoad() {
        //登录成功之后重新存储用户名和密码
        if (isSwiftLog)
            updateData();
        dialog.dismiss();
        Intent intent = new Intent();
        intent.setClass(this, LoadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //控件初始化
        init();

        setEntity = new SetEntity();
        setHelper = new SetSQLiteHelper(this, SetSQLiteHelper.CREATE_SET_TABLE, null, Utils.VERSION);
        queryData(setHelper);
        isSwiftLog = setEntity.isLoginOn();

        //判断是否快速登录
        if (isSwiftLog) {
            //查询数据库以便快速登录
            dbHelper = new LoginSQLiteHelper(this, LoginSQLiteHelper.CREATE_USER_TABLE, null, Utils.VERSION);
            queryData(dbHelper);
        }

        //播放背景音乐
        SoundUtil.playMusic(this, setEntity);
    }

    private void queryData(SQLiteOpenHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (dbHelper instanceof LoginSQLiteHelper) {
            Cursor c = db.rawQuery(LoginSQLiteHelper.FIND_USER_LOGIN, null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                isSwiftLog = false;
                int name = c.getColumnIndex(LoginSQLiteHelper.columnName[0]);
                int pwd = c.getColumnIndex(LoginSQLiteHelper.columnName[1]);
                entity = new UserLoginEntity(c.getString(name), c.getString(pwd));
                mUsername.setText(entity.getUsername());
                mPwd.setText(entity.getPassword());
                mUsername.setEnabled(false);
                mPwd.setEnabled(false);
            }
        }
        else if (dbHelper instanceof SetSQLiteHelper) {
            Cursor c = db.rawQuery(SetSQLiteHelper.FIND_SET_INFO, null);
            if (c.getCount() != 0) {
                c.moveToFirst();
                setEntity.writeToObject(c);
            }
        }
        db.close();
    }

    @Override
    protected void onPause() {
        SoundUtil.pauseMusic(this, setEntity);
        super.onPause();
    }

    @Override
    protected void onStop() {
        SoundUtil.stopMusic();
        super.onStop();
    }

    @Override
    protected void onResume() {
        SoundUtil.resumeMusic(this, setEntity);
        super.onResume();
    }

    private void init() {
        ImageButton mLogin = (ImageButton) findViewById(R.id.login);
        mLogin.setOnClickListener(this);

        TextView mRegister = (TextView) findViewById(R.id.register);
        mRegister.setOnClickListener(this);

        mUsername = (EditText) findViewById(R.id.username);
        mPwd = (EditText) findViewById(R.id.password);
        mSwiftLogin = (CheckBox) findViewById(R.id.login_swift);
    }

    @Override
    public void onClick(View v) {
        SoundUtil.playEffect(this, setEntity, v);
        switch (v.getId()) {
            case R.id.register:
                RegisterDialog d = new RegisterDialog(this);
                d.show();
                break;
            case R.id.login:
                if (isFirstLogin) {
                    loginHandler = new Handler();
                    isFirstLogin = false;
                    dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                    dialog.setConfirmClickListener(this);
                    dialog.setTitleText(LOGIN_PROCESS_HINT);
                    dialog.setCancelable(true);
                    dialog.show();
                    new LoginTask().execute("");
                }
                break;
        }
    }

    //重新存储用户名和密码
    private void updateData() {
        SQLiteOpenHelper dbHelper = new LoginSQLiteHelper(this, LoginSQLiteHelper.CREATE_USER_TABLE, null, Utils.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            db.execSQL(LoginSQLiteHelper.DELETE_USER_LOGIN);
            mUsername.getText();
            entity = new UserLoginEntity(mUsername.getText().toString(), mPwd.getText().toString());
            db.execSQL(LoginSQLiteHelper.INSERT_USER_LOGIN, entity.getValues());
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SQLiteDatabase db = setHelper.getWritableDatabase();
        isSwiftLog = mSwiftLogin.isChecked();
        setEntity.setLoginOn(isSwiftLog);
        db.update(SetSQLiteHelper.TABLE_NAME, setEntity.returnValues(), null, null);
        db.close();
    }

    private class LoginTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String username = mUsername.getText().toString();
            String password = mPwd.getText().toString();

            if (username.trim().equals("") || password.trim().equals("")) {
                return LOGIN_ERROR;
            } else {
                try {
                    password = Utils.encrypt(password, null);
                    URL url = new URL(httpServlet);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setConnectTimeout(5000);
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    DataOutputStream in = new DataOutputStream(con.getOutputStream());
                    String info = "username=" + username + "&password=" + password;
                    in.write(info.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String rec = reader.readLine().trim();
                    String path = reader.readLine();
                    con.disconnect();
                    if (rec.equals("true")) {
                        if (path != null && !path.trim().equals("")) {
                            url = new URL(path);
                            con = (HttpURLConnection) url.openConnection();
                            con.setDoInput(true);
                            con.setRequestMethod("GET");
                            Bitmap bitmap = BitmapFactory.decodeStream(con.getInputStream());
                            HeadDrawabale = new BitmapDrawable(null, bitmap);
                        }
                        else HeadDrawabale = null;
                        return LOGIN_SUCCESS;
                    } else {
                        return LOGIN_FAIL;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return LOGIN_FAIL;
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    return LOGIN_CON;
                }
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            switch ((Integer) o) {
                case LOGIN_ERROR:
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.setTitleText(LOGIN_ERROR_HINT);
                    dialog.setCancelable(true);
                    dialog.show();
                    break;
                case LOGIN_SUCCESS:
                    dialog.setTitleText(LOGIN_SUC_HINT);
                    dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    dialog.showConfirmText(false);
                    dialog.show();
                    loginHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showLoad();
                        }
                    }, 1000);
                    isSwiftLog = true;
                    break;
                case LOGIN_FAIL:
                    dialog.setTitleText(LOGIN_FAIL_HINT);
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.show();
                    break;
                case LOGIN_CON:
                    dialog.setTitleText(LOGIN_CON_HINT);
                    dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    dialog.show();
            }
        }
    }
}
