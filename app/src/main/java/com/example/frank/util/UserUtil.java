package com.example.frank.util;

import android.graphics.Color;

/**
 * Created by frank on 2016/2/3.
 */
public class UserUtil {
    public final String PIE_DATA[] = {"胜利", "打平", "失败"};
    public final int colors[] = {Color.GREEN,Color.rgb(114, 188, 223),Color.RED};
    public final String LABEL = "你的战绩";
    public final String USER_INFO[] = {"游戏场次", "平均得分", "平均答题时间"};
    private float data[] = {50, 40, 10};

    public float getPieData(int i) {
        if (i < PIE_DATA.length && i >= 0)
            return data[i];
        return 0;
    }
}
