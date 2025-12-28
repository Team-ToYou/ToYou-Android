package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSendFinalBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendFinalFragment: Fragment() {
    private var _binding : FragmentSendFinalBinding? = null
    private val binding : FragmentSendFinalBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController

    private val socialViewModel: SocialViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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