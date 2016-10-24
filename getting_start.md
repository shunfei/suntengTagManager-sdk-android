# TagManager SDK for Andoid 使用说明

> SDK Version :0.1.0  
> 2016-10

## 1、SDK使用指南
本指南会为您介绍如何在现有应用中集成舜飞TagManager SDK,以及在集成过程中需要注意的一些重要事项。其中的技术和代码示例试用于 Eclipse 和 Android Studio 项目，因此试用IDE的开发者可以参阅本指南。

### 1.1 集成TagManager SDK
**添加SDK：**  
完成在项目中添加SDK的操作，可能会由于您试用的IDE的不同而有所差异。

#### 以下步骤适用于试用Android Studio的开发者：
**方法一：**将下载得到的 *sunteng-tagManager-sdk-0.1.0.jar* 文件复制到项目libs文件夹 -> 打开Open Module Settings -> 在Dependencies中选择sunteng-tagManager-sdk-0.1.0.jar添加  

**方法二：**将下载得到的 *sunteng-tagManager-sdk-0.1.0.jar* 文件复制到项目libs文件夹 -> 打开项目module的build.gradle文件 -> 在dependencies {...}代码部分加入：  

`compile files('libs/sunteng-tagManager-sdk-0.1.0.jar')`  


#### 以下步骤适用于试用Eclipse的开发者：
**方法一：** Eclipse ADT 17 以下版本用户，在Eclipse中右键点击工程根目录，选择 Properties -> Java Build Path -> Libaries，然后点击 Add External Jars选择指向 *sunteng-tagManager-sdk-0.1.0.jar* 的路径，点击OK，即导入成功。

**方法二：**Eclipse ADT 17 以上版本用户，把 *sunteng-tagManager-sdk-0.1.0.jar* 文件直接拷贝到工程项目的libs文件夹下，再选中jar包然后右键点击 Build Path 选择 Add to Build Path -> Eclipse里刷新工程。  

### 1.2 配置AndroidManifest.xml
在工程AndroidMainfest.xml中<manifest>区域配置SDK所需的Android权限集，如下所示：

	<!-- SDK所需的用户权限，必选 -->
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>


>**注意：权限的缺失会影响到SDK的功能，请务必正确填写**

