package com.anch.wxy_pc.imclient.act;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.base.BaseActivity;
import com.anch.wxy_pc.imclient.custom.CustomSliddingMenu;
import com.anch.wxy_pc.imclient.custom.CustomVideo;
import com.anch.wxy_pc.imclient.custom.TitanicTextView;
import com.anch.wxy_pc.imclient.fragment.ContactsFragment;
import com.anch.wxy_pc.imclient.fragment.ConversationFragment;
import com.anch.wxy_pc.imclient.service.XmppManager;
import com.anch.wxy_pc.imclient.utils.AnimTools;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.ContactsDbUtil;
import com.anch.wxy_pc.imclient.utils.ConversationDbUtil;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.DialogUtil;
import com.anch.wxy_pc.imclient.utils.IntentUtils;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.anch.wxy_pc.imclient.utils.Titanic;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 * Created by wxy-pc on 2015/6/12.
 */
@ContentView(R.layout.act_home)
public class HomeAct extends BaseActivity implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnCompletionListener {

    //    @ViewInject(R.id.pager)
//    private ViewPager pager;
    @ViewInject(R.id.right_content_Rl)
    private RelativeLayout rightContentLl;
    @ViewInject(R.id.mes_fl)
    private FrameLayout mesFl;
    @ViewInject(R.id.friend_fl)
    private FrameLayout friendFl;
    @ViewInject(R.id.conversation_ll)
    private LinearLayout conversationLl;
    @ViewInject(R.id.contacts_ll)
    private LinearLayout contactsLl;
    @ViewInject(R.id.title_name_tv)
    private TitanicTextView currentUserTv;
    @ViewInject(R.id.conversationTv)
    private TextView conversationTv;//会话
    @ViewInject(R.id.contactsTv)
    private TextView contactsTv;//联系人
    @ViewInject(R.id.conversation_iv)
    private ImageView conversationIv;//会话Tab
    @ViewInject(R.id.contacts_iv)
    private ImageView contactsIv;//联系人Tab
    @ViewInject(R.id.right_btn)
    private ImageButton addFriendIb;
    @ViewInject(R.id.toggle_iv)
    private ImageView toggleIV;
    @ViewInject(R.id.menu)
    private CustomSliddingMenu menu;
    @ViewInject(R.id.left_user_head_iv)
    private ImageView leftUserHeadIv;
    @ViewInject(R.id.left_user_id_tv)
    private TitanicTextView leftUserIdTv;
    @ViewInject(R.id.left_add_contacts_ll)
    private LinearLayout leftAddContactsLl;
    @ViewInject(R.id.left_play_music_ll)
    private LinearLayout leftPlayMusicLl;
    @ViewInject(R.id.left_clear_all_mes_ll)
    private LinearLayout leftClearAllMesLl;
    @ViewInject(R.id.play_media_ll)
    private LinearLayout playMediaLl;
    @ViewInject(R.id.wait_ll)
    private LinearLayout waitLl;
    @ViewInject(R.id.add_friend_tv)
    private TextView leftAddContactsTv;
    @ViewInject(R.id.play_music_tv)
    private TextView leftPlayMusicTv;
    @ViewInject(R.id.play_media_tv)
    private TextView leftPlayMediaTv;
    @ViewInject(R.id.clear_mes_tv)
    private TextView leftClearMesTv;
    @ViewInject(R.id.wait_tv)
    private TextView waitTv;
    @ViewInject(R.id.player)
    private CustomVideo mCustomVideo;// 视频播放器
    @ViewInject(R.id.logout_rl)
    private RelativeLayout logoutRl;
    @ViewInject(R.id.exit_rl)
    private RelativeLayout exitRl;
    @ViewInject(R.id.logout_tv)
    private TextView logoutTv;
    @ViewInject(R.id.exit_tv)
    private TextView exitTv;


    private List<FrameLayout> list = new ArrayList<>();
    private List<TextView> tabTextList = new ArrayList<>();
    private List<ImageView> tabImgList = new ArrayList<>();
    private List<RelativeLayout> views = new ArrayList<>();

