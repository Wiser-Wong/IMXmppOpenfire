package com.anch.wxy_pc.imclient.custom;

import com.anch.wxy_pc.imclient.R;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * Created by drakeet on 10/21/14.
 */
public class CustomSliddingMenu extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup    mMenuViewGroup;
    private ViewGroup    mContentViewGroup;

    private int mScreenWidth;
    private int mMenuRightPadding = 50;
    private int mMenuWidth;

    private boolean mIsOnce = true;
    public boolean mIsOpen;

    public CustomSliddingMenu(Context context) {
        this(context, null);
    }

    public CustomSliddingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSliddingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomSliddingMenu, defStyleAttr, 0);
        int n = typedArray.length();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.CustomSliddingMenu_rigthtPadding:
                    mMenuRightPadding = typedArray.getDimensionPixelSize(
                            attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics())
                    );
                    break;
                default:
                    break;
            }
        }
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mIsOnce) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenuViewGroup = (ViewGroup) mWapper.getChildAt(0);
            mContentViewGroup = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenuViewGroup.getLayoutParams().width = mScreenWidth - mMenuRightPadding;
            mContentViewGroup.getLayoutParams().width = mScreenWidth;
            mIsOnce = false;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            this.scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (scrollX >= mMenuWidth / 2) {
                    smoothScrollTo(mMenuWidth, 0);
                    mIsOpen = false;
                } else {
                    smoothScrollTo(0, 0);
                    mIsOpen = true;
                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    public void openMenu() {
        if (mIsOpen)
            return;
        smoothScrollTo(0, 0);
        mIsOpen = true;
    }

    public void closeMenu() {
        if (!mIsOpen)
            return;
        smoothScrollTo(mMenuWidth, 0);
        mIsOpen = false;
    }

    public void toggleMenu() {
        if (mIsOpen)
            closeMenu();
        else
            openMenu();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        float scale = l * 1.0f / mMenuWidth; // l: 1.0 ~ 0
        ViewHelper.setTranslationX(mMenuViewGroup, mMenuWidth * scale * 0.7f);

        float rightScale = 0.7f + 0.3f * scale;
        ViewHelper.setPivotX(mContentViewGroup, 0);
        ViewHelper.setPivotY(mContentViewGroup, mContentViewGroup.getHeight() / 2);
        ViewHelper.setScaleX(mContentViewGroup, rightScale);
        ViewHelper.setScaleY(mContentViewGroup, rightScale);

        float leftScale = 1.0f - 0.3f * scale;
        ViewHelper.setScaleX(mMenuViewGroup, leftScale);
        ViewHelper.setScaleY(mMenuViewGroup, leftScale);

        float leftAlpha = 0.1f + 0.9f *(1 - scale);
        ViewHelper.setAlpha(mMenuViewGroup, leftAlpha);
    }
}
