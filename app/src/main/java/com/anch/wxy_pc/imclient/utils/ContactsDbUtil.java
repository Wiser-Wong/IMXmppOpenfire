package com.anch.wxy_pc.imclient.utils;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.bean.ContactsBean;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/15.
 */
public class ContactsDbUtil {

    private static DbUtils dbUtils = IMApplication.getInstance().dbUtils;

    /**
     * 存数据库
     *
     * @param contactsBean
     * @throws DbException
     */
    public static void save(ContactsBean contactsBean) throws DbException {
        List<ContactsBean> contactsBeans = select(contactsBean.getCurrent_user());
        List<String> contactsName = new ArrayList<>();
        if (contactsBeans != null && contactsBeans.size() > 0) {
            for (ContactsBean bean : contactsBeans) {
                contactsName.add(bean.getAccount());
            }
            if (!new JavaTools<String>().isExistSubset(contactsBean.getAccount(), contactsName)) {
                dbUtils.save(contactsBean);
            }
        } else {
            dbUtils.save(contactsBean);
        }
    }

    /**
     * 查找所有
     *
     * @return
     */
    public static List<ContactsBean> select(String user) {
        try {
            return dbUtils.findAll(Selector.from(ContactsBean.class).where("current_user", "=", user).orderBy("onlineState", false));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查找用户
     *
     * @return
     */
    public static ContactsBean selectName(String account) {
        ContactsBean contactsBean = null;
        try {
            List<ContactsBean> contactsBeans = dbUtils.findAll(Selector.from(ContactsBean.class).where("account", "=", account));
            if (contactsBeans != null && contactsBeans.size() > 0)
                contactsBean = contactsBeans.get(0);

        } catch (DbException e) {
            e.printStackTrace();
        }
        return contactsBean;
    }

    /**
     * 查找昵称
     *
     * @return
     */
    public static ContactsBean selectNickName(String nickName, String currentUser) {
        ContactsBean contactsBean = null;

        try {
            List<ContactsBean> contactsBeans = dbUtils.findAll(Selector.from(ContactsBean.class).where("nickName", "=", nickName).and("current_user", "=", currentUser));
            if (contactsBeans != null && contactsBeans.size() > 0)
                contactsBean = contactsBeans.get(0);

        } catch (DbException e) {
            e.printStackTrace();
        }
        return contactsBean;
    }

    /**
     * 删除好友信息
     *
     * @param account
     */
    public static void deleteFrient(String account, String currentUser) {
        try {
            List<ContactsBean> contactsBeans = dbUtils.findAll(Selector.from(ContactsBean.class).where("account", "=", account).and("current_user", "=", currentUser));
            if (contactsBeans != null && contactsBeans.size() > 0) {
                for (ContactsBean bean : contactsBeans) {
                    dbUtils.delete(bean);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有联系人
     */
    public static void deleteAll() {
        try {
            dbUtils.deleteAll(ContactsBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新在线离线状态
     *
     * @param contactsBean
     */
    public static void updateOnline(ContactsBean contactsBean) {
        List<ContactsBean> contactsBeans = null;
        try {
            contactsBeans = dbUtils.findAll(Selector.from(ContactsBean.class).where("account", "=", contactsBean.getAccount()));
            if (contactsBeans != null && contactsBeans.size() > 0) {
                for (ContactsBean bean : contactsBeans) {
                    if (bean.getAccount().equals(contactsBean.getAccount())) {
                        ContactsBean contactsBean1 = bean;
                        contactsBean1.setOnlineState(contactsBean.getOnlineState());
                        dbUtils.update(contactsBean1);
                    }
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
