package com.anch.wxy_pc.imclient.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;

/**
 * Created by wxy-pc on 2015/6/15.
 */
public class DialogUtil {
    private static Dialog dialog;
    private static ImageView loadingIv;

    /**
     * 自定义显示 Dialog
     *
     * @param mContext
     * @param id       风格id
     * @param view     布局View
     * @param flag     是否点击消失 true 消失 false 不消失
     */
    public static void showDialog(Activity mContext, View view, String msg, int id,
                                  boolean flag) {
        TextView infoTv = (TextView) view.findViewById(R.id.info_tv);
        infoTv.setTypeface(Typeface.createFromAsset(mContext.getAssets(),"fonts/huawenhangkai.ttf"));
        infoTv.setText(msg);
        loadingIv = (ImageView) view.findViewById(R.id.loading_iv);
        new LoadingAnim(loadingIv).startLoading();
        dialog = new Dialog(mContext, id);
        dialog.setCancelable(flag);
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 取消Dialog
     */
    public static void cancleDialog() {
        if (dialog != null && loadingIv != null) {
            new LoadingAnim(loadingIv).endLoading();
            dialog.dismiss();
        }
    }

    /**
     * @param mContext
     * @param msg
     * @param title
     * @param leftBtn
     * @param rightBtn
     * @param id
     * @param flag
     * @param onClickCallBack
     */
    public static void simpleAlertDialog(Context mContext, String msg, String title, String leftBtn, String rightBtn, int id, boolean flag, final boolean isCancle, final OnClickCallBack onClickCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(msg);
        builder.setCancelable(flag);
        builder.setIcon(id);
        builder.setPositiveButton(leftBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isCancle)
                    dialog.cancel();
                onClickCallBack.leftOnclick();
            }
        });
        builder.setNegativeButton(rightBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isCancle)
                    dialog.cancel();
                onClickCallBack.rightOnclick();
            }
        });
        builder.create().show();
    }

    public interface OnClickCallBack {
        void leftOnclick();

        void rightOnclick();
    }
}
