package com.anch.wxy_pc.imclient.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.adapter.ContactsAdapter;
import com.anch.wxy_pc.imclient.bean.ContactsBean;
import com.anch.wxy_pc.imclient.notification.AddFriendNotification;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ContactsDbUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 联系人Frg
 * Created by wxy-pc on 2015/6/12.
 */
public class ContactsFragment extends Fragment implements AdapterView.OnItemClickListener {
    public final static String UPDATE_CONTACTS_OWN_ACTION = "com.anch.wxy_pc.imclient.fragment.UPDATE_CONTACTS_OWN_ACTION";
    public final static String UPDATE_CONTACTS_OTHER_ACTION = "com.anch.wxy_pc.imclient.fragment.UPDATE_CONTACTS_OTHER_ACTION";
    public final static int UPDATE_CONTACTS_ONLINE_STATE = 7777;
    private static ListView contactsLv;
    private static ContactsAdapter contactsAdapter;
    private static List<ContactsBean> contactsBeanList;
    public static ContactsFragment contactsFragment;
    public static MyHandler handler;
    private static Context context;
//    private OnlineService service;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        contactsFragment = this;
        context = activity;
        handler = new MyHandler(activity);
//        initService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        contactsLv = (ListView) view.findViewById(R.id.contacts_lv);
        setAdapter();
        handler.sendEmptyMessage(UPDATE_CONTACTS_ONLINE_STATE);
        return view;
    }

    private void setAdapter() {
        contactsBeanList = ContactsDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        contactsAdapter = new ContactsAdapter(context, contactsBeanList);
        contactsLv.setAdapter(contactsAdapter);
    }

    public static void notifyAdapter() {
        contactsBeanList = ContactsDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        contactsAdapter.setNotifyChange(contactsBeanList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    public static class UpdateContactsBrocast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ContactsFragment.UPDATE_CONTACTS_OWN_ACTION)) {
                notifyAdapter();
                ConversationFragment.handler.sendEmptyMessage(ConversationFragment.UPDATE_DATA);
            } else {
                if (action.equals(ContactsFragment.UPDATE_CONTACTS_OTHER_ACTION)) {
                    notifyAdapter();
                    ConversationFragment.handler.sendEmptyMessage(ConversationFragment.UPDATE_DATA);
                    if (intent != null)
                        AddFriendNotification.showNotification(context, UtilTools.splitStr(intent.getStringExtra("otherJid"), 0) + intent.getStringExtra("addOrRemoveStr"), intent.getStringExtra("titleStr"), intent.getStringExtra("titleStr"), null, null, null, false);
                }
            }
        }
    }

    public class MyHandler extends Handler {
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
                    case UPDATE_CONTACTS_ONLINE_STATE:
                        XmppManager.xmppManager.getFriendOnlineInfo(XmppManager.xmppManager.createXmppConnect());
//                        newContactsBeanList = ContactsDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
//                        if (!new JavaTools<ContactsBean>().isTwoListSame(contactsBeanList, newContactsBeanList)) {
                        notifyAdapter();
//                        }
                        break;
                }
            }
        }
    }
//    //绑定serivice
//    private void initService() {
//        Intent intent = new Intent(HomeAct.homeAct, OnlineService.class);
//        HomeAct.homeAct.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder iBinder) {
//            service = ((OnlineService.OnlineServiceBinder) iBinder).getService();
//            service.updateFirendState();
//            service.setCallBack(new OnlineService.UpdateCallBack() {
//                @Override
//                public void onlineState() {
//                    XmppManager.xmppManager.getFriendOnlineInfo(XmppManager.xmppManager.createXmppConnect());
//                    notifyAdapter();
//                }
//            });
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            service.removeCallBack();
//        }
//    };

    @Override
    public void onDetach() {
        super.onDetach();
        handler.removeMessages(UPDATE_CONTACTS_ONLINE_STATE);
//        HomeAct.homeAct.unbindService(serviceConnection);
//        Intent intent = new Intent(HomeAct.homeAct, OnlineService.class);
//        HomeAct.homeAct.stopService(intent);
    }
}
