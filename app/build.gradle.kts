import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("org.jetbrains.kotlin.kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
    alias(libs.plugins.baselineprofile)
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())
val kakaoApiKey = localProperties.getProperty("kakao_NATIVE_APP_KEY")?:""
val nativeAppKey = localProperties.getProperty("kakao_NATIVE_APP_KEY_MANIFEST")?:""

android {
    signingConfigs {
        getByName("debug") {
            val storeFilePath = localProperties["SIGNED_STORE_FILE"] as String?
            if (storeFilePath != null) {
                keyAlias = localProperties["SIGNED_KEY_ALIAS"] as String?
                keyPassword = localProperties["SIGNED_KEY_PASSWORD"] as String?
                storeFile = file(storeFilePath)
                storePassword = localProperties["SIGNED_STORE_PASSWORD"] as String?
            }
        }
    }
    namespace = "com.toyou.toyouandroid"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.toyou.toyouandroid"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 12
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "kakao_NATIVE_APP_KEY", "\"$kakaoApiKey\"")
        manifestPlaceholders["NATIVE_APP_KEY"] = nativeAppKey
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["appName"] = "@string/app_name"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_toyou"
            manifestPlaceholders["roundAppIcon"] = "@mipmap/ic_toyou_round"
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            manifestPlaceholders["appName"] = "@string/app_name_debug"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_toyou_debug"
            manifestPlaceholders["roundAppIcon"] = "@mipmap/ic_toyou_debug_round"
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

dependencies {
    // Core Modules
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:domain"))
    implementation(project(":core:data"))
    implementation(project(":core:designsystem"))

    // Feature Modules
    implementation(project(":feature:home"))
    implementation(project(":feature:create"))
    implementation(project(":feature:social"))
    implementation(project(":feature:record"))
    implementation(project(":feature:notice"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:mypage"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.security.crypto)

    // Lifecycle
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.lifecycle.viewmodel)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Network
    implementation(libs.bundles.network)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.bundles.room)
    kapt(libs.androidx.room.compiler)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.messaging)

    // Kakao
    implementation(libs.kakao.sdk.all)

    // Others
    implementation(libs.timber)
    implementation(libs.dotsIndicator)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.compose)
    implementation(libs.hilt.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)

    // Baseline Profile
    implementation(libs.androidx.profileinstaller)
    baselineProfile(project(":baselineprofile"))
}
apply(plugin = "com.google.gms.google-services")
