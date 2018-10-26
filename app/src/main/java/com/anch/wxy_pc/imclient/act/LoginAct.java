package com.anch.wxy_pc.imclient.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.adapter.MoreAccountRecordAdt;
import com.anch.wxy_pc.imclient.bean.AccountBean;
import com.anch.wxy_pc.imclient.custom.NoTouchViewPager;
import com.anch.wxy_pc.imclient.custom.TextMarquee;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.AccountDbUtil;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.IntentUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.anch.wxy_pc.imclient.xmpp.XmppCallBack;
import com.anch.wxy_pc.imclient.xmpp.XmppConstant;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.packet.VCard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxy-pc on 2015/6/11.
 */
@ContentView(R.layout.act_login)
public class LoginAct extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {

    @ViewInject(R.id.login_pager)
    private NoTouchViewPager pager;
    @ViewInject(R.id.notify_tm)
    private TextMarquee notifyTm;

    private InputMethodManager im;
    private PopupWindow mPw;
    private View view;
    private ListView recordAccountLv;
    private List<AccountBean> accounts;

    //登陆控件
    private AutoCompleteTextView userNameTv;
    private EditText userPassTv;
    private TextView moveAccountTv;
    private ImageView loginHeadIv;
    private Button newUserBtn;
    private Button loginBtn;
    private Button skipBtn;

    //注册控件
    private EditText signUserNameTv;
    private EditText signUserPassTv;
    private Button signBtn;

    //创建昵称控件
    private EditText createNickNameTv;
    private Button nextBtn;

    //上传头像控件
    private ImageView upHeadIv;
    private Button createFinishBtn;

