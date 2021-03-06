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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.frank.dialog.SweetAlertDialog;
import com.example.frank.test.R;
import com.example.frank.ui.ListButton;
import com.example.frank.ui.RoundImageView;
import com.example.frank.util.GameUtil;
import com.example.frank.util.SoundUtil;
import com.example.frank.util.Utils;
import com.example.model.QuestionEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by frank on 2016/2/11.
 * 随机匹配时的游戏界面
 */
public class MatchRandActivity extends Activity implements View.OnClickListener {

    private String HTTP_SERVLET;
    private static float density;
    private static String rival;
    private static boolean isRight;
    private static List<QuestionEntity> questions;
    public static List<Drawable> drawList;
    private int time_out_count = 0;
    private boolean timer_out_count = true;
    private int time_out_mode = 5;
    //当前已完成的题目计数
    private static int quiz_count;
    private static TextView mLeft_right, mRight_right;
    private static TextView mLeft_score, mRight_score;
    private static RelativeLayout mLayout;
    private static boolean timer_count;
    //双方答题的计数
    private static int answer_count;
    //用户总用时
    private static int time_user_count;
    //用户答对题数
    private static int right_count;
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
    //显示题目的模式
    private static int mode;
    //handler处理的种类
    private final static String TIME = "time";
    private final static String MYANSWER = "myAnswer";
    private final static String MATEANSWER = "mateAnswer";
    private final static String NEXT = "next";
    private final static String ANSWER = "answer";
    private final static String RUNAWAY = "runaway";
    //选项控件
    private static ListButton mAnswer[];
    //选项id
    private static final int btnId[] = {
            R.id.match_first, R.id.match_second,
            R.id.match_third, R.id.match_four
    };

    //题目
    private static TextView mText;

    //答题计时
    private static int time_count = GameUtil.MATCH_TIME_INTERVAL;
    //用户答题计时
    private static int time_answer;
    private static boolean isAnswer;
    //对战双方血条
    private static ProgressBar mLBar, mRBar;
    private static TextView mText_sec;
    //对手的url
    private static String url_rival;
    //对手的分数
    private static int score_rival;
    //用户答一题的计分
    private static int s;

