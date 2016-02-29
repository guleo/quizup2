package com.example.frank.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.ui.RoundImageView;
import com.example.frank.util.GameUtil;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import org.cocos2d.sound.SoundEngine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by frank on 2016/2/11.
 */
public class MatchRandActivity extends Activity implements View.OnClickListener {

    private static float density;
    public static List<Drawable> drawList;
    //当前已完成的题目计数
    private static int quiz_count;
    private static TextView mLeft_right, mRight_right;
    private static TextView mLeft_score, mRight_score;
    private static RelativeLayout mLayout;
    private static boolean timer_count;
    private RoundImageView mLeft, mRight;
    //双方答题的计数
    private int answer_count;
    //正确选项
    private static int answer_right = 1;
    //答题时间
    private final int TIME_INTERVAL = GameUtil.MATCH_TIME_INTERVAL;
    //总分值
    private final int PROGRESS_MAX = GameUtil.MATCH_RAND_SCORE_SUM;
    //题目总数
    private static final int QUIZ_SUM = GameUtil.MATCH_RAND_QUIZ_SUM;
    //普通题目的总数
    private static final int QUIZ_NOM = GameUtil.MATCH_RAND_QUIZ_NOM;
    //普通题目的分值
    private static final int PROGRESS_NOM = GameUtil.MATCH_RAND_SCORE_NUM;
    //压轴题目的分值
    private static final int PROGRESS_EXT = GameUtil.MATCH_RAND_SCORE_EXT;
    //handler处理的种类
    private final static String TIME = "time";
    private final static String MYANSWER = "myAnswer";
    private final static String MATEANSWER = "mateAnswer";
    private final static String NEXT = "next";
    //选项控件
    private static ListButton mAnswer[];
    //选项id
    private static final int btnId[] = {
            R.id.match_first, R.id.match_second,
            R.id.match_third, R.id.match_four
    };

    //答题计时
    private static int time_count = GameUtil.MATCH_TIME_INTERVAL;
    //对战双方血条
    private static ProgressBar mLBar, mRBar;
    private static TextView mText_sec, mText;
    //计时线程
    private Thread t = new Thread() {
        @Override
        public void run() {
            if (timer_count) {
                if (time_count >= 0) {
                    Message msg = new Message();
                    msg.obj = TIME;
                    msg.arg1 = time_count;
                    time_count -= 1;
                    mhandler.handleMessage(msg);
                    mhandler.postDelayed(this, 1000);
                }
                if (time_count == -1) {
                    timer_count = false;
                    mhandler.postDelayed(next, 2500);
                }
            }
        }
    };

