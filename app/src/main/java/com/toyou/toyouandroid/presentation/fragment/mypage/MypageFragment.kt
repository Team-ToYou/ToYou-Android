package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel

class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}
    private val nicknameViewModel: SignupNicknameViewModel by viewModels()
    private val viewModel: MypageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
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

        binding.mypageOpinion.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_opinion_fragment)
        }

        binding.mypageContact.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_contact_fragment)
        }

        binding.mypageTermsOfUse.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_terms_of_use_fragment)
        }

        binding.mypageVersion.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_version_fragment)
        }

        // ViewModel에서 닉네임을 가져와서 TextView에 설정
        nicknameViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}