package com.example.frank.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import com.example.frank.test.R;

import java.io.InputStream;
import java.lang.reflect.Field;

/**
 * Created by frank on 2016/2/1.
 * 可以显示gif图片
 */
public class RoundImageView extends ImageView {

    //gif的参数
    private int gifId;
    private int gWidth;
    private int gHeight;
    private long startTime;
    private Movie mov = null;
    //圆形图片的默认值
    private final int DEFAULT_BORDER_COLOR = Color.WHITE;
    private final int DEFAULT_RADIUS = 50;
    private final int DEFAULT_BORDER_WIDTH = 0;

    private final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    //设置画笔
    private Paint mPicPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private BitmapShader bitmapShader;
    private final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private Bitmap mBitmap;

    //圆形图片的半径，边界宽度，边界颜色
    private float mRadius;
    private float mBorderWidth;
    private int mBorderColor;

    public float getRadius() {
        return mRadius;
    }
    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        super.setScaleType(SCALE_TYPE);
        TypedArray tp = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RoundImageView, defStyleAttr, 0);
        mRadius = tp.getDimensionPixelSize(R.styleable.RoundImageView_radius, DEFAULT_RADIUS);
        mBorderWidth = tp.getDimensionPixelSize(R.styleable.RoundImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = tp.getColor(R.styleable.RoundImageView_border_color, DEFAULT_BORDER_COLOR);

        gifId = getGifId(tp);
        if (gifId != 0) {
            InputStream is = getResources().openRawResource(gifId);
            mov = Movie.decodeStream(is);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            gWidth = bitmap.getWidth();
            gHeight = bitmap.getHeight();
            bitmap.recycle();
        }
        tp.recycle();

    }

    private int getGifId(TypedArray attributes) {
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

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getDrawableBitmap(getDrawable());
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getDrawableBitmap(drawable);
    }

    private Bitmap getDrawableBitmap(Drawable drawable) {
        if (drawable == null)
            return null;

        if (drawable instanceof ColorDrawable) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), BITMAP_CONFIG);
            Canvas canvas = new Canvas(mBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        if (drawable instanceof BitmapDrawable) {
            mBitmap = ((BitmapDrawable) drawable).getBitmap();
        }
        return mBitmap;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (getDrawable() == null)
            return;
        if (mov != null) {
            long curTime = android.os.SystemClock.uptimeMillis();
            if (startTime == 0) {
                startTime = curTime;
            }
            int durTime = mov.duration();
            int relTime = ((int) (curTime - startTime)) % durTime;
            mov.setTime(relTime);
            mov.draw(canvas, (getWidth()  - gWidth)/2, (getHeight()  - gHeight)/ 2);
            if ((curTime - startTime) >= durTime)
                startTime = 0;
            invalidate();
        }

        setPaint();

        if (mov == null) {
            setChange();
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPicPaint);
        }
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mBorderPaint);
    }

    public void setPaint() {
        bitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPicPaint.setAntiAlias(true);
        mPicPaint.setColor(Color.WHITE);
        mPicPaint.setShader(bitmapShader);

        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    //根据ImageView的宽高调整图片的大小,在onDraw方法中调用有效
    public void setChange() {

        float min = (getWidth() < getHeight()) ? getWidth() : getHeight();
        mRadius = Math.min(mRadius, min / 2);
        mBorderWidth = Math.min(mBorderWidth, min / 2 - mRadius);


        //根据图片设置大小，对Bitmap进行缩放
        float mBWidth = mBitmap.getWidth();
        float mBHeight = mBitmap.getHeight();

        float scale;
        float x, y;
        Matrix shaderMatrix = new Matrix();
        if (mBWidth > mBHeight) {
            scale = mRadius / mBHeight * 2;

        } else {
            scale = mRadius / mBWidth * 2;

        }
        x = (getWidth() - mBWidth * scale) * 0.5f;
        y = (getHeight() - mBHeight * scale) * 0.5f;
        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate(x, y);
        bitmapShader.setLocalMatrix(shaderMatrix);

        invalidate();
    }

}
