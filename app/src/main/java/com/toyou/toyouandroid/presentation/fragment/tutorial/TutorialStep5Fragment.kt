package com.toyou.toyouandroid.presentation.fragment.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.databinding.FragmentTutorialStep5Binding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialStep5Fragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentTutorialStep5Binding? = null
    private val binding: FragmentTutorialStep5Binding
        get() = requireNotNull(_binding){"FragmentTutorialStep5Binding -> null"}

    // 튜토리얼 완료 후 화면 백스택 제거를 위한 옵션
    private val navOptions by lazy {
        NavOptions.Builder()
            .setPopUpTo(R.id.navigation_home, true)
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTutorialStep5Binding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        binding.tutorialCompleteBtn.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_4s))

        binding.tutorialCompleteBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_tutorial_to_home_fragment, null, navOptions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}