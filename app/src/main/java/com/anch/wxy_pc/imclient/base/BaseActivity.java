package com.anch.wxy_pc.imclient.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.anch.wxy_pc.imclient.R;

/**
 * Created by wxy-pc on 2015/7/13.
 */
public class BaseActivity extends FragmentActivity{

    public static BaseActivity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_view);
        activity = this;
    }

}
