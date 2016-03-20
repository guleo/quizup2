package com.example.frank.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.widget.ListView;
import com.example.frank.adapter.SetAdapter;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import com.example.persist.sqlite.LoginSQLiteHelper;
import com.example.persist.sqlite.SetSQLiteHelper;

/**
 * Created by frank on 2016/1/31.
 */
public class SetActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        setContentView(R.layout.set);
        intializeView();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
    }


    private void intializeView() {
        ListView mListView = (ListView) findViewById(R.id.listSet);
        mListView.setAdapter(new SetAdapter(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundUtil.resumeMusic(this, LoginActivity.setEntity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        updateData();

    }

    //重新存储用户设置
    private void updateData() {
        SQLiteOpenHelper dbHelper = new LoginSQLiteHelper(this, SetSQLiteHelper.CREATE_SET_TABLE, null, Utils.VERSION);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null) {
            db.update(SetSQLiteHelper.TABLE_NAME, LoginActivity.setEntity.returnValues(), null, null);
            db.close();
        }
    }
}
