pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = java.net.URI("https://devrepo.kakao.com/nexus/content/groups/public/") }
    }
}

rootProject.name = "ToYou"

// App Module
include(":app")

// Baseline Profile
include(":baselineprofile")

// Core Modules
include(":core:common")
include(":core:network")
include(":core:datastore")
include(":core:domain")
include(":core:data")
include(":core:designsystem")

// Feature Modules
include(":feature:home")
include(":feature:social")
include(":feature:record")
include(":feature:mypage")
include(":feature:onboarding")
include(":feature:create")
include(":feature:notice")
