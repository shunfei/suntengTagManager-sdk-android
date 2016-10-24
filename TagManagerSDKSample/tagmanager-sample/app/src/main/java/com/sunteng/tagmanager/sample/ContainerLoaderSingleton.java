/*
 * Copyright (c)  2011-2016.  SUNTENG Corporation. All rights reserved File :
 * Creation :  16-10-17 上午11:41
 * Description : ContainerLoaderSingleton.java
 * Author : baishixian@sunteng.com
 */

package com.sunteng.tagmanager.sample;

import com.sunteng.tagmanager.sdk.ContainerLoader;

/**
 * ContainerLoaderSingleton 保持容器加载类可被全局引用的示例
 * Created by baishixian on 2016/10/8.
 */

public class ContainerLoaderSingleton {

    private static ContainerLoader containerLoader;

    /**
     * Utility class; don't instantiate.
     */
    private ContainerLoaderSingleton() {}

    public static ContainerLoader getContainerLoader() {
        return containerLoader;
    }

    public static void setContainerLoader(ContainerLoader loader) {
        containerLoader = loader;
    }
}