    //发送答案线程
    private class sendThread extends Thread {
        @Override
        public void run() {
            try {
                URL url = new URL(HTTP_SERVLET);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setDoInput(true);
                con.setDoOutput(true);
                DataOutputStream output = new DataOutputStream(con.getOutputStream());
                String send = "username=" + LoginActivity.entity.getUsername() +
                        "&rival=" + rival + "&url=" + url_rival + "&right=" + isRight +
                        "&score=" + s;
                output.write(send.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                url_rival = reader.readLine();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Thread time_out = new Thread() {
        @Override
        public void run() {
            if (timer_out_count && answer_count < 2) {
                time_out_count++;
                mhandler.postDelayed(this, 1000);
                if (time_out_count == time_out_mode) {
                    timer_out_count = false;
                    Message msg = new Message();
                    msg.obj = RUNAWAY;
                    mhandler.sendMessage(msg);
                }
            }
        }
    };
    //计时线程
    private Thread t = new Thread() {
        @Override
        public void run() {
            if (timer_count) {
                if (time_count >= 0) {
                    Message msg = new Message();
                    msg.obj = TIME;
                    msg.arg1 = time_count;
                    mhandler.handleMessage(msg);
                    time_count -= 1;
                    mhandler.postDelayed(this, 1000);
                }

                if (time_count == -1 && answer_count == 1 && !isAnswer || answer_count == 2) {
                    if (!isAnswer) {
                        Message msg = new Message();
                        msg.obj = MYANSWER;
                        msg.arg1 = -1;
                        mhandler.sendMessage(msg);
                    }
                    timer_count = false;
                    mhandler.postDelayed(next, 2500);
                }
                if (time_count == -1 && (answer_count == 1 && isAnswer || answer_count == 0))
                    time_out.run();
            } else if (answer_count == 1) {
                time_out.run();
                time_out_mode = 15;
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
            } else if (quiz_count > QUIZ_SUM)
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
            int arg1 = 0;
            try {
                ServerSocket s = new ServerSocket(10000);
                Socket socket = s.accept();
                BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String info = r.readLine();
                int index = info.lastIndexOf(",");
                Log.d("index", index + "");
                String right = info.substring(0, index);
                arg1 = Boolean.parseBoolean(right) ? 1 : 0;
                if (arg1 == 1)
                    score_rival = Integer.parseInt(info.substring(index + 1));
                Log.d("score_rival", score_rival + "");
                r.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            answer_count++;
            Message msg = new Message();
            msg.obj = MATEANSWER;
            msg.arg1 = arg1;
            Log.d("answer_count", answer_count + "");
            mhandler.sendMessage(msg);
            timer_out_count = false;
            while (true) {
                if (answer_count == 2) {
                    //继续监听，直到双方都已选择
                    Message msg1 = new Message();
                    msg1.obj = ANSWER;
                    msg1.arg1 = 0;
                    mhandler.sendMessage(msg1);
                    mhandler.postDelayed(next, 2500);
                    break;
                }
                if (!timer_count)
                    break;
            }
        }
    };

    //负责ui更新
    private final Handler mhandler = new MatchHandler(this, this);

    private class MatchHandler extends Handler {
        private WeakReference<Context> context;
        private WeakReference<View.OnClickListener> listener;

        public MatchHandler(Context context, View.OnClickListener l) {
            this.context = new WeakReference<>(context);
            listener = new WeakReference<>(l);
        }


        @Override
        public void handleMessage(Message msg) {
            if (msg.obj == MYANSWER) {
                isAnswer = true;
                if (msg.arg1 == answer_right) {
                    isRight = true;
                    right_count++;
                    s = GameUtil.generateScore(time_answer, mode);
                    mRBar.incrementProgressBy(s);
                    mRBar.invalidate();
                    mAnswer[msg.arg1].setButtonColor(GameUtil.MATCH_COLOR_RIGHT);
                    mAnswer[msg.arg1].setTextColor(Color.WHITE);
                    showAdd(mLeft_right, true, s);
                    int score = Integer.parseInt(String.valueOf(mLeft_score.getText()));
                    score += s;
                    mLeft_score.setText(score + "");
                    mLeft_score.setTextColor(Color.GREEN);
                } else {
                    isRight = false;
                    SoundUtil.playViberate(context.get(), LoginActivity.setEntity);
                    if (msg.arg1 != -1)
                        mAnswer[msg.arg1].setButtonColor(Color.RED);
                    mLeft_score.setTextColor(Color.RED);
                    showAdd(mLeft_right, false, 0);
                }
                new sendThread().start();
            }
            if (msg.obj == MATEANSWER) {
                /**
                 * 添加获取对手的选择代码
                 */
                if (msg.arg1 == 1) {
                    mLBar.incrementProgressBy(score_rival);
                    showAdd(mRight_right, true, score_rival);
                    int score = Integer.parseInt(String.valueOf(mRight_score.getText()));
                    score += score_rival;
                    mRight_score.setText(score + "");
                    mRight_score.setTextColor(Color.GREEN);
                } else {
                    mRight_score.setTextColor(Color.RED);
                    showAdd(mRight_right, false, 0);
                }
            }

            if (msg.obj == TIME || msg.obj == ANSWER) {
                if (msg.arg1 >= 0) {
                    mText_sec.setText(msg.arg1 + "");
                    if (msg.arg1 <= 3)
                        mText_sec.setTextColor(Color.RED);
                    if (msg.arg1 == 0 && (answer_count == 1 && isAnswer))
                        showAnswer();
                    if (answer_count==2)
                        showAnswer();
                }
            }

            if (msg.obj == NEXT) {
                Log.d("answer_count", answer_count + "");
                showNext();
            }

            if (msg.obj == RUNAWAY) {
                timer_count = false;
                timer_out_count = false;
                SweetAlertDialog dialog = new SweetAlertDialog(MatchRandActivity.this, SweetAlertDialog.WARNING_TYPE);
                dialog.setTitleText("对手逃跑了...");
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent intent = new Intent();
                        intent.setClass(MatchRandActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
                dialog.show();
            }
        }


        /**
         * 显示下一题
         */
        private void showNext() {
            answer_count = 0;
            new Thread() {
                @Override
                public void run() {
                    int arg1 = 0;

                    try {
                        ServerSocket s = new ServerSocket(10000);
                        Socket socket = s.accept();
                        BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String info = r.readLine();
                        int index = info.lastIndexOf(",");
                        Log.d("index", index + "");
                        String right = info.substring(0, index);
                        arg1 = Boolean.parseBoolean(right) ? 1 : 0;
                        if (arg1 == 1)
                            score_rival = Integer.parseInt(info.substring(index + 1));
                        Log.d("score_rival", score_rival + "");
                        r.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    answer_count++;
                    Message msg = new Message();
                    msg.obj = MATEANSWER;
                    msg.arg1 = arg1;
                    isAnswer = false;
                    Log.d("answer_count", answer_count + "");
                    sendMessage(msg);
                    while (true) {
                        if (answer_count == 2) {
                            //继续监听，直到双方都已选择
                            Message msg1 = new Message();
                            msg1.obj = ANSWER;
                            msg1.arg1 = 0;
                            sendMessage(msg1);
                            postDelayed(next, 2500);
                            break;
                        }
                        if (!timer_count)
                            break;
                    }
                }
            }.start();

            time_count = GameUtil.MATCH_TIME_INTERVAL;
            time_out_count = 0;
            time_out_mode = 5;
            timer_out_count = true;
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

            QuestionEntity question = questions.get(quiz_count - 1);
            mText.setText(question.getText());

            List<Method> methods = randomOptions();
            try {
                if (methods != null)
                    for (int i = 0; i < btnId.length; i++) {
                        mAnswer[i] = (ListButton) mLayout.findViewById(btnId[i]);
                        mAnswer[i].setOnClickListener(listener.get());
                        mAnswer[i].setText((String) methods.get(i).invoke(question));
                    }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            /**
             * 在这里设置下一道题的题干和选项
             */

        }


        /**
         * 显示加分
         */
        private void showAdd(TextView view, boolean type, int score) {

            AlphaAnimation anim = new AlphaAnimation(1f, 0f);
            anim.setDuration(1500);

            if (type) {
                view.setTextColor(GameUtil.MATCH_COLOR_RIGHT);
                view.setText("+" + score);
            } else {
                view.setTextColor(Color.RED);
                view.setText("+" + score);
            }
            view.startAnimation(anim);
        }
    }

    /**
     * 显示分数加倍提醒
     */
    private void showExt() {
        timer_out_count = false;
        timer_count = false;
        mode = PROGRESS_EXT;
        Intent intent = new Intent();
        intent.setClass(this, MatchExtActivity.class);
        startActivityForResult(intent, 0);
    }


    /**
     * 显示正确的答案
     */
    private static void showAnswer() {
        score_rival = 0;
        Log.d("right", answer_right + "");
        mAnswer[answer_right].setButtonColor(GameUtil.MATCH_COLOR_RIGHT);
        //将view 转换为 Drawable
        mLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int width = mLayout.getWidth();
        int height = mLayout.getHeight();
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        mLayout.measure(widthSpec, heightSpec);
        mLayout.layout(0, 0, mLayout.getMeasuredWidth(), mLayout.getMeasuredHeight());
        mLayout.setDrawingCacheEnabled(true);
        mLayout.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(mLayout.getDrawingCache(true));
        bitmap.setDensity((int) density);
        Drawable drawable = new BitmapDrawable(null, bitmap);
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
    private void showResult() {
        Intent intent = new Intent();
        intent.setClass(this, ResultActivity.class);
        int left = Integer.parseInt(String.valueOf(mLeft_score.getText()));
        int right = Integer.parseInt(String.valueOf(mRight_score.getText()));
        String result;
        if (left > right)
            result = GameUtil.SHOW_SUC;
        else if (left == right)
            result = GameUtil.SHOW_DRAW;
        else
            result = GameUtil.SHOW_FAIL;
        intent.putExtra("type", GameUtil.MATCH_RAND_MODE);
        intent.putExtra("rival", rival);
        intent.putExtra("result", result);
        intent.putExtra("score_user", String.valueOf(left));
        intent.putExtra("score_rival", String.valueOf(right));
        intent.putExtra("time", time_user_count);
        intent.putExtra("right_count", right_count);
        startActivityForResult(intent, 1);
    }


    private void parseJSON(String json) {
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                QuestionEntity que = new QuestionEntity();
                que.setText(obj.getString("text"));
                que.setRight(obj.getString("right"));
                que.setWrong1(obj.getString("wrong1"));
                que.setWrong2(obj.getString("wrong2"));
                que.setWrong3(obj.getString("wrong3"));
                questions.add(que);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HTTP_SERVLET = new Utils().getHttpUrl(this) + "answer";
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        setContentView(R.layout.match_rand);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        isAnswer = false;
        density = dm.density;
        drawList = new ArrayList<>();
        timer_count = true;
        time_count = 10;
        quiz_count = 1;
        answer_count = 0;
        right_count = 0;
        time_user_count = 0;
        mode = PROGRESS_NOM;
        url_rival = getIntent().getStringExtra("url_rival");
        questions = new ArrayList<>();
        parseJSON(getIntent().getStringExtra("json"));
        initView();
        t.start();
        mate.start();
    }

    private void initView() {

        mText = (TextView) findViewById(R.id.match_text);
        Typeface tf = Typeface.createFromAsset(getBaseContext().getAssets(), Utils.GAME_TTF);
        mText.setTypeface(tf);
        mText.setText(questions.get(0).getText());
        mText_sec = (TextView) findViewById(R.id.match_sec);
        mText_sec.setText(TIME_INTERVAL + "");

        View view = findViewById(R.id.match_right);
        mRBar = (ProgressBar) view.findViewById(R.id.match_bar);
        mRBar.setMax(PROGRESS_MAX);
        tf = Typeface.createFromAsset(getBaseContext().getAssets(), Utils.SCORE_TTF);
        mRight_right = (TextView) view.findViewById(R.id.match_add);
        mRight_right.setTypeface(tf);
        mRight_score = (TextView) view.findViewById(R.id.match_score);
        RoundImageView mRight = (RoundImageView) view.findViewById(R.id.match_view);
        mRight.setImageDrawable(MateActivity.rivalDrawable);
        TextView mRightUser = (TextView) view.findViewById(R.id.textView1);
        rival = getIntent().getStringExtra("rival");

        mRightUser.setText(rival);

        view = findViewById(R.id.match_left);
        mLBar = (ProgressBar) view.findViewById(R.id.match_bar);
        mRBar.setMax(PROGRESS_MAX);
        mLeft_right = (TextView) view.findViewById(R.id.match_add);
        mLeft_right.setTypeface(tf);
        mLeft_score = (TextView) view.findViewById(R.id.match_score);
        RoundImageView mLeft = (RoundImageView) view.findViewById(R.id.match_view);
        mLeft.setImageDrawable(LoginActivity.HeadDrawabale);
        TextView mLeftUser = (TextView) view.findViewById(R.id.textView1);
        mLeftUser.setText(LoginActivity.entity.getUsername());
        mAnswer = new ListButton[btnId.length];
        QuestionEntity question = questions.get(0);
        List<Method> queue = randomOptions();

        try {
            if (queue != null)
                for (int i = 0; i < btnId.length; i++) {
                    mAnswer[i] = (ListButton) findViewById(btnId[i]);
                    mAnswer[i].setOnClickListener(this);
                    mAnswer[i].setText((String) queue.get(i).invoke(question));
                }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        mLayout = (RelativeLayout) findViewById(R.id.match_frame);
        mLayout.setBackgroundColor(Utils.COLOR_BKG);
    }

    /**
     * 随机选项
     *
     * @return 获得选项方法
     */

    private static List<Method> randomOptions() {
        try {
            Random r = new Random();
            List<Integer> list = new ArrayList<>();
            List<Method> methods = new ArrayList<>();
            while (list.size() < 4) {
                int num = r.nextInt(4);
                if (!list.contains(num)) {
                    String name = "";
                    list.add(num);
                    switch (num) {
                        case 0:
                            answer_right = list.size() - 1;
                            name = "getRight";
                            break;
                        case 1:
                            name = "getWrong1";
                            break;
                        case 2:
                            name = "getWrong2";
                            break;
                        case 3:
                            name = "getWrong3";
                            break;
                    }
                    Method m = Class.forName("com.example.model.QuestionEntity").getDeclaredMethod(name);
                    methods.add(m);
                }
            }
            return methods;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        for (int i = 0; i < btnId.length; i++) {
            mAnswer[i].setClickable(false);
            if (v.getId() == btnId[i]) {
                answer_count++;
                isAnswer = true;
                Message msg = new Message();
                msg.obj = MYANSWER;
                msg.arg1 = i;
                timer_count = false;
                time_answer = time_count;
                time_user_count += TIME_INTERVAL - time_answer;
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
        SoundUtil.pauseMusic(this, LoginActivity.setEntity);
        super.onPause();
    }

    @Override
    protected void onRestart() {
        SoundUtil.playMusic(this, LoginActivity.setEntity);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
//        this.finish();
//        timer_count = false;
    }
}
