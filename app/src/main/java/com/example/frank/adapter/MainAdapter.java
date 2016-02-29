package com.example.frank.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.frank.activity.MateActivity;
import com.example.frank.test.R;
import com.example.frank.ui.Item;
import com.example.frank.ui.ListButton;
import com.example.frank.ui.PinnedSectionListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by frank on 2016/2/5.
 */
public class MainAdapter extends BaseExpandableListAdapter implements SectionIndexer, PinnedSectionListView.PinnedSectionListAdapter, View.OnClickListener {

    private Context context;
    private ExpandableListView.OnGroupClickListener mListener;
    private Button match_pc,match_rand;
    private List<Item> data = new ArrayList<>();
    private Item[] sections;
    private LayoutInflater mInflater;

    public MainAdapter(Context context, Item[] sections) {
        this.context = context;
        if (sections != null)
            for (Item i : sections)
                this.data.add(i);
        mInflater = LayoutInflater.from(context);
        generateDataset('A', 'Z');

    }


    public MainAdapter(Context context) {
        this(context, null);
    }

    public ExpandableListView.OnGroupClickListener getListener() {
        return mListener;
    }

    //对列表进行文本设置
    public void generateDataset(char from, char to) {
        final int sectionsNumber = to - from + 1;
        sections = new Item[sectionsNumber];
        int sectionPosition = 0, listPosition = 0;
        for (char i = 0; i < sectionsNumber; i++) {
            Item section = new Item(Item.SECTION, String.valueOf((char) ('A' + i)));
            section.sectionPosition = sectionPosition;
            sections[sectionPosition] = section;
            section.listPosition = listPosition++;

            data.add(section);

            //设置每一个条目的内容
            final int itemsNumber = (int) Math.abs((Math.cos(2f * Math.PI / 3f * sectionsNumber / (i + 1f)) * 25f));
            for (int j = 0; j < itemsNumber; j++) {
                Item item = new Item(Item.ITEM, section.getText().toUpperCase(Locale.ENGLISH) + " - " + j);
                item.sectionPosition = sectionPosition;
                item.listPosition = listPosition++;
                data.add(item);
            }

            sectionPosition++;
        }

        initListener();
    }

    private void initListener() {
        mListener = new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return data.get(groupPosition).getType() == Item.SECTION;
            }


        };
    }

    private static final int[] COLORS = new int[]{
            android.R.color.holo_green_light, android.R.color.holo_orange_light,
            android.R.color.holo_blue_bright, android.R.color.holo_red_light};

    @Override
    public int getGroupType(int groupPosition) {
        return data.get(groupPosition).getType();
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (data.get(groupPosition).getType() == Item.SECTION)
            return null;
        return data.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        if (data.get(groupPosition).getType() == Item.SECTION)
            return -1;
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        Item item = (Item) getGroup(groupPosition);
        if (item.getType() == Item.SECTION) {
            TextView mText = (TextView) mInflater.inflate(R.layout.list_sector, parent, false);
            mText.setText(item.toString());
            mText.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
            return mText;
        } else {
            convertView = mInflater.inflate(R.layout.list_item, parent, false);
            view = convertView.findViewById(R.id.list);
            convertView.setTag("" + groupPosition);
            view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));

            ListButton listButton = (ListButton) convertView.findViewById(R.id.f_button);
            listButton.setClickable(false);
            if (isExpanded) {
                Drawable d = context.getResources().getDrawable(R.drawable.arrow_expand);
                listButton.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
            }
            return convertView;
        }
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Item item = (Item) getGroup(groupPosition);
        convertView = mInflater.inflate(R.layout.list_item_fold, parent, false);
        View view = convertView.findViewById(R.id.item_fold);
        match_pc = (Button) convertView.findViewById(R.id.match_pc);
        match_rand = (Button) convertView.findViewById(R.id.match_rand);
        match_pc.setOnClickListener(this);
        match_rand.setOnClickListener(this);
        view.setBackgroundColor(parent.getResources().getColor(COLORS[item.sectionPosition % COLORS.length]));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (sectionIndex >= sections.length) {
            sectionIndex = sections.length - 1;
        }
        return sections[sectionIndex].listPosition;
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position >= getGroupCount()) {
            position = getGroupCount() - 1;
        }
        return ((Item) (getGroup(position))).sectionPosition;
    }

    @Override
    public boolean isItemViewTypePinned(int i) {
        return i == Item.SECTION;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.match_rand:
                intent.setClass(context, MateActivity.class);
                context.startActivity(intent);
                break;
        }
    }
}
