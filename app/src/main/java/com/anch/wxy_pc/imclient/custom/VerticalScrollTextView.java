package com.anch.wxy_pc.imclient.custom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

public class VerticalScrollTextView extends TextView {
    private float step = 0f;
    private Paint mPaint;
    private String mtext = "";
    private float width;
    private List<String> textList = new ArrayList<String>(); // 分行保存textview的显示信息。
    private int mwidthMeasureSpec, mheightMeasureSpec;

    private static final int DY = 10;

    private static boolean isInit = false;

    private int textSize;

    Canvas canvas;

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        // invalidate();
    }

    public VerticalScrollTextView(Context context) {
        super(context);
        mPaint = new Paint();
        // invalidate();
    }

    public void setText(String text, int textSize) {

        this.mtext = text;
        this.textSize = textSize;

        // getString(mtext);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mwidthMeasureSpec = widthMeasureSpec;
        mheightMeasureSpec = heightMeasureSpec;

        // if (!isInit) {
        getString(mtext);
        // isInit = true;
        // }

    }

    private void getString(String text) {
        mPaint.setTextSize(textSize);
        mPaint.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/huawenhangkai.ttf"));
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        width = MeasureSpec.getSize(mwidthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(mwidthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            // throw new IllegalStateException(
            // "ScrollLayout only canmCurScreen run at EXACTLY mode!");
            return;
        }

        float length = 0;
        if (text == null || text.length() == 0) {
            return;
        }

        // 下面的代码是根据宽度和字体大小，来计算textview显示的行数。

        textList.clear();

        String[] textStr;
        if (text.indexOf("\n") >= 0) {
            textStr = text.split("\\n");
            if (textStr != null && textStr.length > 0) {
                for (int k = 0; k < textStr.length; k++) {
                    String string = "";
                    string = textStr[k];
                    StringBuilder builder = new StringBuilder();
                    if (string == null || string.length() < 1) {
                        textList.add("   ");
                    }
                    for (int i = 0; i < string.length(); i++) {
                        if (length < width) {
                            builder.append(string.charAt(i));
                            length += mPaint.measureText(string.substring(i,
                                    i + 1));
                            if (i == string.length() - 1) {
                                textList.add(builder.toString());
                                length = 0;
                            }
                        } else {
                            if (builder.toString() != null
                                    && builder.toString().length() > 0) {
                                textList.add(builder.toString().substring(0,
                                        builder.toString().length() - 1));
                                builder.delete(0, builder.length() - 1);
                                length = mPaint.measureText(string.substring(i,
                                        i + 1));
                                i--;
                            }

                        }

                    }
                }
            } else {

                return;

            }
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                if (length < width) {
                    builder.append(text.charAt(i));
                    length += mPaint.measureText(text.substring(i, i + 1));
                    if (i == text.length() - 1) {
                        textList.add(builder.toString());
                        length = 0;
                    }
                } else {
                    if (builder.toString() != null
                            && builder.toString().length() > 0) {
                        textList.add(builder.toString().substring(0,
                                builder.toString().length() - 1));
                        builder.delete(0, builder.length() - 1);
                        length = mPaint.measureText(text.substring(i, i + 1));
                        i--;
                    }
                }

            }
        }

        if (isInit == false) {
            mHandler.sendEmptyMessageDelayed(0, 300);
            isInit = true;
        }

    }

    // 下面代码是利用上面计算的显示行数，将文字画在画布上，实时更新。
    @Override
    public void onDraw(Canvas canvas) {
        this.canvas = canvas;
        mPaint.setColor(Color.parseColor("#CAFF70"));
        if (textList.size() == 0)
            return;
        for (int i = 0; i < textList.size(); i++) {
            canvas.drawText(textList.get(i), 0, this.getHeight() + (i + 1)
                    * (mPaint.getTextSize() + DY) - step, mPaint);
        }

        step = step + 1.0f;
        if (step >= this.getHeight() + textList.size()
                * (mPaint.getTextSize() + DY)) {
            step = 0;
        }

        mHandler.sendEmptyMessageDelayed(0, 0);

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {

                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        invalidate();
                    }
                }, 10);
            }
        }

        ;
    };

}
