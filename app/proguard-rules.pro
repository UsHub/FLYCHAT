# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontoptimize       #优化不优化输入的类文件
-dontpreverify      #混淆时是否做预校验
-keepattributes  EnclosingMethod,Signature      #不混淆泛型
-dontwarn cn.jpush.**       #忽略某个包的警告
-keep class cn.jpush.** { *; }    #不混淆某个包内的所有文件

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.jmessage.**
-keep class cn.jmessage.**{ *; }

-keepclassmembers class ** {        #不混淆资源类
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
-keep class com.google.protobuf.** {*;}