package com.toyou.toyouandroid.presentation.fragment.tutorial

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TutorialVPAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> TutorialStep1Fragment()
            1 -> TutorialStep2Fragment()
            2 -> TutorialStep3Fragment()
            3 -> TutorialStep4Fragment()
            4 -> TutorialStep5Fragment()
            else -> TutorialStep1Fragment()
        }
    }

    override fun getCount(): Int {
        // Fragment의 개수
        return 5
    }
}