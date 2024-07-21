package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSplashBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class SplashFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSplashBinding? = null

    private val binding: FragmentSplashBinding
        get() = requireNotNull(_binding){"FragmentSplashBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        // 3초 후에 다음 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_navigation_splash_to_login_fragment)
        }, 3000)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}