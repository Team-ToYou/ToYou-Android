package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentLoginBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.utils.TokenStorage
import com.toyou.toyouandroid.utils.TutorialStorage
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = requireNotNull(_binding){"FragmentLoginBinding -> null"}

    private lateinit var tokenStorage: TokenStorage
    private lateinit var tutorialStorage: TutorialStorage
    private val loginViewModel: LoginViewModel by activityViewModels {
        AuthViewModelFactory(
            NetworkModule.getClient().create(AuthService::class.java),
            tokenStorage
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        tokenStorage = TokenStorage(requireContext())
        tutorialStorage = TutorialStorage(requireContext())

        return binding.root
    }

    @SuppressLint("LogNotTimber")
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

        binding.kakaoLoginBtn.setOnClickListener{
            context?.let { it1 ->
                // 카카오계정으로 로그인 공통 callback 구성
                // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    if (error != null) {
                        Log.e(TAG, "카카오계정으로 로그인 실패", error)
                    } else if (token != null) {
                        Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
                        loginViewModel.setOAuthAccessToken(token.accessToken)
                        loginViewModel.kakaoLogin(token.accessToken)
                       
                        loginViewModel.kakaoLogin(token.accessToken)  // 로그인 성공 시 호출
                    }
                }

                // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
                if (UserApiClient.instance.isKakaoTalkLoginAvailable(it1)) {
                    UserApiClient.instance.loginWithKakaoTalk(it1) { token, error ->
                        if (error != null) {
                            Log.e(TAG, "카카오톡으로 로그인 실패", error)

                            // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                            // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                                return@loginWithKakaoTalk
                            }

                            // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                            UserApiClient.instance.loginWithKakaoAccount(it1, callback = callback)
                        } else if (token != null) {
                            Log.i(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                            loginViewModel.setOAuthAccessToken(token.accessToken)
                            loginViewModel.kakaoLogin(token.accessToken)
                        }
                    }
                } else {
                    UserApiClient.instance.loginWithKakaoAccount(it1, callback = callback)
                }
            }
        }

        loginViewModel.loginSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                checkIfTokenExists()  // 토큰이 저장되었는지 확인 후 이동
            } else {
                navController.navigate(R.id.action_navigation_login_to_signup_agree_fragment)
            }
        }
    }

    private fun checkIfTokenExists() {
        tokenStorage.let { storage ->
            val accessToken = storage.getAccessToken()
            if (accessToken != null) {
                Timber.d("User Info Existed: $accessToken")
                checkTutorial()
            } else {
                // 액세스 토큰이 없으면 회원가입 동의 화면으로 이동
//                navController.navigate(R.id.action_navigation_login_to_signup_agree_fragment)
                Timber.d("User Info Not Existed")
            }
        }
    }

    private fun checkTutorial() {
        if (!tutorialStorage.isTutorialShown()) {
            navController.navigate(R.id.action_navigation_login_to_tutorial_fragment)
            tutorialStorage.setTutorialShown()
        } else {
            // 튜토리얼을 본 적이 있으면 홈 화면으로 바로 이동
            // 액세스 토큰이 있으면 홈 화면으로 이동
            navController.navigate(R.id.action_navigation_login_to_home_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}