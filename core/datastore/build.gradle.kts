plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.datastore"
}

dependencies {
    implementation(project(":core:common"))

    implementation(libs.androidx.datastore)
    implementation(libs.androidx.security.crypto)
    implementation(libs.timber)
}
