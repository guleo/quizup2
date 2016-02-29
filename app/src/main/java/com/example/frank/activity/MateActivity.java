package com.example.frank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;
import org.cocos2d.sound.SoundEngine;


/**
 * Created by frank on 2016/2/8.
 */
public class MateActivity extends Activity implements Runnable{

    private int duration = 1500;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AlphaAnimation alphaAnim = new AlphaAnimation(1f, 0f);
            alphaAnim.setDuration(duration);
            mLay.startAnimation(alphaAnim);
            ScaleAnimation scaleAnim = new ScaleAnimation(1f, 2f, 1f, 2f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnim.setDuration(duration);

            AnimationSet set = new AnimationSet(getBaseContext(),null);
            set.addAnimation(scaleAnim);
            set.addAnimation(alphaAnim);
            mImage.startAnimation(set);

        }
    };
    private LinearLayout mLay,mLay_right;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mate);
        initView();
        SoundEngine.sharedEngine().playSound(this, SoundUtil.MUSIC_MATCH, true);
        setProcess();
    }

    @Override
    protected void onPause() {
        SoundEngine.sharedEngine().pauseSound();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        SoundEngine.sharedEngine().resumeSound();
        super.onResume();
    }

    private void setProcess() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            Message message = new Message();
            mHandler.sendMessage(message);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(getBaseContext(),MatchRandActivity.class);
                    startActivity(intent);
                }
            },1200);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void initView() {
        mLay = (LinearLayout) findViewById(R.id.mate_layout);
        mImage = (ImageView) findViewById(R.id.mate_img);
        mLay_right = (LinearLayout) findViewById(R.id.lay_right);
        AnimationDrawable animDraw = (AnimationDrawable) mLay_right.getBackground();
        animDraw.start();
    }

}
