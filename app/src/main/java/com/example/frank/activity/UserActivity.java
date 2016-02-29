package com.example.frank.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import com.example.frank.test.R;
import com.example.frank.util.UserUtil;
import com.example.frank.util.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 2016/2/3.
 */
public class UserActivity extends Activity {

    private PieChart mChart;

    private Typeface tf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user);
        initView();
        initChart();
    }

    private void initChart() {
        List<String> data_item = new ArrayList<>();
        List<Entry> data = new ArrayList<>();
        UserUtil userUtil = new UserUtil();
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
    }

    private void initView() {
        mChart = (PieChart) findViewById(R.id.pieChart);
        tf = Typeface.createFromAsset(getAssets(), Utils.GAME_TTF);
    }
}
