package com.toyou.toyouandroid.presentation.base

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.ActivityMainBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { false }

        Timber.plant(Timber.DebugTree())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding.fragmentContainer.setPadding(
            0,
            this.statusBarHeight(),
            0,
            0
        )

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        // BottomNavigationView 설정
        binding.bottomNavi.setupWithNavController(navController)

        // BottomNavigationView 아이템 선택 리스너 설정
        binding.bottomNavi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_social -> {
                    navController.navigate(R.id.navigation_social)
                    true
                }
                R.id.navigation_record -> {
                    navController.navigate(R.id.navigation_record)
                    true
                }
                R.id.navigation_mypage -> {
                    navController.navigate(R.id.navigation_mypage)
                    true
                }
                else -> false
            }
        }

        askNotificationPermission()

    }

    fun Context.statusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else 0
    }

    fun hideBottomNavigation(state:Boolean){
        if(state) binding.bottomNavi.visibility = View.GONE else binding.bottomNavi.visibility=View.VISIBLE
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("FCM", "알림 권한이 이미 허용되었습니다.")
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "알림 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림 권한을 허용해야 알림을 받을 수 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
}