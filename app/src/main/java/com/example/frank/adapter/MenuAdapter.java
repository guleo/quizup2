package com.example.frank.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.frank.test.R;
import com.example.frank.ui.Item;
import com.example.frank.util.Utils;

/**
 * Created by frank on 2015/2/26.
 */
public class MenuAdapter extends BaseAdapter {

    private Typeface tf;
    private Context context;

    public MenuAdapter(Context context) {
        this.context = context;
        tf = Typeface.createFromAsset(context.getAssets(), Utils.GAME_TTF);
    }

    private final int[] imageId = {
            R.drawable.player,
            R.drawable.friend,
            R.drawable.set,
            R.drawable.share
    };

    private final String[] activityName = {
            "UserActivity", "FriendActivity", "SetActivity","ShareActivity"
    };
    public static final String[] texts = {
            "你的战绩",
            "你的战友",
            "你的设定",
            "你的分享"
    };

    @Override
    public int getCount() {
        return imageId.length;
    }

    @Override
    public Object getItem(int i) {
        return new Item(Item.ITEM, texts[i], activityName[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.menu_list, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        imageView.setImageResource(imageId[i]);
        TextView text = (TextView) view.findViewById(R.id.text);
        text.setText(texts[i]);
        text.setTypeface(tf);
        return view;
    }
}
