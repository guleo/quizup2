package com.example.frank.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ProgressBar;
import com.example.frank.test.R;

/**
 * Created by frank on 2016/2/13.
 */
public class MatchProgressBar extends ProgressBar {

    private Bitmap mBar;
    private Paint mBarPaint;
    private final float TOLERANCE = 2.5f;

    public MatchProgressBar(Context context) {
        super(context);
    }

    public MatchProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        BitmapDrawable draw = (BitmapDrawable) getResources().getDrawable(R.drawable.match_progress);
        mBar = draw.getBitmap();
        mBarPaint = new Paint();
    }


    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //画背景图
//        if (mBkg != null && isFirstDraw) {
//            int width = mBkg.getWidth();
//            int height = mBkg.getHeight();
//            scaleX = width / (mBkg.getWidth()+TOLERANCE);
//            scaleY = height / mBkg.getHeight();
//            Matrix m = new Matrix();
//            m.setScale(scaleX, scaleY);
//            mBkg = Bitmap.createBitmap(mBkg,0,0,width,height,m,true);
//            mBkgPaint.setAntiAlias(true);
//           canvas.drawBitmap(mBkg,0,0,mBkgPaint);
//            isFirstDraw = false;
//        }

        //画进度条
        if (mBar != null) {
            Bitmap tmp;
            int clipX;
            Matrix m = new Matrix();
            int width = mBar.getWidth();
            int height = mBar.getHeight();
            float scaleX = ((float) getWidth()) / (width + TOLERANCE);
            float scaleY = ((float) getHeight()) / (height - TOLERANCE);
            clipX = (int) (((float) getMax() - getProgress()) / getMax() * width);
            m.setScale(scaleX, scaleY);
            if (clipX > 0) {
                tmp = Bitmap.createBitmap(mBar, 0, 0, clipX, height, m, false);
                //  mBarPaint.setShader(shader);
                mBarPaint.setAntiAlias(true);
                canvas.drawBitmap(tmp, 0, 0, mBarPaint);
            }
        }
    }
}
