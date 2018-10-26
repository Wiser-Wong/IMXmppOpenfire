package com.anch.wxy_pc.imclient.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;

import com.anch.wxy_pc.imclient.R;

/**
 * 播放器底部
 *
 * @author wxy
 */
public class Custombuttom extends RelativeLayout {

    private View view;
    private LayoutInflater mInflater;
    private Context mContext;

    public Custombuttom(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        addLayout();
        if (isInEditMode())
            return;
    }

    /**
     * 添加布局
     */
    private void addLayout() {
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.custom_player_buttom, this, true);
    }

    /**
     * buttom滑进播放器
     */
    public void buttomInAnim() {
        TranslateAnimation inAnimation = new TranslateAnimation(0, 0,
                getHeight(), 0);
        inAnimation.setDuration(400);
        inAnimation.setFillAfter(true);// 动画执行完view停留在执行完动画的位置
        startAnimation(inAnimation);
    }

    /**
     * buttom滑出播放器
     */
    public void buttomOutAnim() {
        TranslateAnimation outAnimation = new TranslateAnimation(0, 0, 0,
                getHeight());
        outAnimation.setDuration(400);
        outAnimation.setFillAfter(true);// 动画执行完view停留在执行完动画的位置
        startAnimation(outAnimation);
    }
}
