package com.anch.wxy_pc.imclient.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.anch.wxy_pc.imclient.act.HomeAct;
import com.anch.wxy_pc.imclient.act.LoginAct;

import java.lang.ref.WeakReference;

/**
 * Created by wxy-pc on 2015/6/26.
 */
public class OnlineService extends Service {
    private OnlineServiceBinder binder = new OnlineServiceBinder();
    private final static int UPDATE_INFO = 0X1002;
    private UpdateCallBack callBack;
    private MyHandler handler;

    public interface UpdateCallBack {
        void onlineState();
    }

    public void setCallBack(UpdateCallBack callBack) {
        this.callBack = callBack;
    }

    public void removeCallBack() {
        callBack = null;
        handler.removeMessages(UPDATE_INFO);
    }

    private class MyHandler extends Handler {
        WeakReference<Activity> reference;

        MyHandler(Activity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_INFO:
                        if (callBack != null) {
                            callBack.onlineState();
                        }
                        handler.sendEmptyMessageDelayed(UPDATE_INFO, 5000);
                        break;
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void updateFirendState() {
        if (handler == null) {
            handler = new MyHandler(HomeAct.homeAct);
            handler.sendEmptyMessage(UPDATE_INFO);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class OnlineServiceBinder extends Binder {

        public OnlineService getService() {
            return OnlineService.this;
        }
    }
}
