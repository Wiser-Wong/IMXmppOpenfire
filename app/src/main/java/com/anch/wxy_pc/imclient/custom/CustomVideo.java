package com.anch.wxy_pc.imclient.custom;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.IMApplication;
import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.DateUtils;
import com.anch.wxy_pc.imclient.utils.LoadingAnim;
import com.anch.wxy_pc.imclient.utils.NetUtils;
import com.anch.wxy_pc.imclient.utils.Titanic;
import com.anch.wxy_pc.imclient.utils.ToastTools;
import com.anch.wxy_pc.imclient.utils.UtilTools;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;


/**
 * 自定义视频播放器
 *
 * @author wxy
 */
public class CustomVideo extends RelativeLayout implements Callback,
        OnClickListener, OnSeekBarChangeListener, OnInfoListener,
        OnBufferingUpdateListener, OnCompletionListener, OnErrorListener,
        OnPreparedListener {

    private static final int UPDATE_SEEKBAR = 100;// 更新进度条
    private static final int TIMER_HIDE_TITLE_AND_BUTTOM = 101;// 8秒隐藏title和buttom
    private static final int COMPLITE_SHOW_TITLE_AND_BUTTOM = 102;// 播放完毕显示title和buttom
    private static final int PREPARE_COMPLITE_TIME = 103;// 播放准备完成时间
    private static final int PREPARE_COMPLETE = 104;// 播放准备标志
    private static final int LOADING_TIMEOUT = 109;// 加载时间超时标志

    private static final int SPEEDGO = 105;// 更新进度条快进进度
    private static final int SPEEDBACK = 106;// 更新进度条后退
    private static final int MOVEGO = 107;// 快进
    private static final int MOVEBACK = 108;// 后退

    private static final int SOUNDUP = 110;// 调大声音
    private static final int SOUNDDOWN = 111;// 调小声音

    private static final int LIGHTUP = 112;
    private static final int LIGHTDOWN = 113;

    private LayoutInflater mInflater;// 加载器
    private Context mContext;// 上下文

    private View view;// 视频播放器布局
    private CustomTitle mTitle;// 自定义播放器头部
    private Custombuttom mButtom;// 自定义播放器底部
    private SurfaceView mSurfaceView;// 显示视频View
    private ImageView mLoadingAnim;// 缓冲加载帧动画
    private TextView mResetConnectTv;// 当网络断开时再次连接上，点击继续播放
    private LinearLayout mGoOrBackLin;// 快进或者后退显示的布局
    private TextView mGoOrBackBg;// 快进背景
    private TextView mGoOrBackTime;// 快进或者后退显示的时间
    private LinearLayout mSoundLin;// 声音调节的布局
    private TextView mSoundBg;// 声音背景
    private TextView mSoundRate;// 快进百分比
    private LinearLayout mLightLin;// 亮度调节的布局
    private TextView mLightBg;// 亮度背景
    private TextView mLightRate;// 亮度调节百分比

    private TextView mTitleBack;// 头部返回键
    private TitanicTextView mTitleText;// 视频主题

    private ImageButton mButtomPlayAndPause;// 播放和暂停按钮
    private TextView mButtomPlayTime;// 播放时间
    private TextView mButtomVideoLength;// 视频总时间
    private SeekBar mButtomSeekBar;// 视频进度条
    private TextView mAllScreeShow;// 全屏横屏显示视频

    private int mPos;// 开始播放的位置
    private int mNoNetPos;// 当没有网络时候 视频播放的位置
    private int currentPosition;// 播放的当前位置
    private int prepareTime;// 播放准备时间
    private int loadingTime;// 加载时间
    private int prepareCompleteTime;// 播放准备完成的时间
    private boolean isDragSeekBar;// 是否拖动进度条
    private boolean isTouch = false;// 是否点击屏幕
    private boolean isShowTitleAndButtom;// 是否显示title和Buttom
    private int count;// 记录当屏幕4秒钟没有触摸就自动隐藏title和buttom

    private MediaPlayer mPlayer;
    private AudioManager mAudio;

    private Activity mActivity;
    private String playUrl = "video.mp4";

    int maxVolume;// 最大音量
    int currentVolume;// 当前音量

    int currentLight;// 当前亮度

    int pressX;// 按下X轴位置
    int pressY;// 按下Y轴位置
    int moveX;// 移动X轴距离位置
    int moveY;// 移动Y轴距离位置
    int upX;// 抬起X轴位置
    int upY;// 抬起Y轴位置

    int goPos;// 快进之后显示的位置
    int backPos;// 后退之后显示的位置

    public CustomVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        view = mInflater.inflate(R.layout.custom_player, this, true);// 加载主布局
        if (isInEditMode())
            return;
        initView(view);// 初始化布局控件
        setMethod();// 设置对象方法
        setListener();// 启动监听事件注册
    }

    /**
     * 当前Activity
     */
    public void currentAct(Activity mActivity) {
        this.mActivity = mActivity;
    }

    /**
     * 初始化地址
     */
    public void currentUrl(String url) {
        this.playUrl = url;
        if (playUrl.equals("video.mp4")){
            mTitleText.setText("直到世界尽头");
        }else {
            mTitleText.setText("大圣归来");
        }
    }

    /**
     * 播放下一个视频
     */
    public void playNextVideo(String url) {
        this.playUrl = url;
        if (mPlayer != null) {
            stop();
            play(0, url);
        } else {
            play(0, url);
        }
    }

    /**
     * 更新界面Handler
     */
    Handler mUpdateUiHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_SEEKBAR:// 100
                    if (mPlayer != null && mPlayer.isPlaying()) {
                        int currentPos = mPlayer.getCurrentPosition();
                        Log.v("wxy", "currentPos:--->>" + currentPos);
                        mButtomSeekBar.setProgress(currentPos);
                        int duration = mPlayer.getDuration();
                        Log.v("wxy", "duration:--->>" + duration);
                        mButtomPlayTime.setText(DateUtils
                                .millisToString(currentPos));
                        mButtomVideoLength.setText(DateUtils
                                .millisToString(duration));
                        if (!NetUtils.CheckNetwork(mContext)) {
                            mUpdateUiHandler.removeMessages(UPDATE_SEEKBAR);
                        } else {
                            mUpdateUiHandler.sendEmptyMessageDelayed(
                                    UPDATE_SEEKBAR, 1000);// 1秒循环发送消息更新进度条
                        }
                    }
                    break;
                /** 循环发送当点击了屏幕之后隐藏title和buttom的Handler */
                case TIMER_HIDE_TITLE_AND_BUTTOM:// 定时4秒关闭title和buttom
                    count++;// 计数器
                    Log.v("wxy", "count:-->>" + count);
                    if (count == 8) {// 当4秒钟无操作隐藏title和buttom
                        HideTitleAndButtom();
                    }
                    // 循环发送handler，当count=4的时候隐藏title和buttom，count=5不发送
                    if (count < 9) {
                        mUpdateUiHandler.sendEmptyMessageDelayed(
                                TIMER_HIDE_TITLE_AND_BUTTOM, 3000);
                    }
                    break;
                case COMPLITE_SHOW_TITLE_AND_BUTTOM:// 播放完毕显示title和buttom
                    if (!isShowTitleAndButtom) {
                        showTitleAndButtom();
                    }
                    break;
                case PREPARE_COMPLITE_TIME:// 准备完成计时器
                    prepareTime++;
                    Log.v("wxy", "准备时间：" + prepareTime);
                    if (prepareTime == 8 && prepareCompleteTime == PREPARE_COMPLETE) {
                        ToastTools.frameToast(mContext, "开始播放", R.drawable.custom_toast);
                    } else {
                        if (prepareTime == 8
                                && prepareCompleteTime != PREPARE_COMPLETE) {
                            stop();// 超时停止
                            ToastTools.frameToast(mContext, "播放失败", R.drawable.custom_toast);
                            mButtomPlayAndPause
                                    .setBackgroundResource(R.drawable.play);
                            mButtomPlayAndPause.setEnabled(true);
                            endLoadingAnim();// 8秒视频准备时间未完成就停止然后加载动画隐藏起来
                            mUpdateUiHandler
                                    .sendEmptyMessage(COMPLITE_SHOW_TITLE_AND_BUTTOM);// 播放失败显示title和buttom
                        }
                    }
                    if (prepareTime < 8) {
                        mUpdateUiHandler.sendEmptyMessageDelayed(
                                PREPARE_COMPLITE_TIME, 1000);
                    }
                    break;
                case LOADING_TIMEOUT:// 加载时间超时
                    loadingTime++;
                    if (loadingTime == 8) {
                        stop();// 超时停止
                        ToastTools.frameToast(mContext, "超时加载", R.drawable.custom_toast);
                        mButtomPlayAndPause.setBackgroundResource(R.drawable.play);
                        mButtomPlayAndPause.setEnabled(true);
                        endLoadingAnim();// 8秒视频准备时间未完成就停止然后加载动画隐藏起来
                        mUpdateUiHandler
                                .sendEmptyMessage(COMPLITE_SHOW_TITLE_AND_BUTTOM);// 播放失败显示title和buttom
                    }
                    if (loadingTime < 8) {
                        mUpdateUiHandler.sendEmptyMessageDelayed(LOADING_TIMEOUT,
                                1000);
                    }
                    break;
                case SPEEDGO:// 更新视频快进进度
                    if (goPos < mButtomSeekBar.getMax()) {// 当滑动换算值没有超过最大视频长度，更新视频显示的位置
                        mPlayer.seekTo(goPos);
                    } else {// 当滑动换算值超过了最大视频长度，更新视频结束
                        mPlayer.seekTo(mButtomSeekBar.getMax());
                    }
                    break;
                case SPEEDBACK:// 更新视频后退进度
                    if (backPos > 0) {// 当滑动换算值没有到视频头部位置，更新视频显示位置
                        mPlayer.seekTo(backPos);
                    } else {// 当滑动换算值小于0，说明已经滑动到头部位置，显示最开始播放的位置
                        mPlayer.seekTo(0);
                    }
                    break;
                case MOVEGO:// 快进
                    int moveScreenDistanceGo = msg.getData().getInt("MoveXGo");// 快进移动的屏幕宽的距离
                    Log.v("move", "moveScreenDistanceGo:-->>"
                            + moveScreenDistanceGo);
                    speedGo(moveScreenDistanceGo);// 快进
                    break;
                case MOVEBACK:// 后退
                    int moveScreenDistanceBack = msg.getData().getInt("MoveXBack");// 后退移动的屏幕宽的距离
                    Log.v("move", "moveScreenDistanceBack:-->>"
                            + moveScreenDistanceBack);
                    speedBack(moveScreenDistanceBack);// 后退
                    break;
                case SOUNDUP:// 增大声音
                    soundChange(true);
                    break;
                case SOUNDDOWN:// 调小声音
                    soundChange(false);
                    break;
                case LIGHTUP:// 增强亮度
                    lightChange(true);
                    break;
                case LIGHTDOWN:// 减弱亮度
                    lightChange(false);
            }
        }

        ;
    };

    /**
     * 改变屏幕亮度
     */
    private void lightChange(boolean flag) {
        if (flag) {// 增强屏幕亮度
            currentLight = (int) (currentLight + (255 - currentLight)
                    * (Math.abs(moveY - pressY))
                    / UtilTools.getScreenHeight(mContext));
            Log.v("wxy", "currentLight增强:-->>" + currentLight);
        } else {// 减弱屏幕亮度
            currentLight = (int) (currentLight - currentLight
                    * (Math.abs(pressY - moveY))
                    / UtilTools.getScreenHeight(mContext));
            Log.v("wxy", "currentLight减弱:-->>" + currentLight);
        }
        mLightRate.setText(currentLight * 100 / 255 + "%");
        WindowManager.LayoutParams params = mActivity.getWindow()
                .getAttributes();
        params.screenBrightness = currentLight / 255f;
        mActivity.getWindow().setAttributes(params);
    }

    /**
     * 调节增大声音还是减小声音
     */
    private void soundChange(boolean flag) {
        // 当前音量值
        currentVolume = mAudio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (flag) {// true 增大音量
            if (currentVolume < maxVolume) {
                currentVolume++;
            }
        } else {// false 调小音量
            if (currentVolume > 0) {
                currentVolume--;
            }
        }
        if (currentVolume > 0) {// 当前音量不为0时，背景为有声状态
            mSoundBg.setBackgroundResource(R.mipmap.sound_adjust);
        } else {
            if (currentVolume == 0) {
                mSoundBg.setBackgroundResource(R.mipmap.sound_off);
            }
        }
        Log.v("wxy", "currentVolume:-->>" + currentVolume);
        // 设置声音百分比
        mSoundRate.setText(currentVolume * 100 / maxVolume + "%");
        // 设置声音增大多少
        mAudio.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        Log.v("wxy", "mSoundRate:-->>" + mSoundRate.getText());
    }

    /**
     * 启动加载动画方法
     */
    public void startLoadingAnim() {
        mLoadingAnim.setVisibility(View.VISIBLE);// 开启加载动画
        new LoadingAnim(mLoadingAnim).startLoading();
    }

    /**
     * 启动加载动画方法
     */
    public void endLoadingAnim() {
        mLoadingAnim.setVisibility(View.INVISIBLE);// 结束加载动画
        new LoadingAnim(mLoadingAnim).endLoading();
    }

    // 来自于MediaPlayer.OnInfoListener接口
    // 当出现关于播放媒体的特定信息或者需要发出警告的时候
    // 将调用该方法
    // 比如开始缓冲、缓冲结束、下载速度变化(该行待验证)
    // 小结:这些Info都是以MediaPlayer.MEDIA_INFO_开头的
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:// 缓冲开始显示加载
                loadingTime = 0;
                mUpdateUiHandler.sendEmptyMessage(LOADING_TIMEOUT);
                startLoadingAnim();// 开启加载动画
                mButtomPlayAndPause.setEnabled(false);// 暂停不能用
                mButtomSeekBar.setEnabled(false);// 进度条取消点击事件
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:// 缓冲结束 取消加载
                endLoadingAnim();// 隐藏加载动画
                mButtomPlayAndPause.setEnabled(true);// 暂停恢复正常
                mButtomSeekBar.setEnabled(true);// 进度条取消点击事件
                mUpdateUiHandler.removeMessages(LOADING_TIMEOUT);
                break;
        }
        return false;
    }

    /**
     * 初始化布局文件
     */
    private void initView(View view) {
        mTitle = (CustomTitle) view.findViewById(R.id.playerTitle);
        mButtom = (Custombuttom) view.findViewById(R.id.playerButtom);
        mSurfaceView = (SurfaceView) view.findViewById(R.id.playerSurface);
        mLoadingAnim = (ImageView) view.findViewById(R.id.playerLoadingAnim);
        mResetConnectTv = (TextView) view.findViewById(R.id.playerNetErroText);
        mGoOrBackLin = (LinearLayout) view.findViewById(R.id.goOrBackLin);
        mGoOrBackBg = (TextView) view.findViewById(R.id.goOrBackIcon);
        mGoOrBackTime = (TextView) view.findViewById(R.id.goOrBackTime);
        mSoundLin = (LinearLayout) view.findViewById(R.id.soundLin);
        mSoundBg = (TextView) view.findViewById(R.id.soundIcon);
        mSoundRate = (TextView) view.findViewById(R.id.soundRate);
        mLightLin = (LinearLayout) findViewById(R.id.lightLin);
        mLightBg = (TextView) findViewById(R.id.lightIcon);
        mLightRate = (TextView) findViewById(R.id.lightRate);

        mTitleBack = (TextView) mTitle.findViewById(R.id.customTitleBack);
        mTitleText = (TitanicTextView) mTitle
                .findViewById(R.id.customTitleText);

        mButtomPlayAndPause = (ImageButton) mButtom
                .findViewById(R.id.playAndPauseBtn);
        mButtomPlayTime = (TextView) mButtom.findViewById(R.id.playTime);
        mButtomSeekBar = (SeekBar) mButtom.findViewById(R.id.playSeekBar);
        mButtomVideoLength = (TextView) mButtom
                .findViewById(R.id.playVideoLength);
        mAllScreeShow = (TextView) view.findViewById(R.id.showAllScree);

        mAudio = (AudioManager) mContext
                .getSystemService(Context.AUDIO_SERVICE);// 初始化AudioManager
        maxVolume = mAudio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);// 获得声音最大值

        ContentResolver resolver = mContext.getContentResolver();
        try {
            currentLight = android.provider.Settings.System.getInt(resolver,
                    Settings.System.SCREEN_BRIGHTNESS);
            Log.v("wxy", "lightCurrent:-->>" + currentLight);
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置对象方法
     */
    @SuppressWarnings("deprecation")
    private void setMethod() {
        mSurfaceView.getHolder().setType(
                SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView.getHolder().addCallback(this);// Holder创建销毁改变回调

        mButtomSeekBar.setEnabled(false);// 没有播放不让用户滑动进度条

        mTitle.bringToFront();// 置所有控件最上层
        mButtom.bringToFront();
        mTitleText.setText("直到世界尽头");
        new Titanic().start(mTitleText);// 启动波浪文字主题内容

    }

    /**
     * 点击事件
     */
    private void setListener() {
        mButtomSeekBar.setOnSeekBarChangeListener(this);// 进度条改变监听

        mTitleBack.setOnClickListener(this);
        mButtomPlayAndPause.setOnClickListener(this);
        mResetConnectTv.setOnClickListener(this);
        mAllScreeShow.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customTitleBack:// 返回键
                // 当处于横屏 显示，按返回键先回到竖屏，再次返回退出该页面
                if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mActivity
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 当前activity强制设置为竖屏
//                    // 重新添加重力感应器 (相当于在配置文件中添加android:screenOrientation="sensor")
//                    mActivity
//                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                } else {
                    destoryPlayer();
                    setVisibility(View.GONE);
                    NiftyNotificationView.build(mActivity, "关闭视频播放", Effects.slideIn, R.id.notify_rl)
                            .setIcon(R.mipmap.notify_bg).show();
                }
                break;
            case R.id.showAllScree:// 全屏显示按钮
                /**
                 * "unspecified" 默认值 由系统来判断显示方向.判定的策略是和设备相关的，所以不同的设备会有不同的显示方向.
                 * "landscape" 横屏显示（宽比高要长） "portrait" 竖屏显示(高比宽要长) "user" 用户当前首选的方向
                 * "behind" 和该Activity下面的那个Activity的方向一致(在Activity堆栈中的) "sensor"
                 * 有物理的感应器来决定。如果用户旋转设备这屏幕会横竖屏切换。 "nosensor"
                 * 忽略物理感应器，这样就不会随着用户旋转设备而更改了 （ "unspecified"设置除外 ）。
                 */
                // 是横屏 旋转成竖向屏幕
                if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mActivity
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 当前activity强制设置为竖屏
                    // 重新添加重力感应器 (相当于在配置文件中添加android:screenOrientation="sensor")
//                    mActivity
//                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                } else {// 是竖屏 旋转成横向屏幕
                    mActivity
                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 当前activity强制设置为横屏
                    // 重新添加重力感应器 (相当于在配置文件中添加android:screenOrientation="sensor")
//                    mActivity
//                            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
                }
                break;
            case R.id.playerNetErroText:// 重新连接，继续播放文字
                if (NetUtils.CheckNetwork(mContext)) {
                    if (!Constanst.ISEXIST_HOLDER.equals("ISEXIST_HOLDER")) {
                        play(mNoNetPos, playUrl);// 当Holder存在的时候断网的位置
                    } else {
                        play(currentPosition, playUrl);// 当Holder不存在的情况断网，当网络连接上，继续播放
                    }
                    Log.v("wxy", "playerNetErroText:--->>>" + currentPosition);
                    mResetConnectTv.setVisibility(View.GONE);
                    mButtomSeekBar.setEnabled(true);
                } else {
                    ToastTools.frameToast(mContext, "请检查网络", R.drawable.custom_toast);
                }
                break;
            case R.id.playAndPauseBtn:// 播放暂停按钮
                if (NetUtils.CheckNetwork(mContext)) {// 判断网络是否可用
                    if (mResetConnectTv.getVisibility() == View.VISIBLE) {
                        play(currentPosition, playUrl);// 当网络连接上，继续播放
                        Log.v("wxy", "playAndPauseBtn:--->>>" + currentPosition);
                        mResetConnectTv.setVisibility(View.GONE);
                    } else {
                        if (mPlayer == null) {
                            play(0, playUrl);// 最初开始播放
                        } else {
                            if (mPlayer.getCurrentPosition() < mPlayer
                                    .getDuration() && mPlayer.isPlaying()) {// 判断视频当前位置是否播完，没有播完并且正在播放，就暂停
                                pause();// 暂停播放
                            } else {
                                if (mPlayer.getCurrentPosition() < mPlayer
                                        .getDuration() && !mPlayer.isPlaying()) {// 判断视频当前位置是否播完，没有播完并且处于暂停状态，就播放
                                    start();// 开始播放
                                }
                            }
                        }
                    }
                } else {
                    ToastTools.frameToast(mContext, "网络连接不可用", R.drawable.custom_toast);
                }
                break;
        }
    }

    /**
     * 快进
     */
    private void speedGo(int moveScreenDistanceGo) {
        if (mButtomSeekBar.getMax() > UtilTools.getScreenWidth(mContext)) {// 文件长度大于屏幕宽度
            int everyScreenWidth = mButtomSeekBar.getMax()
                    / UtilTools.getScreenWidth(mContext);// 屏幕宽度每一距离代表视频进度多少值
            int proMoveGo = moveScreenDistanceGo * everyScreenWidth;// 滑动的宽度占视频总长多多少值
            goPos = proMoveGo + mPlayer.getCurrentPosition();// 视频走多少长度+当前播放的长度,就是滑动之后当前应该显示的进度
            if (goPos >= mButtomSeekBar.getMax()) {// 判断滑动的进度+当前进度大于或等于总长度时，进度条直接设置为总长度。
                mButtomSeekBar.setProgress(mButtomSeekBar.getMax());// 进度条跟随滑动显示
                mGoOrBackTime.setText(DateUtils.millisToString(mButtomSeekBar
                        .getMax()));// 滑动显示的时间
                mButtomPlayTime.setText(DateUtils.millisToString(goPos));// 底部时间跟随走
            } else {
                mButtomSeekBar.setProgress(goPos);// 进度条跟随滑动显示
                mGoOrBackTime.setText(DateUtils.millisToString(goPos));// 滑动显示的时间
                mButtomPlayTime.setText(DateUtils.millisToString(goPos));// 底部时间跟随走
            }
        }
    }

    /**
     * 后退
     */
    private void speedBack(int moveScreenDistanceBack) {
        if (mButtomSeekBar.getMax() > UtilTools.getScreenWidth(mContext)) {// 文件长度大于屏幕宽度
            int everyScreenWidth = mButtomSeekBar.getMax()
                    / UtilTools.getScreenWidth(mContext);// 屏幕宽度每一距离代表视频进度多少值
            int proMoveBack = moveScreenDistanceBack * everyScreenWidth;// 滑动宽度相当于视频总长多多少值
            backPos = mPlayer.getCurrentPosition() - proMoveBack;// 当前播放的长度-后退视频走多少长度，就是滑动之后应该显示的进度
            if (backPos > 0) {// 当前播放位置大于滑动产生的距离
                mButtomSeekBar.setProgress(backPos);// 进度条跟随滑动显示
                mGoOrBackTime.setText(DateUtils.millisToString(backPos));// 滑动显示的时间
                mButtomPlayTime.setText(DateUtils.millisToString(backPos));// 底部时间跟随走
            } else {// 如果当前播放进小于滑动产生的距离就重新播放位置
                mButtomSeekBar.setProgress(0);// 进度条跟随滑动显示
                mGoOrBackTime.setText(DateUtils.millisToString(0));// 滑动显示的时间,与底部时间同步
                mButtomPlayTime.setText(DateUtils.millisToString(0));// 底部时间跟随走
            }
        }
    }

    /**
     * 按下移动快进和后退Touch事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressX = (int) event.getX();// 按下X位置
                pressY = (int) event.getY();// 按下Y位置
                break;
            case MotionEvent.ACTION_MOVE:// 移动过程
                moveX = (int) event.getX();// 移动到X的位置
                moveY = (int) event.getY();// 移动到Y的位置
                // 判断X轴移动的距离大于Y轴移动的距离时，执行快进后退反之执行调节声音
                if (Math.abs(moveX - pressX) > Math.abs(moveY - pressY)) {
                    if (moveX - pressX > 20 && mPlayer != null) {// 快进中
                        Log.v("move", "moveX:-->>" + moveX);
                        Log.v("move", "moveX-pressX:-->>" + (moveX - pressX));
                        mGoOrBackLin.setVisibility(View.VISIBLE);// 快进布局显示
                        mGoOrBackBg.setBackgroundResource(R.mipmap.go_next_play);// 快进icon
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("MoveXGo", moveX - pressX);
                        msg.setData(bundle);
                        msg.what = MOVEGO;
                        mUpdateUiHandler.sendMessage(msg);
                        break;
                    }
                    if (pressX - moveX > 20 && mPlayer != null) {// 后退中
                        mGoOrBackLin.setVisibility(View.VISIBLE);// 后退布局显示
                        mGoOrBackBg.setBackgroundResource(R.mipmap.back_up_play);// 后退icon
                        Message msg = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putInt("MoveXBack", pressX - moveX);
                        msg.setData(bundle);
                        msg.what = MOVEBACK;
                        mUpdateUiHandler.sendMessage(msg);
                        break;
                    }
                } else {
                    // 判断Y轴向下移动的距离大于20并且按下的位置在右半屏幕上
                    if (moveY - pressY > 20 && mPlayer != null
                            && pressX > UtilTools.getScreenWidth(mContext) / 2) {
                        mSoundLin.setVisibility(View.VISIBLE);// 显示声音调节布局
                        mUpdateUiHandler.sendEmptyMessage(SOUNDDOWN);// 发送减小声音Handler
                        break;
                    } else {
                        if (moveY - pressY > 20 && mPlayer != null
                                && pressX < UtilTools.getScreenWidth(mContext) / 2) {
                            mLightLin.setVisibility(View.VISIBLE);// 显示亮度调节布局
                            mUpdateUiHandler.sendEmptyMessage(LIGHTDOWN);// 发送减弱亮度Handler
                            break;
                        }
                    }
                    // 判断Y轴向上移动的距离大于20并且按下的位置在右半屏幕上
                    if (pressY - moveY > 20 && mPlayer != null
                            && pressX > UtilTools.getScreenWidth(mContext) / 2) {
                        mSoundLin.setVisibility(View.VISIBLE);// 显示声音调节布局
                        mUpdateUiHandler.sendEmptyMessage(SOUNDUP);// 发送增大声音Handler
                        break;
                    } else {
                        if (pressY - moveY > 20 && mPlayer != null
                                && pressX < UtilTools.getScreenWidth(mContext) / 2) {
                            mLightLin.setVisibility(View.VISIBLE);// 显示亮度调节布局
                            mUpdateUiHandler.sendEmptyMessage(LIGHTUP);// 发送增强亮度Handler
                            break;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:// 抬起位置
                if (mGoOrBackLin.getVisibility() == View.VISIBLE) {
                    mGoOrBackLin.setVisibility(View.GONE);// 抬起消失快进或者后退布局
                }
                if (mSoundLin.getVisibility() == View.VISIBLE) {
                    mSoundLin.setVisibility(View.GONE);// 抬起消失调节声音布局
                }
                if (mLightLin.getVisibility() == View.VISIBLE) {
                    mLightLin.setVisibility(View.GONE);// 抬起消失亮度调节布局
                }
                upX = (int) event.getX();// 抬起X位置
                upY = (int) event.getY();// 抬起Y位置
                Log.v("wxy", "UPX:-->>" + upX);
                Log.v("wxy", "pressX:-->>" + pressX);
                if (Math.abs(upX - pressX) > Math.abs(upY - pressY)) {
                    if (upX - pressX > 20 && mPlayer != null) {// 抬起位置减去按下位置大于30执行快进
                        mUpdateUiHandler.sendEmptyMessage(SPEEDGO);
                        break;
                    }
                    if (pressX - upX > 20 && mPlayer != null) {// 按下位置减去抬起位置大于30执行后退
                        mUpdateUiHandler.sendEmptyMessage(SPEEDBACK);
                        break;
                    }
                }
                touchShowTitleAndButtom();// 点击屏幕显示title和Buttom方法

                break;
        }
        return true;
    }

    /**
     * 点击屏幕显示title和Buttom方法
     */
    private void touchShowTitleAndButtom() {
        count = 0;// 再次点击重新发送count清空为0
        if (isTouch && mPlayer != null) {// true为title和buttom从隐藏到显示
            showTitleAndButtom();// 显示title和buttom
            // 发送点击屏幕显示title和buttom之后8秒钟无操作，然后隐藏title和buttom
            mUpdateUiHandler.sendEmptyMessage(TIMER_HIDE_TITLE_AND_BUTTOM);
        } else {// false为title和buttom从显示到隐藏
            if (!isTouch && mPlayer != null) {
                HideTitleAndButtom();// 隐藏title和buttom
                Log.v("wxy", "getVisibility:-->>" + mTitle.getVisibility()
                        + "VISIBLE:-->>" + View.INVISIBLE);
            }
        }
    }

    /**
     * 显示title
     */
    private void showTitleAndButtom() {
        mTitle.titleInAnim();
        mButtom.buttomInAnim();
        isTouch = false;
        isShowTitleAndButtom = true;
    }

    /**
     * 显示Buttom
     */
    private void HideTitleAndButtom() {
        mUpdateUiHandler.removeMessages(TIMER_HIDE_TITLE_AND_BUTTOM);
        mTitle.titleOutAnim();
        mButtom.buttomOutAnim();
        isTouch = true;
        isShowTitleAndButtom = false;
    }

    /**
     * 播放视频方法
     */
    public void play(int currentPos, String currentPath) {
        prepareTime = 0;// 多次点击准备时间重置
        mButtomPlayAndPause.setEnabled(false);// 没有加载出视频之前不让点击播放和暂停
        if (NetUtils.CheckNetwork(mContext)) {
            startLoadingAnim();// 准备阶段显示加载进度条
        }
        this.mPos = currentPos;// 开始播放的位置

        Log.v("wxy", "网络状态断开");

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setDisplay(mSurfaceView.getHolder());// 设置video影片以SurfaceHolder播放
        mPlayer.setScreenOnWhilePlaying(true);// 设置屏幕常亮

        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnInfoListener(this);
        mPlayer.setOnBufferingUpdateListener(this);

        try {
            AssetFileDescriptor fileDescriptor = IMApplication.getInstance().getAssets().openFd(currentPath);
            mPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getLength());
            mPlayer.prepareAsync();// 准备播放阶段，异步方法
            mUpdateUiHandler.sendEmptyMessage(PREPARE_COMPLITE_TIME);// 发送准备时间消息5秒未加载出来提示加载失败
        } catch (Exception e) {
            e.printStackTrace();
            ToastTools.frameToast(mContext, "播放文件出错了", R.drawable.custom_toast);
        }
    }

    /**
     * 准备完成
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        prepareCompleteTime = PREPARE_COMPLETE;// 准备完成5秒时间
        Log.v("wxy", "prepareCompleteTime:-->>" + prepareCompleteTime);
        Log.v("wxy", "getDuration:-->>" + mp.getDuration());
        mButtomPlayAndPause.setEnabled(true);// 视频播放出来之后可以点击暂停播放按钮
        mButtomSeekBar.setEnabled(true);// 视频播放出来之后可以滑动进度条
        endLoadingAnim();// 准备完毕取消加载进度条
        count = 0;// 播放开始的时候清为0
        mp.start();// 启动播放视频
        mp.seekTo(mPos);// 设置播放位置
        mButtomPlayAndPause.setBackgroundResource(R.drawable.pause);// 按钮变为播放状态
        int max = mp.getDuration();// 视频长度
        mButtomSeekBar.setMax(max);// 进度条最大值
        mUpdateUiHandler.sendEmptyMessage(UPDATE_SEEKBAR);// 更新进度条Handler
        mUpdateUiHandler.sendEmptyMessage(TIMER_HIDE_TITLE_AND_BUTTOM);// 当开始播放的时候，如果没有点击屏幕就自动4秒隐藏title和buttom
    }

    /**
     * 错误监听方法
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.v("wxy", "what:--->>" + what);
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                mUpdateUiHandler.sendEmptyMessage(COMPLITE_SHOW_TITLE_AND_BUTTOM);
                mUpdateUiHandler.removeMessages(TIMER_HIDE_TITLE_AND_BUTTOM);
                stop();// 网络断开状态停止播放器
                endLoadingAnim();// 播放出错结束加载进度
                mResetConnectTv.setVisibility(View.VISIBLE);// 重新连接，继续播放文字显示
                mButtomPlayAndPause.setBackgroundResource(R.drawable.play);// 播放状态转换停止状态
                ToastTools.frameToast(mContext, "网络视频出错了", R.drawable.custom_toast);
                mButtomSeekBar.setEnabled(false);// 进度条取消点击事件
                break;
            case MediaPlayer.MEDIA_ERROR_IO:
                mUpdateUiHandler.sendEmptyMessage(COMPLITE_SHOW_TITLE_AND_BUTTOM);
                mUpdateUiHandler.removeMessages(TIMER_HIDE_TITLE_AND_BUTTOM);
                stop();
                endLoadingAnim();// 播放出错结束加载进度
                mResetConnectTv.setVisibility(View.VISIBLE);// 重新连接，继续播放文字显示
                mButtomPlayAndPause.setBackgroundResource(R.drawable.play);// 播放状态转换停止状态
                mButtomSeekBar.setEnabled(false);// 进度条取消点击事件
                break;
        }
        return false;
    }

    /**
     * 视频播放完毕方法
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // 播放完毕如果是全屏，就让它变成小屏幕
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mActivity
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        mUpdateUiHandler.removeMessages(TIMER_HIDE_TITLE_AND_BUTTOM);// 停止发送消息4秒消失title和Buttom
        mUpdateUiHandler.sendEmptyMessage(COMPLITE_SHOW_TITLE_AND_BUTTOM);// 播放完毕显示title和buttom
        mButtomPlayAndPause.setBackgroundResource(R.drawable.play);// 完成按钮再变回未播放状态
        mButtomSeekBar.setEnabled(false);
        endLoadingAnim();// 播放完毕如果进度条还在就隐藏
        mPlayer.setDisplay(null);
        mPlayer.release();// 释放资源
        mPlayer = null;
        if (playUrl.equals("video.mp4")) {
            ToastTools.frameToast(mContext, "播放下一个", R.drawable.custom_toast);
            this.playUrl = "dasheng.mp4";
            play(0, playUrl);
            mTitleText.setText("大圣归来");
        } else ToastTools.frameToast(mContext, "全部播放结束", R.drawable.custom_toast);
    }

    /**
     * 暂停视频方法
     */
    private void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();// 暂停
            mButtomPlayAndPause.setBackgroundResource(R.drawable.play);// 按钮变为暂停状态
            mUpdateUiHandler.removeMessages(UPDATE_SEEKBAR);// 暂停移除更新进度条消息
            return;
        }
    }

    /**
     * 由暂停再次开始播放
     */
    public void start() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();// 继续播放
            mUpdateUiHandler.sendEmptyMessage(UPDATE_SEEKBAR);// 更新进度条
            mButtomPlayAndPause.setBackgroundResource(R.drawable.pause);// 按钮变为播放状态
            if (isDragSeekBar) {// 已经拖拽过进度条
                mUpdateUiHandler.sendEmptyMessage(UPDATE_SEEKBAR);// 更新进度条
                isDragSeekBar = false;
            }
        }
    }

    /**
     * 停止视频播放
     */
    public void stop() {
        if (mPlayer != null) {
            mPlayer.seekTo(0);// 播放置开始位置
            mPlayer.stop();// 停止播放
            mPlayer.release();// 释放资源
            mPlayer = null;
//            mButtomPlayAndPause.setBackgroundResource(R.drawable.play);
        }
    }

    /**
     * SurfaceHolder CallBack
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (NetUtils.CheckNetwork(mContext)) {
            endLoadingAnim();// 如果不判断就会重新创建Holder的时候，加载动画一直显示
        }
        // Holder创建
        if (currentPosition > 0 && isTouch) {// 视频由睡眠状态再次进入，从记录销毁Holder的位置继续播放。
            showTitleAndButtom();
            play(currentPosition, playUrl);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // Holder大小改变
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Holder 销毁
        if (mPlayer != null) {
            Constanst.ISEXIST_HOLDER = "ISEXIST_HOLDER";
            // 销毁Holder 记录播放到的位置，以便下次回来继续播放。
            currentPosition = mPlayer.getCurrentPosition();
            isTouch = true;
            stop();// 停止播放
            mUpdateUiHandler.removeMessages(UPDATE_SEEKBAR);
        }
    }

    /**
     * 缓冲第二进度条更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        mButtomSeekBar
                .setSecondaryProgress((mButtomSeekBar.getMax() * percent) / 100);
        mNoNetPos = (mButtomSeekBar.getMax() * percent) / 100;// 记录断网时缓冲的进度，当再次连网的时候，继续播放断网时的进度
        Log.v("wxy", "percent:-->>"
                + ((mButtomSeekBar.getMax() * percent) / 100));
    }

    /**
     * 进度条手动拖拽监听事件
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (fromUser) {// 拖动进度条判断
            isDragSeekBar = true;// 拖动进度条标志
            int barPos = seekBar.getProgress();// 获得进度条位置
            if (mPlayer != null && mPlayer.isPlaying()) {// 播放状态拖拽
                mPlayer.seekTo(barPos);// 播放到拖拽的位置
                mButtomPlayTime.setText(DateUtils.millisToString(barPos));// 更新时间
            } else {
                if (mPlayer != null && !mPlayer.isPlaying()) {// 暂停状态拖拽
                    start();
                    mPlayer.seekTo(barPos);// 播放到拖拽的位置
                    mButtomPlayTime.setText(DateUtils.millisToString(barPos));// 更新时间
                }
            }
        }
        if (!NetUtils.CheckNetwork(mContext)
                && seekBar.getProgress() >= mNoNetPos) {// 当没有网络时，手动拖动到大于等于缓冲条位置显示加载条
            Log.v("wxy", "seekBar:--》》" + seekBar.getProgress()
                    + "-->>mNoNetPos:-->>" + mNoNetPos);
            startLoadingAnim();// 加载动画
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    /**
     * 销毁播放器资源
     */
    public void destoryPlayer() {
        if (mPlayer != null) {
            mPlayer.seekTo(0);
            mPlayer.stop();
            mButtomSeekBar.setProgress(0);
            mButtomPlayTime.setText(DateUtils.millisToString(0));
            mPlayer.setDisplay(null);
            mPlayer.release();
            mPlayer = null;
            mUpdateUiHandler.removeMessages(COMPLITE_SHOW_TITLE_AND_BUTTOM);
            mUpdateUiHandler.removeMessages(TIMER_HIDE_TITLE_AND_BUTTOM);
            mUpdateUiHandler.removeMessages(UPDATE_SEEKBAR);
            mUpdateUiHandler.removeMessages(PREPARE_COMPLITE_TIME);
            mUpdateUiHandler.removeMessages(PREPARE_COMPLETE);
            mUpdateUiHandler.removeMessages(SPEEDBACK);
            mUpdateUiHandler.removeMessages(SPEEDGO);
            mUpdateUiHandler.removeMessages(LOADING_TIMEOUT);
            mUpdateUiHandler.removeMessages(MOVEBACK);
            mUpdateUiHandler.removeMessages(MOVEGO);
            mButtomPlayAndPause.setBackgroundResource(R.drawable.play);

        }
    }
}
