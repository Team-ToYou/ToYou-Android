package com.toyou.toyouandroid.presentation.fragment.mypage

import android.content.ContentValues.TAG
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager
import timber.log.Timber

class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}
    private val nicknameViewModel: SignupNicknameViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val viewModel: MypageViewModel by viewModels()
    private lateinit var viewModelManager: ViewModelManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewModelManager = ViewModelManager(nicknameViewModel, homeViewModel)

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

        binding.mypageLogoutBtn.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Timber.tag(TAG).e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
                }
                else {
                    Timber.tag(TAG).i("로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
            viewModelManager.resetAllViewModels()
//            resetApp()
            navController.navigate(R.id.action_navigation_mypage_to_login_fragment)
        }

        binding.mypageSignoutBtn.setOnClickListener {
            activity?.finishAffinity()
        }

        // ViewModel에서 닉네임을 가져와서 TextView에 설정
        nicknameViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }

        homeViewModel.mypageEmotionStamp.observe(viewLifecycleOwner) { emotion ->
            binding.mypageEmotionStamp.setImageResource(emotion)
        }
    }

    private fun resetApp() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navController.graph = navGraph
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}