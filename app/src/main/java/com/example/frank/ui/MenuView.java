package com.example.frank.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.frank.test.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by frank on 2015/2/27.
 */
public class MenuView extends HorizontalScrollView {

    private boolean once = true;
    private LinearLayout mwrapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWith;
    private int mMenuRightpaddding;
    private int mMenuWidth;
    private Context context;
    private boolean isOpen = false;

    /**
     * 未自定义属性时，调用此构造方法
     *
     * @param context
     * @param attrs
     */
    public MenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * 当使用了自定义的属性时，会调用此构造方法
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MenuView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWith = outMetrics.widthPixels;

        //获取我们定义的属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlideMenu, defStyle, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            if (a.getIndex(i) == R.styleable.SlideMenu_menuRightPadding) {
                Log.d("tag", "menuRight");
                mMenuRightpaddding = (int) a.getDimensionPixelSize(a.getIndex(i), 50);
                break;
            }
        }
        a.recycle();
    }

    /**
     * 设置子View的宽高，并设置自己的宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (once) {
            mwrapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mwrapper.getChildAt(0);
            mContent = (ViewGroup) mwrapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWith - mMenuRightpaddding;
            mContent.getLayoutParams().width = mScreenWith;
            once = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置初始化显示位置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        this.scrollTo(mScreenWith, 0);

        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int scrollX = getScrollX();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
                if (scrollX >= mMenuWidth / 2) {
                    this.smoothScrollTo(mMenuWidth, 0);
                    isOpen = false;
                } else {
                    this.smoothScrollTo(0, 0);
                    isOpen = true;
                }
                // 必须返回true值，否则无法产生滑动效果
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public void toggle() {
        if (isOpen) {
            this.smoothScrollTo(mMenuWidth, 0);
            isOpen = false;
        } else {
            this.smoothScrollTo(0, 0);
            isOpen = true;
        }

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        ViewHelper.setTranslationX(mMenu, (float) (l * 0.7));
        float scale = (mMenuWidth - l) / (float) mMenuWidth;
        //设置内容比例缩小
        ViewHelper.setPivotX(mContent, 0f);
        ViewHelper.setPivotY(mContent, mContent.getHeight() / 2);
        ViewHelper.setScaleX(mContent, 1 - 0.3f * scale);
        ViewHelper.setScaleY(mContent, 1 - 0.3f * scale);

        //设置菜单透明度，以及缩放变化
        ViewHelper.setAlpha(mMenu, scale * 0.6f + 0.4f);
        ViewHelper.setScaleX(mMenu, scale * 0.3f + 0.7f);
        ViewHelper.setScaleY(mMenu, scale * 0.3f + 0.7f);
    }
}
