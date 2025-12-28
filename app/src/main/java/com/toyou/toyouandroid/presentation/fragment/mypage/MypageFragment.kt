package com.toyou.toyouandroid.presentation.fragment.mypage

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import com.kakao.sdk.user.UserApiClient
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentMypageBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.onboarding.SignupNicknameViewModel
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.fragment.record.CalendarDialogViewModel
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager
import com.toyou.core.datastore.TutorialStorage
import com.toyou.core.datastore.NotificationPreferences
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber
import androidx.navigation.findNavController
import androidx.core.net.toUri

@AndroidEntryPoint
class MypageFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentMypageBinding? = null
    private val binding: FragmentMypageBinding
        get() = requireNotNull(_binding){"FragmentMypageBinding -> null"}

    private lateinit var viewModelManager: ViewModelManager

    private val nicknameViewModel: SignupNicknameViewModel by activityViewModels()
    private val mypageDialogViewModel: MypageDialogViewModel by activityViewModels()
    private val calendarDialogViewModel: CalendarDialogViewModel by activityViewModels()
    private var mypageDialog: MypageDialog? = null
    private var myPageLogoutDialog: MyPageLogoutDialog? = null

    private val homeViewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var tutorialStorage: TutorialStorage

    @Inject
    lateinit var notificationPreferences: NotificationPreferences

    private val mypageViewModel: MypageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        // MypageViewModel과 HomeViewModel은 Hilt로 주입됨

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        viewModelManager = ViewModelManager(nicknameViewModel, homeViewModel)
        mypageViewModel.updateMypage()

        binding.mypageProfileBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_mypage_to_profile_fragment)
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

        mypageViewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.nickname?.let { nickname ->
                binding.profileNickname.text = nickname
            }

            val friendText = if (uiState.friendNum != null) {
                "친구 ${uiState.friendNum}명"
            } else {
                "친구 0명"
            }
            binding.profileFriendCount.text = friendText
        }

        nicknameViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            if (mypageViewModel.uiState.value?.nickname == null) {
                binding.profileNickname.text = nickname
            }
        }

        mypageViewModel.logoutSuccess.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                mypageViewModel.setLogoutSuccess(false)
                viewModelManager.resetAllViewModels()

                Timber.d("로그아웃 후 로그인 화면으로 이동")

                navController.navigate(R.id.action_navigation_mypage_to_login_fragment)
            }
        }

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
            calendarDialogViewModel.setDialogData(
                title = "정말 로그아웃하시겠어요?",
                leftButtonText = "취소",
                rightButtonText = "로그아웃",
                leftButtonTextColor = Color.BLACK,
                rightButtonTextColor = Color.RED,
                leftButtonClickAction = { dismissLogoutDialog() },
                rightButtonClickAction = { handleLogout() }
            )
            myPageLogoutDialog = MyPageLogoutDialog()
            myPageLogoutDialog?.show(parentFragmentManager, "CustomDialog")
        }
    }

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

        tutorialStorage.setTutorialNotShownSync()
        notificationPreferences.setSubscribedSync(true)

        mypageViewModel.kakaoSignOut()
        mypageDialog?.dismiss()
    }

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
        myPageLogoutDialog?.dismiss()
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        mypageDialog?.dismiss()
    }

    private fun dismissLogoutDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        myPageLogoutDialog?.dismiss()
    }

    private fun redirectLink(uri: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = uri.toUri()
        startActivity(i)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}