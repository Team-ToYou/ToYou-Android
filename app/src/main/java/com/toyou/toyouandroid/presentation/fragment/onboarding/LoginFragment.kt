package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
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
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentLoginBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.notice.network.NetworkModule
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthService
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = requireNotNull(_binding){"FragmentLoginBinding -> null"}

    private lateinit var tokenStorage: TokenStorage
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
                UserApiClient.instance.loginWithKakaoTalk(it1) { token, error ->
                    if (error != null) {
                        Timber.tag(TAG).e(error, "로그인 실패")
                    } else if (token != null) {
                        Log.i(TAG, "로그인 성공 ${token.accessToken}")
                        loginViewModel.setOAuthAccessToken(token.accessToken)
                        loginViewModel.kakaoLogin(token.accessToken)
                        //loginViewModel.sendTokenToServer(tokenStorage.getFcmToken() ?: "")
                        Log.d("fcm토큰!", tokenStorage.getFcmToken().toString())
                    }
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
            if (accessToken != "") {
                // 액세스 토큰이 있으면 홈 화면으로 이동
                navController.navigate(R.id.action_navigation_login_to_home_fragment)
                Timber.d("User Info Existed")
            } else {
                // 액세스 토큰이 없으면 회원가입 동의 화면으로 이동
                navController.navigate(R.id.action_navigation_login_to_signup_agree_fragment)
                Timber.d("User Info Not Existed")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}