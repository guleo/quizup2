package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import com.example.frank.test.R;
import com.example.frank.util.GameUtil;
import com.example.frank.util.SoundUtil;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by frank on 2016/2/17.
 */
public class MatchExtActivity extends Activity {

    public static final int duration = 1500;
    private LinearLayout mLayout;

    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_ext);
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        initView();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setResult(0);
                finish();
            }
        }, duration + 500);
    }

    private void initView() {
        mLayout = (LinearLayout) findViewById(R.id.match_ext);
        AlphaAnimation anim = new AlphaAnimation(0f,1f);
        anim.setDuration(duration);
        mLayout.startAnimation(anim);
    }

    @Override
    protected void onPause() {
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
        super.onPause();
    }

    @Override
    public void onBackPressed() {

    }
}
