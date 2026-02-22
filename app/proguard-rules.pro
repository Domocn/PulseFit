# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Firestore model classes
-keep class com.pulsefit.app.data.remote.model.** { *; }
-keep class com.pulsefit.app.data.remote.CloudProfile { *; }
-keep class com.pulsefit.app.data.remote.PublicProfile { *; }
-keep class com.pulsefit.app.data.remote.SharedWorkout { *; }
-keep class com.pulsefit.app.data.remote.FriendRequest { *; }
-keep class com.pulsefit.app.data.remote.FriendEntry { *; }

# Room entities
-keep class com.pulsefit.app.data.local.entity.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Health Connect
-keep class androidx.health.connect.** { *; }

# Lottie
-dontwarn com.airbnb.lottie.**
-keep class com.airbnb.lottie.** { *; }

# Kotlin
-keep class kotlin.Metadata { *; }
-keepclassmembers class * {
    @kotlin.Metadata *;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
