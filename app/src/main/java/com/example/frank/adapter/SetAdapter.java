package com.example.frank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
//import com.example.frank.preference.CheckBoxPreference;
import com.example.frank.test.R;
import com.example.frank.ui.Item;
import com.example.frank.ui.ListButton;
import com.example.frank.ui.SwitchButton;
import com.example.frank.util.SoundUtil;
import org.cocos2d.sound.SoundEngine;


/**
 * Created by frank on 2016/2/1.
 */
public class SetAdapter extends BaseAdapter {
    private Context context;
    private SwitchButton mSButton;
    private final String texts1[] = {
            "音效","音乐","振动"
    };
    private final String texts2[] = {
            "用户反馈","关于","游戏规则"
    };

    public SetAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return texts1.length + texts2.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(position < texts1.length) {
            convertView = inflater.inflate(R.layout.set_list, null);
            TextView view = (TextView) convertView.findViewById(R.id.text);
            view.setText(texts1[position]);
            mSButton = (SwitchButton) convertView.findViewById(R.id.check);
            mSButton.setTag(position);
            initListener();
        }
        else {
            convertView = inflater.inflate(R.layout.list_item,null);
            ListButton button = (ListButton) convertView.findViewById(R.id.f_button);
            button.setText(texts2[position - texts1.length]);
            button.setTag(position);
        }
        return convertView;
    }

    private void initListener() {
        mSButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SoundEngine.sharedEngine().playEffect(context, SoundUtil.EFFECT_BUTTON);
                int tag = (int) buttonView.getTag();
                if (tag == 1) {
                    if (isChecked) {
                        SoundEngine.sharedEngine().resumeSound();
                    } else
                        SoundEngine.sharedEngine().pauseSound();
                }

                if (tag == 0) {
                    if (isChecked) {
                        SoundEngine.sharedEngine().resumeSound();
                    } else
                        SoundEngine.sharedEngine().pauseSound();
                }
            }
        });
    }
}
