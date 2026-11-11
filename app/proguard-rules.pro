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


# --- Atributos que Retrofit/Gson necesitan ---
-keepattributes Signature
-keepattributes *Annotation*

# --- OkHttp / Okio / Retrofit ---
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Mantener interfaces/clases donde declaras tus endpoints de Retrofit
# (si las tienes en otro paquete, ajusta el prefijo)
-keep interface com.mozama.catalogossat.** { *; }

# --- Gson: campos anotados con @SerializedName en cualquier paquete ---
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Clases anotadas con @Keep ---
-keep @androidx.annotation.Keep class * { *; }

# --- Tus modelos JSON reales (paquete correcto) ---
-keep class com.mozama.impuestos.data.model.** { *; }

# Huawei Ads
-keep class com.huawei.openalliance.ad.** { *; }
-keep class com.huawei.hms.ads.** { *; }

# Huawei Ads / AppGallery optional classes
-dontwarn com.huawei.android.app.PackageManagerEx
-dontwarn com.huawei.appgallery.log.LogAdaptor