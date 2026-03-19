// Top-level build file for FreeStream Music App
// This is the project-level build configuration

plugins {
    // Android Gradle Plugin - manages Android app building
    id("com.android.application") version "8.2.2" apply false
    
    // Kotlin Android plugin - enables Kotlin support for Android
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    
    // Hilt plugin - dependency injection framework
    id("com.google.dagger.hilt.android") version "2.50" apply false
    
    // Kotlin Serialization plugin - for JSON parsing
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" apply false
}

// Define versions for all modules to use
buildscript {
    extra.apply {
        set("compose_version", "1.6.1")
        set("compose_bom_version", "2024.02.00")
        set("hilt_version", "2.50")
        set("room_version", "2.6.1")
        set("exoplayer_version", "1.2.1")
        set("retrofit_version", "2.9.0")
        set("okhttp_version", "4.12.0")
        set("coil_version", "2.5.0")
        set("coroutines_version", "1.7.3")
    }
}

// Repository configuration for all modules
allprojects {
    repositories {
        google()           // Google's Maven repository (Android libraries)
        mavenCentral()     // Maven Central repository (general libraries)
        maven { url = uri("https://jitpack.io") }  // JitPack for GitHub libraries
    }
}