    //显示下一题的线程
    private Thread next = new Thread() {
        @Override
        public void run() {
            Message msg = new Message();
            msg.obj = NEXT;

            quiz_count++;
            if (quiz_count > QUIZ_NOM && quiz_count <= QUIZ_SUM) {
                showExt();
            }
            else if (quiz_count > QUIZ_SUM)
                showResult();
            else {
                mhandler.handleMessage(msg);
                mhandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timer_count = true;
                        t.run();
                    }
                }, 1500);
            }
        }
    };

    //监听对手答题的线程
    private Thread mate = new Thread() {
        @Override
        public void run() {
            /**
             * 添加对手已选择的代码
             */
            answer_count++;
            Message msg = new Message();
            msg.obj = MATEANSWER;
            mhandler.handleMessage(msg);

            //继续监听，直到双方都已选择
            msg.obj = NEXT;
            //mhandler.handleMessage(msg);
        }
    };

    //负责ui更新
    private final Handler mhandler = new MatchHandler(this, this);

    private static class MatchHandler extends Handler {
        private static WeakReference<Context> context;
        private static WeakReference<View.OnClickListener> listener;

        public MatchHandler(Context context, View.OnClickListener l) {
            this.context = new WeakReference<>(context);
            listener = new WeakReference<>(l);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj == MYANSWER) {
                if (msg.arg1 == answer_right) {
                    mRBar.incrementProgressBy(PROGRESS_NOM);
                    mRBar.invalidate();
                    mAnswer[msg.arg1].setButtonColor(GameUtil.MATCH_COLOR_RIGHT);
                    mAnswer[msg.arg1].setTextColor(Color.WHITE);
                    showAdd(mLeft_right, true);
                    int score = Integer.parseInt(String.valueOf(mLeft_score.getText()));
                    score += PROGRESS_NOM;
                    mLeft_score.setText(score + "");
                    mLeft_score.setTextColor(Color.GREEN);
                } else {
                    mAnswer[msg.arg1].setButtonColor(Color.RED);
                    mLeft_score.setTextColor(Color.RED);
                    showAdd(mLeft_right, false);
                }
            }
//            if (msg.obj == MATEANSWER) {
//                /**
//                 * 添加获取对手的选择代码
//                 */
//                if (msg.arg1 == answer_right) {
//                    mLBar.incrementProgressBy(PROGRESS_NOM);
//                    showAdd(mRight_right, true);
//                    int score = Integer.parseInt(String.valueOf(mRight_score.getText()));
//                    score += PROGRESS_NOM;
//                    mRight_score.setText(score + "");
//                    mRight_score.setTextColor(Color.GREEN);
//                } else {
//                    mRight_score.setTextColor(Color.RED);
//                    showAdd(mRight_right, false);
//                }
//            }

            if (msg.obj == TIME) {
                if (msg.arg1 >= 0) {
                    mText_sec.setText(msg.arg1 + "");
                    if (msg.arg1 <= 3)
                        mText_sec.setTextColor(Color.RED);
                    if (msg.arg1 == 0)
                        showAnswer();
                }

            }

            if (msg.obj == NEXT) {
                showNext();
            }
        }

        /**
         * 显示下一题
         */
        private static void showNext() {

            time_count = GameUtil.MATCH_TIME_INTERVAL;
            mText_sec.setText(time_count + "");
            mText_sec.setTextColor(context.get().getResources()
                    .getColor(android.R.color.holo_orange_light));
            mLeft_score.setTextColor(Color.WHITE);
            mRight_score.setTextColor(Color.WHITE);
            AlphaAnimation anim = new AlphaAnimation(0f, 1f);
            anim.setDuration(1500);

            mLayout.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context.get());
            View view = inflater.inflate(R.layout.match_frame, null);
            mLayout.addView(view);

            Typeface tf = Typeface.createFromAsset(context.get().getAssets(), Utils.GAME_TTF);
            mText = (TextView) mLayout.findViewById(R.id.match_text);
            mText.setTypeface(tf);
            mLayout.invalidate();
            mLayout.startAnimation(anim);
            for (int i = 0; i < btnId.length; i++) {
                mAnswer[i] = (ListButton) mLayout.findViewById(btnId[i]);
                mAnswer[i].setOnClickListener(listener.get());
            }
            /**
             * 在这里设置下一道题的题干和选项
             */
        }
    }

    /**
     * 显示分数加倍提醒
     */
    private void showExt() {
        Intent intent = new Intent();
        intent.setClass(this, MatchExtActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 显示加分
     */
    private static void showAdd(TextView view, boolean type) {

        AlphaAnimation anim = new AlphaAnimation(1f, 0f);
        anim.setDuration(1500);
        int text = PROGRESS_NOM;
        if (quiz_count > QUIZ_NOM)
            text = PROGRESS_EXT;
        if (type) {
            view.setTextColor(GameUtil.MATCH_COLOR_RIGHT);
            view.setText("+" + text);
        } else {
            view.setTextColor(Color.RED);
            view.setText("+" + 0);
        }
        view.startAnimation(anim);
    }

    /**
     * 显示正确的答案
     */
    private static void showAnswer() {
        mAnswer[answer_right].setButtonColor(GameUtil.MATCH_COLOR_RIGHT);
        //将view 转换为 Drawable
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = mLayout.getWidth();
        int height = mLayout.getHeight();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width,View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height,View.MeasureSpec.EXACTLY);
        mLayout.measure(widthSpec, heightSpec);
        mLayout.layout(0, 0, mLayout.getMeasuredWidth(), mLayout.getMeasuredHeight());
        mLayout.setDrawingCacheEnabled(true);
        mLayout.buildDrawingCache();
        Bitmap bitmap=Bitmap.createBitmap(mLayout.getDrawingCache(true));
        bitmap.setDensity((int) density);
        Drawable drawable=new BitmapDrawable(null,bitmap);
        mLayout.destroyDrawingCache();
        drawList.add(drawable);

        ScaleAnimation anim = new ScaleAnimation(1f, 1.5f, 1f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(1000);
        for (int i = 0; i < btnId.length; i++)
            mAnswer[i].setVisibility(View.INVISIBLE);

        anim.setFillBefore(false);
        mAnswer[answer_right].setAnimation(anim);
        mAnswer[answer_right].setTextColor(Color.WHITE);
    }

    /**
     * 显示比赛结果
     */
    private  void showResult() {
        Intent intent = new Intent();
        intent.setClass(this, ResultActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SoundEngine.sharedEngine().playSound(this, SoundUtil.MUSIC_BATTLE, true);
        setContentView(R.layout.match_rand);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        drawList = new ArrayList<>();
        timer_count = true;
        time_count = 10;
        quiz_count = 1;
        answer_count = 0;
        initView();
        t.start();
        mate.start();
    }

    private void initView() {

        mText = (TextView) findViewById(R.id.match_text);
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), Utils.GAME_TTF);
        mText.setTypeface(tf);
        mText_sec = (TextView) findViewById(R.id.match_sec);
        mText_sec.setText(TIME_INTERVAL + "");

        View view = findViewById(R.id.match_right);
        mRBar = (ProgressBar) view.findViewById(R.id.match_bar);
        mRBar.setMax(PROGRESS_MAX);
        tf = Typeface.createFromAsset(getBaseContext().getAssets(), Utils.SCORE_TTF);
        mRight_right = (TextView) view.findViewById(R.id.match_add);
        mRight_right.setTypeface(tf);
        mRight_score = (TextView) view.findViewById(R.id.match_score);

        view = findViewById(R.id.match_left);
        mLBar = (ProgressBar) view.findViewById(R.id.match_bar);
        mRBar.setMax(PROGRESS_MAX);
        mLeft_right = (TextView) view.findViewById(R.id.match_add);
        mLeft_right.setTypeface(tf);
        mLeft_score = (TextView) view.findViewById(R.id.match_score);

        mAnswer = new ListButton[btnId.length];
        for (int i = 0; i < btnId.length; i++) {
            mAnswer[i] = (ListButton) findViewById(btnId[i]);
            mAnswer[i].setOnClickListener(this);
        }

        mLayout = (RelativeLayout) findViewById(R.id.match_frame);
        mLayout.setBackgroundColor(Utils.COLOR_BKG);
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < btnId.length; i++) {
            mAnswer[i].setClickable(false);
            if (v.getId() == btnId[i]) {
                answer_count++;
                Message msg = new Message();
                msg.obj = MYANSWER;
                msg.arg1 = i;
                timer_count = false;
                mhandler.handleMessage(msg);
            }
        }
    }


    //返回处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            Message msg = new Message();
            msg.obj = NEXT;
            mhandler.handleMessage(msg);
            mhandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    timer_count = true;
                    t.run();
                }
            }, 1500);
        }
        if (requestCode == 1) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        SoundEngine.sharedEngine().pauseSound();
        super.onPause();
    }

    @Override
    protected void onRestart() {
        SoundEngine.sharedEngine().resumeSound();
        super.onResume();
    }

}
