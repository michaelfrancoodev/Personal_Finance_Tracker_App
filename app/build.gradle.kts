plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // IMPORTANT: Required for Room Database to generate code
    id("kotlin-kapt")
}

android {
    namespace = "com.example.personalfinancetracker"

    // FIXED: Changed from 35 to 36 to match dependency requirements
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.personalfinancetracker"
        minSdk = 24 // Android 7.0+ (keeps device compatibility)
        targetSdk = 35 // Keep at 35 for stability (can update later)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Java 11 is correct for modern Android Studio projects
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- Core Android & Compose ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- 1. EXTENDED ICONS (Crucial for Finance App) ---
    // Adds icons like 'AccountBalance', 'ShoppingBag', 'Restaurant', etc.
    implementation("androidx.compose.material:material-icons-extended:1.7.6")

    // --- 2. VIEWMODEL (MVVM Architecture) ---
    // Allows us to use 'viewModel()' in Compose and handle rotation/state
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // --- 3. ROOM DATABASE (Local Storage) ---
    val roomVersion = "2.6.1"

    // The core database library
    implementation("androidx.room:room-runtime:$roomVersion")

    // 'KTX' adds Coroutines/Flow support (Required for Live Balance updates)
    implementation("androidx.room:room-ktx:$roomVersion")

    // 'Compiler' generates the SQL code automatically (Must match runtime version)
    kapt("androidx.room:room-compiler:$roomVersion")

    // --- 4. SPLASH SCREEN (Beautiful app launch) ---
    implementation("androidx.core:core-splashscreen:1.0.1")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}