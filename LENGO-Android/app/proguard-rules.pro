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

-verbose
-allowaccessmodification
-repackageclasses

-keepattributes LineNumberTable,SourceFile
-renamesourcefileattribute SourceFile

-keepclasseswithmembernames class * {
    native <methods>;
}

# We only need to keep ComposeView + FragmentContainerView
-keep public class androidx.compose.ui.platform.ComposeView {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# AndroidX + support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**
-dontwarn androidx.**

-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                AnnotationDefault
-renamesourcefileattribute SourceFile

-dontwarn org.conscrypt.**

# Dagger
-dontwarn com.google.errorprone.annotations.*

-keepattributes *Annotation*
-dontwarn sun.misc.**

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

# Retain annotation default values for all annotations.
# Required until R8 version >= 3.1.12-dev (expected in AGP 7.1.0-alpha4).
#-keep,allowobfuscation,allowshrinking @interface *

-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE


-keep,allowobfuscation,allowshrinking class com.squareup.moshi.JsonAdapter

# common
-keep class com.lengo.common.** {
   *;
}
-keep class com.lengo.data.** {
   *;
}
-keep class com.lengo.database.** {
   *;
}
-keep class com.lengo.modalsheet.** {
   *;
}
-keep class com.lengo.network.** {
   *;
}
-keep class com.lengo.preferences.** {
   *;
}
-keep class com.lengo.model.** {
   *;
}
-keep class com.lengo.model.data.network.** {
   *;
}
-keep class com.lengo.model.data.quiz.** {
   *;
}


-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

-printusage build/outputs/mapping/release/remove/usage.txt