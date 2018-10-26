package com.anch.wxy_pc.imclient.xmpp;


/**
 * Created by wxy-pc on 2015/6/12.
 */
public interface XmppInterface {

    void onStart();

    void onSuccess();

    void onFail(int result, int id);
}
