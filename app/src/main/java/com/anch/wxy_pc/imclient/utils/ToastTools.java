package com.anch.wxy_pc.imclient.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Toast工具
 *
 * @author wxy
 */
public class ToastTools {


    private static Toast toast = null;

    /**
     * 默认Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void defaultToast(Context mContext, String showMsg) {
        Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 左侧Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void leftToast(Context mContext, String showMsg) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.LEFT, 0, 0);
        toast.show();
    }

    /**
     * 右侧Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void rightToast(Context mContext, String showMsg) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.RIGHT, 0, 0);
        toast.show();
    }

    /**
     * 中间Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void centerToast(Context mContext, String showMsg) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 下面Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void bottomToast(Context mContext, String showMsg) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    /**
     * 上面Toast
     *
     * @param mContext
     * @param showMsg
     */
    public static void topToast(Context mContext, String showMsg) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    /**
     * 带图片Toast
     *
     * @param mContext
     * @param showMsg
     * @param id       图片id
     */
    public static void imageToast(Context mContext, String showMsg, int id) {
        Toast toast = Toast.makeText(mContext, showMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        LinearLayout layout = (LinearLayout) toast.getView();
        ImageView image = new ImageView(mContext);
        image.setImageResource(id);
        layout.addView(image, 0);
        toast.show();
    }

    /**
     * 带边框的Toast
     *
     * @param mContext
     * @param showMsg
     * @param id       边框id
     */
    public static void frameToast(Context mContext, String showMsg, int id) {
        TextView toastView = new TextView(mContext);
        toastView.setPadding(30, 20, 30, 20);
        toastView.setBackgroundResource(id);
        toastView.setText(showMsg);
        toastView.setTextColor(Color.WHITE);
        toastView.setTextSize(15.0f);
        if (toast == null) {
            toast = new Toast(mContext);
        }
        toast.setView(toastView);
        toast.show();

    }

}
