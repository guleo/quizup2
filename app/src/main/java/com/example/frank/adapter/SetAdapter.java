package com.example.frank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.example.frank.activity.LoginActivity;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.ui.SwitchButton;
import com.example.frank.util.SoundUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by frank on 2016/2/1.
 */
public class SetAdapter extends BaseAdapter {
    private Context context;
    private SwitchButton mSButton;
    private Map<String, Method> map = new HashMap<>();
    private final String texts1[] = {
            "音效", "自动登录", "音乐", "振动"
    };
    private final String texts2[] = {
            "用户反馈", "关于", "游戏规则"
    };

    public SetAdapter(Context context) {
        this.context = context;
        try {
            Class c = Class.forName("com.example.model.SetEntity");
            Method[] methods = c.getDeclaredMethods();
            int i = 0, k = 0;
            for (int j = 0; j < methods.length; j++) {
                if (methods[j].getName().startsWith("is"))
                    map.put(texts1[i++], methods[j]);
                else if (methods[j].getName().startsWith("set"))
                    map.put(texts1[k++] + "Set", methods[j]);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        if (position < texts1.length) {
            convertView = inflater.inflate(R.layout.set_list, null);
            TextView view = (TextView) convertView.findViewById(R.id.text);
            view.setText(texts1[position]);
            mSButton = (SwitchButton) convertView.findViewById(R.id.check);
            mSButton.setTag(position);
            try {
                mSButton.setChecked((Boolean) map.get(texts1[position]).invoke(LoginActivity.setEntity));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            initListener();
        } else {
            convertView = inflater.inflate(R.layout.list_item, null);
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
                int pos = (int) buttonView.getTag();
                try {
                    map.get(texts1[pos] + "Set").invoke(LoginActivity.setEntity, isChecked);
                    switch (pos) {
                        case 0:
                            if (isChecked) {
                                SoundUtil.playEffect(context, LoginActivity.setEntity, null);
                            }
                            break;
                        case 2:
                            if (isChecked) {
                                SoundUtil.playMusic(context, LoginActivity.setEntity);
                            } else {
                                SoundUtil.pauseMusic(context, LoginActivity.setEntity);
                            }
                            break;
                        case 3:
                            if (isChecked) {
                                SoundUtil.playViberate(context, LoginActivity.setEntity);
                            }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
