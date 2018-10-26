package com.anch.wxy_pc.imclient.custom;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.anch.wxy_pc.imclient.R;

/**
 * 播放器头部
 *
 * @author wxy
 */
public class CustomTitle extends LinearLayout {

    private View view;
    private Context mContext;
    private LayoutInflater mInflater;

    public CustomTitle(Context context, AttributeSet attrs) {
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
        mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.custom_player_title, this, true);
    }

    /**
     * title滑进播放器
     */
    public void titleInAnim() {
        TranslateAnimation inAnimation = new TranslateAnimation(0, 0,
                -getHeight(), 0);
        inAnimation.setDuration(400);
        inAnimation.setFillAfter(true);// 动画执行完view停留在执行完动画的位置
        startAnimation(inAnimation);
    }

    /**
     * title滑出播放器
     */
    public void titleOutAnim() {
        TranslateAnimation outAnimation = new TranslateAnimation(0, 0, 0,
                -getHeight());
        outAnimation.setDuration(400);
        outAnimation.setFillAfter(true);// 动画执行完view停留在执行完动画的位置
        startAnimation(outAnimation);
    }
}