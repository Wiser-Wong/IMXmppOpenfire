package com.anch.wxy_pc.imclient.utils;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/15.
 */
public class ConversationDbUtil {

    private static DbUtils dbUtils = IMApplication.getInstance().dbUtils;

    /**
     * 存数据库
     *
     * @param conversationBean
     * @throws DbException
     */
    public static void save(ConversationBean conversationBean) {
        try {
            dbUtils.save(conversationBean);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查找所有不相同的联系人的记录返回最新一条信息
     *
     * @return
     */
    public static List<ConversationBean> select(String user) {
        try {
            List<ConversationBean> conversationBeans = dbUtils.findAll(Selector.from(ConversationBean.class).where("current_user", "=", user));

            if (conversationBeans != null && conversationBeans.size() > 0) {
                List<ConversationBean> convers;
                List<String> converNames = new ArrayList<>();
                List<String> newConverNames;
                List<ConversationBean> newConversationBean = new ArrayList<>();
                for (ConversationBean bean : conversationBeans) {
                    converNames.add(bean.getWho_name());
                }
                if (converNames.size() > 0) {
                    newConverNames = new JavaTools<String>().resetValue(converNames);
                    for (int i = 0; i < newConverNames.size(); i++) {
                        convers = selectName(newConverNames.get(i), user);
                        if (convers != null && convers.size() > 0) {
                            newConversationBean.add(convers.get(0));
                        }
                        convers.clear();
                    }
                }
                return new JavaTools<ConversationBean>().downSort(newConversationBean);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询联系人名称
     *
     * @param name
     * @return
     */
    public static List<ConversationBean> selectName(String name, String currentUser) {
        try {
            return dbUtils.findAll(Selector.from(ConversationBean.class).where("who_name", "=", name).and("current_user", "=", currentUser).orderBy("date", true));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询发送接收消息，聊天记录
     *
     * @param sessionId
     * @return
     */
    public static List<ConversationBean> selectMssage(String sessionId, String currentUser) {
        try {
            return dbUtils.findAll(Selector.from(ConversationBean.class).where("session_name", "=", sessionId).and("current_user", "=", currentUser).orderBy("date", false));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 更新未读消息个数
     *
     * @param whoId
     */
    public static int updateLastMsgCount(String whoId) {
        try {
            List<ConversationBean> conversationBeans = dbUtils.findAll(Selector.from(ConversationBean.class).where("who_id", "=", whoId).orderBy("date", true));
            if (conversationBeans != null && conversationBeans.size() > 0) {
                for (ConversationBean bean : conversationBeans) {
                    if (bean.getWho_id().equals(whoId)) {
                        ConversationBean conversationBean = bean;
                        conversationBean.setCount(bean.getCount() + 1);
                        return bean.getCount();
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 清空该聊天记录的消息个数信息
     *
     * @param whoId
     */
    public static void clearMesCount(String whoId) {
        try {
            List<ConversationBean> conversationBeans = dbUtils.findAll(Selector.from(ConversationBean.class).where("who_id", "=", whoId));
            if (conversationBeans != null && conversationBeans.size() > 0) {
                for (ConversationBean bean : conversationBeans) {
                    bean.setCount(0);
                    dbUtils.update(bean);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除与谁的聊天记录
     *
     * @param whoId
     * @param currentUser
     */
    public static void clearFriendMsg(String whoId, String currentUser) {
        try {
            List<ConversationBean> conversationBeans = dbUtils.findAll(Selector.from(ConversationBean.class).where("who_id", "=", whoId).and("current_user", "=", currentUser));
            if (conversationBeans != null && conversationBeans.size() > 0) {
                for (ConversationBean bean : conversationBeans) {
                    dbUtils.delete(bean);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空该账号下所有聊天记录
     *
     * @param currentUser
     */
    public static boolean clearAllMes(String currentUser) {

        try {
            List<ConversationBean> conversationBeans = dbUtils.findAll(Selector.from(ConversationBean.class).where("current_user", "=", currentUser));
            if (conversationBeans != null && conversationBeans.size() > 0) {
                dbUtils.deleteAll(conversationBeans);
                return true;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return false;
    }
}
