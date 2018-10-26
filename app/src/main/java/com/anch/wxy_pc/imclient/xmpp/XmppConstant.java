package com.anch.wxy_pc.imclient.xmpp;

/**
 * Created by wxy-pc on 2015/6/12.
 */
public class XmppConstant {

    //服务器连接地址
//    public final static String XMPP_CON_HOST = "192.168.200.114";
    public final static String XMPP_CON_HOST = "wxiangyu.xicp.net";
    //服务器端口号
//    public final static int XMPP_CON_PORT = 5222;
    public final static int XMPP_CON_PORT = 30193;
    //资源名
    public final static String XMPP_RESOURCE = "IMCLINE";
    //登陆成功
    public final static int XMPP_LOGIN_SUCCESS = 200;
    //登陆超时
    public final static int XMPP_LOGIN_TMOUT = 400;
    //登陆失败
    public final static int XMPP_LOGIN_FAILD = 401;
    //重复登录
    public final static int XMPP_CONFIRM = 409;
    //连接服务器失败
    public final static int XMPP_CON_SER_FAILD = 410;
    //连接服务器成功
    public final static int XMPP_CON_SER_SUCCESS = 411;
    //连接服务器超时
    public final static int XMPP_CON_SER_TIMEOUT = 412;
    //退出登录
    public final static int XMPP_CON_SER_EXIT = 413;
    //服务器没有结果
    public final static int XMPP_REG_NO_RESPONSE = 444;
    //注册成功
    public final static int XMPP_REG_SUCCESS = 445;
    //账号存在
    public final static int XMPP_REG_ACCOUNT_EXIST = 446;
    //注册失败
    public final static int XMPP_REG_FAILD = 447;

    //添加好友成功
    public final static int XMPP_ADD_FRIEND_SUCCESS = 1000;
    //该好友没有注册呢
    public final static int XMPP_ADD_FRIEND_FAIL = 1001;
    //您已经有该好友了
    public final static int XMPP_ADD_FRIEND_RESIVE = 1002;

    //发起添加好友
    public static String XMPP_LAUNCH_ADD_FRIEND = "";
    public static String XMPP_LAUNCH_ADD_FRIEND1 = "";
}
