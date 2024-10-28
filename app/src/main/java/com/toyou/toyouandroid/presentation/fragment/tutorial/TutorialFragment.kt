package com.toyou.toyouandroid.presentation.fragment.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentTutorialBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class TutorialFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentTutorialBinding? = null
    private val binding: FragmentTutorialBinding
        get() = requireNotNull(_binding){"FragmentTutorialBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentTutorialBinding.inflate(inflater, container, false)

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

        binding.tutorialSkipBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_tutorial_to_home_fragment)
        }

        val viewPager = binding.viewPager
        val wormDotsIndicator = binding.dotsIndicator

        val adapter = TutorialVPAdapter(childFragmentManager)
        viewPager.adapter = adapter

        wormDotsIndicator.attachTo(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}