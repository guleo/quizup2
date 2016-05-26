package com.example.frank.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.frank.adapter.ClassAdapter;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.util.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/5/3.
 * 分类界面
 */
public class ClassActivity extends Activity {
    private List<String> list = new ArrayList<>();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kind);
        initView();
        initData();
    }

    private void initData() {
        final int type = getIntent().getIntExtra("type", -1);
        if (type == Utils.FIRST_CLASS) {
            for (List<String> a : LoadActivity.type)
                list.add(a.get(0));
        } else if (type == Utils.SUB_CLASS) {
            int first = getIntent().getIntExtra("first", -1);
            List<String> ref = LoadActivity.type.get(first);
            list.addAll(ref.subList(1, ref.size()));

        }
        ClassAdapter adapter = new ClassAdapter(list, this, type);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListButton button = (ListButton) view;
                Intent intent = new Intent(ClassActivity.this,ShareActivity.class);
                intent.putExtra("class", button.getText());
                intent.putExtra("index",position);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list_class);
    }

}
