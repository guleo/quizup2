package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
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


/**
 * Created by frank on 2016/2/8.
 * 匹配界面
 */
public class MateActivity extends Activity {

    private int duration = 3500;
    private String url_rival;
    private static final String HTTP_SERVLET = Utils.HTTP_URL + "match";
    private String rival;
    public static Drawable rivalDrawable;
    private SweetAlertDialog d;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0f);
            alphaAnim.setDuration(duration);
            mLay.startAnimation(alphaAnim);
            ScaleAnimation scaleAnim = new ScaleAnimation(1f, 2f, 1f, 2f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(duration);

            AnimationSet set = new AnimationSet(getBaseContext(), null);
            set.addAnimation(scaleAnim);
            set.addAnimation(alphaAnim);
            mImage.startAnimation(set);
        }
    };
    private LinearLayout mLay;
    private ImageView mImage;
    private RoundImageView mUserImage, mRivalImage;
    private TextView mText_rival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mate);
        initView();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        setProcess();
    }

    @Override
    protected void onPause() {
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        SoundUtil.resumeMusic(this, LoginActivity.setEntity);
        super.onResume();
    }

    private void setProcess() {
        new MatchTask().execute();
    }

    private class MatchTask extends AsyncTask {

        private String questions = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                try {
                    URL url = new URL(HTTP_SERVLET);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setReadTimeout(25000);
                    int num = GameUtil.MATCH_RAND_QUIZ_SUM;
                    DataOutputStream output = new DataOutputStream(con.getOutputStream());
                    String firstClass = getIntent().getStringExtra("firstClass");
                    String subClass = getIntent().getStringExtra("subClass");
                    String info = "username=" + LoginActivity.entity.getUsername() +
                            "&firstClass=" + firstClass + "&subClass=" + subClass +
                            "&num=" + num + "&type=matchServlet";
                    Log.d("info", info);
                    output.write(info.getBytes());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String json = reader.readLine();
                    if (json != null) {

                        JSONObject obj = new JSONObject(json);
                        String path = (String) obj.get("image");
                        url = new URL(path);
                        con = (HttpURLConnection) url.openConnection();
                        con.setDoInput(true);
                        con.setRequestMethod("GET");
                        Bitmap bitmap = BitmapFactory.decodeStream(con.getInputStream());
                        rivalDrawable = new BitmapDrawable(null, bitmap);
                        rival = obj.getString("rival");
                        url_rival = obj.getString("url_rival");
                        Thread.sleep(2000);
                        final JSONArray a = new JSONArray(obj.getString("questions"));
                        questions = a.toString();
                        return "success";
                    }

                    return "fail";

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o != null)
                switch ((String) o) {
                    case "fail":
                        d = new SweetAlertDialog(MateActivity.this, SweetAlertDialog.ERROR_TYPE);
                        d.setTitleText("匹配超时");
                        d.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent(MateActivity.this, MainActivity.class);
                                startActivity(intent);
                                MateActivity.this.finish();
                            }
                        });
                        d.show();
                        break;
                    case "success":
                        mRivalImage.setMovie(null);
                        mRivalImage.setImageDrawable(rivalDrawable);
                        mRivalImage.invalidate();
                        mHandler.handleMessage(new Message());
                        mText_rival.setText(rival);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getBaseContext(), MatchRandActivity.class);
                                intent.putExtra("json", questions);
                                intent.putExtra("rival", rival);
                                intent.putExtra("url_rival",url_rival);
                                Log.d("url_rival",url_rival);
                                startActivity(intent);
                            }
                        }, 2500);
                        break;
                }
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initView() {
        mLay = (LinearLayout) findViewById(R.id.mate_layout);
        mUserImage = (RoundImageView) findViewById(R.id.userHead);
        mUserImage.setImageDrawable(LoginActivity.HeadDrawabale);
        mRivalImage = (RoundImageView) findViewById(R.id.rivalHead);
        mImage = (ImageView) findViewById(R.id.mate_img);
        LinearLayout mLay_right = (LinearLayout) findViewById(R.id.lay_right);
        TextView mText_user = (TextView) findViewById(R.id.username);
        mText_user.setText(LoginActivity.entity.getUsername());
        mText_rival = (TextView) findViewById(R.id.rival);
        AnimationDrawable animDraw = (AnimationDrawable) mLay_right.getBackground();
        animDraw.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
}
