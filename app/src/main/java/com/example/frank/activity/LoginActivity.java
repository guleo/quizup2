package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by frank on 2016/1/23.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        //播放背景音乐
        SoundEngine.sharedEngine().playSound(this, SoundUtil.MUSIC_BACKGROUND, true);
        setContentView(R.layout.login);

        //控件初始化
        init();
    }

    @Override
    protected void onPause() {
        SoundEngine.sharedEngine().pauseSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SoundEngine.sharedEngine().resumeSound();
        super.onResume();
    }

    private void init() {
        ImageButton mLogin = (ImageButton) findViewById(R.id.login);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