### 1.3 添加默认容器到项目中
> **温馨提示:如果您还没有[配置TagManager容器](http://web.tagmanager.cn/)，请先移步至此。**  

在首次启动应用时，TagManager启动时候，会首先使用默认容器数据，直到应用从网络获取到最新的容器数据为止。 
 
##### 下载并添加默认容器的JSON文件到项目中。

1. 登录[TagManager网站](http://web.tagmanager.cn/)；
2. 选择想要下载的容器版本；
3. 下载容器JSON文件；
4. 添加下载的JSON文件到项目下的raw资源文件中；  
a）如果 <project-root>/res/ 文件下没有raw文件夹，则需要创建它；  
b）如果有必要，请重命名下载下来的JSON文件，因为raw夹下文件的命名只支持小写字母、数字和下划线；  
c）添加容器JSON文件到  <project-root>/res/raw 下。 

>**注意：请勿擅自修改JSON文件中的数据**，以免在SDK解析JSON时出现错误，从而导致SDK不能正常运行的情况。如果发现有错误或者需要修改默认容器的数据，请前往TagManger网站修改容器后重新发布后再进行上面的1-4的步骤。


### 1.4 初始化TagManage

#####在项目中，对TagManager进行初始化。

**1.4.1. 获取TagManager实例：**  
<pre>//this参数为当前Activity或者应用的上下文参数Context;
TagManager tagManager = TagManager.getInstance(this);</pre>

> **Tips:**TagManager采用单例设计模式，多次调用*getInstance（）*方法并不会导致多次刷新容器中的数据，如果需要主动刷新容器获取最新的容器数据，我们提供了相应的刷新方法，这个方法将在下文中提及。但是如果频繁点击‘返回键’退出应用，再又迅速启动应用，会导致重新创建TagManger的实例，从而浪费不必要的内存，如果开发者硬要如此操作，建议获取TagManager实例时候，把ApplicationContext作为参数传入。
 
**1.4.2. 加载容器数据：**  
用TagManager的实例调用 *loadContainerPreferFresh(String containerID,int containerResourceId)* 方法去加载容器container，这个步骤，需要传入两个参数，其中*CONTAINER_ID*是默认容器的ID，即容器发布的时候网页上生产的ID，*containerResourceId*是默认容器的在应用中的资源ID。最终这个方法将返回一个PendingResult做为结果。

<pre>
PendingResult<ContainerLoader> pendingResult = mTagManager.loadContainerPreferFresh(CONTAINER_ID, R.raw.tgm_201609);
</pre>

**1.4.3. 获取容器实例：**  
在容器加载完成之后，使用 *FinalResultCallback* 回调，获得一个 *ContainerLoader*，再调用ContainerLoader的*getContainer（）*方法，即可获取到Container的实例，代码如下：

<pre>
<code>pendingResult.setFinalResultCallback(new FinalResultCallback<ContainerLoader>() {
@Override
public void onResult(ContainerLoader loader) {
	mContainerLoader = loader;
	ContainerLoaderSingleton.setContainerLoader(mContainerLoader);
	mContainer = loader.getContainer();
}	
});</code>
</pre>

>**Tips：**ContainerLoader中提供了一个*refresh()*方法用来刷新容器中的数据(此操作会从网络的拉取最新的已发布的容器数据并替应用中当前容器数据)。使用示例如下：  
>`mContainerLoader.refresh();`

**1.4.4. 建立一个保存ContainerLoader实例的类：**  
每次运行应用，开发者都只应该维护一个ContainerLoader的实例，这就是上面的代码中，为什么使用ContainerLoaderSingleton类去管理保存ContainerLoader实例的原因，下面的代码提供了一个ContainerLoaderSingleton的模板：

<pre>
<code>package com.sunteng.tagmanager.demo;

import com.sunteng.tagmanager.sdk.ContainerLoader;

/**
 * Singleton to hold the Container (since it should be only created once per run of the app).
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
}</code>
</pre>

**1.4.5. 获取Container中配置的值：**  
一旦容器数据被加载完成，我们就可以调用*Container*中的*getString()*方法去获取容器中配置的变量集的值。下面的代码是根据key获取变量宏具体的值的操作：  

<pre>
<code>ContainerLoader containerLoader  = ContainerLoaderSingleton.getContainerLoader();
String value = containerLoader.getContainer().getString(key);</code>
</pre>

>*Container.getString(key)*会获取到开发者在网站配置的变量宏对应key的value，如果网络端的数据由于某些原因而未能成功加载，则会从上次成功的网络请求中去获取值，如果本地未有过缓存的数据，则会去默认的容器中获取值。

**1.4.6. 向数据层push事件或者数据：**  
TagManager中包含一个数据层dataLayer,开发者可以向其中push一些在应用的其他地方可能用得到的值，或者用来触发网页上配置的容器中的tags。

**向dataLayer中push值**  
DataLayer类提供了两个push值更新数据层的方法，push到数据层的数据有可能在应用的其他地方被使用，也有可能是作为TagManager中tags的输入：  

**Method 1：***updateDataLayer(String,Object)*方法。调用这个方法，用户可以数据层中push键值对（key-value）类型的数据，向dataLayer中push数据的代码如范例下：

<pre>
//获取DataLayer的实例
DataLayer dataLayer = mTagManager.getTGDataLayer();
//把image_name的值放入DataLayer以备后用
dataLayer.updateDataLayer(IMAGE_NAME_KEY,imageName);
</pre>

**Method 2：***updateDataLayer(Map<String,Object> data)*方法。这个方法的需要开发者先把key-value的值封装到一个Map中。使用如下： 
 
<pre>
//获取DataLayer的实例
DataLayer dataLayer = mTagManager.getTGDataLayer();
//把Map放入DataLayer以备后用
dataLayer.updateDataLayer(DataLayer.mapOf(IMAGE_NAME_KEY, imageName));
</pre>

>**DataLayer.mapof()**是一个工具方法，它会把传入的“key-value”形式的值放入一个符合DataLayer要求的Map然后返回，如果用户传入的值不是key-value成对出现，则会抛出一个*IllegalArgumentException*。

>调用DataLayer的的*get(String key)*方法可获取到push到数据层的值,*get()*方法的返回值是Object类型，开发者可能需要做强制类型转换以得到自己想要的数据类型。

<!--调用DataLayer的的*get(String key)*方法或者Container的*getString(String key)*方法均可获取到push到数据层的值，这两个方法的不同之处在于DataLayer的*get(key)*返回一个Object类型的对象，并且如果*get()*方法传入的是一个宏名（本SDK认为所有以“{{”开头，并且以“}}”结尾的字符串均为宏），此方法只会把这个宏当成一个普通的字符串作为key，而不会去先计算宏的结果；而Container的*getString（key）*顾名思义，返回结果是字符串类型，并且这个方法的传入参数必须是宏，如果不是宏则会返回一个空字符串，如果是宏，会去计算宏的结果作为返回值，而不是push到DataLayer的时候，以宏为key值的那个值。-->

**向dataLayer中push事件（触发tags）**  
向DataLayer中push事件，针对这些事件，可以把容器中用户想触发或者不想触发的Tag区分开来。例如：比起我们以前的在应用中以硬编码的方式加入统计代码，使用TagManager,用户可以在网页中定义跟踪的Tag,然后向DataLayer中push相应的事件来触发或者禁止Tag的执行，这样开发在不需要修改应用的前提下，通过修改网页中的tag或者添加额外的tag就能修改原先的统计代码。
向dataLayer中push事件的代码范例如下：
<pre>
DataLayer dataLayer = mTagManager.getTGDataLayer();
dataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", screenName));
</pre>


至此，你已经知道并且会使用TagManager的一些基本功能，下面介绍TagManager的另外两种功能，函数回调和函数宏回调。  

**1.4.7 函数回调接口Container.FunctionCallTagCallback**  
介绍:应用提供的tag执行的回调  
接口中的方法：`void execute(String functionName, Map<String, Object> map)`  
这个方法有两个参数，第一个是宏的函数名，它的值和注册回调时候传入的函数名称相同，多个函数名称相同的宏,只需要注册一个回调，第二个参数是Map类型的parameters(这个map可能包含String，Double,Boolean,Integer,Long,Map,以及List类型的值),当应用中出现向dataLayer中push events的代码时，可能会导致回调被执行，并且回调会在和push调用的相的线程中执行。回调注册的代码范例如下:

<pre>
 private void registerFuncCallTag(String funcName) {
    if (this.mContainer == null){
        LogUtil.i("mContainer is null");
        return;
    }
    mContainer.registerFunctionCallTagCallback(funcName, new Container.FunctionCallTagCallback() {
        @Override
        public void execute(String functionName, Map<String, Object> map) {
            showToast("execute " + functionName);

            if (map != null && !map.isEmpty()){
                Set<String> keys = map.keySet();
                if (keys == null){
                    LogUtil.i("execute " + functionName + " map is null");
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (String key : keys){
                    stringBuilder.append("[key = " + key + " value = " + map.get(key) + "] ");
                    LogUtil.i("execute " + functionName + " parameters  is:"+stringBuilder.toString());
                }
            }
        }
    });
}
</pre>

**1.4.8 函数宏回调接口Container.FunctionCallMacroCallback**  
介绍：应用提供的用来计算宏值的回调  
接口中的方法：`Object getValue(String functionName, Map<String, Object> map)`
这个方法也需要传入两个参数，一是宏的函数名，另外一个是一个Map类型的parameters(这个map可能包含String，Double,Boolean,Integer,Long,Map,以及List类型的值),这个方法最后需要返回一个Object类型的宏计算结果。当应用中出现向DataLayer中push events或者从Container中取值的代码的时候，这个回调可能会执行，并且在push或者get相同的线程中执行。注册回调代码范例如下：
<pre>
private void registerFuncCallMacro(String funcName) {
    if (this.mContainer == null){
        showToast("mContainer is null");
        return;
    }
    mContainer.registerFunctionCallMacroCallback(funcName, new Container.FunctionCallMacroCallback() {
        @Override
        public Object getValue(String functionName, Map<String, Object> map) {
            if (map != null && !map.isEmpty()){
                Set<String> keys = map.keySet();
                if (keys == null){
                    LogUtil.i("getValue " + functionName + " map is null");
                }else{
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String key : keys){
                        stringBuilder.append("[key = " + key + " value = " + map.get(key) + "] ");
                    }
                    LogUtil.i(functionName + " getValue() map " + stringBuilder.toString());
                }
            }

            Object vaule = null;
            //此处计算value值的代码需开发者补充
            return value;
        }
    });
}
</pre>

### 1.5 其他设置
**开启日志，可查看SDK所有日志输出:**
<pre>
/**
*若在开发测试阶段需要开启日志输出
*@param verboseLogEnabled 值为true时候，开启
*/
public void setVerboseLogEnabled(boolean verboseLogEnabled);

使用示例：mTagManager.setVerboseLogEnabled(true);
</pre>

### 1.6 打包（*）
<pre>
打包APK时，因为SDK支持的最低API版本为14，建议在AndroidMainfest.xml中指定：  
android:targetSdkVersion >= 14
</pre>

## 2、使用建议

### 2.1 运行及开发环境：
>最低环境为 Andorid Api Level 14  
>编译环境为 Android Api Level 23 

### 2.2 关于代码混淆：
由于 TagManager SDK 代码已经经过混淆，再次对SDK代码混淆，可能导致SDK不能正常发挥作用，所以在对应用进行混淆时，请参考加入以下混淆代码：  
<pre>
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/ variable
-dontoptimize
-dontwarn
-libraryjars libs/proguard-STagManager.jar 
-keep class com.sunteng,*.** {*;}
-keep interface com.sunteng.*.** {*;}
</pre>
