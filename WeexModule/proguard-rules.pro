#-obfuscationdictionary dictionary.txt

#-library dyuproject
-optimizationpasses 7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
#添加java关键字做混淆字典
#-obfuscationdictionary proguard-dictionary.txt
#-classobfuscationdictionary proguard-dictionary.txt
#-packageobfuscationdictionary proguard-dictionary.txt
-keeppackagenames java.util.**
-dontwarn java.util.**
-dontwarn sun.reflect.**
-dontwarn sun.misc.**
-dontwarn org.apache.log4j.**
-dontwarn gov.nist.javax.sip.**
-dontwarn javax.**
-dontwarn org.joda.**
-dontwarn org.w3c.dom.**
-dontwarn com.jcraft.**
-dontwarn sun.security.**
-dontwarn org.jaudiotagger.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
# 混淆采用的算法
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 保留注解等不混淆
-keepattributes Exceptions,InnerClasses,Deprecated,Signature,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
# dump.txt文件列出apk包内所有class的内部结构
-dump class_files.txt
# seeds.txt文件列出未混淆的类和成员
-printseeds seeds.txt
# usage.txt文件列出从apk中删除的代码
-printusage unused.txt
# mapping.txt文件列出混淆前后的映射
-printmapping mapping.txt
#设置源文件中给定的字符串常量
-renamesourcefileattribute SourceFile
#优化时允许访问并修改类和类的成员的 访问修饰符，可能作用域会变大
-mergeinterfacesaggressively
#过度加载，多个属性和方法使用相同的名字，只是参数和返回类型不同 可能各种异常
#-overloadaggressively
# For retrolambda
-dontwarn java.lang.invoke.*
-keep class org.json.** {*;}
#关闭 Log日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
    public static *** w(...);
}
# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 保留在Activity中的方法参数是view的方法，从而我们在layout里面编写onClick就不会被影响
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
# 枚举类不能被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
# 保留Parcelable序列化的类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
# 保留Serializable序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 对于R（资源）下的所有类及其方法，都不能被混淆
-keep class **.R$* {
    *;
}
# webview混淆
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
    public *;
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.webViewClient {
    public void *(android.webkit.webView, jav.lang.String);
}
#避免混淆自定义控件类的 get/set 方法和构造函数
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#support v7
-dontwarn android.support.v7.**
-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
#support v4
-dontwarn android.support.v4.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v4.** { *; }
#不混淆类
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}

#忽略警告
-ignorewarnings

#kotlin协程保持fix java.lang.IllegalStateException: Module with the Main dispatcher is missing
# ServiceLoader support
-keep class kotlinx.coroutines.internal.MainDispatcherFactory {*;}
-keep class kotlinx.coroutines.CoroutineExceptionHandler {*;}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler {*;}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
#kotlin协程保持-end

#weex混淆 start
-keep class com.taobao.weex.bridge.** { *; }
-keep class com.taobao.weex.layout.** { *; }
-keep class com.taobao.weex.WXSDKEngine { *; }
-keep class com.taobao.weex.base.SystemMessageHandler { *; }
-dontwarn com.taobao.weex.bridge.**
-keep public class * extends com.taobao.weex.common.WXModule{*;}
-keep public class * extends com.taobao.weex.ui.component.WXComponent{*;}
-keep class com.taobao.weex.WXDebugTool{*;}
-keep class com.taobao.weex.devtools.common.LogUtil{*;}
-keepclassmembers class ** {
  @com.taobao.weex.ui.component.WXComponentProp public *;
}
-keep class com.taobao.weex.bridge.**{*;}
-keep class com.taobao.weex.dom.**{*;}
-keep class com.taobao.weex.adapter.**{*;}
-keep class com.taobao.weex.common.**{*;}
-keep class * implements com.taobao.weex.IWXObject{*;}
-keep class com.taobao.weex.ui.**{*;}
-keep class com.taobao.weex.ui.component.**{*;}
-keep class com.taobao.weex.utils.**{
    public <fields>;
    public <methods>;}
-keep class com.taobao.weex.view.**{*;}
-keep class com.taobao.weex.module.**{*;}
-dontwarn com.taobao.weex.bridge.**
-dontwarn com.taobao.prettyfish.**
-dontwarn com.taobao.weex.analyzer.**
-keep class com.taobao.** {*;}
-dontwarn com.taobao.**
#weex混淆 end

#okhttp3
-keep class com.squareup.okhttp3.** { *;}
-keep class okhttp3.Cache { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn okhttp3.**
-dontwarn okio.**

#cookie-jar -start
-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
#cookie-jar -end

#fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#glide混淆规则start
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule
#glide混淆规则end