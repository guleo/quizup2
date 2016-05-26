package com.example.frank.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.frank.adapter.FriendAdapter;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import com.example.frank.ui.SearchBox;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import com.example.model.UserFriendEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/2/3.
 * 显示好友界面
 */
public class FriendActivity extends Activity implements View.OnClickListener, SearchBox.SearchListener {

    private Handler mHandler = new Handler();
    private SweetAlertDialog d;
    private EditText mEdit;
    private String text;
    private static final int SHOW_USER = 0;
    private static final int ADD_USER = 1;
    private static final int FIND_USER = 2;
    private RecyclerView mListView;
    private SearchBox mBox;
    private FriendAdapter mAdapter;
    private List<UserFriendEntity> friends;
    private List<UserFriendEntity> search;
    private BitmapDrawable drawable;
    private List<BitmapDrawable> heads = new ArrayList<>();
    private  String SHOW_USER_SERVLET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend);
        initView();
        SHOW_USER_SERVLET =new Utils().getHttpUrl(this) + "friend";
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        new FriendTask().execute(SHOW_USER);
    }

    private void initView() {
        mListView = (RecyclerView) findViewById(R.id.friends);
        mListView.setHasFixedSize(true);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new FriendAdapter(this);
        mListView.setAdapter(mAdapter);
        mEdit = (EditText) findViewById(R.id.edit_in);
        mBox = (SearchBox) findViewById(R.id.searchBox);
        mBox.setSearchListener(this);
        mBox.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);

    }


    @Override
    protected void onStop() {
        SoundUtil.stopMusic();
        super.onStop();
    }

    private List<UserFriendEntity> getUserFriends(InputStream stream) {
        List<UserFriendEntity> friends = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        // char[] buffer = new char[1024];
        try {

            String s = reader.readLine();
            if (s != null && !s.trim().equals("")) {
                JSONArray a = new JSONArray(s);
                int length = a.length();
                for (int i = 0; i < length; i++) {
                    UserFriendEntity friend = new UserFriendEntity();
                    JSONObject o = a.getJSONObject(i);
                    friend.setHead(o.getString("head"));
                    friend.setUsername(o.getString("username"));
                    friend.setSex(o.getInt("sex"));
                    friend.setSchool(o.getString("school"));
                    friends.add(friend);
                }
                return friends;
            }

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_btn:
                text = mEdit.getText().toString().trim();
                if (text.equals("")) {
                    mAdapter.setFriends(friends, heads);
                    mAdapter.notifyDataSetChanged();
                } else {
                    int index = isFriend(text);
                    if (index == -1) {
                        d = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
                        d.setTitleText("搜索中...");
                        d.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        d.show();
                        new FriendTask().execute(FIND_USER, text);
                    } else {
                        mAdapter.setFriends(friends.subList(index, index + 1), heads.subList(index, index + 1));
                        mAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.add:
                d.dismiss();
                d = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
                d.setTitleText("添加好友?");
                d.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.setTitleText("添加好友中...");
                        sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                        new FriendTask().execute(ADD_USER, text);
                    }
                });
                d.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                d.show();
        }
    }

    private int isFriend(String name) {
        for (int i = 0; i < friends.size(); i++) {
            UserFriendEntity u = friends.get(i);
            if (u.getUsername().equals(name))
                return i;
        }
        return -1;
    }

    @Override
    public void onSearch(String s) {

        List<String> list = new ArrayList<>();
        if (friends != null && friends.size() > 0) {
            for (UserFriendEntity friend : friends)
                if (friend.getUsername().contains(s))
                    list.add(friend.getUsername());
            mBox.setData(list);
        }
    }

    //设置用户查询的对话框内容
    private View setSearchView(UserFriendEntity friend) {
        View view = LayoutInflater.from(getBaseContext()).inflate(R.layout.friend_list, null);
        ImageView sex = (ImageView) view.findViewById(R.id.sex);
        if (friend.getSex() == 1)
            sex.setImageResource(R.drawable.female);
        RoundImageView head = (RoundImageView) view.findViewById(R.id.head);
        if (drawable != null)
            head.setImageDrawable(drawable);
        TextView username = (TextView) view.findViewById(R.id.username);
        username.setText(friend.getUsername());
        TextView school = (TextView) view.findViewById(R.id.school);
        school.setText(friend.getSchool());

        ImageButton button = (ImageButton) view.findViewById(R.id.add);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
        return view;
    }

    private class FriendTask extends AsyncTask {

        @Override
        protected Object doInBackground(final Object[] params) {
            try {

                URL url = new URL(SHOW_USER_SERVLET);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                String info;

                switch ((Integer) params[0]) {
                    case FIND_USER:
                        info = "type=" + params[0] + "&username=" + params[1];
                        out.write(info.getBytes());
                        search = getUserFriends(con.getInputStream());
                        if (search != null && search.size() > 0)
                            drawable = (BitmapDrawable) getHead(search.get(0));
                        break;
                    case SHOW_USER:
                        info = "username=" + LoginActivity.entity.getUsername() +
                                "&type=" + params[0];
                        out.write(info.getBytes());
                        friends = getUserFriends(con.getInputStream());
                        if (friends != null)
                            for (UserFriendEntity entity : friends)
                                heads.add((BitmapDrawable) getHead(entity));
                        break;
                    case ADD_USER:
                        info = "user=" + LoginActivity.entity.getUsername() +
                                "&type=" + params[0] + "&username=" + params[1];
                        out.write(info.getBytes());
                        friends.addAll(getUserFriends(con.getInputStream()));
                        heads.add((BitmapDrawable) getHead(friends.get(friends.size() - 1)));
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return params[0];
        }


        @Override
        protected void onPostExecute(Object o) {
            switch ((Integer) o) {
                case ADD_USER:
                    d.setTitleText("添加成功");
                    d.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    d.showConfirmText(false);
                    d.show();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            d.dismiss();
                            mAdapter.notifyItemInserted(friends.size() - 1);
                        }
                    }, 1000);

                    break;

                case SHOW_USER:
                    mAdapter.setFriends(friends, heads);
                    for (int i = 0; i < friends.size(); i++)
                        mAdapter.notifyItemInserted(i);
                    break;
                case FIND_USER:
                    if (search != null && search.size() > 0) {
                        UserFriendEntity friend = search.get(0);
                        d.setContentView(setSearchView(friend));
                        d.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                        d.show();
                    } else {
                        d.setTitleText("查无此人!");
                        d.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                    break;
            }
        }

        private Drawable getHead(UserFriendEntity friend) {

            String path = friend.getHead();
            if (path != null || !path.trim().equals("")) {
                path = new Utils().getHttpUrl(FriendActivity.this) + "img/" + path;
                try {
                    Log.d("img", path);
                    URL url = new URL(path);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setDoInput(true);
                    con.setRequestMethod("GET");
                    Bitmap bitmap = BitmapFactory.decodeStream(con.getInputStream());
                    con.disconnect();
                    return new BitmapDrawable(null, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
