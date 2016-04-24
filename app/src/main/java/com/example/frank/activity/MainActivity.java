package com.example.frank.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;;
import android.view.*;
import android.widget.*;
import com.example.frank.adapter.MainAdapter;
import com.example.frank.adapter.MenuAdapter;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.Item;
import com.example.frank.ui.MenuView;
import com.example.frank.ui.PinnedSectionListView;
import com.example.frank.ui.RoundImageView;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;

/**
 * Created by frank on 2016/1/27.
 * 游戏主界面
 */
public class MainActivity extends Activity implements View.OnClickListener{

    private static final String EXIT_TEXT = "确定要退出吗?";
    private Intent intent;
    private Button match_pc;
    private Button match_rand;
    private ListView mListView;
    private MainAdapter mMainAdapter;
    private PinnedSectionListView mExListView;
    private MenuView menuView;
    private ImageButton mSet;
    private RoundImageView mHeadImage,mSHeadImage;

    private TextView mTitle;
    private boolean hasHeaderAndFooter = true;
    private boolean isFastScroll = true;
    private boolean addPadding;
    private boolean isShadowVisible = true;
    private int mDatasetUpdateCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = new Intent();
        setContentView(R.layout.maingame);
        if (savedInstanceState != null) {
            isFastScroll = savedInstanceState.getBoolean("isFastScroll");
            addPadding = savedInstanceState.getBoolean("addPadding");
            isShadowVisible = savedInstanceState.getBoolean("isShadowVisible");
            hasHeaderAndFooter = savedInstanceState.getBoolean("hasHeaderAndFooter");
        }

        SoundUtil.playMusic(this, LoginActivity.setEntity);
        initializeView();
        //     initializeHeaderAndFooter();
        initializeAdapter();
        initializePadding();
        initializeClick();
    }

    private void initializeClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SoundUtil.playEffect(getBaseContext(),LoginActivity.setEntity,view);
                Item item = (Item) mListView.getAdapter().getItem(position);
                intent.setClassName(getApplicationContext(), Utils.ACTIVITY_PACKAGE_PATH + "." + item.getJumpActivityName());
                startActivity(intent);
            }
        });
        mSet.setOnClickListener(this);
        mHeadImage.setOnClickListener(this);

    }

    private void initializeView() {
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setAdapter(new MenuAdapter(this));
        menuView = (MenuView) findViewById(R.id.menuView);
        mSet = (ImageButton) findViewById(R.id.menuButton);
        mExListView = (PinnedSectionListView) findViewById(R.id.expand_list);
        mHeadImage = (RoundImageView) findViewById(R.id.image_head_cur);
        mSHeadImage = (RoundImageView) findViewById(R.id.smallHead);
        mTitle = (TextView) findViewById(R.id.textView);
        if (LoginActivity.entity != null)
        mTitle.setText(LoginActivity.entity.getUsername());
        if (LoginActivity.HeadDrawabale != null) {
            mHeadImage.setImageDrawable(LoginActivity.HeadDrawabale);
            mSHeadImage.setImageDrawable(LoginActivity.HeadDrawabale);
        }
        //设置图标为不显示状态
        mExListView.setGroupIndicator(null);
    }

    @Override
    protected void onPause() {
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
        super.onPause();
    }

    @Override
    protected void onStop() {
        SoundUtil.stopMusic();
        super.onStop();
    }

    @Override
    protected void onResume() {
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isFastScroll", isFastScroll);
        outState.putBoolean("addPadding", addPadding);
        outState.putBoolean("isShadowVisible", isShadowVisible);
        outState.putBoolean("hasHeaderAndFooter", hasHeaderAndFooter);
    }



    private void initializePadding() {
        float density = getResources().getDisplayMetrics().density;
        int padding = addPadding ? (int) (16 * density) : 0;
        mExListView.setPadding(padding, padding, padding, padding);
    }



    @SuppressLint("NewApi")
    private void initializeAdapter() {
        mMainAdapter = new MainAdapter(this);
        mExListView.setAdapter(mMainAdapter);
        mExListView.setFastScrollEnabled(isFastScroll);
        mExListView.setOnGroupClickListener(mMainAdapter.getListener());
        mExListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                for (int i = 0; i < mMainAdapter.getGroupCount(); i++) {
                    if (i != groupPosition)
                        mExListView.collapseGroup(i);
                }
            }
        });
        if (isFastScroll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mExListView.setFastScrollAlwaysVisible(true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.menuButton:
                SoundUtil.playEffect(getApplicationContext(), LoginActivity.setEntity,v);
                menuView.toggle();
                break;
            case R.id.image_head_cur:
                intent.setClass(this,UserHeadActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (LoginActivity.HeadDrawabale != null) {
            mHeadImage.setImageDrawable(LoginActivity.HeadDrawabale);
            mSHeadImage.setImageDrawable(LoginActivity.HeadDrawabale);
        }
    }

    @Override
    public void onBackPressed() {
        final SweetAlertDialog dialog  = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(EXIT_TEXT);
        dialog.setCancelText("no");
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.hide();
            }
        });
        dialog.show();
    }
}