    private List<View> views = new ArrayList<>();
    private LayoutInflater mInflater;
    private View loginView;
    private View signView;
    private View createNickView;
    private View upHeadView;
    private int index = 0;
    private XMPPConnection connection;
    private String accountName, accountPassWord, accountNickName;
    // 创建一个以当前时间为名称的文件
    private File tempFile = new File(Environment.getExternalStorageDirectory(), DateUtils.getPhotoFileName());
    private byte[] avatarByte;
    private boolean regState = false;//注册是否成功
    private List<String> textStrs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        mInflater = LayoutInflater.from(this);
        im = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
//        禁止触摸滑动
        pager.setNoScroll(true);
        initTextNotify();
        loginInit();
        signInit();
        createNickInit();
        upHeadInit();
        addViews();
        pager.setAdapter(new LoginPagerAdapter());
        setCurrentItem(index);
//        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new LoginFragment()).commit();
    }

    private void initTextNotify() {
        textStrs.add("(作者：王祥宇)欢迎使用我的IM聊天室,祝你玩的愉快");
        notifyTm.setBgColor("#D02090");
        notifyTm.setFontColor("#CAFF70");
        notifyTm.setFontSize(55f);
        notifyTm.setTexts(textStrs);
    }

    //登陆View初始化
    private void loginInit() {
        loginView = mInflater.inflate(R.layout.fragment_login, null);
        userNameTv = loginView.findViewById(R.id.user_name_tv);
        userPassTv = loginView.findViewById(R.id.user_pass_tv);
        moveAccountTv = loginView.findViewById(R.id.move_account_record_tv);
        loginHeadIv = loginView.findViewById(R.id.login_head_iv);
        newUserBtn = loginView.findViewById(R.id.new_user_btn);
        loginBtn = loginView.findViewById(R.id.login_btn);
        skipBtn = loginView.findViewById(R.id.skip_btn);
        userNameTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        userPassTv.setText(SharePrefUtils.getString(Constanst.PASSWORD, ""));
        newUserBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        skipBtn.setOnClickListener(this);
        moveAccountTv.setOnClickListener(this);
        userNameTv.addTextChangedListener(this);
        loginHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.left_head), 200));
        addAutoTextAdapter();

    }

    /**
     * AutoCompleteTextView方法
     */
    private void addAutoTextAdapter() {
        List<AccountBean> accounts = AccountDbUtil.select();
        if (accounts != null && accounts.size() > 0) {
            String[] accountInfo = new String[accounts.size()];
            for (int i = 0; i < accounts.size(); i++) {
                accountInfo[i] = accounts.get(i).getAccountName();
            }
            userNameTv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, accountInfo));
        }
    }

    //注册View初始化
    private void signInit() {
        signView = mInflater.inflate(R.layout.fragment_sign, null);
        signUserNameTv = signView.findViewById(R.id.sign_user_name_tv);
        signUserPassTv = signView.findViewById(R.id.sign_user_pass_tv);
        signBtn = signView.findViewById(R.id.sign_btn);
        signBtn.setOnClickListener(this);
    }

    //创建昵称View初始化
    private void createNickInit() {
        createNickView = mInflater.inflate(R.layout.fragment_nick_name, null);
        createNickNameTv = createNickView.findViewById(R.id.create_nick_name_tv);
        nextBtn = createNickView.findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
    }

    //上传头像View初始化
    private void upHeadInit() {
        upHeadView = mInflater.inflate(R.layout.fragment_head_up, null);
        upHeadIv = upHeadView.findViewById(R.id.up_head_iv);
        createFinishBtn = upHeadView.findViewById(R.id.create_finish_btn);
        upHeadIv.setOnClickListener(this);
        createFinishBtn.setOnClickListener(this);
    }

    private void addViews() {
        views.add(loginView);
        views.add(signView);
        views.add(createNickView);
        views.add(upHeadView);
    }

    private View createView() {
        if (view == null) {
            view = mInflater.inflate(R.layout.record_account_pw, null);
            recordAccountLv = view.findViewById(R.id.record_account_lv);
            accounts = AccountDbUtil.select();
            recordAccountLv.setAdapter(new MoreAccountRecordAdt(this, accounts));
            recordAccountLv.setOnItemClickListener(this);
        }
        if (accounts == null || accounts.size() == 0)
            return null;
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (accounts != null && accounts.size() > 0)
            userNameTv.setText(accounts.get(position).getAccountName());
        mPw.dismiss();
    }

    /**
     * PopupWindow 弹出记录账户
     *
     * @param view
     */
    public void showPop(View view) {
        if (mPw == null) {
            mPw = new PopupWindow(
                    view,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPw.setFocusable(true);
            mPw.setTouchable(true);
            mPw.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.record_account_bg));// 点击空白时popupwindow关闭,设置背景图片，不能在布局中设置，要通过代码来设置
            mPw.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功
            mPw.setAnimationStyle(R.style.AnimPush);// 设置窗口显示的动画效果
        }
        mPw.update();
        mPw.showAsDropDown(userNameTv);
        mPw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        AccountBean accountBean = AccountDbUtil.selectAccount(userNameTv.getText().toString());
        if (accountBean != null)
            userPassTv.setText(accountBean.getUserPass());
        else userPassTv.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_user_btn://登陆
                setCurrentItem(1);
                break;
            case R.id.login_btn://登陆
                if (userNameTv.length() == 0) {
                    ToastTools.frameToast(this, "用户名不能为空", R.drawable.custom_toast);
                    return;
                }
                if (userPassTv.length() == 0) {
                    ToastTools.frameToast(this, "密码不能为空", R.drawable.custom_toast);
                    return;
                }
                //登陆
                login(userNameTv.getText().toString().trim(), userPassTv.getText().toString().trim());
                break;

            case R.id.sign_btn://注册创建
                if (signUserNameTv.length() == 0) {
                    ToastTools.frameToast(this, "注册用户名不能为空", R.drawable.custom_toast);
                    return;
                }
                if (signUserPassTv.length() == 0) {
                    ToastTools.frameToast(this, "注册密码不能为空", R.drawable.custom_toast);
                    return;
                }
                //注册
                sign(signUserNameTv.getText().toString().trim(), signUserPassTv.getText().toString().trim());
                break;
            case R.id.skip_btn://跳过
                IntentUtils.skip(LoginAct.this, HomeAct.class, true);
                break;
            case R.id.next_btn://创建下一步
                accountNickName = createNickNameTv.getText().toString().trim();
                if (createNickNameTv.length() == 0) {
                    ToastTools.frameToast(this, "创建昵称不能为空", R.drawable.custom_toast);
                    return;
                }
                setCurrentItem(3);
                createNickNameTv.setText("");
                break;

            case R.id.up_head_iv://上传头像点击
                DialogUtil.simpleAlertDialog(LoginAct.this, "上传头像", "上传头像", "拍照", "相册", R.mipmap.head_, true, false, new DialogUtil.OnClickCallBack() {
                    @Override
                    public void leftOnclick() {
                        IntentUtils.skipCamera(LoginAct.this, Uri.fromFile(tempFile), Constanst.TASK_SKIP_CAMERA);
                    }

                    @Override
                    public void rightOnclick() {
                        IntentUtils.skipPhoto(LoginAct.this, Constanst.TASK_SKIP_PHOTO);
                    }
                });
                break;

            case R.id.create_finish_btn://完成注册
                //开始注册创建
                upHead();
                break;
            case R.id.move_account_record_tv:
                if (im.isActive()) {
                    im.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                View view = createView();
                if (view != null)
                    showPop(view);
                break;
        }
    }

    private void setCurrentItem(int pos) {
        index = pos;
        pager.setCurrentItem(index, true);
    }

    //登陆方法
    private void login(final String userName, final String userPass) {
        XmppManager.xmppManager.connectOpenfireSer(LoginAct.this, R.id.login_notify_rl, userName, userPass, new XmppCallBack(LoginAct.this) {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess() {

                SharePrefUtils.saveString(Constanst.ACCOUNT, userName);
                SharePrefUtils.saveString(Constanst.PASSWORD, userPass);

                AccountBean accountBean = new AccountBean();
                accountBean.setAccountName(userName);
                accountBean.setUserPass(userPass);
                accountBean.setTime(DateUtils.getCurrDateTime(DateUtils.getCurrTime()));
                AccountDbUtil.saveAccount(accountBean);

                IntentUtils.skip(LoginAct.this, HomeAct.class, true);
                super.onSuccess();
            }

            @Override
            public void onFail(int result, int id) {
                super.onFail(result, id);
            }
        });
    }

    //注册用户名和密码
    private void sign(String newUserName, String newUserPass) {
        new AsyncTask<String, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(LoginAct.this, mInflater.inflate(R.layout.dialog_pro, null), "注册中", R.style.ProgressDialog, false);
            }

            @Override
            protected Integer doInBackground(String... params) {
                connection = XmppManager.xmppManager.createXmppConnect();
                try {
                    connection.connect();
                    Registration registration = new Registration();
                    registration.setType(IQ.Type.SET);
                    registration.setTo(connection.getServiceName());
                    registration.addAttribute("username", params[0]);
                    registration.addAttribute("password", params[1]);
                    accountName = params[0];
                    accountPassWord = params[1];
//                registration.setUsername(params[0]);// 注意这里createAccount注册时，参数是username，不是jid，是“@”前面的部分。
//                registration.setPassword(params[1]);
                    registration.addAttribute("android", "geolo_createUser_android");// 这边addAttribute不能为空，否则出错。所以做个标志是android手机创建的吧！！！！！
                    PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));
                    PacketCollector collector = connection.createPacketCollector(filter);
                    connection.sendPacket(registration);// 向服务器端，发送注册Packet包，注意其中Registration是Packet的子类
                    IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
                    collector.cancel();//停止请求result
                    if (result == null) return XmppConstant.XMPP_REG_NO_RESPONSE;
                    else if (result.getType() == IQ.Type.RESULT)
                        return XmppConstant.XMPP_REG_SUCCESS;
                    else if (result.getError().toString().equalsIgnoreCase("conflict(409)"))
                        return XmppConstant.XMPP_REG_ACCOUNT_EXIST;
                    else return XmppConstant.XMPP_REG_FAILD;
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                return XmppConstant.XMPP_REG_FAILD;
            }

            @Override
            protected void onPostExecute(Integer result) {
                DialogUtil.cancleDialog();
                switch (result) {
                    case XmppConstant.XMPP_REG_NO_RESPONSE:
                        regState = false;
                        NiftyNotificationView.build(LoginAct.this, "服务器可能发呆了吧", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                        break;
                    case XmppConstant.XMPP_REG_SUCCESS:
                        regState = true;
                        setCurrentItem(2);
                        signUserNameTv.setText("");
                        signUserPassTv.setText("");
                        try {
                            connection.login(accountName, accountPassWord, XmppConstant.XMPP_RESOURCE);
                        } catch (XMPPException e) {
                            e.printStackTrace();
                        }
                        NiftyNotificationView.build(LoginAct.this, "真幸运,竟然注册成功了", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                        break;
                    case XmppConstant.XMPP_REG_ACCOUNT_EXIST:
                        regState = false;
                        NiftyNotificationView.build(LoginAct.this, "账户竟然已经存在了", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                        break;
                    case XmppConstant.XMPP_REG_FAILD:
                        regState = false;
                        NiftyNotificationView.build(LoginAct.this, "注册失败了吧", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                        break;
                }
            }
        }.execute(newUserName, newUserPass);
    }

    /**
     * 上传头像
     */
    private void upHead() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected void onPreExecute() {
                DialogUtil.showDialog(LoginAct.this, mInflater.inflate(R.layout.dialog_pro, null), "正在上传头像", R.style.ProgressDialog, false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    VCard vCard = new VCard();
                    vCard.load(connection);
                    vCard.setNickName(accountNickName);
                    String encodedImage = StringUtils.encodeBase64(avatarByte);
                    vCard.setAvatar(avatarByte, encodedImage);
                    vCard.setField("PHOTO", "<TYPE>image/jpg</TYPE><BINVAL>" + encodedImage
                            + "</BINVAL>", true);
                    vCard.save(connection);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                DialogUtil.cancleDialog();

                if (aBoolean) {
                    NiftyNotificationView.build(LoginAct.this, "注册成功了呀！可用该账号登陆了！", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                    setCurrentItem(0);
                    XmppManager.xmppManager.exitLogin();
                } else {
                    NiftyNotificationView.build(LoginAct.this, "注册失败了呀！", Effects.slideIn, R.id.login_notify_rl).setIcon(R.mipmap.notify_bg).show();
                }
            }
        }.execute();
    }

    //ViewPager适配器
    private class LoginPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }
    }

    @Override
    public void onBackPressed() {
        switch (index) {
            case 0:
                finish();
                break;
            default:
                DialogUtil.simpleAlertDialog(LoginAct.this, "您想取消注册新用户了吗？", "放弃注册", "放弃", "继续", R.mipmap.head_, true, true, new DialogUtil.OnClickCallBack() {
                    @Override
                    public void leftOnclick() {
                        setCurrentItem(0);
                        upHeadIv.setImageResource(R.mipmap.ic_launcher);
                        if (connection != null && regState) {
                            try {//删除账户
                                connection.getAccountManager().deleteAccount();
                                regState = false;
                                XmppManager.xmppManager.exitLogin();
                            } catch (XMPPException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void rightOnclick() {

                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constanst.TASK_SKIP_CAMERA://跳转相机裁剪图片
                IntentUtils.cropImageUri(LoginAct.this, Uri.fromFile(tempFile), Constanst.TASK_CUT_PHOTO);
                break;
            case Constanst.TASK_CUT_PHOTO://裁剪图片后设置图片
                if (data != null)
                    setPicToView(data);
                break;
            case Constanst.TASK_SKIP_PHOTO://跳转相册裁剪图片
                if (data != null)
                    IntentUtils.cropImageUri(LoginAct.this, data.getData(), Constanst.TASK_CUT_PHOTO);
                break;
        }
    }

    //将进行剪裁后的图片显示到UI界面上
    private void setPicToView(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            upHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(photo, 130));
            avatarByte = BitmapUtils.Bitmap2Bytes(photo);
        }
    }
}
