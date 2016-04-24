package com.example.frank.util;

import android.graphics.Color;

/**
 * Created by frank on 2016/2/15.
 */
public class GameUtil {

    //随机匹配的相关参数
    public static final int MATCH_RAND_QUIZ_SUM = 3;
    public static final int MATCH_RAND_QUIZ_NOM = 2;
    public static final int MATCH_RAND_QUIZ_EXT = MATCH_RAND_QUIZ_SUM - MATCH_RAND_QUIZ_NOM;
    public static final int MATCH_TIME_INTERVAL = 10;
    public static final int MATCH_RAND_SCORE_NUM = 20;
    public static final int MATCH_RAND_SCORE_EXT = 40;
    public static final int MATCH_RAND_SCORE_SUM = MATCH_RAND_QUIZ_EXT * MATCH_RAND_SCORE_EXT +
            MATCH_RAND_QUIZ_NOM * MATCH_RAND_SCORE_NUM;

    //自测的相关参数
    public static final int MATCH_PC_SCORE_NUM = 20;
    public static final int MATCH_PC_QUIZ_SUM = 5;
    //比赛的两种模式
    public static final int MATCH_RAND_MODE = 0;
    public static final int MATCH_PC_MODE = 1;


    public static final int MATCH_COLOR_RIGHT = Color.rgb(0, 148, 46);

    //玩家自测的相关参数

    //游戏结果
    public static final String SHOW_SUC = "YOU WIN";
    public static final String SHOW_FAIL = "YOU LOSE";
    public static final String SHOW_DRAW = "YOU DRAW";

    /**
     * 分数计算模式
     *
     * @param sec  回答时计数显示
     * @param mode 是普通分数，还是双倍分数模式
     * @return 答题德妃
     */
    public static int generateScore(int sec, int mode) {
        return sec * mode / MATCH_TIME_INTERVAL;
    }
}
