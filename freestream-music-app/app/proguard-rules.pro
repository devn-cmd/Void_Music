# ProGuard Rules for FreeStream Music App
# These rules preserve code that should not be obfuscated in release builds

# ===== General Android Rules =====

# Keep line numbers for debugging stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep annotations
-keepattributes *Annotation*

# Keep exceptions
-keepattributes Exceptions

# Keep signature
-keepattributes Signature

# Keep inner classes
-keepattributes InnerClasses,EnclosingMethod

# ===== Kotlin Rules =====

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepclassmembers class **$WhenMappings { *; }

# Keep Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ===== Compose Rules =====

# Keep Compose functions
-keepclassmembers class * {
    @androidx.compose.runtime.Composable <methods>;
}

# Keep Compose preview functions
-keepclassmembers class * {
    @androidx.compose.ui.tooling.preview.Preview <methods>;
}

# ===== Retrofit / Gson Rules =====

# Keep Retrofit interfaces
-keep interface * { @retrofit2.http.* <methods>; }

# Keep Gson models
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep generic signatures
-keepattributes Signature

# Keep Retrofit response classes
-keep class retrofit2.Response { *; }

# Keep DTO classes (all classes in dto packages)
-keep class com.freestream.data.remote.dto.** { *; }

# ===== Room Rules =====

# Keep Room entities
-keep class com.freestream.data.local.entity.** { *; }

# Keep Room DAOs
-keep class com.freestream.data.local.database.*Dao { *; }

# Keep Room database
-keep class com.freestream.data.local.database.AppDatabase { *; }

# ===== Hilt Rules =====

# Keep Hilt components
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Keep Hilt entry points
-keep class * implements dagger.hilt.internal.GeneratedEntryPoint { *; }

# Keep @HiltAndroidApp
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }

# Keep @AndroidEntryPoint
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# ===== ExoPlayer Rules =====

# Keep ExoPlayer classes
-keep class com.google.android.exoplayer2.** { *; }
-keep class androidx.media3.** { *; }

# ===== Serializable Rules =====

# Keep Kotlin serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep serializers
-keep class * implements kotlinx.serialization.KSerializer { *; }

# ===== Enum Rules =====

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ===== Data Class Rules =====

# Keep data classes
-keep class com.freestream.data.model.** { *; }

# ===== OkHttp Rules =====

# Keep OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# ===== Coil Rules =====

# Keep Coil
-keep class coil.** { *; }

# ===== Coroutines Rules =====

# Keep coroutines
-keep class kotlinx.coroutines.** { *; }

# ===== Remove Log Calls in Release =====

# Remove Log calls in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
