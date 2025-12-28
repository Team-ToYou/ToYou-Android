plugins {
    id("toyou.android.library")
    id("toyou.android.hilt")
}

android {
    namespace = "com.toyou.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:network"))
    implementation(project(":core:datastore"))
    implementation(project(":core:domain"))

    implementation(libs.bundles.network)
    implementation(libs.timber)
}
