package com.anch.wxy_pc.imclient.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by wxy-pc on 2015/6/23.
 */
public class IntentUtils {

    /**
     * 无参跳转
     *
     * @param activity
     * @param object
     * @param flag
     */
    public static void skip(Activity activity, Class object, boolean flag) {
        Intent intent = new Intent(activity, object);
        activity.startActivity(intent);
        if (flag) activity.finish();
    }

    /**
     * 有参跳转
     *
     * @param activity
     * @param object
     * @param flag
     */
    public static void skip(Activity activity, Class object, String key, String value, boolean flag) {
        Intent intent = new Intent(activity, object);
        intent.putExtra(key, value);
        activity.startActivity(intent);
        if (flag) activity.finish();
    }

    /**
     * Intent 跳转传字符串或者整型
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param strKeys     传递字符串值数组
     * @param strKeys     传递字符串键数组
     * @param strValues   传递整型值数组
     * @param intKeys     传递整型键数组
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipStringOrInt(Activity mActivity, Class<?> clazz,
                                       String[] strKeys, String[] strValues, String[] intKeys,
                                       int[] intValues, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        if (strValues != null && strKeys != null) {
            if (strValues.length > 0 && strKeys.length == strValues.length) {
                for (int i = 0; i < strValues.length; i++) {
                    intent.putExtra(strKeys[i], strValues[i]);
                }
            }
        }
        if (intValues != null && intKeys != null) {
            if (intValues.length > 0 && intKeys.length == intValues.length) {
                for (int i = 0; i < intValues.length; i++) {
                    intent.putExtra(intKeys[i], intValues[i]);
                }
            }
        }
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * Intent 跳转传单一整型数值
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         单一整型数值键
     * @param value       单一整型数值值
     * @param isCloseSelf 是否关闭当前页面
     */
    public static void skipInteger(Activity mActivity, Class<?> clazz,
                                   String key, int value, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtra(key, value);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * Intent 跳转传多个整型数值
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param intKeys     多个整型数值键数组
     * @param intValues   多个整型数值值数组
     * @param isCloseSelf 是否关闭当前页面
     */
    public static void skipIntegers(Activity mActivity, Class<?> clazz,
                                    String[] intKeys, int[] intValues, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        if (intValues != null && intKeys != null) {
            if (intValues.length > 0 && intKeys.length == intValues.length) {
                for (int i = 0; i < intValues.length; i++) {
                    intent.putExtra(intKeys[i], intValues[i]);
                }
            }
        }
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * Intent 跳转传单一字符串数值
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         单一字符串数值键
     * @param value       单一字符串数值值
     * @param isCloseSelf 是否关闭当前页面
     */
    public static void skipString(Activity mActivity, Class<?> clazz,
                                  String key, String value, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtra(key, value);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * Intent 跳转传多个字符串数值
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param strKeys     多个字符串数值键数组
     * @param strValues   多个字符串数值值数组
     * @param isCloseSelf 是否关闭当前页面
     */
    public static void skipStrings(Activity mActivity, Class<?> clazz,
                                   String[] strKeys, String[] strValues, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        if (strValues != null && strKeys != null) {
            if (strValues.length > 0 && strKeys.length == strValues.length) {
                for (int i = 0; i < strValues.length; i++) {
                    intent.putExtra(strKeys[i], strValues[i]);
                }
            }
        }
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 传递字符串集合Intent
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         键
     * @param datas       字符串集合
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipStringArray(Activity mActivity, Class<?> clazz,
                                       String key, ArrayList<String> datas, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(key, datas);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 获取传递过来的字符串集合
     *
     * @param mActivity 当前Activity
     * @param key       键
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<String> getSkipStringArray(Activity mActivity,
                                                       String key) {
        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        return (ArrayList<String>) bundle.getSerializable(key);
    }

    /**
     * 传递整型集合Intent
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         键
     * @param datas       整型集合
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipIntegerArray(Activity mActivity, Class<?> clazz,
                                        String key, ArrayList<Integer> datas, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(key, datas);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 获取传递过来的整型集合
     *
     * @param mActivity 当前Activity
     * @param key       键
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Integer> getSkipIntegerArray(Activity mActivity,
                                                         String key) {
        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        return (ArrayList<Integer>) bundle.getSerializable(key);
    }

    /**
     * Intent 传递JavaBean集合
     *
     * @param <T>
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         键
     * @param datas       Bean数据类集合，bean实现Serializable
     * @param isCloseSelf 是否关闭本界面
     */
    public static <T> void skipSerializableList(Activity mActivity,
                                                Class<?> clazz, String key, ArrayList<T> datas, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, datas.toArray());
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * Intent 接收传递的JavaBean 集合
     *
     * @param <T>
     * @param mActivity 当前Activity
     * @param key       键
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getSkipSerializableList(Activity mActivity,
                                                      String key) {
        ArrayList<T> datas = new ArrayList<>();
        Intent intent = mActivity.getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] cobjs = (Object[]) bundle.getSerializable(key);
            if (cobjs != null) {
                if (cobjs.length > 0) {
                    for (Object cobj : cobjs) {
                        T beans = (T) cobj;
                        datas.add(beans);
                    }
                }
            }
            return datas;
        }
        return null;
    }

    /**
     * Intent 传递Bean值
     *
     * @param mActivity   当前Activity
     * @param clazz       跳转类
     * @param key         键
     * @param t           Bean对象 bean 实现Serializable
     * @param isCloseSelf 是否finish掉当前Activity
     */
    public static <T> void skipSerializable(Activity mActivity, Class<?> clazz,
                                            String key, T t, boolean isCloseSelf) {
        Intent mIntent = new Intent(mActivity, clazz);
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(key, (Serializable) t);
        mIntent.putExtras(mBundle);
        mActivity.startActivity(mIntent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 获得Intent传递的Bean
     *
     * @param mActivity 当前Activity
     * @param key       键
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getSkipSerializable(Activity mActivity, String key) {
        return (T) mActivity.getIntent().getSerializableExtra(key);
    }

    /**
     * startActivityForResult跳转
     *
     * @param <T>
     * @param mActivity 当前Activity
     * @param clazz     要跳向的界面的class
     * @param key       键
     * @param datas     Bean数据类集合，bean实现Serializable
     */
    public static <T> void skipForResultSerializable(Activity mActivity,
                                                     Class<?> clazz, String key, ArrayList<T> datas, int requestCode) {
        Intent intent = new Intent(mActivity, clazz);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, datas.toArray());
        intent.putExtras(bundle);
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * SetResult Intent 回传JavaBean集合 给onActivityResult
     *
     * @param <T>
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param key         键
     * @param datas       Bean数据类集合，bean实现Serializable
     * @param isCloseSelf 是否关闭本界面
     */
    public static <T> void skipSetResultSerializable(Activity mActivity,
                                                     Class<?> clazz, String key, ArrayList<T> datas, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, datas.toArray());
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 回传字符串集合到onActivityResult方法中
     *
     * @param mActivity   当前Activity
     * @param key         键
     * @param datas       字符串集合
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipSetResultStringArray(Activity mActivity, String key,
                                                ArrayList<String> datas, boolean isCloseSelf) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(key, datas);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * @param mActivity
     * @param clazz
     * @param key
     * @param value
     * @param requestCode
     * @param isCloseSelf
     */
    public static void skipForResultString(Activity mActivity, Class<?> clazz, String key,
                                           String value, int requestCode, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        intent.putExtra(key, value);
        mActivity.startActivityForResult(intent, requestCode);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 封装Intent跳转
     *
     * @param mActivity   当前Activity
     * @param clazz       要跳向的界面的class
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipForResult(Activity mActivity, Class<?> clazz,
                                     int requestCode, boolean isCloseSelf) {
        Intent intent = new Intent(mActivity, clazz);
        mActivity.startActivityForResult(intent, requestCode);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * 回传intent 消息
     *
     * @param mActivity   当前Activity
     * @param isCloseSelf 是否关闭本界面
     */
    public static void skipSetResult(Activity mActivity, boolean isCloseSelf) {
        Intent intent = new Intent();
        //noinspection AccessStaticViaInstance
        mActivity.setResult(mActivity.RESULT_OK, intent);
        if (isCloseSelf) {
            mActivity.finish();
        }
    }

    /**
     * android用于 直接打电话intent跳转 需要权限 <uses-permission
     * android:name="android.permission.CALL_PHONE"/>
     *
     * @param mActivity 当前Activity
     * @param telnum    电话号码
     */
    public static void startActivityCallPhone(Activity mActivity, String telnum) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                + telnum));
        mActivity.startActivity(intent);
    }

    /**
     * android用于 打开拨号键盘intent跳转
     *
     * @param mActivity 当前Activity
     * @param telnum    电话号码
     */
    public static void startActivityCallNumber(Activity mActivity, String telnum) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
                + telnum));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开HTML文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     Html网址路径
     */
    public static void startActivityHtmlFile(Activity mActivity, String param) {
        Uri uri = Uri.parse(param).buildUpon()
                .encodedAuthority("com.android.htmlfileprovider")
                .scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开图片文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     图片文件路径
     */
    public static void startActivityImageFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "image/*");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开PDF文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     Pdf文件路径
     */
    public static void startActivityPdfFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/pdf");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开文本文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     文本文件路径
     */
    public static void startActivityTextFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri1 = Uri.parse(param);
        intent.setDataAndType(uri1, "text/plain");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开音频文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     音频文件路径
     */
    public static void startActivityAudioFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "audio/*");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开视频文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     视频文件路径
     */
    public static void startActivityVideoFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "video/*");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开CHM文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     视频文件路径
     */
    public static void startActivityChmFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/x-chm");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开Word文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     视频文件路径
     */
    public static void startActivityWordFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/msword");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开Excel文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     视频文件路径
     */
    public static void startActivityExcelFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        mActivity.startActivity(intent);
    }

    /**
     * android用于打开PPT文件的intent跳转
     *
     * @param mActivity 当前Activity
     * @param param     视频文件路径
     */
    public static void startActivityPptFile(Activity mActivity, String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        mActivity.startActivity(intent);
    }

    /**
     * 调用系统照相机拍照
     *
     * @param mActivity   当前Activity
     * @param uri         照片路径Uri
     * @param requestCode 请求码
     */
    public static void cameraIntent(Activity mActivity, Uri uri, int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * 截图方法
     *
     * @param mActivity   当前Activity
     * @param uri         照片路径Uri
     * @param requestCode 请求码
     * @param width       截图宽度
     * @param height      截图高度
     */
    public static void cropImageUri(Activity mActivity, Uri uri,
                                    int requestCode, int width, int height) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        mActivity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转照相机
     *
     * @param activity
     * @param imageUri
     */
    public static void skipCamera(Activity activity, Uri imageUri, int requstCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intent, requstCode);
    }

    /**
     * 跳转相册
     *
     * @param activity
     * @param requstCode
     */
    public static void skipPhoto(Activity activity, int requstCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        activity.startActivityForResult(intent, requstCode);
    }

    /**
     * 截图方法
     *
     * @param uri
     * @param requestCode
     */
    public static void cropImageUri(Activity activity, Uri uri, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        intent.putExtra("scale", true);
        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, requestCode);
    }

}
