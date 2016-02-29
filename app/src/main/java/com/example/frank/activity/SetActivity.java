package com.example.frank.activity;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ListView;
import com.example.frank.adapter.SetAdapter;
import com.example.frank.test.R;
import com.example.frank.ui.SwitchButton;
import org.cocos2d.sound.SoundEngine;

/**
 * Created by frank on 2016/1/31.
 */
public class SetActivity extends Activity {

    private ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.set);
        intializeView();
        SoundEngine.sharedEngine().resumeSound();
    }


    private void intializeView() {
        mListView = (ListView) findViewById(R.id.listSet);
        mListView.setAdapter(new SetAdapter(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundEngine.sharedEngine().resumeSound();
    }
}
