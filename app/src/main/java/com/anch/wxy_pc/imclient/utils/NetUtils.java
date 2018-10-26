package com.anch.wxy_pc.imclient.utils;

import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetUtils {

    /**
     * 是否有网络
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED
                            || info[i].getState() == NetworkInfo.State.CONNECTING) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据url得到一个文件名
     *
     * @param url
     * @return
     */
    public static String getFileNameFromUrl(String url) {
        // 通过 ‘？’ 和 ‘/’ 判断文件名
        int index = url.lastIndexOf('?');
        String filename;
        if (index > 1) {
            filename = url.substring(url.lastIndexOf('/') + 1, index);
        } else {
            filename = url.substring(url.lastIndexOf('/') + 1);
        }

        if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
            filename = UUID.randomUUID() + ".apk";// 默认取一个文件名
        }
        return filename;
    }

    /**
     * 检查网络
     *
     * @param paramContext
     * @return
     */
    public static boolean CheckNetwork(Context paramContext) {
        boolean isConneted = false;
        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
                .getSystemService("connectivity");
        if (localConnectivityManager.getActiveNetworkInfo() != null)
            isConneted = localConnectivityManager.getActiveNetworkInfo()
                    .isAvailable();
        return isConneted;
    }

    /**
     * 是否cmwap
     */
    public static boolean isCMWAPMobileNet(Context paramContext) {
        if (isWifi(paramContext)) {
            return false;
        }
        ConnectivityManager localConnectivityManager = (ConnectivityManager) paramContext
                .getSystemService("connectivity");
        if (localConnectivityManager != null) {
            NetworkInfo localNetworkInfo = localConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (localNetworkInfo != null) {
                String str = localNetworkInfo.getExtraInfo();
                if ("cmwap".equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否wifi
     *
     * @param paramContext
     * @return
     */
    public static boolean isWifi(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService("connectivity")).getActiveNetworkInfo();
        if ((localNetworkInfo != null)
                && (localNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            return true;
        }
        return false;

    }

    /**
     * 得到网络状态
     *
     * @param paramContext
     * @return
     */
    public static int getNetType(Context paramContext) {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService("connectivity")).getActiveNetworkInfo();
        if (localNetworkInfo != null) {
            return localNetworkInfo.getType();
        }
        return -1;
    }
}
