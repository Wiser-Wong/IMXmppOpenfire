package com.anch.wxy_pc.imclient.utils;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;

/**
 * 动画工具类
 *
 * @author wxy
 */
public class AnimTools {

    private static Animation operatingAnim;

    public interface EndAnimation {
        void endAnim();
    }

    /**
     * 透明动画(从不透明~透明(1~0) 从透明~不透明(0~1))
     *
     * @param view
     */
    public static void alphaAnim(View view, int time, boolean flag, float one, float two, final EndAnimation endAnimation) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(one, two);
        alphaAnimation.setDuration(time);
        alphaAnimation.setInterpolator(new AccelerateInterpolator());
        alphaAnimation.setFillAfter(flag);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                endAnimation.endAnim();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 显示动画
     *
     * @param view
     */
    public static void animateView(final View view) {
        view.setVisibility(View.VISIBLE);
        FlipAnimation rotation = new FlipAnimation(90, 0, view.getWidth(), 0.0f);
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotation);
    }

    /**
     * 隐藏动画
     *
     * @param view
     */
    public static void animateHideView(final View view) {
        FlipAnimation rotation =
                new FlipAnimation(0, 90, view.getWidth(), 0.0f);
        rotation.setDuration(300);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(rotation);
    }

    /**
     * 启动旋转动画
     *
     * @param mContext
     */
    public static void startRotateAnim(View iv, Context mContext) {
        operatingAnim = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        operatingAnim.setDuration(3000);
        iv.startAnimation(operatingAnim);
    }

    /**
     * 结束旋转动画
     *
     * @param iv
     */
    public static void stopRotateAnim(View iv) {
        if (operatingAnim != null) {
            iv.clearAnimation();
            operatingAnim = null;
        }
    }

    /**
     * 控件背景颜色之间缓慢切换
     *
     * @param view 颜色变化View
     * @param RED  颜色切换其中一种颜色
     * @param BLUE 颜色切换其中另一种颜色
     */
    public static void changeColorAnim(View view, int RED, int BLUE) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view, "backgroundColor",
                RED, BLUE);
        colorAnim.setDuration(3000);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }

    /**
     * 白色焦点框飞动 到控件做边框
     *
     * @param whiteBorder 白色边框图片
     * @param width       白色框的宽度(非放大后宽度)
     * @param height      白色框的高度(非放大后高度)
     * @param paramFloat1 x坐标偏移量，相对于初始的白色框的中心x坐标
     * @param paramFloat2 y坐标偏移量，相对于初始的白色框的中心y坐标
     *                    <p/>
     *                    通过Handler发送消息处理飞动焦点框 int x = view.getLeft() + view.getWidth()
     *                    / 2 - whiteBorder.getWidth() / 2; int y = view.getTop() +
     *                    view.getHeight() / 2 - whiteBorder.getHeight() /
     *                    2;该x为paramFloat1，y为paramFloat2
     */
    public static void flyWhiteBorder(View whiteBorder, int width, int height,
                                      float paramFloat1, float paramFloat2) {
        if (whiteBorder != null) {
            whiteBorder.setVisibility(View.VISIBLE);
            int mWidth = whiteBorder.getWidth();
            int mHeight = whiteBorder.getHeight();
            if (mWidth == 0 || mHeight == 0) {
                mWidth = 1;
                mHeight = 1;
            }
            ViewPropertyAnimator localViewPropertyAnimator = whiteBorder
                    .animate();
            localViewPropertyAnimator.setDuration(150L);
            localViewPropertyAnimator.scaleX((float) (width * 1.205)
                    / (float) mWidth);
            localViewPropertyAnimator.scaleY((float) (height * 1.205)
                    / (float) mHeight);
            localViewPropertyAnimator.x(paramFloat1);
            localViewPropertyAnimator.y(paramFloat2);
            localViewPropertyAnimator.start();
        }
    }

    /**
     * 放大方法
     *
     * @param view 控件布局
     * @param flag true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void magnifyAnim(View view, float m, boolean flag) {
        ScaleAnimation maginfy = new ScaleAnimation(1.0f, m, 1.0f, m,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        maginfy.setDuration(100);
        maginfy.setFillAfter(flag);
        view.startAnimation(maginfy);
    }

    /**
     * 缩小方法
     *
     * @param view 控件布局
     * @param flag true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void shrinkAnim(View view, float m, boolean flag) {
        ScaleAnimation shrink = new ScaleAnimation(m, 1.0f, m, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        shrink.setDuration(100);
        shrink.setFillAfter(flag);
        view.startAnimation(shrink);
    }

    /**
     * 图片加载动画
     *
     * @param view
     * @param id
     */
    public static void loadingAnim(ImageView view, int id) {
        AnimationDrawable ad = new AnimationDrawable();
        view.setImageResource(id);
        ad = (AnimationDrawable) view.getDrawable();
        ad.start();
    }

    /**
     * 一个控件从自己位置移动到另一个控件位置(带缩小效果)
     *
     * @param view1
     * @param view2
     */
    public static void transAndScaleAnim(View view1, View view2) {
        int[] location = new int[2];
        view1.getLocationOnScreen(location);
        int startX = location[0];
        int startY = location[1];
        view2.getLocationOnScreen(location);
        int lastX = location[0];
        int lastY = location[1];
        int x = lastX - startX;
        int y = lastY - startY;
        AnimationSet set = new AnimationSet(false);
        // 移动动画
        TranslateAnimation trans = new TranslateAnimation(0, x, 0, y);
        // 缩小动画
        ScaleAnimation scaleAnimation = new ScaleAnimation(2.0f, 1.0f, 2.0f,
                1.0f, Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        set.addAnimation(trans);
        set.addAnimation(scaleAnimation);
        set.setDuration(1500);
        set.setInterpolator(new AccelerateInterpolator());// 先加速后减速
        view1.startAnimation(set);
    }

    /**
     * 直线移动动画
     *
     * @param view   移动控件
     * @param startX 开始x位置
     * @param endX   结束x位置
     * @param startY 开始y位置
     * @param endY   结束y位置
     * @param flag   true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void transAnim(View view, int startX, int endX, int startY,
                                 int endY, boolean flag) {
        TranslateAnimation trans = new TranslateAnimation(startX, endX, startY,
                endY);
        trans.setDuration(500);
        trans.setFillAfter(flag);
        trans.setInterpolator(new AccelerateInterpolator());// 现加速后减速
        view.startAnimation(trans);
    }

    /**
     * 某一控件上方正中间播放显示(剧集光标处于某一集，然后弹出缩略图显示图片)
     *
     * @param view1      从某一控件开始移动
     * @param view2      移动的控件
     * @param viewWidth  view2的X位置
     * @param viewHeight view2的Y位置
     * @param flag       true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void transAnim(View view1, final View view2, int viewWidth,
                                 int viewHeight, boolean flag) {
        int[] location = new int[2];
        view1.getLocationOnScreen(location);
        int fixationX = location[0];// 固定控件x坐标
        int fixationY = location[1];// 固定控件y坐标
        view2.getLocationOnScreen(location);
        int liveX = location[0];// 移动的控件x坐标
        int liveY = location[1];// 移动的控件y坐标
        AnimationSet set = new AnimationSet(false);
        TranslateAnimation trans = new TranslateAnimation(
                (fixationX + view1.getWidth() / 2) - (liveX + viewWidth / 2),
                (fixationX + view1.getWidth() / 2) - (liveX + viewWidth / 2),
                fixationY - viewHeight - view1.getHeight() / 2 + 15, fixationY
                - viewHeight - view1.getHeight() / 2 + 15);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        set.addAnimation(trans);
        set.addAnimation(alpha);
        set.setDuration(200);
        set.setFillAfter(flag);
        set.setInterpolator(new AccelerateInterpolator());// 现加速后减速
        set.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view2.setVisibility(View.VISIBLE);// 刚刚启动动画显示图片
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        view2.startAnimation(set);
    }

    /**
     * 直线移动动画
     *
     * @param view1 控件1
     * @param view2 控件2
     * @param flag  true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void transAnim(View view1, View view2, boolean flag) {
        int[] location = new int[2];
        view1.getLocationOnScreen(location);
        int startX = location[0];
        int startY = location[1];
        view2.getLocationOnScreen(location);
        int lastX = location[0];
        int lastY = location[1];
        int x = lastX - startX;
        int y = lastY - startY;
        TranslateAnimation trans = new TranslateAnimation(0, x, 0, y);
        trans.setDuration(1000);
        trans.setFillAfter(flag);
        trans.setInterpolator(new AccelerateInterpolator());// 现加速后减速
        view1.startAnimation(trans);
    }

    /**
     * 从屏幕边缘移进移出动画
     *
     * @param view     控件对象
     * @param mContext 所属Activity
     * @param id       动画布局id
     * @param flag     true是该控件动画结束时处于结束位置，false是该控件动画结束时处于开始位置
     */
    public static void transAnim(View view, Activity mContext, int id,
                                 boolean flag) {
        Animation sildAnim = AnimationUtils.loadAnimation(mContext, id);
        sildAnim.setDuration(500);
        sildAnim.setFillAfter(flag);
        view.startAnimation(sildAnim);
    }

    /**
     * 一个控件移动到另一个控件切换动画
     *
     * @param view     启动动画View
     * @param toTransX 开始移动X坐标位置
     * @param toTransY 开始移动Y坐标位置
     * @param toScaleX 放大或缩小X比例
     * @param toScaleY 放大或缩小Y比例
     * @param left     移动到的位置getleft值
     * @param top      移动到的位置gettop值
     * @param right    移动到的位置getright值
     * @param bottom   移动到的位置getbottom值
     * @param width    移动到的位置控件宽
     * @param height   移动到的位置控件高
     * @param mHandler 启动动画Handler对象
     * @param flag     是否当动画结束时 启动 下一个开始的动画
     * @param CODE     常量
     */
    public static void animSet(final View view, float toTransX, float toTransY,
                               float toScaleX, float toScaleY, final int left, final int top,
                               final int right, final int bottom, final float width,
                               final float height, final Handler mHandler, final boolean flag,
                               final int CODE) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation trans1 = new TranslateAnimation(0, toTransX, 0,
                toTransY);
        ScaleAnimation scale1 = new ScaleAnimation(1.0f, toScaleX, 1.0f,
                toScaleY);
        set.addAnimation(scale1);
        set.addAnimation(trans1);
        set.setDuration(500);
        set.setFillAfter(true);
        view.startAnimation(set);
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        (int) width, (int) height);
                params.setMargins(left, top, right, bottom);
                view.setLayoutParams(params);
                if (flag) {
                    mHandler.sendEmptyMessage(CODE);
                }
            }
        });
    }
}
