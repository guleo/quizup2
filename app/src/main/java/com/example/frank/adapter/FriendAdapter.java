package com.example.frank.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import com.example.model.UserFriendEntity;
import java.util.List;

/**
 * Created by frank on 2016/3/22.
 * 用户好友适配器
 */
public class FriendAdapter extends RecyclerView.Adapter {

    private List<UserFriendEntity> friends;
    private List<BitmapDrawable> heads;
    private Context context;

    public FriendAdapter(Context context) {
        this.context = context;
    }

    public void setFriends(List<UserFriendEntity> friends, List<BitmapDrawable> heads) {

        this.heads = heads;
        this.friends = friends;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new FriendViewHolder(
                LayoutInflater.from(context).inflate(R.layout.friend_list, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (friends != null) {
            FriendViewHolder holder = (FriendViewHolder) viewHolder;
            holder.username.setText(friends.get(i).getUsername());
            holder.head.setImageDrawable(heads.get(i));
            if (friends.get(i).getSex() == 1)
                holder.sex.setImageResource(R.drawable.female);
            holder.school.setText(friends.get(i).getSchool());
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return friends == null ? 0 : friends.size();
    }


    private class FriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView sex;
        public TextView username, school;
        public RoundImageView head;

        public FriendViewHolder(View itemView) {
            super(itemView);
            sex = (ImageView) itemView.findViewById(R.id.sex);
            head = (RoundImageView) itemView.findViewById(R.id.head);
            username = (TextView) itemView.findViewById(R.id.username);
            school = (TextView) itemView.findViewById(R.id.school);
        }
    }
}
