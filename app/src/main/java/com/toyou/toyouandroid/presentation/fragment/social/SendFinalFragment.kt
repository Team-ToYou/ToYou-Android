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
import com.toyou.toyouandroid.databinding.FragmentSendFinalBinding
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
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

        socialViewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(tokenManager)
        )[SocialViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendFinalBinding.inflate(inflater, container, false)

        val translate: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.translate)

        binding.cardIv.startAnimation(translate)

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