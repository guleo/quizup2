package com.example.frank.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.example.frank.adapter.MailAdapter;
import com.example.frank.test.R;

/**
 * Created by frank on 2016/5/30.
 * 用户消息界面
 */
public class MailActivity extends Activity {

    private ListView mailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail);
        init();
    }

    private void init() {
        mailList = (ListView) findViewById(R.id.list_mail);
        mailList.setAdapter(new MailAdapter(this));
        //mailList.setFocusable(false);
        mailList.setClickable(false);
    }
}
