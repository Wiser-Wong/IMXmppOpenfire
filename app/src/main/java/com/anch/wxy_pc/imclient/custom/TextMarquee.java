package com.anch.wxy_pc.imclient.custom;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.anch.wxy_pc.imclient.R;


/**
 * 字幕区
 *
 * @author wxy-pc
 */
public class TextMarquee extends SurfaceView implements Callback {


    /**
     * 是否滚动
     */
    private boolean isMove = false;
    /**
     * 移动方向
     */
    private int orientation = 0;
    /**
     * 向左移动
     */
    private final static int MOVE_LEFT = 0;
    /**
     * 向右移动
     */
    private final static int MOVE_RIGHT = 1;
    /**
     * 移动速度　1.5s　移动一次
     */
    private long speed = 10;
    /**
     * 字幕内容
     */
    private String text;

    private List<String> texts = new ArrayList<>();

    /**
     * 字幕背景色
     */
    private String bgColor = "#E7E7E7";

    /**
     * 字幕透明度　默认：60
     */
    private int bgalpha = 255;

    /**
     * 字体颜色 　默认：白色 (#FFFFFF)
     */
    private String fontColor = "#FFFFFF";

    /**
     * 字体透明度　默认：不透明(255)
     */
    private int fontAlpha = 255;

    /**
     * 字体大小 　默认：20
     */
    private float fontSize = 20f;
    /**
     * 容器
     */
    private SurfaceHolder mSurfaceHolder;
    /**
     * 线程控制
     */
    private boolean loop = true;
    /**
     * 内容滚动位置起始坐标
     */
    private float x = 0;

    private float y = 0;

    private Paint paint = null;

    private Paint bgPaint = null;

    private Rect targetRect;
    private boolean isInit = true;
    private float conlen = 0.0f;
    private int text_position = 0;
    /**
     * @param context
     * <see>默认滚动</see>
     */

    private Thread marqueeThread;
    private boolean isStart = true;

    public TextMarquee(Context context) {
        super(context);

    }

    public TextMarquee(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode())
            return;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        //设置画布背景不为黑色　继承Sureface时这样处理才能透明
        setZOrderOnTop(true);
        mSurfaceHolder.setFormat(PixelFormat.RGBX_8888);
        this.isMove = true;
        setLoop(isMove());
        if (isInEditMode())
            return;
    }

    /**
     * @param context
     * @param move    <see>是否滚动</see>
     */
    public TextMarquee(Context context, boolean move) {
        this(context);

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Log.d("WIDTH:", "" + getWidth());

        if (paint == null || bgPaint == null) {
            initPaint();
        }
        if (isMove) {//滚动效果
            int mWidth = TextMarquee.this.getWidth();
            int mHeight = TextMarquee.this.getHeight();
            targetRect = new Rect(0, 0, mWidth, mHeight);
            FontMetricsInt fontMetrics = paint.getFontMetricsInt();
            y = targetRect.top + (targetRect.bottom - targetRect.top - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            marqueeThread = new Thread(new CanvasRunnable());
            loop = true;
            marqueeThread.start();
        } else {//不滚动只画一次
            draw();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // 终止自定义线程
        loop = false;
        marqueeThread.interrupt();

    }

    /**
     * 画图
     */
    private void draw() {
        //锁定画布
        Canvas canvas = null;
        if (mSurfaceHolder != null)
            canvas = mSurfaceHolder.lockCanvas(targetRect);
        if (mSurfaceHolder == null || canvas == null) {
            return;
        }


        //滚动效果
        if (isMove) {
            //内容所占像素
            if (isInit) {
                if (orientation == MOVE_LEFT) {
                    x = getWidth();
                } else {
                    x = -(text.length() * 20);
                }
                conlen = paint.measureText(texts.get(text_position));
                setText(texts.get(text_position));
                //				conlen = paint.measureText(text);
                isInit = !isInit;
            }
            //组件宽度
            int w = getWidth();
            //方向
            /** 移动速度*/
            int scroll_pixel = 2;
            if (orientation == MOVE_LEFT) {//向左
                if (x < -conlen) {
                    x = w;
                    text_position++;
                    if (text_position >= texts.size()) {
                        text_position = 0;
                    }
                    isInit = !isInit;
                } else {
                    x -= scroll_pixel;
                }
            } else if (orientation == MOVE_RIGHT) {//向右
                if (x >= w) {
                    x = -conlen;
                    text_position++;
                    if (text_position >= texts.size()) {
                        text_position = 0;
                    }
                    isInit = !isInit;
                } else {
                    x += scroll_pixel;
                }
            }
        }

        //清屏
        canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        // 设置样式-填充
        canvas.drawRect(new Rect(0, 0, getWidth(), getHeight()), bgPaint);
        //画文字
        canvas.drawText(text, 0, text.length(), x, y, paint);
        //解锁显示
        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }


    private void initPaint() {
        paint = new Paint();
        //锯齿
        paint.setAntiAlias(true);
        //字体大小
        paint.setTextSize(getFontSize());
        //字体
        paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/huawenhangkai.ttf"));
        //字体颜色
        paint.setColor(Color.parseColor(getFontColor()));
        //字体透明度
        paint.setAlpha(getFontAlpha());
        //背景色
        setBackgroundColor(Color.parseColor(getBgColor()));
        //设置透明
        getBackground().setAlpha(bgalpha);
        bgPaint = new Paint();
        //锯齿
        bgPaint.setAntiAlias(true);

        bgPaint.setColor(Color.parseColor(getBgColor()));

    }


    private class CanvasRunnable implements Runnable {

        @Override
        public void run() {
            while (loop) {

                try {
                    Thread.sleep(speed);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                if (isStart) {
//                    synchronized (mSurfaceHolder) {
                    draw();
//                    }
                }
            }

        }

    }

    public void onSwitch() {
        if (marqueeThread != null && marqueeThread.isAlive()) {
            isStart = !isStart;
        }
    }

    /**
     * ***************************set get method**********************************
     */

    private int getOrientation() {
        return orientation;
    }

    /**
     * @param orientation <li>可以选择类静态变量</li>
     *                    <li>1.MOVE_RIGHT 向右 (默认)</li>
     *                    <li>2.MOVE_LEFT  向左</li>
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private long getSpeed() {
        return speed;
    }

    /**
     * @param speed <li>速度以毫秒计算两次移动之间的时间间隔</li>
     *              <li>默认为 1500 毫秒</li>
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    private boolean isMove() {
        return isMove;
    }

    /**
     * @param isMove <see>默认滚动</see>
     */
    public void setMove(boolean isMove) {
        this.isMove = isMove;
    }

    private void setLoop(boolean loop) {
        this.loop = loop;
    }

    private void setText(String text) {
        this.text = text;
    }

    public void setTexts(List<String> texts) {
        this.texts.clear();
        this.texts.addAll(texts);
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public void setBgalpha(int bgalpha) {
        this.bgalpha = bgalpha;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public void setFontAlpha(int fontAlpha) {
        this.fontAlpha = fontAlpha;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    private String getBgColor() {
        return bgColor;
    }

    public int getBgalpha() {
        return bgalpha;
    }

    public String getFontColor() {
        return fontColor;
    }

    public int getFontAlpha() {
        return fontAlpha;
    }

    private float getFontSize() {
        return fontSize;
    }
}