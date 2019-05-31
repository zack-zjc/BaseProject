集成：
  1.依赖添加：(版本可提升)
    implementation 'com.android.support:appcompat-v7:27.1.1'
    implementation 'com.just.agentweb:agentweb:4.0.2'
    implementation project(':BaseModule') //扫码跳转和弹窗
    implementation project(':NetWorkModule') //网络请求
    implementation project(':ShareModule') //分享功能

  2.混淆文件添加
       混淆内容参考proguard-rules.pro

  3.使用：
      1.application中初始化
          WebLib.init(setting)
      2.文件说明：
         WebSetting:关于web的设置，ua,jsInterface等自定义界面
         ActBaseWebView，ActWebView展示webview的类
         参数：intent_url网页地址
              page_name 网页的名称
              show_more 是否展示更多操作按钮
              show_share 是否展示分享操作