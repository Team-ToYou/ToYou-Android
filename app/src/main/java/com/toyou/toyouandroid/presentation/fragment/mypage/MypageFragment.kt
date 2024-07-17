package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.mypageBackBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_home_fragment)
        }

        binding.mypageProfileBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_profile_fragment)
        }

        binding.mypageNoticeSetting.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_notice_setting_fragment)
        }

        binding.mypageContact.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_contact_fragment)
        }

        binding.mypageVersion.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_version_fragment)
        }

        binding.mypageTermsOfUse.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_terms_of_use_fragment)
        }

        binding.mypagePrivacyPolicy.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_privacy_policy_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}