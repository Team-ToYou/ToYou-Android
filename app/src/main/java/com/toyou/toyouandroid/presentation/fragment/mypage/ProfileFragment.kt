package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentProfileBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = requireNotNull(_binding){"FragmentProfileBinding -> null"}

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var userViewModel: UserViewModel

    private val mypageViewModel: MypageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService: AuthService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)

        // ProfileViewModel은 Hilt로 주입됨

        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(createRepository,tokenManager)
        )[UserViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.resetNicknameEditState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

//        // 닉네임 변경시 기존 닉네임 불러오기
//        mypageViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
//            binding.signupNicknameInput.setText(nickname)
//        }
//
//        // 상태에 따른 배경 변경
//        mypageViewModel.status.observe(viewLifecycleOwner) { status ->
//            Timber.d("Status changed: $status")  // 상태 변경 시 로그 출력
//
//            when (status) {
//                "SCHOOL" -> {
//                    Timber.d("Setting background for SCHOOL status")  // SCHOOL 상태에 맞는 배경 설정 전 로그
//                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
//                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
//                }
//                "COLLEGE" -> {
//                    Timber.d("Setting background for COLLEGE status")  // COLLEGE 상태에 맞는 배경 설정 전 로그
//                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
//                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
//                }
//                "OFFICE" -> {
//                    Timber.d("Setting background for OFFICE status")  // OFFICE 상태에 맞는 배경 설정 전 로그
//                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
//                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
//                }
//                "ETC" -> {
//                    Timber.d("Setting background for ETC status")  // ETC 상태에 맞는 배경 설정 전 로그
//                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
//                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
//                }
//            }
//        }

        // 이전 버튼
        binding.signupNicknameBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_profile_to_mypage_fragment)
        }

        // 닉네임 입력
        binding.signupNicknameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                viewModel.updateTextCount(length)
                viewModel.duplicateBtnActivate()
                viewModel.updateLength15(length)
            }

            override fun afterTextChanged(s: Editable?) {
                val nickname = s?.toString() ?: ""
                viewModel.setNickname(nickname)
            }
        })

        // 프로필 최종 변경 버튼
        binding.signupNicknameBtn.setOnClickListener{
            viewModel.changeNickname()
            viewModel.changeStatus()
            mypageViewModel.updateMypage()
            navController.navigate(R.id.action_navigation_profile_to_mypage_fragment)
            Toast.makeText(requireContext(), "프로필 수정이 완료되었습니다", Toast.LENGTH_SHORT).show()
        }

//        // 닉네임 중복 확인
//        binding.signupAgreeNicknameDoublecheckBtn.setOnClickListener {
//            mypageViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
//                mypageViewModel.userId.observe(viewLifecycleOwner) { userId ->
//                    if (nickname != null && userId != null) {
//                        viewModel.checkDuplicate(nickname, userId)
//                    }
//                }
//            }
//            hideKeyboard()
//        }
//
//        val buttonList = listOf(
//            binding.signupStatusOption1,
//            binding.signupStatusOption2,
//            binding.signupStatusOption3,
//            binding.signupStatusOption4
//        )
//
//        buttonList.forEach { button ->
//            button.setOnClickListener {
//                viewModel.onButtonClicked(button.id)
//                updateButtonBackgrounds()
//            }
//        }

//        viewModel.selectedStatusButtonId.observe(viewLifecycleOwner) { id ->
//            id?.let {
//                updateButtonBackgrounds()
//            }
//        }

        // 완료 버튼 활성화
        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupNicknameBtn.isEnabled = isEnabled
        }

//        binding.root.setOnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                hideKeyboard()
//                v.performClick()
//            }
//            false
//        }
//        binding.root.setOnClickListener {
//            hideKeyboard()
//        }
    }

//    private fun updateButtonBackgrounds() {
//        val buttonList = listOf(
//            binding.signupStatusOption1,
//            binding.signupStatusOption2,
//            binding.signupStatusOption3,
//            binding.signupStatusOption4
//        )
//        buttonList.forEach { button ->
//            button.setBackgroundResource(viewModel.getButtonBackground(button.id))
//        }
//    }
//
//    private fun hideKeyboard() {
//        val imm = requireActivity().getSystemService(InputMethodManager::class.java)
//        imm.hideSoftInputFromWindow(view?.windowToken, 0)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        viewModel.setSelectedStatusButtonId(null)
//        _binding = null
//    }
}