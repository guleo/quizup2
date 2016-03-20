package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by frank on 2016/2/6.
 */
public class UserHeadActivity extends Activity implements View.OnClickListener, SweetAlertDialog.OnSweetClickListener {

    private static final String SERVLET = Utils.HTTP_URL + "head";
    private static final String POST_WAIT = "上传中...";
    private static final String POST_PASS = "上传成功";
    private static final String POST_FAIL = "上传失败";
    //private static final String END = "\r\n";
    private Button mButton;
    private RoundImageView mHeadImage;
    private SweetAlertDialog dialog;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_head);
        initView();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
    }

    private void initView() {
        mHeadImage = (RoundImageView) findViewById(R.id.image_head);
        if (LoginActivity.HeadDrawabale != null)
        mHeadImage.setImageDrawable(LoginActivity.HeadDrawabale);
        mButton = (Button) findViewById(R.id.image_post);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_post:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == RESULT_OK)
        if (data != null && data.getData() != null) {
            Uri uri = data.getData();
            dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            dialog.setConfirmClickListener(this);
            dialog.setTitleText(POST_WAIT);
            dialog.show();
            new HeadPostTask().execute(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(SweetAlertDialog sweetAlertDialog) {
        sweetAlertDialog.dismiss();
    }

    private class HeadPostTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL url = new URL(SERVLET);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setRequestProperty("Connection", "Keep-Alive");
                con.setRequestProperty("Charset", "UTF-8");
                con.setRequestProperty("Content-Type",
                        "multipart/form-data;boundary=" + "*****");
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                String filePath = params[0].toString();
                Log.d("username", LoginActivity.entity.getUsername());
                filePath = filePath.substring(filePath.lastIndexOf(":") + 1);
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                FileInputStream fout = new FileInputStream(filePath);

                out.writeBytes("--" + "*****" + "\r\n");
                out.writeBytes("Content-Disposition: form-data; " +
                        "name=\"file1\";filename=\"" +
                        fileName + "\"" + "\r\n");
                out.writeBytes("\r\n");
                byte buffer[] = new byte[1024];
                int length;
                while ((length = fout.read(buffer)) != -1) {
            /* 将资料写入DataOutputStream中 */
                    out.write(buffer, 0, length);
                }
                out.writeBytes("\r\n");
                out.writeBytes("--" + "*****"  + "\r\n");
                out.writeBytes("Content-Disposition: form-data; " +
                        "name=\"user\"\r\n\r\n" +
                        URLEncoder.encode(LoginActivity.entity.getUsername(),"utf-8")  + "\r\n");
                out.writeBytes("--" + "*****--" + "\r\n");
                fout.close();
                out.flush();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String result = in.readLine();
                if (result != null && result.trim().equals("true")) {
                    return filePath;
                }
                con.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Object o) {
            if (o != null) {
                dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                dialog.setTitleText(POST_PASS);
                dialog.show();
                dialog.showConfirmText(false);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            dialog.dismiss();
                            FileInputStream in = new FileInputStream(o.toString());
                            Bitmap b = BitmapFactory.decodeStream(in);
                            mHeadImage.setImageBitmap(b);
                            LoginActivity.HeadDrawabale = (BitmapDrawable) mHeadImage.getDrawable();
                            mHeadImage.invalidate();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }, 1000);
            } else {
                dialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText(POST_FAIL);
            }
        }
    }
}

