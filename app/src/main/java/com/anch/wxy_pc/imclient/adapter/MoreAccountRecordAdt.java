package com.anch.wxy_pc.imclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.bean.AccountBean;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by wxy-pc on 2015/7/7.
 */
public class MoreAccountRecordAdt extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AccountBean> accounts;

    public MoreAccountRecordAdt(Context context, List<AccountBean> accounts) {
        this.mContext = context;
        this.accounts = accounts;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (accounts == null)
            return 0;
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        if (accounts == null)
            return null;
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.record_account_pw_item, parent, false);
            holder = new Holder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (accounts != null) {
            holder.moreAcccountRecordTv.setText(accounts.get(position).getAccountName());
        }
        return convertView;
    }

    private class Holder {
        @ViewInject(R.id.account_tv)
        TextView moreAcccountRecordTv;
    }
}
