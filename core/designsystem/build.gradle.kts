plugins {
    id("toyou.android.library")
    id("toyou.android.compose")
}

android {
    namespace = "com.toyou.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
}
