package com.anch.wxy_pc.imclient;

import android.app.Application;

import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.lidroid.xutils.DbUtils;

/**
 * Created by wxy-pc on 2015/6/11.
 */
public class IMApplication extends Application {
    public DbUtils dbUtils;
    private static IMApplication imApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        new XmppManager().createXmppManager();
        dbUtils = DbUtils.create(this, Constanst.FileDb, Constanst.SQ_NAME);
    }

    private void setInstance(IMApplication instance) {
        if (IMApplication.imApplication == null)
            IMApplication.imApplication = instance;
    }

    public static IMApplication getInstance() {
        return imApplication;
    }
}
