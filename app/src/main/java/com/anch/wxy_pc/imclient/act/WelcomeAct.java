package com.anch.wxy_pc.imclient.act;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.anch.wxy_pc.imclient.R;
import com.anch.wxy_pc.imclient.utils.AnimTools;
import com.anch.wxy_pc.imclient.utils.LoadingAnim;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by wxy-pc on 2015/6/11.
 */
@ContentView(R.layout.act_welcome)
public class WelcomeAct extends Activity {
    @ViewInject(R.id.wel_tv)
    private TextView welTv;
    @ViewInject(R.id.wel_loading_iv)
    private ImageView welLoadingIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        welTv.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/huawenhangkai.ttf"));
        new LoadingAnim(welLoadingIv).startLoading();
        AnimTools.alphaAnim(welTv, 3000, true, 0, 1, new AnimTools.EndAnimation() {
            @Override
            public void endAnim() {
                new LoadingAnim(welLoadingIv).endLoading();
                startActivity(new Intent(WelcomeAct.this, LoginAct.class));
                finish();
            }
        });
    }
}
