package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSplashBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.utils.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSplashBinding? = null
    private val binding: FragmentSplashBinding
        get() = requireNotNull(_binding){"FragmentSplashBinding -> null"}

    private val loginViewModel: LoginViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val runnableLogin = Runnable {
        // 프래그먼트가 attach된 상태에서만 네비게이션을 수행
        if (isAdded && !isStateSaved) {
            findNavController().navigate(R.id.action_navigation_splash_to_login_fragment)
        }
    }
    private val runnableHome = Runnable {
        if (isAdded && !isStateSaved) {
            findNavController().navigate(R.id.action_navigation_splash_to_home_fragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentSplashBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        val refreshToken = TokenStorage(requireContext()).getRefreshToken()
        val fcmToken = TokenStorage(requireContext()).getFcmToken()

        if (refreshToken != null) {
            loginViewModel.reissueJWT(refreshToken)
        } else {
            handler.postDelayed(runnableLogin, 3000)
        }

        loginViewModel.navigationEvent.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                loginViewModel.patchFcm(fcmToken.toString())
                Timber.tag("fcm token").d(fcmToken.toString())
                Handler(Looper.getMainLooper()).postDelayed({
                    findNavController().navigate(R.id.action_navigation_splash_to_home_fragment)
                }, 1000)
            } else {
                navController.navigate(R.id.action_navigation_splash_to_login_fragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnableLogin)
        handler.removeCallbacks(runnableHome)
        _binding = null
    }
}