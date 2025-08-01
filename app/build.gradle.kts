import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("org.jetbrains.kotlin.kapt")
    id("com.google.gms.google-services")
}

val localProperties = Properties()
localProperties.load(project.rootProject.file("local.properties").inputStream())
val kakaoApiKey = localProperties.getProperty("kakao_NATIVE_APP_KEY")?:""
val nativeAppKey = localProperties.getProperty("kakao_NATIVE_APP_KEY_MANIFEST")?:""

android {
    signingConfigs {
        getByName("debug") {
            keyAlias = localProperties["SIGNED_KEY_ALIAS"] as String?
            keyPassword = localProperties["SIGNED_KEY_PASSWORD"] as String?
            storeFile = localProperties["SIGNED_STORE_FILE"]?.let { file(it) }
            storePassword = localProperties["SIGNED_STORE_PASSWORD"] as String?
        }
    }
    namespace = "com.toyou.toyouandroid"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.toyou.toyouandroid"
        minSdk = 28
        targetSdk = 35
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
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.security:security-crypto-ktx:1.1.0-alpha06")
    implementation("androidx.tracing:tracing-perfetto-handshake:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")

    implementation("com.jakewharton.timber:timber:4.7.1")

    implementation ("com.kakao.sdk:v2-all:2.20.3")

    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation ("com.google.dagger:hilt-android:2.50")
    kapt ("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-messaging-ktx")
    implementation("com.tbuonomo:dotsindicator:5.0")

    implementation("androidx.core:core-splashscreen:1.0.1")
}
apply(plugin = "com.google.gms.google-services")
