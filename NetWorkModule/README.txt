集成：
  1.依赖添加：(版本可提升)
    implementation 'com.squareup.okhttp3:okhttp:3.13.1'
    implementation 'com.alibaba:fastjson:1.1.46.android'

  2.混淆文件添加
    混淆内容参考proguard-rules.pro

  3.使用：
    1.application中初始化
      HttpClientLib.init(setting)
    2.网络请求：
      HttpClientUtil.fetch(options:JSONObject): MutableMap<String, Any?>
      返回数据结构：
        result["code"] = 返回对应的http请求码或对应的错误码
        result["headers"] = 返回请求的header信息
        result["data"] = 返回对应的数据
      <T> fetch(options:JSONObject,responseType:Class<T>) : MutableMap<String, Any?>
       返回数据结构：
         result["code"] = 返回对应的http请求码或对应的错误码
         result["headers"] = 返回请求的header信息
         result["data"] = 返回学序列化的T对象
    3.打开网络调试Log
      HttpClientLib.setMode(true)
    4.setting:HttpSetting关于httpClient的设置
        ClientSettingInterface请求OKHTTPCLIENT的设置
        解析器继承ContentEncoder，自定义body解析器
        解析器继承ResponseDecoder，自定义返回数据decoder
        handler继承ErrorHandler，自定义错误提示处理
