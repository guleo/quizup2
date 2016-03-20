package com.example.frank.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

/**
 * Created by frank on 2016/3/1.
 */
public class RegisterDialog extends Dialog implements View.OnClickListener {
    private HttpURLConnection con;
    private RadioGroup radioGroup;
    private EditText edit_name;
    private EditText edit_pwd;
    private EditText edit_school;
    private EditText edit_confirm;
    private ListButton mRegister;
    private Context context;

    private static final String USER_EMPTY = "用户名不能为空";
    private static final String PWD_EMPTY = "密码不能为空";
    private static final String PWD_DUP = "密码不一致";
    private static final String USER_DUP = "用户名已存在";
    private static final String REG_CON = "用户已连接";
    private static final String REG_NET = "网络不畅";
    private static final String REG_SUC = "注册成功";
    private static final String REG_FAIL = "注册失败";

    private Handler mHandler;

    private static final String HttpServlet = Utils.HTTP_URL + "register";

    public RegisterDialog(Context context) {
        this(context, 0);
    }

    public RegisterDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        init();

        mHandler = new Handler() {
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
                if (msg.obj != REG_SUC)
                    Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT).show();
//                else {
//                     Intent intent = new Intent();
//                     intent.setClass(context, MainActivity.class);
//                     ActivityManager m = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//                 }
            }
        };
    }

    private void init() {
        edit_name = (EditText) findViewById(R.id.editText3);
        edit_pwd = (EditText) findViewById(R.id.editText4);
        edit_confirm = (EditText) findViewById(R.id.editText5);
        edit_school = (EditText) findViewById(R.id.editText6);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        mRegister = (ListButton) findViewById(R.id.logUp);
        mRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logUp) {
            String username = edit_name.getText().toString();
            String password = edit_pwd.getText().toString();
            String confirm = edit_confirm.getText().toString();
            Message m = new Message();
            if (username.trim().equals("")) {
                m.obj = USER_EMPTY;
                mHandler.dispatchMessage(m);
            } else if (password.trim().equals("")) {
                m.obj = PWD_EMPTY;
                mHandler.dispatchMessage(m);
            } else if (!password.equals(confirm)) {
                m.obj = PWD_DUP;
                mHandler.dispatchMessage(m);
            } else {
                new RegisterThread().start();
            }

        }
    }

    private class RegisterThread extends Thread {
        @Override
        public void run() {
            Message m = new Message();
            try {
                Looper.prepare();
                URL url = new URL(HttpServlet);
                con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);

                OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
                String username = edit_name.getText().toString();
                String school = edit_school.getText().toString();
                String password = edit_pwd.getText().toString();
                password = Utils.encrypt(password, null);
                String sex = (radioGroup.getCheckedRadioButtonId() == R.id.male_btn) ? "0" : "1";
                String info = "username=" + URLEncoder.encode(username, "utf-8") + "&password=" +
                        URLEncoder.encode(password, "utf-8")
                        + "&userSchool=" + URLEncoder.encode(school, "utf-8") + "&userSex=" + URLEncoder.encode(sex, "utf-8");
                out.write(info);
                out.flush();
                out.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String ss = reader.readLine().trim();

                if (ss != null) {
                    m.obj = REG_CON;
                    if (ss.equals("success")) {
                        m.obj = REG_SUC;
                    } else if (ss.equals("fail"))
                        m.obj = REG_FAIL;
                    else
                        m.obj = USER_DUP;
                } else {
                    m.obj = REG_NET;
                }
                mHandler.dispatchMessage(m);
                con.disconnect();
                Looper.loop();
            } catch (ConnectException e) {
                m.obj = REG_CON;
                mHandler.dispatchMessage(m);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
