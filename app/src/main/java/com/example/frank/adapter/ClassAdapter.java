package com.example.frank.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.example.frank.ui.ListButton;

import java.util.List;

/**
 * Created by frank on 2016/5/3.
 * 显示分类适配器
 */
public class ClassAdapter extends BaseAdapter{

    private List<String> data;
    private Context context;
    private int type;

    public ClassAdapter(List<String> data, Context context, int type) {
        this.data = data;
        this.context = context;
        this.type = type;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListButton button = new ListButton(context, null);
        button.setText(data.get(position));
        button.setTag(position);
        button.setFocusable(false);
        convertView = button;
        convertView.setFocusable(false);
        convertView.setClickable(false);
        return convertView;
    }

}
