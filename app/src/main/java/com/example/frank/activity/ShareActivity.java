package com.example.frank.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.util.SoundUtil;

/**
 * Created by frank on 2016/2/3.
 */
public class ShareActivity extends Activity implements View.OnClickListener{

    private final int edit_id[] = {R.id.text, R.id.answer1,
            R.id.answer2, R.id.answer3, R.id.answer4};
    private ListButton mButton_first, mButton_second,mButton_post;
    private TextView[] mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        switch (v.getId()) {
            case R.id.first_class:break;
                case R.id.second_class:break;
            case R.id.post:break;
        }
    }
}
