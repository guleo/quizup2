package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by frank on 2016/2/3.
 * 贡献一题界面
 */
public class ShareActivity extends Activity implements View.OnClickListener {

    private final int FIRST_CLASS = Utils.FIRST_CLASS;
    private final int SUB_CLASS = Utils.SUB_CLASS;
    private final int edit_id[] = {R.id.text, R.id.answer1,
            R.id.answer2, R.id.answer3, R.id.answer4};
    private ListButton mButton_first, mButton_second, mButton_post;
    private TextView[] mText;
    private boolean isSelected;
    private SweetAlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSelected = false;
        setContentView(R.layout.share);
        initView();
        initClick();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
    }


    private void initView() {
        mText = new TextView[edit_id.length];
        for (int i = 0; i < edit_id.length; i++)
            mText[i] = (TextView) findViewById(edit_id[i]);

        mButton_first = (ListButton) findViewById(R.id.first_class);
        mButton_second = (ListButton) findViewById(R.id.second_class);
        mButton_post = (ListButton) findViewById(R.id.post);
    }

    private void initClick() {
        mButton_first.setOnClickListener(this);
        mButton_second.setOnClickListener(this);
        mButton_post.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(this, ClassActivity.class);
        switch (v.getId()) {
            case R.id.first_class:
                intent.putExtra("type", FIRST_CLASS);
                startActivityForResult(intent, FIRST_CLASS);
                break;

            case R.id.second_class:
                if (isSelected) {
                    intent.putExtra("type", SUB_CLASS);
                    int index = (int) mButton_first.getTag();
                    intent.putExtra("first", index);
                    startActivityForResult(intent, SUB_CLASS);
                } else
                    Toast.makeText(this, "请选择父主题!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.post:
                new ShareTask().execute();
                mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                mDialog.setTitleText("题目上传中...");
                mDialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        String text = data.getStringExtra("class");
        if (text != null) {
            switch (requestCode) {
                case FIRST_CLASS:
                    mButton_first.setText(text);
                    int first = data.getIntExtra("index", -1);
                    mButton_first.setTag(first);
                    isSelected = true;
                    break;
                case SUB_CLASS:
                    mButton_second.setText(text);
                    break;
            }
        }
    }

    private class ShareTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            String servlet = new Utils().getHttpUrl(ShareActivity.this) + "share";
            try {
                URL url = new URL(servlet);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                DataOutputStream writer = new DataOutputStream(con.getOutputStream());
                String info = "first=" + mButton_first.getText() + "&sub=" + mButton_second.getText() +
                        "&text=" + mText[0].getText() + "&right=" + mText[1].getText() + "&wrong1=" + mText[2].getText() +
                        "&wrong2=" + mText[3].getText() + "&wrong3=" + mText[4].getText() + "&username=" + LoginActivity.entity.getUsername();
                writer.write(info.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String read = reader.readLine();
                Log.d("reader", read);
                return read;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o != null) {
                boolean isSend = Boolean.parseBoolean((String) o);
                if (isSend) {
                    mDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    mDialog.setTitleText("分享成功");
                    mDialog.show();
                    mDialog.showConfirmText(false);
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mDialog.dismiss();
                        }
                    }.start();

                    return;
                }

            }
            mDialog.setTitleText("分享失败!");
            mDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }
    }
}
