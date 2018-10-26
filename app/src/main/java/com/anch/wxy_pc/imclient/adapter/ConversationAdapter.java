package com.anch.wxy_pc.imclient.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.bean.ConversationBean;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/16.
 */
public class ConversationAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<ConversationBean> conversationBeans;
    public List<Boolean> states = new ArrayList<>();

    public ConversationAdapter(Context context, List<ConversationBean> conversationBeans) {
        this.mContext = context;
        this.conversationBeans = conversationBeans;
        inflater = LayoutInflater.from(mContext);
        addState(conversationBeans);
    }

    private void addState(List<ConversationBean> beans) {
        if (states.size() > 0) states.clear();
        if (beans != null && beans.size() > 0) {
            for (int i = 0; i < beans.size(); i++) {
                if (beans.get(i).getCount() > 0) {
                    states.add(true);
                } else {
                    states.add(false);
                }
            }
        }
    }

    public void setNotifyChangeData(List<ConversationBean> newConversationBean) {
        if (conversationBeans == null)
            conversationBeans = new ArrayList<>();
        conversationBeans.clear();
        this.conversationBeans = newConversationBean;
        addState(conversationBeans);
        notifyDataSetChanged();
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
        ConversationHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_conversation_item, parent, false);
            holder = new ConversationHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ConversationHolder) convertView.getTag();
        }

        if (conversationBeans != null) {
            ConversationBean conversationBean = conversationBeans.get(position);
            holder.messageName.setText(UtilTools.splitStr(conversationBean.getWho_name(), 0));
            holder.messageBody.setText(conversationBean.getBody());
            holder.messageTime.setText(conversationBean.getTime());
            holder.messageAvatarIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.right_head), 130));
            if (states.size() > 0) {
                if (states.get(position)) {
                    holder.messageCount.setVisibility(View.VISIBLE);
                    holder.messageCount.setText(conversationBean.getCount() + "");
                } else {
                    holder.messageCount.setVisibility(View.INVISIBLE);
                }
            }
        }
        return convertView;
    }

    private class ConversationHolder {
        @ViewInject(R.id.message_name_tv)
        TextView messageName;
        @ViewInject(R.id.message_body_tv)
        TextView messageBody;
        @ViewInject(R.id.message_time_tv)
        TextView messageTime;
        @ViewInject(R.id.message_count_tv)
        TextView messageCount;
        @ViewInject(R.id.message_avatar_iv)
        ImageView messageAvatarIv;
    }
}
