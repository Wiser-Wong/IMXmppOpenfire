package com.anch.wxy_pc.imclient.custom;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.R;

import java.io.IOException;

/**
 * Created by wxy-pc on 2015/7/13.
 */
public class WindowMediaView {
    private WindowManager manager;
    private WindowManager.LayoutParams wmParams;

    public boolean createWindow() {
        if (manager == null && wmParams == null) {
            manager = (WindowManager) IMApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
            wmParams = new WindowManager.LayoutParams();
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            wmParams.gravity = Gravity.TOP | Gravity.CENTER;
            wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            return true;
        } else
            return false;
    }

    public void addWindowView(final View view) {
        manager.addView(view, wmParams);
    }

}
