package com.anch.wxy_pc.imclient.act;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.base.BaseActivity;
import com.anch.wxy_pc.imclient.fragment.ContactsFragment;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ContactsDbUtil;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.anch.wxy_pc.imclient.xmpp.XmppConstant;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/24.
 */
@ContentView(R.layout.act_add_friends)
public class AddFriendAct extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.input_friend_account_et)
    private EditText addFriendEt;
    private XMPPConnection connection;
    private ContactsFragment.UpdateContactsBrocast brocast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        getData();
    }

    private void getData() {
        Intent intent = getIntent();
//        if (intent != null)
//            currentUser = intent.getStringExtra(Constanst.SKIP_ADD_FRIENT_PASS_USER);
    }

    @OnClick(R.id.add_friend_btn)
    @Override
    public void onClick(View v) {
        if (addFriendEt.length() == 0)
            ToastTools.frameToast(AddFriendAct.this, "请输入好友账户", R.drawable.custom_toast);
        else {
            addFriend();
        }
    }

    private void addFriend() {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(AddFriendAct.this, LayoutInflater.from(AddFriendAct.this).inflate(R.layout.dialog_pro, null), "搜索好友中", R.style.ProgressDialog, false);
            }

            @Override
            protected Integer doInBackground(Void... params) {
                String userName = addFriendEt.getText().toString().trim();
                connection = XmppManager.xmppManager.createXmppConnect();
                try {
                    if (!searchUsers(userName)) {
                        return XmppConstant.XMPP_ADD_FRIEND_FAIL;
                    } else {
                        if (!userName.equals(SharePrefUtils.getString(Constanst.ACCOUNT, "")) && ContactsDbUtil.selectNickName(userName + "@" + UtilTools.splitStr(connection.getUser(), 1), SharePrefUtils.getString(Constanst.ACCOUNT, "")) != null) {
                            return XmppConstant.XMPP_ADD_FRIEND_RESIVE;
                        } else {
                            //发起添加好友发起人判断
                            XmppConstant.XMPP_LAUNCH_ADD_FRIEND = "XMPP_LAUNCH_ADD_FRIEND";
                            XmppConstant.XMPP_LAUNCH_ADD_FRIEND1 = "XMPP_LAUNCH_ADD_FRIEND1";
                            Presence presence = new Presence(Presence.Type.subscribe);
                            presence.setTo(userName + "@" + UtilTools.splitStr(connection.getUser(), 1) + "/" + XmppConstant.XMPP_RESOURCE);//接收方jid
                            presence.setFrom(connection.getUser() + "/" + XmppConstant.XMPP_RESOURCE);//发送方jid
                            connection.sendPacket(presence);//connection是你自己的XMPPConnection链接
                            return XmppConstant.XMPP_ADD_FRIEND_SUCCESS;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return XmppConstant.XMPP_ADD_FRIEND_FAIL;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                DialogUtil.cancleDialog();
                switch (result) {
                    case XmppConstant.XMPP_ADD_FRIEND_SUCCESS://添加成功
                        NiftyNotificationView.build(AddFriendAct.this, "添加成功", Effects.thumbSlider, R.id.add_friend_notify_rl)
                                .setIcon(R.mipmap.notify_bg)
                                .show();
                        break;
                    case XmppConstant.XMPP_ADD_FRIEND_FAIL://添加失败
                        NiftyNotificationView.build(AddFriendAct.this, "添加失败", Effects.thumbSlider, R.id.add_friend_notify_rl)
                                .setIcon(R.mipmap.notify_bg)
                                .show();
                        break;
                    case XmppConstant.XMPP_ADD_FRIEND_RESIVE://有该好友了
                        NiftyNotificationView.build(AddFriendAct.this, "你们已经是好友了，不需要重新添加。", Effects.thumbSlider, R.id.add_friend_notify_rl)
                                .setIcon(R.mipmap.notify_bg)
                                .show();
                        break;
                }
            }
        }.execute();
    }

    /**
     * 查询用户
     *
     * @param userName
     * @return
     * @throws XMPPException
     */
    public boolean searchUsers(String userName) {

        if (connection == null)
            return true;
        HashMap<String, String> user = null;
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        try {
//            connection.connect();
            ProviderManager.getInstance();
            UserSearchManager usm = new UserSearchManager(connection);
            Form searchForm = usm.getSearchForm("search." + XmppConstant.XMPP_CON_HOST);
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = usm.getSearchResults(answerForm, "search." + XmppConstant.XMPP_CON_HOST);
            Iterator<ReportedData.Row> it = data.getRows();
            ReportedData.Row row = null;
            while (it.hasNext()) {
                user = new HashMap<String, String>();
                row = it.next();
                user.put("userAccount", row.getValues("username").next().toString());
                user.put("userPhote", row.getValues("name").next()
                        .toString());
                results.add(user);
                // 若存在，则有返回,UserName一定非空，其他两个若是有设，一定非空
            }
            if (results.size() > 0) {
                return false;
            } else {
                return true;
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
        return true;
    }

    //发送广播
    private void sendBrocast() {
        Intent intent = new Intent(ContactsFragment.UPDATE_CONTACTS_OWN_ACTION);
        sendBroadcast(intent);
    }
}

