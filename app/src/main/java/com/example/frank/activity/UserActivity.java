package com.example.frank.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import com.example.frank.test.R;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.UserUtil;
import com.example.frank.util.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/2/3.
 * 用户数据界面
 */
public class UserActivity extends Activity {

    private PieChart mChart;
    private int win,lose,draw;
    private float score;
    private Typeface tf;
    private UserUtil userUtil = new UserUtil();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        new UserTask().execute();
        SoundUtil.playMusic(this, LoginActivity.setEntity);
    }

    private void initChart() {
        List<String> data_item = new ArrayList<>();
        List<Entry> data = new ArrayList<>();
        for (int i = 0; i < userUtil.PIE_DATA.length; i++) {
            data_item.add(userUtil.PIE_DATA[i]);
            data.add(new Entry(userUtil.getPieData(i),i));
        }

        PieDataSet pieDataSet = new PieDataSet(data,"");
        pieDataSet.setColors(userUtil.colors);
        pieDataSet.setSliceSpace(0);
        PieData pieData = new PieData(data_item,pieDataSet);
        Legend l = mChart.getLegend();
        l.setTextColor(Color.WHITE);
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        mChart.setDrawSliceText(false);
        mChart.setData(pieData);
        mChart.setCenterTextColor(Color.WHITE);
        mChart.setCenterText(userUtil.LABEL);
        mChart.setHoleColor(Color.BLACK);
        mChart.setDescriptionTextSize(20);
        mChart.setUsePercentValues(true);
        mChart.setCenterTextTypeface(tf);
        mChart.setCenterTextSize(20);

        TextView mMatches = (TextView) findViewById(R.id.match);
        mMatches.setText((win + lose + draw) + "");

        TextView mTime = (TextView) findViewById(R.id.score);
        mTime.setText(score + "");
        mChart.invalidate();
    }

    private void initView() {
        mChart = (PieChart) findViewById(R.id.pieChart);
        tf = Typeface.createFromAsset(getAssets(), Utils.GAME_TTF);
    }

    private class UserTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                URL url = new URL(new Utils().getHttpUrl(UserActivity.this) + "user");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                DataOutputStream writer = new DataOutputStream(con.getOutputStream());
                String send = "username=" + LoginActivity.entity.getUsername();
                writer.write(send.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String info =  reader.readLine();
                JSONObject obj = new JSONObject(info);
                win = obj.getInt("win");
                lose = obj.getInt("lose");
                draw = obj.getInt("draw");
                score = (float) obj.getDouble("score");
                return true;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (o != null) {
                userUtil.setDataWin(win);
                userUtil.setDataLost(lose);
                userUtil.setDataDraw(draw);
                initView();
                initChart();
            }
        }
    }
}
