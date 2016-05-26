package com.example.frank.activity;

import android.app.Activity;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.*;
import android.widget.*;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import com.example.frank.util.GameUtil;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/2/17.
 * 结果页面
 */
public class ResultActivity extends Activity implements View.OnTouchListener {

    private AnimationSet leftInAnim, leftOutAnim,
            rightInAnim, rightOutAnim;
    private ViewPager mPager;

    private int middle;
    private ImageView view_un, view_ck;
    private ImageView imageDot[];
    private int index = 0;
    private float downX, upX;
    private ImageSwitcher mSwitcher;
    private List<Drawable> imageList;
    private int time, user_score;
    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        int mode = getIntent().getIntExtra("type", -1);
        imageList = new ArrayList<>();
        result = getIntent().getStringExtra("result");
        if (mode == GameUtil.MATCH_RAND_MODE) {
            new ResultTask().execute();
            imageList.addAll(MatchRandActivity.drawList);
        }
        if (mode == GameUtil.MATCH_PC_MODE) {
            imageList.addAll(MatchPCActivity.drawList);
            ImageView image = (ImageView) findViewById(R.id.imageView6);
            image.setVisibility(View.GONE);
        }
        initView();
        initAnim();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        SoundUtil.playEffect(this, LoginActivity.setEntity, null);
    }

    private void initAnim() {
        int duration = 1000;
        AlphaAnimation alpha_in = new AlphaAnimation(0f, 1f);
        AlphaAnimation alpha_out = new AlphaAnimation(1f, 0f);
        TranslateAnimation left_in = new TranslateAnimation(-500, 0, -500, 0);
        TranslateAnimation left_out = new TranslateAnimation(0, -500, 0, -500);
        TranslateAnimation right_out = new TranslateAnimation(0, 500, 0, 500);
        TranslateAnimation right_in = new TranslateAnimation(500, 0, 500, 0);
        ScaleAnimation scale_in = new ScaleAnimation(0.3f, 1f, 0.3f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        ScaleAnimation scale_out = new ScaleAnimation(1f, 0.3f, 1f, 0.3f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);

        leftInAnim = new AnimationSet(this, null);
        leftOutAnim = new AnimationSet(this, null);
        rightInAnim = new AnimationSet(this, null);
        rightOutAnim = new AnimationSet(this, null);
        leftInAnim.setDuration(duration);
        leftOutAnim.setDuration(duration);
        rightInAnim.setDuration(duration);
        rightOutAnim.setDuration(duration);

        leftInAnim.addAnimation(alpha_in);
        leftInAnim.addAnimation(left_in);
        leftInAnim.addAnimation(scale_in);

        leftOutAnim.addAnimation(alpha_out);
        leftOutAnim.addAnimation(left_out);
        leftOutAnim.addAnimation(scale_out);

        rightInAnim.addAnimation(alpha_in);
        rightInAnim.addAnimation(right_in);
        rightInAnim.addAnimation(scale_in);

        rightOutAnim.addAnimation(alpha_out);
        rightOutAnim.addAnimation(right_out);
        rightOutAnim.addAnimation(scale_out);
    }

    private void initView() {

        int mode = getIntent().getIntExtra("type", -1);
        RoundImageView leftHead = (RoundImageView) findViewById(R.id.view);
        leftHead.setImageDrawable(LoginActivity.HeadDrawabale);

        TextView user = (TextView) findViewById(R.id.textView4);
        user.setText(LoginActivity.entity.getUsername());

        TextView rival = (TextView) findViewById(R.id.textView5);
        rival.setText(getIntent().getStringExtra("rival"));


        RoundImageView rightHead = (RoundImageView) findViewById(R.id.view2);
        rightHead.setImageDrawable(MateActivity.rivalDrawable);
        if (mode == GameUtil.MATCH_PC_MODE) {
            rival.setVisibility(View.GONE);
            rightHead.setVisibility(View.GONE);
        }
        TextView right_count = (TextView) findViewById(R.id.right_count);
        right_count.setText(getIntent().getIntExtra("right_count", 0) + "");

        TextView score = (TextView) findViewById(R.id.score);
        user_score = Integer.parseInt(getIntent().getStringExtra("score_user"));
        score.setText(user_score + "");

        TextView time_count = (TextView) findViewById(R.id.time_count);
        user_score = Integer.parseInt(getIntent().getStringExtra("score_user"));

        time = getIntent().getIntExtra("time", 0);
        time_count.setText(time + "");

        imageDot = new ImageView[imageList.size()];
        for (int i = 0; i < imageDot.length; i++) {
            imageDot[i] = new ImageView(this);
        }
        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);
        //设置渐入渐入动画
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
        //添加滑动监听
        mSwitcher.setOnTouchListener(this);
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView image = new ImageView(getBaseContext());
                image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                image.setLayoutParams(new ImageSwitcher.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                return image;
            }
        });
        mSwitcher.setImageDrawable(imageList.get(0));
        TextView mResult = (TextView) findViewById(R.id.match_result);
        Typeface tf = Typeface.createFromAsset(getAssets(), Utils.GAME_TTF);
        mResult.setText(result);
        mResult.setTypeface(tf);

        mPager = (ViewPager) findViewById(R.id.gallery);
        mPager.setOffscreenPageLimit(imageList.size());
        mPager.setPageMargin(4);

        Bitmap bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(10, 10, 6, paint);
        view_un = new ImageView(this);
        view_un.setImageBitmap(bitmap);
        view_ck = new ImageView(this);
        paint.setColor(getResources().getColor(android.R.color.holo_orange_light));
        bitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitmap);
        canvas1.drawCircle(10, 10, 6, paint);
        view_ck.setImageBitmap(bitmap);
        mPager.setAdapter(new PagerAdapter() {


            @Override
            public int getCount() {
                return imageList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                Log.d("count", position + "");
                if (position == index) {
                    imageDot[position].setImageDrawable(view_ck.getDrawable());
                } else {
                    imageDot[position].setImageDrawable(view_un.getDrawable());

                }
                container.addView(imageDot[position]);
                return imageDot[position];
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        });
        middle = (imageList.size() - 1) / 2;
        mPager.setCurrentItem(middle, false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cur;
        int pre;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mSwitcher.getParent() instanceof ScrollView)
                mSwitcher.getParent().requestDisallowInterceptTouchEvent(true);
            downX = event.getX();
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            upX = event.getX();
            if (mSwitcher.getParent() instanceof ScrollView)
                mSwitcher.getParent().requestDisallowInterceptTouchEvent(true);
            //左移
            float offSet = 100;
            if (downX - upX >= offSet && index < imageList.size() - 1) {
                index++;
                if (index == middle) {
                    pre = 1;
                    cur = 0;
                } else if (index < middle) {
                    pre = middle - index + 1;
                    cur = middle - index;
                } else if (index == middle + 1) {
                    pre = 0;
                    cur = index;
                } else {
                    pre = index - 1;
                    cur = index;
                }
                mSwitcher.setInAnimation(rightInAnim);
                mSwitcher.setOutAnimation(leftOutAnim);
                mSwitcher.setImageDrawable(imageList.get(index));
                ((ImageView) mPager.getChildAt(pre)).setImageDrawable(view_un.getDrawable());
                ((ImageView) mPager.getChildAt(cur)).setImageDrawable(view_ck.getDrawable());
            }

            //右移
            if (upX - downX >= offSet && index > 0) {
                index--;
                if (index == middle) {
                    pre = index + 1;
                    cur = 0;
                } else if (index < middle - 1) {
                    pre = middle - index - 2;
                    cur = middle - index - 1;
                } else if (index + 1 == middle) {
                    pre = 0;
                    cur = index + 1;
                } else {
                    pre = index + 1;
                    cur = index;
                }
                mSwitcher.setInAnimation(leftInAnim);
                mSwitcher.setOutAnimation(rightOutAnim);
                mSwitcher.setImageDrawable(imageList.get(index));
                ((ImageView) mPager.getChildAt(pre)).setImageDrawable(view_un.getDrawable());
                ((ImageView) mPager.getChildAt(cur)).setImageDrawable(view_ck.getDrawable());
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
        finish();
    }

    @Override
    protected void onPause() {
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
        super.onPause();
    }

    private class ResultTask extends AsyncTask {

        @Override
        protected Boolean doInBackground(Object[] params) {
            String servlet = new Utils().getHttpUrl(ResultActivity.this) + "result";
            try {
                URL url = new URL(servlet);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream writer = new DataOutputStream(con.getOutputStream());
                String send = "username=" + LoginActivity.entity.getUsername() +
                        "&time=" + time + "&score=" + user_score + "&result=" + result;
                writer.write(send.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                return Boolean.parseBoolean(reader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Object o) {
            boolean isSend = Boolean.parseBoolean(o.toString());
            if (!isSend) {
                SweetAlertDialog d = new SweetAlertDialog(ResultActivity.this, SweetAlertDialog.ERROR_TYPE);
                d.setTitleText("连接断开，请重新登录");
                d.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        new ResultTask().execute();
                    }
                });
            }
        }
    }
}
