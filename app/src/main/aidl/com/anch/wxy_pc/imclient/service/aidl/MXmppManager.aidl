package com.anch.wxy_pc.imclient.service.aidl;

interface MXmppManager{

    boolean connect();

    boolean login(String userName,String userPass);

    boolean disConnect();
}