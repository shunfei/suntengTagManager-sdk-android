
/*
 * Copyright (c)  2011-2016.  SUNTENG Corporation. All rights reserved File :
 * Creation :  16-10-17 上午10:55
 * Description : SampleApplication.java
 * Author : baishixian@sunteng.com
 */

package com.sunteng.tagmanager.sample;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.sunteng.tagmanager.sdk.ContainerLoader;
import com.sunteng.tagmanager.sdk.FinalResultCallback;
import com.sunteng.tagmanager.sdk.PendingResult;
import com.sunteng.tagmanager.sdk.TagManager;

/**
 * StudioProjects
 * Created by baishixian on 2016/10/17.
 */

public class SampleApplication extends Application {

    private final String CONTAINER_ID = "10487";

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)){
            return;
        }
        LeakCanary.install(this);

        //进行tagManager的初始化操作
        initTagManagerSDK(this);
    }

    /**
     * suntengTagManager sdk初始化方法
     * @param ctx
     */
    private void initTagManagerSDK(Context ctx){
        TagManager tagManager = TagManager.getInstance(ctx);
        tagManager.setVerboseLogEnabled(true);
        PendingResult<ContainerLoader> pendingResult = tagManager.loadContainerPreferFresh(CONTAINER_ID, R.raw.tgm_201609);
        pendingResult.setFinalResultCallback(new FinalResultCallback<ContainerLoader>() {
            @Override
            public void onResult(ContainerLoader result) {
                //全局存放ContainerLoader
                ContainerLoaderSingleton.setContainerLoader(result);
            }
        });
    }
}
