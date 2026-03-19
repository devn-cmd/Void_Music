// Settings file for FreeStream Music App
// Defines project structure and plugin repositories

pluginManagement {
    repositories {
        google()                    // Android plugin repository
        mavenCentral()              // General plugin repository
        gradlePluginPortal()        // Gradle's official plugin portal
    }
}

dependencyResolutionManagement {
    // Use strict repository mode - prevents accidental usage of unexpected repositories
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    
    repositories {
        google()                    // Google's Maven repository
        mavenCentral()              // Maven Central repository
        maven { url = uri("https://jitpack.io") }  // JitPack for additional libraries
    }
}

// Root project name
rootProject.name = "FreeStream"

// Include the app module
include(":app")
