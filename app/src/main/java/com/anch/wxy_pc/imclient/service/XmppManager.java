package com.anch.wxy_pc.imclient.service;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.ChatAct;
import com.anch.wxy_pc.imclient.bean.ContactsBean;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.fragment.ContactsFragment;
import com.anch.wxy_pc.imclient.fragment.ConversationFragment;
import com.anch.wxy_pc.imclient.notification.AddFriendNotification;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ContactsDbUtil;
import com.anch.wxy_pc.imclient.utils.ConversationDbUtil;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.anch.wxy_pc.imclient.xmpp.XmppCallBack;
import com.anch.wxy_pc.imclient.xmpp.XmppConstant;
import com.lidroid.xutils.exception.DbException;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * Xmpp连接配置
 * Created by wxy-pc on 2015/6/11.
 */
public class XmppManager {
    private Context context;
    private int id;
    private XMPPConnection connection;
    private ConnectionListener connectionListener;
    private RosterListener rosterListener;
    private PacketListener messageListener;
    private PacketListener packetListener;
    private XmppCallBack xmppCallBack;
    public static XmppManager xmppManager;
    private ConnectionConfiguration config;
    private String userName, userPass;
    private Roster roster;
    private String addStr = "与您建立了好友关系，你们是好友了。";
    private String removeStr = "删除了与您的好友关系，你们不是好友了。";
    private String addTitleStr = "添加好友";
    private String removeTitleStr = "删除好友";

    public XmppManager() {
        super();
    }

    /**
     * 连接openfire开始登陆
     */
    public void connectOpenfireSer(Context context, int id, String userName, String userPass, XmppCallBack xmppCallBack) {

        if (isAlreadyLogin()) {
            ToastTools.frameToast(context, "已经登陆", R.drawable.custom_toast);
            return;
        }
        if (isOnline(userName)) {
            ToastTools.frameToast(context, "已经在线", R.drawable.custom_toast);
            return;
        }

        this.context = context;
        this.id = id;
        this.xmppCallBack = xmppCallBack;
        this.userName = userName;
        this.userPass = userPass;
        if (xmppCallBack != null) xmppCallBack.onStart();
        new LoginAsyncTask().execute(userName, userPass);
    }

    /**
     * 初始化Config
     *
     * @return
     */
    private ConnectionConfiguration configuration() {
        if (config == null) {
            config = new ConnectionConfiguration(XmppConstant.XMPP_CON_HOST, XmppConstant.XMPP_CON_PORT);
            config.setDebuggerEnabled(true);
            config.setCompressionEnabled(true);// 默认false
            config.setSASLAuthenticationEnabled(true);// 默认true
            config.setKeystoreType(null);
            config.setKeystorePath(null);
            config.setSelfSignedCertificateEnabled(true);// 默认false
            config.setTruststoreType(null);
            config.setTruststorePassword(null);
            config.setTruststorePath(null);
            config.setRosterLoadedAtLogin(true);// 默认true
            config.setReconnectionAllowed(false);// 默认true

            config.setReconnectionAllowed(true);
            config.setCompressionEnabled(false);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setSASLAuthenticationEnabled(false);
            //设置安全登陆 不加出问题
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        }
        return config;
    }

