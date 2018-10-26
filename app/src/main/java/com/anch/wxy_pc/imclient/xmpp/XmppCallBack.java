package com.anch.wxy_pc.imclient.xmpp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;

/**
 * Created by wxy-pc on 2015/6/12.
 */
public class XmppCallBack implements XmppInterface {

    private Activity mContext;

    public XmppCallBack(Activity context) {
        this.mContext = context;
    }

    @Override
    public void onStart() {
        DialogUtil.showDialog(mContext, LayoutInflater.from(mContext).inflate(R.layout.dialog_pro, null), "登陆中", R.style.ProgressDialog, false);
    }

    @Override
    public void onSuccess() {
        DialogUtil.cancleDialog();
        ToastTools.frameToast(mContext, "真幸运--登陆成功了", R.drawable.custom_toast);
    }

    @Override
    public void onFail(int result, int id) {
        DialogUtil.cancleDialog();
        switch (result) {
            case XmppConstant.XMPP_CON_SER_FAILD:
                NiftyNotificationView.build(mContext, "连接失败,请检查网络", Effects.slideIn, id).setIcon(R.mipmap.notify_bg).show();
                break;
            case XmppConstant.XMPP_CON_SER_TIMEOUT:
                NiftyNotificationView.build(mContext, "连接超时", Effects.slideIn, id).setIcon(R.mipmap.notify_bg).show();
                break;
            case XmppConstant.XMPP_LOGIN_FAILD:
                NiftyNotificationView.build(mContext, "登陆失败", Effects.slideIn, id).setIcon(R.mipmap.notify_bg).show();
                break;
            case XmppConstant.XMPP_LOGIN_TMOUT:
                NiftyNotificationView.build(mContext, "登陆超时,请检查网络", Effects.slideIn, id).setIcon(R.mipmap.notify_bg).show();
                break;
            case XmppConstant.XMPP_CON_SER_EXIT:
                ToastTools.frameToast(mContext, "恭喜你--成功退出登录", R.drawable.custom_toast);
                break;
        }
    }
}
