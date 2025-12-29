plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.domain"
}

dependencies {
    implementation(project(":core:common"))

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
}
