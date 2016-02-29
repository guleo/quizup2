package com.example.frank.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import com.example.frank.anim.Vertical3DAnim;
import com.example.frank.test.R;

/**
 * Created by frank on 2016/2/5.
 */
public class FoldableItem extends ViewGroup implements View.OnClickListener {

    private ListButton mListButton;
    private LinearLayout mLayout;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FoldableItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FoldableItem(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        locateItem();
    }

    private void locateItem() {
        mLayout = (LinearLayout) findViewById(R.id.item_fold);
        mListButton = (ListButton) findViewById(R.id.f_button);
        int width = mListButton.getMeasuredWidth();
        int height = mListButton.getMeasuredHeight();
        mListButton.layout(0,0,150,40);
        mLayout.setVisibility(GONE);
        mLayout.setFocusable(false);
    }

    @Override
        public void onClick(View v) {
            if (v instanceof ListButton) {
                float x = mLayout.getMeasuredWidth() / 2;
                float y = mLayout.getMeasuredHeight();
                AnimationSet aSet = new AnimationSet(true);
                Vertical3DAnim anim = new Vertical3DAnim(0,180,x,y);
                        mLayout.setAnimation(anim);
            }
        }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        //获得每个子View的宽高度
        for (int i = 0; i < count;i++) {
            getChildAt(i).measure(widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}

