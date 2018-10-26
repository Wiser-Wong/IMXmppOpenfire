package com.anch.wxy_pc.imclient.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.ChatAct;
import com.anch.wxy_pc.imclient.act.HomeAct;
import com.anch.wxy_pc.imclient.utils.DateUtils;

/**
 * Created by wxy-pc on 2015/7/1.
 */
public class AddFriendNotification {

    private static NotificationManager manager;

    /**
     * 消息通知
     *
     * @param contentText
     * @param ticker
     * @param title
     * @param friendId
     * @param friendName
     * @param currentUser
     * @param flag
     */
    public static void showNotification(Context mContext, String contentText, String ticker, String title, String friendId, String friendName, String currentUser, boolean flag) {
        manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
        builder.setTicker(ticker);
        builder.setSmallIcon(R.mipmap.im_icon);
        builder.setContentTitle(title);
        builder.setContentText(contentText);
        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.im_icon));
        builder.setOngoing(false);
        builder.setAutoCancel(true);//点击清楚通知栏
        builder.setWhen(DateUtils.getCurrTime());
        builder.setDefaults(Notification.DEFAULT_SOUND);
        if (flag) {
            Intent notificationIntent = new Intent(mContext, ChatAct.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString("friendId", friendId);
            bundle.putString("friendName", friendName);
            bundle.putString("currentUser", currentUser);
            notificationIntent.putExtras(bundle);
            PendingIntent contentIntent = PendingIntent.getActivity(HomeAct.homeAct, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
        }
        manager.notify(0, builder.build());
    }

    /**
     * 移除通知
     */
    public static void cancleNotification() {
        if (manager != null)
            manager.cancel(0);
    }
}
