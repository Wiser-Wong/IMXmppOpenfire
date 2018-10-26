package com.anch.wxy_pc.imclient.utils;

import android.content.Context;

/**
 * Created by wxy-pc on 2015/6/19.
 */
public class UtilTools {

    public static String splitStr(String str, int i) {
        if (str.contains("@")) {
            String[] datas = str.split("@");
            return datas[i];
        }
        if (str == null) {
            return "";
        }
        return str;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
