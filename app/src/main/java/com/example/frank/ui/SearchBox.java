package com.example.frank.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.example.frank.test.R;

import java.util.List;

/**
 * Created by frank on 2016/2/3.
 */
public class SearchBox extends LinearLayout {

    private List<String> list;
    private ArrayAdapter<String> data;
    private EditText mEditText;
    private Button mB_search;
    private PopupWindow mPopup;
    private ListView mListView;
    private OnClickListener listener;
    private SearchListener searchListener;
    private Context context;

    public SearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.search_box, this);
        initView();
    }

    public SearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public void setData(List<String> list) {

        if (this.list == null)
            this.list = list;
        else {
            this.list.clear();
            this.list.addAll(list);
        }
        if (this.list.size() > 0) {
            Log.d("set", list.get(0));
            if (data == null) {
                data = new ArrayAdapter<>(context, R.layout.search_item, list);
                if (mListView.getAdapter() == null)
                    mListView.setAdapter(data);
            } else
                data.notifyDataSetChanged();
            mPopup.showAsDropDown(mEditText);
            mListView.setVisibility(VISIBLE);
        } else
            mPopup.dismiss();
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
        if (this.listener != null)
            mB_search.setOnClickListener(this.listener);
    }

    private void initView() {
        mEditText = (EditText) findViewById(R.id.edit_in);
        mB_search = (Button) findViewById(R.id.search_btn);
        if (listener != null)
        mB_search.setOnClickListener(listener);

        mEditText.addTextChangedListener(new FriendTextWatcher());

        View content = LayoutInflater.from(context).inflate(R.layout.search_hint,null);
        mPopup = new PopupWindow(content, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, false);

        mListView = (ListView) content.findViewById(R.id.search_list);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String text = data.getItem(position);
                mEditText.setText(text);
                mPopup.dismiss();
            }
        });
        if (data != null)
            mListView.setAdapter(data);
    }

    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }

    private class FriendTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().trim().equals("")) {
                if (searchListener != null)
                    searchListener.onSearch(s.toString());
                Log.d("search", s.toString());
                return;
            }
            mPopup.dismiss();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public interface SearchListener {
        void onSearch(String s);
    }
}
