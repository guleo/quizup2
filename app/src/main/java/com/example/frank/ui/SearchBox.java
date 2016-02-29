package com.example.frank.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.*;
import com.example.frank.test.R;

/**
 * Created by frank on 2016/2/3.
 */
public class SearchBox extends LinearLayout {

    private EditText mEditText;
    private Button mB_back;
    private ImageView mV_delete;
    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.search_box, this);
        initView();
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.edit_in);
        mV_delete = (ImageView) findViewById(R.id.search_delete);
        mB_back = (Button) findViewById(R.id.search_btn_back);
    }

    public SearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
}
