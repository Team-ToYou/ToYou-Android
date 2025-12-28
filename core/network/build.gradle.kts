plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.network"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.bundles.network)
    implementation(libs.timber)
}
