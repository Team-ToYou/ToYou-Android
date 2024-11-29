package com.toyou.toyouandroid.presentation.fragment.mypage

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.AuthViewModelFactory
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager
import com.toyou.toyouandroid.utils.TokenStorage
import com.toyou.toyouandroid.utils.TutorialStorage
import timber.log.Timber

class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}

    private lateinit var viewModelManager: ViewModelManager

    private val nicknameViewModel: SignupNicknameViewModel by activityViewModels()
    private val mypageDialogViewModel: MypageDialogViewModel by activityViewModels()
    private var mypageDialog: MypageDialog? = null

    private lateinit var homeViewModel: HomeViewModel

    private var sharedPreferences: SharedPreferences? = null

    private val mypageViewModel: MypageViewModel by activityViewModels {
        val tokenStorage = TokenStorage(requireContext())
        val authService: AuthService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val authService2: AuthService = AuthNetworkModule.getClient().create(AuthService::class.java)
        AuthViewModelFactory(authService2, tokenStorage, tokenManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService: AuthService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(tokenManager)
        )[HomeViewModel::class.java]

        sharedPreferences = requireActivity().getSharedPreferences("FCM_PREFERENCES", Context.MODE_PRIVATE)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        viewModelManager = ViewModelManager(nicknameViewModel, homeViewModel)
        mypageViewModel.updateMypage()

        binding.mypageProfileBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_profile_fragment) // 프로필 화면으로 이동
        }

        binding.mypageNoticeSetting.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_notice_setting_fragment)
        }

        binding.mypageNoticeSettingBtn.setOnClickListener{
            navController.navigate(R.id.action_navigation_mypage_to_notice_setting_fragment)
        }

        binding.mypageOpinion.setOnClickListener {
            redirectLink("https://forms.gle/fJweAP16cT4Tc3cA6")
        }

        binding.mypageOpinionBtn.setOnClickListener{
            redirectLink("https://forms.gle/fJweAP16cT4Tc3cA6")
        }

        binding.mypageContact.setOnClickListener {
            redirectLink("http://pf.kakao.com/_xiuPIn/chat")
        }

        binding.mypageContactBtn.setOnClickListener{
            redirectLink("http://pf.kakao.com/_xiuPIn/chat")
        }

        binding.mypageTermsOfUse.setOnClickListener {
            redirectLink("https://sumptuous-metacarpal-d3a.notion.site/1437c09ca64e80fb88f6d8ab881ffee3")
        }

        // 사용자 닉네임 설정
        nicknameViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }

        // 사용자 친구 수 설정
        mypageViewModel.friendNum.observe(viewLifecycleOwner) {friendNum ->
            val friendText = if (friendNum != null) {
                "친구 ${friendNum}명"
            } else {
                "친구 0명"
            }
            binding.profileFriendCount.text = friendText
        }

        // 닉네임 변경시 프로필에 반영
        mypageViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }

        // 로그아웃 성공시 로그인 화면으로 이동
        mypageViewModel.logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                mypageViewModel.setLogoutSuccess(false)
                viewModelManager.resetAllViewModels()

                Timber.d("로그아웃 후 로그인 화면으로 이동")

                navController.navigate(R.id.action_navigation_mypage_to_login_fragment)
            }
        }

        // 회원 탈퇴 성공시 로그인 화면으로 이동
        mypageViewModel.signOutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                mypageViewModel.setSignOutSuccess(false)
                viewModelManager.resetAllViewModels()

                navController.navigate(R.id.action_navigation_mypage_to_login_fragment)
            }
        }

        binding.mypageSignoutBtn.setOnClickListener {
            mypageDialogViewModel.setDialogData(
                title = "정말 탈퇴하시겠어요?",
                subTitle = "작성하신 일기카드가 모두\n삭제되며 복구할 수 없어요",
                leftButtonText = "탈퇴하기",
                rightButtonText = "취소",
                leftButtonTextColor = Color.RED,
                rightButtonTextColor = Color.BLACK,
                leftButtonClickAction = { handleSignout() },
                rightButtonClickAction = { dismissDialog() }
            )
            mypageDialog = MypageDialog()
            mypageDialog?.show(parentFragmentManager, "CustomDialog")

        }

        binding.mypageLogoutBtn.setOnClickListener {
            mypageDialogViewModel.setDialogData(
                title = "정말 로그아웃하시겠어요?",
                subTitle = "",
                leftButtonText = "취소",
                rightButtonText = "로그아웃",
                leftButtonTextColor = Color.BLACK,
                rightButtonTextColor = Color.RED,
                leftButtonClickAction = { dismissDialog() },
                rightButtonClickAction = { handleLogout() }
            )
            mypageDialog = MypageDialog()
            mypageDialog?.show(parentFragmentManager, "CustomDialog")
        }
    }

    // 회원 탈퇴
    private fun handleSignout() {
        Timber.tag("handleSignout").d("handleSignout")

        UserApiClient.instance.unlink { error ->
            if (error != null) {
                Timber.tag(TAG).e(error, "연결 끊기 실패")
            }
            else {
                Timber.tag(TAG).i("연결 끊기 성공. SDK에서 토큰 삭제 됨")
            }
        }

        // 회원 탈퇴 후 튜토리얼 다시 보이도록 설정
        TutorialStorage(requireContext()).setTutorialNotShown()
        sharedPreferences?.edit()?.putBoolean("isSubscribed", true)?.apply()

        mypageViewModel.kakaoSignOut()
        mypageDialog?.dismiss()
    }

    // 회원 로그아웃
    private fun handleLogout() {
        Timber.tag("handleLogout").d("handleWithdraw")

        UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.tag(TAG).e(error, "로그아웃 실패. SDK에서 토큰 삭제됨")
            }
            else {
                Timber.tag(TAG).i("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }

        mypageViewModel.kakaoLogout()
        mypageDialog?.dismiss()
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        mypageDialog?.dismiss()
    }

    private fun redirectLink(uri: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(uri)
        startActivity(i)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}