package com.example.frank.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.frank.test.R;

/**
 * Created by chenupt@gmail.com on 11/20/14.
 * Description : custom layout to draw bezier
 */
public class BezierView extends FrameLayout {

    // 默认定点圆半�?
    public static final float DEFAULT_RADIUS = 20;

    private Paint paint;
    private Path path;

    // 手势坐标
    float x = 300;
    float y = 300;

    // 锚点坐标
    float anchorX = 200;
    float anchorY = 300;

    // 起点坐标
    float startX;
    float startY;

    // 定点圆半�?
    float radius = DEFAULT_RADIUS;

    // 判断动画是否�?�?
    boolean isAnimStart;
    // 判断是否�?始拖�?
    boolean isTouch;

    ImageView exploredImageView;
    TextView mText;

    public BezierView(Context context) {
        super(context);
        init();
    }

    public BezierView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.RED);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        exploredImageView = new ImageView(getContext());
        exploredImageView.setLayoutParams(params);
        exploredImageView.setImageResource(R.drawable.tip_anim);
        exploredImageView.setVisibility(View.INVISIBLE);
        exploredImageView.bringToFront();

        mText = new TextView(getContext());
        mText.setLayoutParams(params);
        mText.setGravity(Gravity.CENTER);
        mText.setTextColor(Color.WHITE);
        mText.setTextSize(12);
        mText.setWidth(10);
        mText.setHeight(10);
        mText.setBackgroundResource(R.drawable.skin_tips_newmessage);
        mText.bringToFront();
        addView(mText);
        addView(exploredImageView);
    }

    public void setText(String text) {
        int size = Integer.parseInt(text);
        if (size >= 99)
            mText.setBackgroundResource(R.drawable.skin_tips_newmessage_ninetynine);
        else
            mText.setText(text);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //exploredImageView.setX(startX - exploredImageView.getWidth() / 2);
        //exploredImageView.setY(startY - exploredImageView.getHeight() / 2);

        //mText.setX(startX - mText.getWidth() / 2);
        //mText.setY(startY - mText.getHeight() / 2);
        super.onLayout(changed, left, top, right, bottom);
        startX = mText.getX();
        startY = mText.getY();
        anchorX = mText.getPivotX();
        anchorY = mText.getPivotY();
    }


    private void calculate() {
        float distance = (float) Math.sqrt(Math.pow(y - startY, 2) + Math.pow(x - startX, 2));
        radius = -distance / 15 + DEFAULT_RADIUS;

        if (radius < 9) {
            isAnimStart = true;

            exploredImageView.setVisibility(View.VISIBLE);
            exploredImageView.setImageResource(R.drawable.tip_anim);
            ((AnimationDrawable) exploredImageView.getDrawable()).stop();
            ((AnimationDrawable) exploredImageView.getDrawable()).start();

            mText.setVisibility(View.GONE);
        }

        // 根据角度算出四边形的四个�?
        float offsetX = (float) (radius * Math.sin(Math.atan((y - startY) / (x - startX))));
        float offsetY = (float) (radius * Math.cos(Math.atan((y - startY) / (x - startX))));

        float x1 = startX - offsetX;
        float y1 = startY + offsetY;

        float x2 = x - offsetX;
        float y2 = y + offsetY;

        float x3 = x + offsetX;
        float y3 = y - offsetY;

        float x4 = startX + offsetX;
        float y4 = startY - offsetY;

        path.reset();
        path.moveTo(x1, y1);
        path.quadTo(anchorX, anchorY, x2, y2);
        path.lineTo(x3, y3);
        path.quadTo(anchorX, anchorY, x4, y4);
        path.lineTo(x1, y1);

        // 更改图标的位�?
        mText.setX(x - mText.getWidth() / 2);
        mText.setY(y - mText.getHeight() / 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isAnimStart || !isTouch) {
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);

        } else {
            calculate();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.OVERLAY);
            canvas.drawPath(path, paint);
            canvas.drawCircle(startX, startY, radius, paint);
            canvas.drawCircle(x, y, radius, paint);
        }
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // 判断触摸点是否在mText�?
            Rect rect = new Rect();
            int[] location = new int[2];
            mText.getDrawingRect(rect);
            mText.getLocationOnScreen(location);
            rect.left = location[0];
            rect.top = location[1];
            rect.right = rect.right + location[0];
            rect.bottom = rect.bottom + location[1];
            if (rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                isTouch = true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            isTouch = false;
            mText.setX(startX);
            mText.setY(startY);
        }
        invalidate();
        if (isAnimStart) {
            return super.onTouchEvent(event);
        }
        anchorX = (event.getX() + startX) / 2;
        anchorY = (event.getY() + startY) / 2;
        x = event.getX();
        y = event.getY();
        return true;
    }


}
