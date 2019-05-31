集成：
  1.依赖添加：(版本可提升)
    implementation 'com.android.support:recyclerview-v7:27.1.1'
    implementation 'com.github.yilylong:CBDialog:v1.0.7'
    #qq分享包在编译时动态依赖
    #微信分享sdk
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:+'
    #微博分享sdk
    implementation 'com.sina.weibo.sdk:core:4.3.6:openDefaultRelease@aar'

  2.混淆文件添加
    混淆内容参考proguard-rules.pro

  3.使用：
    1.application中初始化对应的支持的分享平台
      例如：添加微博，qq,微信分享平台
      val platforms = arrayListOf(QQFriendPlatformSetting(/*注册的id*/),QQSpacePlatformSetting(/*注册的id*/),
            WeiBoPlatformSetting(/*appkey*/,/*redirect*/,/*scope*/),WeChatPlatformSetting(/*注册的id*/),WeChatScenePlatformSetting(/*注册的id*/))
      ShareLib.init(platforms)
    2.分享调用：
      ShareUtil.share(activity,/*标题*/，/*文字*/，/*图片*/，/*网址等参数*/)
    3.分享返回：
      ShareUtil.handleIntent(data)在onActivityResult中调用
    4.自定义平台：
      继承PlatformSetting并在ShareLib.init时注入
    5.分享界面自定义
      各平台分享调用接口为对应PlatformSetting的SharePlatformInterface方法中实现
      先调用register，然后调用share方法实现分享


    <activity
        android:name="com.tencent.tauth.AuthActivity"
        android:noHistory="true"
        android:launchMode="singleTask" >
      <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data android:scheme="tencent(你的appId)" />
      </intent-filter>
    </activity>
