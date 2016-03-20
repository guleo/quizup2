package com.example.frank.activity;

import android.app.Activity;
import android.os.Bundle;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;

/**
 * Created by frank on 2016/2/3.
 */
public class FriendActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        SoundUtil.playMusic(this,LoginActivity.setEntity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundUtil.playMusic(this,LoginActivity.setEntity);
    }
}
