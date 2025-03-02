package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSignupstatusBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.data.onboarding.dto.request.SignUpRequest
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.AuthViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.LoginViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import com.toyou.toyouandroid.utils.TutorialStorage
import timber.log.Timber

class SignupStatusFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupstatusBinding? = null
    private val binding: FragmentSignupstatusBinding
        get() = requireNotNull(_binding){"FragmentSignupstatusBinding -> null"}

    private val signUpStatusViewModel: SignupStatusViewModel by activityViewModels()
    private val signupNicknameViewModel: SignupNicknameViewModel by activityViewModels()

    private lateinit var tutorialStorage: TutorialStorage

    /*private val loginViewModel: LoginViewModel by activityViewModels{
        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        AuthViewModelFactory(authService, tokenStorage, tokenManager)
    }*/
    private val loginViewModel: LoginViewModel by activityViewModels{
        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val fcmService = AuthNetworkModule.getClient().create(FCMService::class.java)
        val fcmRepository = FCMRepository(fcmService)

        LoginViewModelFactory(authService, tokenStorage, tokenManager,fcmRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupstatusBinding.inflate(inflater, container, false)

        binding.viewModel = signUpStatusViewModel
        binding.lifecycleOwner = this

        tutorialStorage = TutorialStorage(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        val buttonList = listOf(
            binding.signupStatusOption1,
            binding.signupStatusOption2,
            binding.signupStatusOption3,
            binding.signupStatusOption4
        )

        buttonList.forEach { button ->
            button.setOnClickListener {
                signUpStatusViewModel.onButtonClicked(button.id)
                updateButtonBackgrounds()
            }
        }

        signUpStatusViewModel.selectedButtonId.observe(viewLifecycleOwner) {
            updateButtonBackgrounds()
        }

        signUpStatusViewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupStatusCompleteBtn.isEnabled = isEnabled
        }

        binding.signupStatusBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_signup_status_to_signup_nickname_fragment)
        }

        // 회원가입 API 호출
        binding.signupStatusCompleteBtn.setOnClickListener {
            val nickname = signupNicknameViewModel.nickname.value ?: ""
            val status = signUpStatusViewModel.status.value ?: ""
            val accessToken = loginViewModel.oAuthAccessToken.value ?: ""

            val signUpRequest = SignUpRequest(
                adConsent = true,
                nickname = nickname,
                status = status
            )

            Timber.d("nickname: $nickname, status: $status, accessToken: $accessToken, signUpRequest: $signUpRequest")

            // request body 구성 후 login viewmodel 회원가입 api 호출
            loginViewModel.signUp(signUpRequest)

            navController.navigate(R.id.action_navigation_signup_status_to_tutorial_fragment)
            tutorialStorage.setTutorialShown()
        }
    }

    private fun updateButtonBackgrounds() {
        val buttonList = listOf(
            binding.signupStatusOption1,
            binding.signupStatusOption2,
            binding.signupStatusOption3,
            binding.signupStatusOption4
        )
        buttonList.forEach { button ->
            button.setBackgroundResource(signUpStatusViewModel.getButtonBackground(button.id))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}