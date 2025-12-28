plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.domain"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.timber)

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
