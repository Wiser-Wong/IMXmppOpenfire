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
import com.anch.wxy_pc.imclient.act.ChatAct;
import com.anch.wxy_pc.imclient.adapter.ConversationAdapter;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.notification.AddFriendNotification;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ConversationDbUtil;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 会话Frg
 * Created by wxy-pc on 2015/6/12.
 */
public class ConversationFragment extends Fragment implements AdapterView.OnItemClickListener{
    public static final String CHANGE_MES_RECEIVER = "com.anch.wxy_pc.imclient.receiver.CHANGE_MES_RECEIVER";
    public static final int UPDATE_DATA = 201021;
    @ViewInject(R.id.conversation_lv)
    private static ListView conversationLv;
    private static ConversationAdapter conversationAdapter;
    private static List<ConversationBean> conversationBeans;
    private static Context context;
    public static ConversationHandler handler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Constanst.CURRENT_PAGE = Constanst.CURRENT_PAGE_VALUE;
        handler = new ConversationHandler(activity);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation, container, false);
        ViewUtils.inject(this, view);
        context = getActivity();
        setAdapter();
        setListener();
        return view;
    }

    private void setAdapter() {
        conversationBeans = ConversationDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        conversationAdapter = new ConversationAdapter(context, conversationBeans);
        conversationLv.setAdapter(conversationAdapter);
    }

    private static void notifyDataSetChanged() {
        conversationBeans = ConversationDbUtil.select(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        conversationAdapter.setNotifyChangeData(conversationBeans);
    }

    private void setListener() {
        conversationLv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (Constanst.NOTI_FRIEND_ID.equals(conversationBeans.get(position).getWho_id())) {
            Constanst.NOTI_FRIEND_ID = "";
            AddFriendNotification.cancleNotification();
        }
        Intent intent = new Intent(getActivity(), ChatAct.class);
        Bundle bundle = new Bundle();
        bundle.putString("friendId", conversationBeans.get(position).getWho_id());
        bundle.putString("friendName", conversationBeans.get(position).getWho_name());
        bundle.putString("currentUser", SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
//        getActivity().startActivityForResult(intent, Constanst.RESULT_CODE);
    }

    public static class ChangeMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CHANGE_MES_RECEIVER)) {
                notifyDataSetChanged();
            }
        }
    }

    public class ConversationHandler extends Handler {
        WeakReference<Activity> reference;

        ConversationHandler(Activity activity) {
            reference = new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Activity activity = reference.get();
            if (activity != null) {
                switch (msg.what) {
                    case UPDATE_DATA:
                        notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    /**
     * 跳到聊天，再返回到会话页更新UI
     *
     * @param who_id
     */
    public void updateMes(String who_id) {
        ConversationDbUtil.clearMesCount(who_id);//清除未读条数
        notifyDataSetChanged();//更新适配器
    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case Constanst.RESULT_CODE:
//                if (data != null)
//                    ConversationDbUtil.clearMesCount(data.getStringExtra("WHO_ID"));
//                notifyDataSetChanged();
//                break;
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        Constanst.CURRENT_PAGE = 0;
    }
}
