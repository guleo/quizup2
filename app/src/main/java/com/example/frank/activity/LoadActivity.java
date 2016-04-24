package com.example.frank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.ProgressHelper;
import com.example.frank.ui.ProgressWheel;
import com.example.frank.util.GameUtil;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/3/28.
 * 载入界面
 */
public class LoadActivity extends Activity {

    private static final String HTTP_SERVLET = Utils.HTTP_URL + "question";
    private JSONArray class_data;
    private Handler mHandler = new Handler();
    private String questions = null;
    private int mode = -1;
    public static List<List<String>> type = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        initView();
        new LoadTask(this).execute();
    }

    private void initView() {
        TextView mText = (TextView) findViewById(R.id.text);
        Typeface face = Typeface.createFromAsset(getAssets(), Utils.GAME_TTF);
        mText.setTypeface(face);
        ProgressWheel mWheel = (ProgressWheel) findViewById(R.id.progressWheel);
        ProgressHelper bar = new ProgressHelper(this);
        bar.setProgressWheel(mWheel);
        bar.setBarColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    @Override
    protected void onStop() {
        super.onStop();
        SoundUtil.stopMusic();
    }

    private class LoadTask extends AsyncTask {
        private Context context;

        public LoadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                mode = getIntent().getIntExtra("mode", -1);
                URL url = new URL(HTTP_SERVLET);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream writer = new DataOutputStream(con.getOutputStream());
                if (mode == -1) {
                    String send = "type=" + Utils.QUESTION_SERVLET_CLASS;
                    writer.write(send.getBytes());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String info = reader.readLine();
                    if (info != null) {
                        class_data = new JSONArray(info);
                        for (int i = 0; i < class_data.length(); i++) {
                            type.add(getStringData(i));
                        }
                        return true;
                    }
                } else {
                    String firstClass = getIntent().getStringExtra("firstClass");
                    String subClass = getIntent().getStringExtra("subClass");
                    String info = "username=" + LoginActivity.entity.getUsername() +
                            "&firstClass=" + firstClass + "&subClass=" + subClass +
                            "&type=" + Utils.QUESTION_SERVLET_QUESTION + "&num=" + GameUtil.MATCH_PC_QUIZ_SUM;
                    Log.d("info", info);
                    writer.write(info.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json = reader.readLine();
                    if (json != null) {
                        JSONObject obj = new JSONObject(json);
                        final JSONArray a = new JSONArray(obj.getString("questions"));
                        questions = a.toString();
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            if ((boolean) o) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LoadActivity.this.finish();
                        Intent intent = new Intent();
                        if (mode == -1)
                            intent.setClass(LoadActivity.this, MainActivity.class);
                        else
                            intent.setClass(LoadActivity.this, MatchPCActivity.class);
                        startActivity(intent);
                    }
                }, 1000);
            } else {
                SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                dialog.setTitleText("加载失败!");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        LoadActivity.this.finish();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();

            }
        }
    }

    private List<String> getStringData(int i) {
        if (class_data != null && class_data.length() > i)
            try {
                List<String> data = new ArrayList<>();
                JSONObject o = class_data.getJSONObject(i);
                data.add(o.getString("first"));
                JSONArray a = new JSONArray(o.getString("name"));
                for (int index = 0; index < a.length(); index++) {
                    JSONObject o1 = a.getJSONObject(index);
                    Log.d("sub", o1.getString("sub"));
                    data.add(o1.getString("sub"));
                }
                return data;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        return null;
    }
}
