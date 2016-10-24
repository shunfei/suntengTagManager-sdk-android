/*
 * Copyright (c)  2011-2016.  SUNTENG Corporation. All rights reserved File :
 * Creation :  16-10-17 上午11:01
 * Description : SplashActivity.java
 * Author : baishixian@sunteng.com
 */

package com.sunteng.tagmanager.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;

import com.sunteng.tagmanager.sdk.ContainerLoader;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //可以在SplashActivity中进行异步刷新操作，网络获取成功后会自动完成容器动态替换，不需要回调。
        ContainerLoader containerLoader = ContainerLoaderSingleton.getContainerLoader();
        containerLoader.refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, SampleActivity.class));
                finish();
            }
        },800);
    }
}
