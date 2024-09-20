package com.toyou.toyouandroid.presentation.fragment.mypage

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthService
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}
    private val nicknameViewModel: SignupNicknameViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val mypageDialogViewModel: MypageDialogViewModel by activityViewModels()
    private lateinit var viewModelManager: ViewModelManager
    private var mypageDialog: MypageDialog? = null
    private lateinit var tokenStorage: TokenStorage

    private val mypageViewModel: MypageViewModel by activityViewModels {
        AuthViewModelFactory(
            AuthNetworkModule.getClient().create(AuthService::class.java),
            tokenStorage
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        tokenStorage = TokenStorage(requireContext())

        binding.viewModel = mypageViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        viewModelManager = ViewModelManager(nicknameViewModel, homeViewModel)
        mypageViewModel.updateMypage()

        binding.mypageProfileBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_profile_fragment)
        }

        binding.mypageNoticeSetting.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_notice_setting_fragment)
        }

        binding.mypageOpinion.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("https://forms.gle/fJweAP16cT4Tc3cA6")
            startActivity(i)
        }

        binding.mypageContact.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse("http://pf.kakao.com/_xiuPIn/chat")
            startActivity(i)
        }

        binding.mypageTermsOfUse.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_terms_of_use_fragment)
        }

        // ViewModel에서 닉네임을 가져와서 TextView에 설정
        nicknameViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }

        mypageViewModel.friendNum.observe(viewLifecycleOwner) {friendNum ->
            val friendText = if (friendNum != null) {
                "친구 ${friendNum}명"
            } else {
                "친구 0명"
            }
            binding.profileFriendCount.text = friendText
        }

        mypageViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.profileNickname.text = nickname
        }

        binding.mypageSignoutBtn.setOnClickListener {
            mypageDialogViewModel.setDialogData(
                title = "정말 탈퇴하시겠어요?",
                subTitle = "탈퇴 시, 모든 정보가 사라집니다",
                leftButtonText = "탈퇴하기",
                rightButtonText = "취소",
                leftButtonTextColor = Color.RED,
                rightButtonTextColor = R.color.black,
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
                leftButtonTextColor = R.color.black,
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
                mypageViewModel.kakaoSignOut()
                tokenStorage.clearTokens()
            }
        }

        // 탈퇴할 경우 사용자 정보 삭제 후 앱 종료
        activity?.finishAffinity()
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
                mypageViewModel.kakaoLogout()
                tokenStorage.clearTokens()
            }
        }
        viewModelManager.resetAllViewModels()
        navController.navigate(R.id.action_navigation_mypage_to_login_fragment)
        mypageDialog?.dismiss()
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        mypageDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}