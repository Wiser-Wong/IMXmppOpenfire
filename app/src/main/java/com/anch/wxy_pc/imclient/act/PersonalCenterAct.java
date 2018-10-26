package com.anch.wxy_pc.imclient.act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.custom.VerticalScrollTextView;
import com.anch.wxy_pc.imclient.utils.BitmapUtils;
import com.anch.wxy_pc.imclient.utils.Constanst;
import com.anch.wxy_pc.imclient.utils.SharePrefUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by wxy-pc on 2015/7/14.
 */
@ContentView(R.layout.act_person_center)
public class PersonalCenterAct extends Activity {

    @ViewInject(R.id.person_center_iv)
    private ImageView personCenterIv;
    @ViewInject(R.id.person_center_tv)
    private TextView personCenterTv;
    @ViewInject(R.id.person_center_info_tv)
    private VerticalScrollTextView personCenterInfoTv;

    private String info = "    我相信爱是夜空最美的流星,坠落的光明灿烂我的生命,陪我穿过黑暗孤单里前行,为了遇见有你的风景," +
            "我故意出现在你的面前。你不需要刻意的来迁就我,你不需要很辛苦的,无论什么事,我都会尊重你,不会勉强你。" +
            "你不喜欢去的地方,我不会带着你去；你不喜欢做的事,我不会逼你；你不喜欢我任性,我不会再任性；当你不想我烦着你的时候,我会静静的站在一旁," +
            "我送你的礼物,你不喜欢,我不会勉强你。你不需要那么辛苦,你可以把我当成一个值得信任的人,当你开心的时候,让我也一起开心。" +
            "你可以把我当成一个值得信任的人,当你不开心的时候,让我和你一起分担。你不需要压抑着自己的情绪,生气的时候想说就说出来," +
            "想骂就骂出来,想打就打过来,如果什么都不对我说,只会让我觉得我只是一个多余的人。你可以做你自己,那个你说的性格直率,想说就说,想做就做的人。" +
            "你不需要再改变了，因为我在改变着自己。我真的很爱你。";
    private String userNameStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        getUserInfo();
        setUi();
    }

    private void getUserInfo() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                userNameStr = bundle.getString("WHO_USER");
            }
        }
    }

    private void setUi() {
        personCenterIv.setImageBitmap(BitmapUtils.getRoundedCornerBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.right_head), 150));
        personCenterTv.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/youyuan.ttf"));
        personCenterTv.setText(userNameStr);
        personCenterInfoTv.setText(info, 50);
    }
}
