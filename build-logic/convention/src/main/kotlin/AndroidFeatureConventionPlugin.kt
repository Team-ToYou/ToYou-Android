import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("toyou.android.library")
                apply("toyou.android.hilt")
                apply("toyou.android.compose")
            }

            extensions.configure<LibraryExtension> {
                defaultConfig {
                    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                }
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "implementation"(project(":core:domain"))
                "implementation"(project(":core:data"))
                "implementation"(project(":core:designsystem"))
                "implementation"(project(":core:common"))
                "implementation"(project(":core:datastore"))
                "implementation"(project(":core:network"))

                "implementation"(libs.findBundle("lifecycle").get())
                "implementation"(libs.findBundle("navigation").get())
                "implementation"(libs.findLibrary("timber").get())
                "implementation"(libs.findLibrary("retrofit").get())
            }
        }
    }
}
