package com.toyou.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() {
        rule.collect(
            packageName = "com.toyou.toyouandroid",
            stableIterations = 2,
            maxIterations = 8,
        ) {
            // Start the app
            pressHome()
            startActivityAndWait()

            // Wait for the main content to load
            device.wait(Until.hasObject(By.res("nav_home")), 5000)

            // Navigate through main screens
            // 1. Home screen - scroll if content exists
            device.findObject(By.res("home_content"))?.let { homeContent ->
                homeContent.scroll(Direction.DOWN, 0.8f)
                device.waitForIdle()
                homeContent.scroll(Direction.UP, 0.8f)
                device.waitForIdle()
            }

            // 2. Navigate to Social tab
            device.findObject(By.res("nav_social"))?.click()
            device.waitForIdle()
            device.wait(Until.hasObject(By.res("social_content")), 3000)

            // 3. Navigate to Record tab
            device.findObject(By.res("nav_record"))?.click()
            device.waitForIdle()
            device.wait(Until.hasObject(By.res("record_content")), 3000)

            // 4. Navigate to Mypage tab
            device.findObject(By.res("nav_mypage"))?.click()
            device.waitForIdle()
            device.wait(Until.hasObject(By.res("mypage_content")), 3000)

            // 5. Return to Home
            device.findObject(By.res("nav_home"))?.click()
            device.waitForIdle()
        }
    }

    @Test
    fun generateStartupProfile() {
        rule.collect(
            packageName = "com.toyou.toyouandroid",
            includeInStartupProfile = true,
            stableIterations = 2,
            maxIterations = 5,
        ) {
            pressHome()
            startActivityAndWait()

            // Wait for initial content to be displayed
            device.wait(Until.hasObject(By.res("home_content")), 5000)
        }
    }
}
