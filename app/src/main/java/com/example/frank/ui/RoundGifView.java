package com.example.frank.ui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import com.example.frank.test.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by frank on 2016/2/9.
 */
public class RoundGifView extends ImageView {

    private int width, height;
    private int imageId;
    private long startTime = 0;
    private Movie mov;

    public RoundGifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundGifView, defStyleAttr, 0);
        imageId = getResourceId(ta, context, attrs);
        if (imageId != 0) {
            InputStream is = getResources().openRawResource(imageId);
            mov = Movie.decodeStream(is);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            width = bitmap.getWidth();
            height = bitmap.getHeight();
            bitmap.recycle();
        }
        ta.recycle();
    }

    public RoundGifView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mov != null) {
            long curTime = android.os.SystemClock.uptimeMillis();
            if (startTime == 0) {
                startTime = curTime;
            }
            int durTime = mov.duration();
            int relTime = ((int) (curTime - startTime)) % durTime;
            Log.d("gif", "" + relTime);
            mov.setTime(relTime);
            invalidate();

            mov.draw(canvas, 0, 0);
            if ((curTime - startTime) >= durTime)
                startTime = 0;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mov!=null)
            setMeasuredDimension(width,height);
    }

    private int getResourceId(TypedArray attributes, Context context,
                              AttributeSet attrs) {
        try {
            Field filed = TypedArray.class.getDeclaredField("mValue");
            filed.setAccessible(true);
            TypedValue typeValue = (TypedValue) filed.get(attributes);
            return typeValue.resourceId;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (attributes != null) {
                attributes.recycle();
            }
        }
        return 0;
    }

}