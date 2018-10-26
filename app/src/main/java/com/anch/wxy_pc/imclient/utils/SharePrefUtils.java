package com.anch.wxy_pc.imclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.anch.wxy_pc.imclient.IMApplication;

/**
 * 共享参数
 *
 * @author wxy
 */
public class SharePrefUtils {

    private final static String SP_NAME = "Im_Share";
    private static SharedPreferences sp;

    /**
     * 创建共享参数
     *
     * @return sp
     */
    private static SharedPreferences getPreferences() {

        if (sp == null) {
            sp = IMApplication.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    /**
     * 是否包含当前key
     *
     * @param key
     * @return
     */
    public static boolean isContains(String key) {

        return getPreferences().contains(key);
    }

    /**
     * 保存boolean类型
     *
     * @param key
     * @param value
     */
    public static void saveBoolean(String key, boolean value) {

        getPreferences().edit().putBoolean(key, value).commit();

    }

    /**
     * 保存String类型
     *
     * @param key
     * @param value
     */
    public static void saveString(String key, String value) {

        getPreferences().edit().putString(key, value).commit();

    }

    /**
     * 保存int整型
     *
     * @param key
     * @param value
     */
    public static void saveInt(String key, int value) {

        getPreferences().edit().putInt(key, value).commit();

    }

    /**
     * 保存float类型
     *
     * @param key
     * @param value
     */
    public static void saveFload(String key, float value) {

        getPreferences().edit().putFloat(key, value).commit();

    }

    /**
     * 保存long类型
     *
     * @param key
     * @param value
     */
    public static void saveLong(String key, long value) {

        getPreferences().edit().putLong(key, value).commit();

    }

    /**
     * 获得boolean值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defValue) {

        return getPreferences().getBoolean(key, defValue);

    }

    /**
     * 获得String值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(String key, String defValue) {

        return getPreferences().getString(key, defValue);

    }

    /**
     * 获得整型int值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(String key, int defValue) {

        return getPreferences().getInt(key, defValue);

    }

    /**
     * 获得浮点型float值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static float getFloat(String key, float defValue) {

        return getPreferences().getFloat(key, defValue);

    }

    /**
     * 获得长整型long值
     *
     * @param key
     * @param defValue
     * @return
     */
    public static long getLong(String key, long defValue) {

        return getPreferences().getLong(key, defValue);

    }

    /**
     * 读取String
     *
     * @param context
     * @param key
     * @param defaultValue
     */
    public static int readIntPreferences(Context context, String key, int defaultValue) {
        if (context != null) {
            return getPreferences().getInt(key, defaultValue);
        } else {
            return 0;
        }
    }

    /**
     * 写入Int
     *
     * @param key
     * @param value
     */
    public static void writeIntPreferences(String key, int value) {
        Editor edit = getPreferences().edit();
        edit.putInt(key, value);
        edit.apply();
    }

    /**
     * 清空数据
     */
    public static void clearShareData() {
        getPreferences().edit().clear().commit();
    }

    /**
     * 根据key删除
     *
     * @param context
     * @param key
     */
    public static void removePreferences(Context context, String key) {
        if (context != null) {
            Editor edit = getPreferences().edit();
            edit.remove(key);
            edit.apply();
        }
    }
}
