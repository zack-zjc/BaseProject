集成：
  1.依赖添加：(版本可提升)
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.taobao.android:weex_sdk:0.20.3.0-beta@aar'
    implementation project(':BaseModule') //glide加载图片依赖
    implementation project(':NetWorkModule') //网络请求依赖

  2.混淆文件添加
     混淆内容参考proguard-rules.pro

  3.使用：
    1.application中初始化
        WeexLib.init(application: Application,weexInterface:WeexInterface当前默认集成的几个module类的方法)
    2.文件说明：
        WXLCEventModule   注册名 lc_function  功能：挑选图片
        WXLCPageModule    注册名 lc_page      功能：设置界面样式
        WXLCStreamModule  注册名 lc_stream    功能：实现httpclient网络请求代理