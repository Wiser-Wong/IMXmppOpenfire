package com.anch.wxy_pc.imclient.utils;

import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

/**
 * 加载动画
 *
 * @author wxy
 */
public class LoadingAnim {

    private AnimationDrawable loadingAnim;

    public LoadingAnim(ImageView view) {
        loadingAnim = (AnimationDrawable) view.getDrawable();
    }

    /**
     * 开始加载动画
     */
    public void startLoading() {
        loadingAnim.start();
    }

    /**
     * 结束加载动画
     */
    public void endLoading() {
        loadingAnim.stop();
    }
}