    /**
     * 异步加载连接
     */
    public class LoginAsyncTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            XMPPConnection connection = createXmppConnect();
            //连接成功
            if (connect(connection)) {
                //登陆成功
                if (login(connection, params[0], params[1])) {
                    return XmppConstant.XMPP_LOGIN_SUCCESS;
                } else {//登陆失败
                    return XmppConstant.XMPP_LOGIN_FAILD;
                }
            } else {//连接失败
                return XmppConstant.XMPP_CON_SER_TIMEOUT;
            }
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case XmppConstant.XMPP_LOGIN_SUCCESS://登陆成功
                    xmppCallBack.onSuccess();
                    break;
                case XmppConstant.XMPP_LOGIN_FAILD://登陆失败
                    xmppCallBack.onFail(XmppConstant.XMPP_LOGIN_FAILD, id);
                    clearRoster();
                    break;
                case XmppConstant.XMPP_CON_SER_TIMEOUT://连接失败
                    xmppCallBack.onFail(XmppConstant.XMPP_CON_SER_TIMEOUT, id);
                    clearRoster();
                    break;
            }
        }
    }

    /**
     * 建立Xmpp连接
     */
    public boolean connect(XMPPConnection connection) {
        if (connection.isConnected()) return true;
        else {
            try {
                //开始连接
                connection.connect();
                //添加连接监听器
                if (connectionListener == null)
                    connectionListener = new MConnectionListener();
                connection.addConnectionListener(connectionListener);
                return true;
            } catch (XMPPException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 登陆连接
     */
    public boolean login(XMPPConnection connection, String userName, String userPass) {
        try {
            //最后一个参数是资源名就是项目名称
            connection.login(userName, userPass, XmppConstant.XMPP_RESOURCE);
            //消息监听
            if (messageListener == null)
                messageListener = new MessageListener();
            //添加监听 过滤出Message包
            connection.addPacketListener(messageListener, new PacketTypeFilter(Message.class));
            //添加好友监听
            if (packetListener == null)
                packetListener = new MPacketListener();
            //添加监听 过滤出Presence包
            connection.addPacketListener(packetListener, new PacketTypeFilter(Presence.class));
            //获取花名册
            Roster ros = createRoster();
            if (rosterListener == null)
                rosterListener = new MRosterListener();
            if (ros != null && ros.getEntries().size() > 0) {
                ros.addRosterListener(rosterListener);
                addContacts(ros);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 创建花名册对象
     *
     * @return
     */
    public Roster createRoster() {
        if (roster == null)
            roster = createXmppConnect().getRoster();
        return roster;
    }

    /**
     * 添加联系人
     */
    public void addContacts(Roster ros) {
        for (RosterEntry entry : ros.getEntries()) {
            ContactsBean contactsBean = new ContactsBean();
            contactsBean.setCurrent_user(userName);
            contactsBean.setNickName(entry.getName());
            contactsBean.setAccount(entry.getUser());
            //获取好友在线状态
            Presence presence = ros.getPresence(entry.getUser());
            if (presence.isAvailable()) {//判断好友是否在线
                contactsBean.setOnlineState("在线");
            } else {
                contactsBean.setOnlineState("离线");
            }
            try {
                ContactsDbUtil.save(contactsBean);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送聊天信息
     */
    public void sendMessage(XMPPConnection connet, String firendJID, String firendName, String message, String type) {
        ChatManager chatManager = connet.getChatManager();
//        //查找chat对象
//        if (jidChats.containsKey(firendJID)) {
//            chat = jidChats.get(firendJID);
//        } else {//创建Chat
        Chat chat = chatManager.createChat(firendJID, null);
        //添加进集合
//            jidChats.put(firendJID, chat);
//        }
        if (chat != null) {
            try {
                //发送消息
                chat.sendMessage(message);
                //保存聊天记录
                ConversationBean conversationBean = new ConversationBean();
                conversationBean.setCurrent_user(userName);
                conversationBean.setWho_id(firendJID);
                conversationBean.setWho_name(firendName);
                conversationBean.setBody(message);
                conversationBean.setType(type);
                conversationBean.setSession_id(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
                conversationBean.setSession_name(firendName);
                conversationBean.setTime(DateUtils.getCurTime());
                conversationBean.setDate(DateUtils.getCurrDateTime(DateUtils.getCurrTime()));
                ConversationDbUtil.save(conversationBean);
                sendUpdateMesBroadcast();
            } catch (Exception e) {
                e.printStackTrace();
                ToastTools.frameToast(context, "服务器响应失败，导致发送失败", R.drawable.custom_toast);
            }
        }
    }

    /**
     * 包监听
     */
    private class MPacketListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof Presence) {
                Presence presence = (Presence) packet;
                String from = presence.getFrom();//发送方
                String to = presence.getTo();//接收方
                if (presence.getType().equals(Presence.Type.subscribe)) {//好友申请
                    System.out.println("wxy:-->>" + to + "好友申请:-->>" + from);
                    applyAddFriend(from);
                } else {
                    if (presence.getType().equals(Presence.Type.subscribed)) {//同意添加好友
                        System.out.println("wxy:-->>" + to + "同意添加好友:-->>" + from);
                    } else {
                        if (presence.getType().equals(Presence.Type.unsubscribe)) {//拒绝添加好友和删除好友
                            System.out.println("wxy:-->>" + to + "拒绝添加好友和删除好友:-->>" + from);
                            deleteFriend(from);
                        } else {
                            if (presence.getType().equals(Presence.Type.unsubscribed)) {

                            } else {
                                if (presence.getType().equals(Presence.Type.unavailable)) {//好友下线   要更新好友列表，可以在这收到包后，发广播到指定页面   更新列表
                                    System.out.println("wxy:-->>" + to + "好友下线:-->>" + from);
                                    ContactsFragment.handler.sendEmptyMessage(ContactsFragment.UPDATE_CONTACTS_ONLINE_STATE);
                                } else {//好友上线
                                    System.out.println("wxy:-->>" + to + "好友上线:-->>" + from);
                                    ContactsFragment.handler.sendEmptyMessage(ContactsFragment.UPDATE_CONTACTS_ONLINE_STATE);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 添加好友 无分组
     *
     * @param ros
     * @param userName
     * @param name
     * @return
     */
    public static boolean addUser(Roster ros, String userName, String name) {
        try {
            ros.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            ros.createEntry(userName, name, null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 添加好友 有分组
     *
     * @param roster
     * @param userName
     * @param name
     * @param groupName
     * @return
     */
    public static boolean addUser(Roster roster, String userName, String name,
                                  String groupName) {
        try {
            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.setSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.createEntry(userName, name, new String[]{groupName});
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 该用户申请添加好友对方做处理的方法
     *
     * @param from 来自哪个用户的Jid
     */
    private void applyAddFriend(String from) {
        Roster ros = createRoster();
        addUser(ros, from, UtilTools.splitStr(from, 0));
        addContacts(ros);
        if (XmppConstant.XMPP_LAUNCH_ADD_FRIEND.equals("XMPP_LAUNCH_ADD_FRIEND")) {
            XmppConstant.XMPP_LAUNCH_ADD_FRIEND = "";
            sendUpdateOwnContactsBrocast();
        } else sendUpdateOtherContactsBrocast(from, addStr, addTitleStr);
    }

    /**
     * 该用户删除好友对方做处理的方法
     *
     * @param from 来自哪个用户的Jid
     */
    private void deleteFriend(String from) {
        XMPPConnection xmppConnection = createXmppConnect();
        removeUser(xmppConnection, from, SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        ContactsDbUtil.deleteFrient(from, SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        ConversationDbUtil.clearFriendMsg(from, SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        sendUpdateOtherContactsBrocast(from, removeStr, removeTitleStr);
    }

    /**
     * Xmpp连接监听器
     */
    private class MConnectionListener implements ConnectionListener {


        @Override
        public void connectionClosed() {
            xmppCallBack.onFail(XmppConstant.XMPP_CON_SER_EXIT, id);
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            xmppCallBack.onFail(XmppConstant.XMPP_CON_SER_TIMEOUT, id);
        }

        @Override
        public void reconnectingIn(int i) {

        }

        @Override
        public void reconnectionSuccessful() {

        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    }


    /**
     * 花名册监听器
     */
    private class MRosterListener implements RosterListener {

        @Override
        public void entriesAdded(Collection<String> collection) {
            System.out.println("entriesAdded:-->>" + collection);
            Roster ros = createRoster();
            for (String jid : collection) {
                addUser(ros, jid, UtilTools.splitStr(jid, 0));
                addContacts(ros);
                if (XmppConstant.XMPP_LAUNCH_ADD_FRIEND1.equals("XMPP_LAUNCH_ADD_FRIEND1")) {
                    XmppConstant.XMPP_LAUNCH_ADD_FRIEND1 = "";
                    sendUpdateOwnContactsBrocast();
                } else sendUpdateOtherContactsBrocast(jid, addStr, addTitleStr);
                break;
            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            System.out.println("entriesUpdated:-->>" + collection);
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            System.out.println("entriesDeleted:-->>" + collection);
            for (String jid : collection) {
                ContactsDbUtil.deleteFrient(jid, SharePrefUtils.getString(Constanst.ACCOUNT, ""));
                ConversationDbUtil.clearFriendMsg(jid, SharePrefUtils.getString(Constanst.ACCOUNT, ""));
                sendUpdateOwnContactsBrocast();
            }
        }

        @Override
        public void presenceChanged(Presence presence) {
            System.out.println("presenceChanged:-->>" + presence);
        }
    }

    //发送删除联系人之后更新自己联系人广播
    private void sendUpdateOwnContactsBrocast() {
        Intent intent = new Intent(ContactsFragment.UPDATE_CONTACTS_OWN_ACTION);
        IMApplication.getInstance().sendBroadcast(intent);
    }

    //发送删除联系人之后更新其他人联系人广播
    private void sendUpdateOtherContactsBrocast(String jid, String addOrRemoveStr, String titleStr) {
        Intent intent = new Intent(ContactsFragment.UPDATE_CONTACTS_OTHER_ACTION);
        intent.putExtra("otherJid", jid);
        intent.putExtra("addOrRemoveStr", addOrRemoveStr);
        intent.putExtra("titleStr", titleStr);
        IMApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 消息监听器
     */
    private class MessageListener implements PacketListener {

        @Override
        public void processPacket(Packet packet) {
            if (packet instanceof Message) {
                Message message = (Message) packet;
                //聊天消息
                if (message.getType() == Message.Type.chat) {
                    ConversationBean conversationBean = new ConversationBean();
                    String whoAccountId = StringUtils.parseBareAddress(message.getFrom());//去掉资源名
                    String whoNameStr = ContactsDbUtil.selectName(whoAccountId).getAccount();
                    String bodyStr = message.getBody();
                    String typeStr = "char";
                    String timeStr = DateUtils.getCurTime();
                    String dateStr = DateUtils.getCurrDateTime(DateUtils.getCurrTime());
                    if (message.getBody() != null) {
                        conversationBean.setCurrent_user(userName);
                        conversationBean.setWho_id(whoAccountId);
                        conversationBean.setWho_name(whoNameStr);
                        conversationBean.setBody(bodyStr);
                        conversationBean.setType(typeStr);
                        conversationBean.setSession_id(whoAccountId);
                        conversationBean.setSession_name(whoNameStr);
                        conversationBean.setTime(timeStr);
                        conversationBean.setDate(dateStr);
                        conversationBean.setCount(ConversationDbUtil.updateLastMsgCount(whoAccountId));

                        ConversationDbUtil.save(conversationBean);
                        if (Constanst.CURRENT_PAGE == Constanst.CURRENT_PAGE_VALUE || Constanst.CHAT_PAGE == Constanst.CHAT_PAGE_VALUE) {
                            if (Constanst.CURRENT_PAGE == Constanst.CURRENT_PAGE_VALUE) {
                                sendChangerMesBroadcast();
                            }
                            if (Constanst.CHAT_PAGE == Constanst.CHAT_PAGE_VALUE) {
                                sendUpdateMesBroadcast();
                                return;
                            }
                            //发送消息显示通知
                            AddFriendNotification.showNotification(context, bodyStr, whoAccountId, UtilTools.splitStr(whoAccountId, 0), whoAccountId, whoNameStr, SharePrefUtils.getString(Constanst.ACCOUNT, ""), true);
                            Constanst.NOTI_FRIEND_ID = whoAccountId;
                        }
                    }
                }
            }
        }

    }

    /**
     * 获取好友在线状态
     *
     * @param connection
     */
    public void getFriendOnlineInfo(XMPPConnection connection) {
        Roster ros = createRoster();
        Collection<RosterEntry> entries = ros.getEntries();
        ContactsBean contactsBean;
        for (RosterEntry entry : entries) {
            contactsBean = new ContactsBean();
            //获取好友在线状态
            Presence presence = ros.getPresence(entry.getUser());
            if (presence.isAvailable()) {//判断好友是否在线
                contactsBean.setOnlineState("在线");
                contactsBean.setAccount(entry.getUser());
                ContactsDbUtil.updateOnline(contactsBean);
            } else {
                contactsBean.setOnlineState("离线");
                contactsBean.setAccount(entry.getUser());
                ContactsDbUtil.updateOnline(contactsBean);
            }
        }
    }

    /**
     * 发送广播
     */

    public static void sendChangerMesBroadcast() {
        Intent intent = new Intent(ConversationFragment.CHANGE_MES_RECEIVER);
        IMApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 发送广播
     */
    public static void sendUpdateMesBroadcast() {
        Intent intent = new Intent(ChatAct.UPDATE_MES_RECEIVER);
        IMApplication.getInstance().sendBroadcast(intent);
    }

    /**
     * 判断用户是否在线
     *
     * @param userName 用户名
     */
    public boolean isOnline(String userName) {
        if (isConnectionNull()) {
            Presence preference = createRoster().getPresence(userName);
            if (preference.isAvailable()) return true;
            else return false;
        }
        return false;
    }

    /**
     * 同时删除双方
     *
     * @param connection
     * @param firendName
     * @param currentUser
     * @return
     */
    public boolean removeUser(XMPPConnection connection, String firendName, String currentUser) {
        if (connection == null)
            return false;
        try {
            removeFriend(connection, firendName);
            removeFriend(connection, currentUser);
            ContactsDbUtil.deleteFrient(firendName, currentUser);
            ContactsDbUtil.deleteFrient(currentUser, firendName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除好友方法
     *
     * @param name
     */
    private void removeFriend(XMPPConnection connect, String name) {
        if (name.contains("@")) {
            name = UtilTools.splitStr(name, 0);
        }
//        RosterEntry entry = connect.getRoster().getEntry(name);
//        try {
//            connect.getRoster().removeEntry(entry);
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        }
        Roster ros = createRoster();
        Collection<RosterEntry> entries = ros.getEntries();
        for (RosterEntry entry : entries) {
            if (UtilTools.splitStr(entry.getUser(), 0).equals(name)) {
                try {
                    ros.removeEntry(entry);
                    break;
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * /**
     * 连接是否为null
     */
    public boolean isConnectionNull() {
        if (connection != null)
            return true;
        return false;
    }

    /**
     * 是否已经登陆
     */
    public boolean isAlreadyLogin() {
        return connection != null && connection.isConnected() && connection.isAuthenticated();
    }

    /**
     * 失去连接 退出登录
     */
    public void exitLogin() {
        if (isConnectionNull()) {
            if (connection.isConnected())
                connection.disconnect();
            connection = null;
            roster = null;
        }
    }

    private void clearRoster() {
        connection = null;
        roster = null;
    }

    /**
     * 创建XmppManager对象
     *
     * @return
     */
    public XmppManager createXmppManager() {
        if (xmppManager == null) {
            xmppManager = new XmppManager();
        }
        return xmppManager;
    }

    /**
     * 创建XmppConnection对象
     *
     * @return
     */
    public XMPPConnection createXmppConnect() {
        if (connection == null)
            connection = new XMPPConnection(configuration());
        return connection;
    }

}
