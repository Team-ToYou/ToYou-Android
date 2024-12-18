package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.databinding.FragmentSendFinalBinding
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage

class SendFinalFragment: Fragment() {
    private var _binding : FragmentSendFinalBinding? = null
    private val binding : FragmentSendFinalBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController

    private lateinit var socialViewModel : SocialViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val socialService = AuthNetworkModule.getClient().create(SocialService::class.java)
        val socialRepository = SocialRepository(socialService)
        val fcmService = AuthNetworkModule.getClient().create(FCMService::class.java)
        val fcmRepository = FCMRepository(fcmService)

        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(socialRepository, tokenManager, fcmRepository)
        )[SocialViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendFinalBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        Handler(Looper.getMainLooper()).postDelayed({
            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(false)
            navController.navigate(R.id.action_sendFinalFragment_to_navigation_home)
        }, 2000)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}