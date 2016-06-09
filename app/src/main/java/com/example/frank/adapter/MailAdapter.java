package com.example.frank.adapter;

import android.content.Context;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.frank.activity.LoadActivity;
import com.example.frank.activity.LoginActivity;
import com.example.frank.test.R;
import com.example.frank.util.Utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by frank on 2016/5/30.
 * 消息适配器
 */
public class MailAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<String> mails = LoadActivity.mails;
    private static final int ACCEPT_USER = 3;
    private static final int CANCEL_USER = 4;
    boolean uploaded = false;
    boolean isSuccess = false;

    public MailAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mails.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.mail_item, null);
        TextView text = (TextView) convertView.findViewById(R.id.text);
        Button accept = (Button) convertView.findViewById(R.id.accept);
        accept.setOnClickListener(this);
        Button cancel = (Button) convertView.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        cancel.setTag(position);
        accept.setTag(position);
        String t = mails.get(position) + " 请求添加好友";
        text.setText(t);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        int mode;
        int position = (int) v.getTag();
        if (v.getId() == R.id.accept)
            mode = ACCEPT_USER;
        else
        mode = CANCEL_USER;
        String info = "user=" + LoginActivity.entity.getUsername() +
                "&type=" + mode + "&username=" + mails.get(position);
        new MailThread(info, position).start();
        while (true) {
            if (uploaded) {
                if (isSuccess) {
                    mails.remove(position);
                    notifyDataSetChanged();
                } else
                    Toast.makeText(context, "网络故障！", Toast.LENGTH_LONG).show();
                break;
            }
        }

    }

    private class MailThread extends Thread {
        private String info;
        private int position;

        public MailThread(String info, int position) {
            this.info = info;
            this.position = position;
        }

        @Override
        public void run() {
            Looper.prepare();
            upload(info);

        }

        private void upload(String info) {
            try {
                URL url = new URL(new Utils().getHttpUrl(context) + "friend");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                DataOutputStream out = new DataOutputStream(con.getOutputStream());
                out.write(info.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                isSuccess = Boolean.parseBoolean(reader.readLine());
                uploaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
