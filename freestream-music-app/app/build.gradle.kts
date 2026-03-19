/**
 * App-level build configuration for FreeStream Music App
 * 
 * This file defines:
 * - Android SDK versions and app metadata
 * - Build features (Compose, BuildConfig)
 * - All dependencies for the app
 * - Compilation options
 */

plugins {
    // Android application plugin
    id("com.android.application")
    
    // Kotlin Android plugin
    id("org.jetbrains.kotlin.android")
    
    // Hilt for dependency injection
    id("com.google.dagger.hilt.android")
    
    // Kotlin annotation processing (for Room, Hilt)
    id("kotlin-kapt")
    
    // Kotlin serialization for JSON parsing
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    // Application namespace for generated R and BuildConfig classes
    namespace = "com.freestream"
    
    // Compile SDK version - use latest stable
    compileSdk = 34

    defaultConfig {
        // Unique application ID
        applicationId = "com.freestream"
        
        // Minimum SDK - Android 8.0 (covers 95%+ of devices)
        minSdk = 26
        
        // Target SDK - Android 14
        targetSdk = 34
        
        // App version
        versionCode = 1
        versionName = "1.0.0"

        // Test runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Vector drawable support for older devices
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Room schema export location
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas"
                )
            }
        }
    }

    buildTypes {
        // Debug build - for development
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }
        
        // Release build - for distribution
        release {
            isMinifyEnabled = true      // Enable ProGuard/R8 code shrinking
            isShrinkResources = true    // Remove unused resources
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    // Enable Compose build feature
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    // Compose compiler version
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    
    // Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    // Kotlin JVM target
    kotlinOptions {
        jvmTarget = "17"
    }
    
    // Packaging options to resolve conflicts
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/INDEX.LIST"
            excludes += "/META-INF/io.netty.versions.properties"
        }
    }
    
    // Test options
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// Dependency versions
val composeBomVersion = "2024.02.00"
val hiltVersion = "2.50"
val roomVersion = "2.6.1"
val exoplayerVersion = "1.2.1"
val retrofitVersion = "2.9.0"
val okhttpVersion = "4.12.0"
val coilVersion = "2.5.0"
val coroutinesVersion = "1.7.3"
val lifecycleVersion = "2.7.0"

dependencies {
    // ===== Core Android Dependencies =====
    
    // Android KTX - Kotlin extensions for Android framework
    implementation("androidx.core:core-ktx:1.12.0")
    
    // Lifecycle - ViewModel, LiveData, LifecycleScope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    
    // Activity Compose - for using Compose in Activities
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")
    
    // Browser - for opening attribution links
    implementation("androidx.browser:browser:1.7.0")

    // ===== Jetpack Compose Dependencies =====
    
    // Compose BOM - manages compatible Compose versions
    val composeBom = platform("androidx.compose:compose-bom:$composeBomVersion")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.ui:ui-util")
    
    // Material 3 - latest Material Design components
    implementation("androidx.compose.material3:material3:1.2.0")
    
    // Material Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation Compose - for screen navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Hilt Navigation Compose - for ViewModel injection in Compose
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // ===== Networking Dependencies =====
    
    // Retrofit - type-safe HTTP client
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    
    // OkHttp - efficient HTTP client
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    
    // Kotlin Serialization - for JSON parsing
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // ===== Media Playback Dependencies =====
    
    // ExoPlayer (Media3) - audio/video playback
    implementation("androidx.media3:media3-exoplayer:$exoplayerVersion")
    implementation("androidx.media3:media3-exoplayer-dash:$exoplayerVersion")
    implementation("androidx.media3:media3-session:$exoplayerVersion")
    implementation("androidx.media3:media3-ui:$exoplayerVersion")
    implementation("androidx.media3:media3-common:$exoplayerVersion")

    // ===== Image Loading Dependencies =====
    
    // Coil - Kotlin-first image loading
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("io.coil-kt:coil-svg:$coilVersion")

    // ===== Database Dependencies =====
    
    // Room - SQLite ORM
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    
    // DataStore - for preferences storage
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.datastore:datastore-core:1.0.0")

    // ===== Dependency Injection Dependencies =====
    
    // Hilt - compile-time DI framework
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // ===== Coroutines Dependencies =====
    
    // Kotlin Coroutines - for async programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutinesVersion")

    // ===== Testing Dependencies =====
    
    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
    testImplementation("androidx.room:room-testing:$roomVersion")
    
    // Android instrumented testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    
    // Debug testing
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.tracing:tracing:1.2.0")
}

// KAPT configuration
kapt {
    correctErrorTypes = true
    useBuildCache = true
}
