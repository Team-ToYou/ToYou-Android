plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.common"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle for MVI base
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
}
