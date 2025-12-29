plugins {
    id("toyou.android.feature")
}

android {
    namespace = "com.toyou.feature.notice"
}

dependencies {
    // Feature specific dependencies
    implementation(project(":feature:social"))
}