    private FragmentManager manager;
    private ConversationFragment conversationFragment;
    private ContactsFragment contactsFragment;
    private PopupWindow mPopupWindow;
    public static HomeAct homeAct;
    private MediaPlayer player;
    private LayoutInflater mInflater;
    private long exitTime;
//    private View mediaView;
//    private WindowMediaView wmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        homeAct = this;
        manager = getSupportFragmentManager();
        mInflater = LayoutInflater.from(this);
//        mediaView = mInflater.inflate(R.layout.media_view, null);
//        wmView = new WindowMediaView();
        setTypeFace();
        addData();
        setListener();
        leftMenu();
        new Titanic().start(leftUserIdTv);
        new Titanic().start(currentUserTv);
    }

    private void setTypeFace() {
        currentUserTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawencaiyun.ttf"));
        conversationTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        contactsTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        logoutTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawencaiyun.ttf"));
        exitTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawencaiyun.ttf"));
        leftUserIdTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/youyuan.ttf"));
        leftAddContactsTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        leftPlayMusicTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        leftPlayMediaTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        leftClearMesTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
        waitTv.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/huawenhangkai.ttf"));
    }

    private void addData() {
        conversationFragment = new ConversationFragment();
        contactsFragment = new ContactsFragment();
        manager.beginTransaction().replace(R.id.mes_fl, conversationFragment).commitAllowingStateLoss();
        manager.beginTransaction().replace(R.id.friend_fl, contactsFragment).commitAllowingStateLoss();
        list.add(mesFl);
        list.add(friendFl);

        tabTextList.add(conversationTv);
        tabTextList.add(contactsTv);

        tabImgList.add(conversationIv);
        tabImgList.add(contactsIv);

        views.add(logoutRl);
        views.add(exitRl);

        currentUserTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
        currentUserTv.setTextColor(Color.YELLOW);

        setFrameLayout(0);

        toggleIV.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.head_), 120));
        AnimTools.startRotateAnim(toggleIV, this);
        AnimTools.startRotateAnim(conversationIv, this);

        mCustomVideo.currentAct(HomeAct.this);// 传当前Activity给播放器
    }

    private void leftMenu() {
        leftUserHeadIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.head_), 130));
        leftUserIdTv.setText(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
    }

    private void setListener() {
        addFriendIb.setOnClickListener(this);
        toggleIV.setOnClickListener(this);
        conversationLl.setOnClickListener(this);
        contactsLl.setOnClickListener(this);
        leftAddContactsLl.setOnClickListener(this);
        leftPlayMusicLl.setOnClickListener(this);
        leftClearAllMesLl.setOnClickListener(this);
        playMediaLl.setOnClickListener(this);
        waitLl.setOnClickListener(this);
        rightContentLl.setOnTouchListener(this);
        logoutRl.setOnClickListener(this);
        exitRl.setOnClickListener(this);
        leftUserHeadIv.setOnClickListener(this);
    }

    private void setBar(int index) {
        if (index == 0) {
            contactsLl.setEnabled(true);
            conversationLl.setEnabled(false);
            tabImgList.get(0).setImageResource(R.mipmap.mes_ps);
            tabImgList.get(1).setImageResource(R.mipmap.contacts_bar_df);
            AnimTools.stopRotateAnim(contactsIv);
            AnimTools.startRotateAnim(conversationIv, this);
        } else {
            contactsLl.setEnabled(false);
            conversationLl.setEnabled(true);
            tabImgList.get(0).setImageResource(R.mipmap.mes_df);
            tabImgList.get(1).setImageResource(R.mipmap.contacts_bar_ps);
            AnimTools.stopRotateAnim(conversationIv);
            AnimTools.startRotateAnim(contactsIv, this);
        }
        for (int i = 0; i < tabTextList.size(); i++) {
            if (i == index) {
                tabTextList.get(index).setTextColor(Color.BLUE);
            } else {
                tabTextList.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void setFrameLayout(int index) {
        for (int i = 0; i < list.size(); i++) {
            if (i == index)
                list.get(index).setVisibility(View.VISIBLE);
            else
                list.get(i).setVisibility(View.GONE);
        }
    }

    private void setTab(int index) {
        setFrameLayout(index);
        setBar(index);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (menu.mIsOpen) {
                menu.toggleMenu();
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从聊天页面返回到消息界面更新UI
        if (Constanst.UPDATE_NEW_MES.equals("UPDATE_NEW_MES") && conversationFragment != null) {
            Constanst.UPDATE_NEW_MES = "";
            conversationFragment.updateMes(Constanst.UPDATE_WHO_ID);
            Constanst.UPDATE_WHO_ID = "";
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        conversationFragment.onActivityResult(requestCode, resultCode, data);
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_user_head_iv:
                IntentUtils.skip(HomeAct.this, PersonalCenterAct.class, "WHO_USER", SharePrefUtils.getString(Constanst.ACCOUNT, ""), false);
                break;
            case R.id.logout_rl://注销账户
                IntentUtils.skip(HomeAct.this, LoginAct.class, true);
                XmppManager.xmppManager.exitLogin();//退出登录
                break;
            case R.id.exit_rl://退出账户
                finish();
                break;
            case R.id.toggle_iv:
                menu.toggleMenu();
                break;
            case R.id.conversation_ll:
                setTab(0);
                break;
            case R.id.contacts_ll:
                setTab(1);
                break;
            case R.id.right_btn://添加好友
                IntentUtils.skip(HomeAct.this, AddFriendAct.class, Constanst.SKIP_ADD_FRIENT_PASS_USER, SharePrefUtils.getString(Constanst.ACCOUNT, ""), false);
                break;
            case R.id.logout_btn://注销账户
                IntentUtils.skip(HomeAct.this, LoginAct.class, true);
                XmppManager.xmppManager.exitLogin();//退出登录
                break;
            case R.id.exit_btn://退出账户
                finish();
                break;
            case R.id.left_add_contacts_ll://添加好友
                IntentUtils.skip(HomeAct.this, AddFriendAct.class, Constanst.SKIP_ADD_FRIENT_PASS_USER, SharePrefUtils.getString(Constanst.ACCOUNT, ""), false);
                break;
            case R.id.left_play_music_ll://播放音乐
//                if (wmView.isPlaying()) {
//                    NiftyNotificationView.build(this, "请先关掉视频,再次尝试播放音乐", Effects.slideIn, R.id.notify_rl)
//                            .setIcon(R.mipmap.notify_bg)
//                            .show();
//                } else {
                if (!createMediaPlayer()) {
                    AnimTools.stopRotateAnim(leftUserHeadIv);
                    NiftyNotificationView.build(this, "已经停止播放", Effects.slideIn, R.id.notify_rl)
                            .setIcon(R.mipmap.notify_bg)
                            .show();
                    ((TextView) leftPlayMusicLl.getChildAt(1)).setText("播放音乐");
                    stopPlayMusic();
                } else {
                    AnimTools.startRotateAnim(leftUserHeadIv, this);
                    NiftyNotificationView.build(this, "开始播放张栋梁--当你孤单你会想起谁。", Effects.thumbSlider, R.id.notify_rl)
                            .setIcon(R.mipmap.notify_bg)
                            .show();
                    ((TextView) leftPlayMusicLl.getChildAt(1)).setText("退出播放");
                }
//                }
                break;
            case R.id.left_clear_all_mes_ll://清空聊天记录
                DialogUtil.simpleAlertDialog(HomeAct.this, "是否清空所有聊天信息?", "是否清空所有聊天信息", "取消", "确定", R.mipmap.head_, true, true, new DialogUtil.OnClickCallBack() {
                            @Override
                            public void leftOnclick() {

                            }

                            @Override
                            public void rightOnclick() {
                                new AsyncTask<Void, Void, Boolean>() {
                                    @Override
                                    protected void onPreExecute() {
                                        DialogUtil.showDialog(HomeAct.this, mInflater.inflate(R.layout.dialog_pro, null), "清空记录", R.style.ProgressDialog, false);
                                    }

                                    @Override
                                    protected Boolean doInBackground(Void... params) {
                                        try {
                                            return ConversationDbUtil.clearAllMes(SharePrefUtils.getString(Constanst.ACCOUNT, ""));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        return false;
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean aBoolean) {
                                        DialogUtil.cancleDialog();
                                        if (aBoolean) {
                                            NiftyNotificationView.build(HomeAct.this, "清除成功", Effects.thumbSlider, R.id.notify_rl)
                                                    .setIcon(R.mipmap.notify_bg)
                                                    .show();
                                            sendClearMesBrocast();
                                        } else {
                                            NiftyNotificationView.build(HomeAct.this, "清除失败", Effects.thumbSlider, R.id.notify_rl)
                                                    .setIcon(R.mipmap.notify_bg)
                                                    .show();
                                        }
                                    }
                                }.execute();
                            }
                        }
                );
                break;
            case R.id.play_media_ll://播放视频
                if (player != null) {
                    NiftyNotificationView.build(this, "请先关闭音乐,再尝试播放视频", Effects.slideIn, R.id.notify_rl)
                            .setIcon(R.mipmap.notify_bg).show();
                } else {
                    if (mCustomVideo.getVisibility() == View.GONE) {
                        mCustomVideo.setVisibility(View.VISIBLE);
                        mCustomVideo.currentUrl("video.mp4");
                    } else {
                        mCustomVideo.stop();
                        mCustomVideo.setVisibility(View.GONE);
                    }
//                    if (wmView.createWindow()) {
//                        wmView.addWindowView(mediaView);
//                        NiftyNotificationView.build(this, "开始播放视频", Effects.slideIn, R.id.notify_rl)
//                                .setIcon(R.mipmap.notify_bg).show();
//                        ((TextView) playMediaLl.getChildAt(1)).setText("关闭视频");
//                    } else {
//                        wmView.removeWindowView(mediaView);
//                        NiftyNotificationView.build(this, "结束播放视频", Effects.slideIn, R.id.notify_rl)
//                                .setIcon(R.mipmap.notify_bg).show();
//                        ((TextView) playMediaLl.getChildAt(1)).setText("播放视频");
//                    }
                }
                break;
            case R.id.wait_ll://有待开发
                NiftyNotificationView.build(this, "有待开发", Effects.thumbSlider, R.id.notify_rl)
                        .setIcon(R.mipmap.notify_bg).show();
                break;
        }
    }

    /**
     * 发送广播
     */

    public void sendClearMesBrocast() {
        Intent intent = new Intent(ConversationFragment.CHANGE_MES_RECEIVER);
        IMApplication.getInstance().sendBroadcast(intent);
    }

    private boolean createMediaPlayer() {
        if (player == null) {
            player = new MediaPlayer().create(this, R.raw.music);
            player.setOnCompletionListener(this);
            player.start();
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmppManager.xmppManager.exitLogin();//退出登录
        ContactsDbUtil.deleteAll();
        stopPlayMusic();
    }

    @Override
    public void onBackPressed() {
        if (mCustomVideo.getVisibility() == View.VISIBLE && this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (DateUtils.getCurrTime() - exitTime > 2000) {
                ToastTools.frameToast(HomeAct.this, "再按一次退出全屏", R.drawable.custom_toast);
                exitTime = DateUtils.getCurrTime();
                return;
            }
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            exitTime = 0;
        } else {
            if (mCustomVideo.getVisibility() == View.VISIBLE && this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (DateUtils.getCurrTime() - exitTime > 2000) {
                    ToastTools.frameToast(HomeAct.this, "再按一次退出播放", R.drawable.custom_toast);
                    exitTime = DateUtils.getCurrTime();
                    return;
                }
                mCustomVideo.destoryPlayer();
                mCustomVideo.setVisibility(View.GONE);
                NiftyNotificationView.build(this, "关闭视频播放", Effects.slideIn, R.id.notify_rl)
                        .setIcon(R.mipmap.notify_bg).show();
            } else {
                if (menu.mIsOpen) {
                    menu.toggleMenu();
                } else {
//                    if (view == null) {
//                        view = LayoutInflater.from(this).inflate(R.layout.act_logout, null);
//                        view.findViewById(R.id.logout_btn).setOnClickListener(this);
//                        view.findViewById(R.id.exit_btn).setOnClickListener(this);
//                    }
//                    showPop(view);
                    if (logoutRl.getVisibility() == View.VISIBLE) {
                        hideView();
                    } else {
                        showView();
                    }
                }
            }
        }
    }

    /**
     * 隐藏注销退出布局
     */
    private void hideView() {
        for (int i = views.size() - 1; i >= 0; i--) {
            final double delay;
            if (i == 1) delay = 100;
            else delay = 150;
            final int position = i;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < views.size()) {
                        AnimTools.animateHideView(views.get(position));
                    }
                }
            }, (long) delay);
        }
    }

    /**
     * 显示注销退出布局
     */
    private void showView() {
        for (int i = 0; i < views.size(); i++) {
            final double delay;
            if (i == 0) delay = 100;
            else delay = 150;
            final int position = i;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (position < views.size()) {
                        AnimTools.animateView(views.get(position));
                    }
                }
            }, (long) delay);
        }
    }

    /**
     * PopupWindow 弹出详情介绍
     *
     * @param view
     */
    public void showPop(View view) {
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view, UtilTools.getScreenWidth(getApplicationContext()),
                    (UtilTools.getScreenHeight(getApplicationContext()) * 2) / 5);
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
                R.mipmap.logout_bg));// 点击空白时popupwindow关闭,设置背景图片，不能在布局中设置，要通过代码来设置
        mPopupWindow.setOutsideTouchable(true);// 触摸popupwindow外部，popupwindow消失。这个要求你的popupwindow要有背景图片才可以成功
        mPopupWindow.setAnimationStyle(R.style.ExitAnim);// 设置窗口显示的动画效果
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopupWindow.update();
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (player != null) {
            stopPlayMusic();
            if (createMediaPlayer()) {
                NiftyNotificationView.build(this, "单曲循环中", Effects.thumbSlider, R.id.notify_rl)
                        .setIcon(R.mipmap.notify_bg)
                        .show();
            }
        }
    }

    private void stopPlayMusic() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
