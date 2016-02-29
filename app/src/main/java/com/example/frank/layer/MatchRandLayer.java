package com.example.frank.layer;

import android.content.Context;
import android.view.LayoutInflater;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.nodes.CCNode;
import org.cocos2d.nodes.CCSprite;

/**
 * Created by frank on 2016/2/11.
 */
public class MatchRandLayer extends CCLayer {

    private RoundImageView mLeft,mRight;
    private Context context;
    public MatchRandLayer(Context context) {

        this.context = context;
        //初始化图像
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);

        CCNode node = CCNode.node();
        mLeft = new RoundImageView(context,null);
        mLeft.setImageResource(R.drawable.head);
        mRight = new RoundImageView(context,null);
        mRight.setImageResource(R.drawable.head1);

    }
}
