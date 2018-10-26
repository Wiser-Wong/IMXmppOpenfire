package com.anch.wxy_pc.imclient.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.act.PersonalCenterAct;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.IntentUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by wxy-pc on 2015/6/16.
 */
public class ChatAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private List<ConversationBean> conversationBeans;

    public ChatAdapter(Context context, List<ConversationBean> conversationBeanList) {
        this.mContext = context;
        this.conversationBeans = conversationBeanList;
        inflater = LayoutInflater.from(mContext);
    }

    public interface ViewType {
        int COME_MSG = 0;
        int TO_MSG = 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int getItemViewType(int position) {
        ConversationBean conversationBean = conversationBeans.get(position);
        if (conversationBean.getSession_id().equals(conversationBean.getCurrent_user())) {
            return ViewType.TO_MSG;
        } else {
            return ViewType.COME_MSG;
        }
    }

    @Override
    public int getCount() {
        if (conversationBeans == null)
            return 0;
        return conversationBeans.size();
    }

    @Override
    public Object getItem(int position) {
        if (conversationBeans == null)
            return null;
        return conversationBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ConversationBean conversationBean = conversationBeans.get(position);
        final ChatHolder holder;
        if (convertView == null) {
            switch (getItemViewType(position)) {
                case ViewType.COME_MSG:
                    convertView = inflater.inflate(R.layout.adt_chat_item_left, parent, false);
                    break;
                case ViewType.TO_MSG:
                    convertView = inflater.inflate(R.layout.adt_chat_item_right, parent, false);
                    break;
            }
            holder = new ChatHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChatHolder) convertView.getTag();
        }

        if (conversationBeans != null) {
            holder.sendTimeTv.setText(conversationBean.getDate());
            holder.chatContentTv.setText(conversationBean.getBody());
            switch (getItemViewType(position)) {
                case ViewType.COME_MSG:
                    holder.chatUserNameTv.setText(UtilTools.splitStr(conversationBean.getSession_id(), 0));
                    holder.userHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.left_head), 130));
                    break;
                case ViewType.TO_MSG:
                    holder.chatUserNameTv.setText(conversationBean.getSession_id());
                    holder.userHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.right_head), 130));
                    break;
            }
            holder.userHeadIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtils.skip(((Activity) mContext), PersonalCenterAct.class, "WHO_USER", holder.chatUserNameTv.getText().toString(), false);
                }
            });
        }
        return convertView;
    }

    private class ChatHolder {
        @ViewInject(R.id.chat_content_tv)
        TextView chatContentTv;
        @ViewInject(R.id.send_time_tv)
        TextView sendTimeTv;
        @ViewInject(R.id.user_head_iv)
        ImageView userHeadIv;
        @ViewInject(R.id.chat_username_tv)
        TextView chatUserNameTv;
    }
}
