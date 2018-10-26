package com.anch.wxy_pc.imclient.utils;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.bean.AccountBean;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/7/7.
 */
public class AccountDbUtil {
    private static DbUtils dbUtils = IMApplication.getInstance().dbUtils;

    /**
     * 保存账户
     *
     * @param accountBean
     */
    public static void saveAccount(AccountBean accountBean) {
        try {
            List<AccountBean> accountBeans = select();
            List<String> accountNames = new ArrayList<>();
            if (accountBeans != null && accountBeans.size() > 0) {
                for (AccountBean bean : accountBeans) {
                    accountNames.add(bean.getAccountName());
                }
                if (!new JavaTools<String>().isExistSubset(accountBean.getAccountName(), accountNames))
                    dbUtils.save(accountBean);
                else {
                    for (AccountBean bean : accountBeans) {
                        if (bean.getAccountName().equals(accountBean.getAccountName())) {
                            AccountBean bean1 = bean;
                            bean1.setTime(accountBean.getTime());
                            dbUtils.update(bean1);
                        }
                    }
                }
            } else {
                dbUtils.save(accountBean);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAccount(String accountName) {

        try {
            List<AccountBean> accounts = dbUtils.findAll(Selector.from(AccountBean.class).where("accountName", "=", accountName));
            if (accounts != null && accounts.size() > 0) {
                for (AccountBean bean : accounts) {
                    dbUtils.delete(bean);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询
     *
     * @return
     */
    public static List<AccountBean> select() {
        try {
            List<AccountBean> accountBeans = dbUtils.findAll(Selector.from(AccountBean.class).orderBy("time", true));
            List<AccountBean> accounts = new ArrayList<>();
            if (accountBeans != null && accountBeans.size() > 0) {
                for (int i = 0; i < accountBeans.size(); i++) {
                    if (i > 2) {
                        deleteAccount(accountBeans.get(i).getAccountName());
                    } else {
                        accounts.add(accountBeans.get(i));
                    }
                }
                return accounts;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据用户查询密码
     *
     * @param accountName
     * @return
     */
    public static AccountBean selectAccount(String accountName) {
        try {
            List<AccountBean> accountBeans = dbUtils.findAll(Selector.from(AccountBean.class).where("accountName", "=", accountName));
            if (accountBeans != null && accountBeans.size() > 0)
                return accountBeans.get(0);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
