package com.toyou.toyouandroid.di

import androidx.fragment.app.FragmentActivity
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.HomeResultFragment
import com.toyou.toyouandroid.presentation.fragment.home.HomeFragment
import javax.inject.Inject

class NavigatorImpl @Inject constructor(
    private val activity: FragmentActivity
) : FragmentNavigator {

    override fun navigateToResultFragment() {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeResultFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun navigateToHomeFragment() {
        activity.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
}
