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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentProfileBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthService
import com.toyou.toyouandroid.presentation.fragment.onboarding.network.AuthViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage

class ProfileFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentProfileBinding? = null
    private val binding: FragmentProfileBinding
        get() = requireNotNull(_binding){"FragmentProfileBinding -> null"}
    private val viewModel: ProfileViewModel by activityViewModels()
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
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.resetNicknameEditState()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // tokenStorage 초기화
        tokenStorage = TokenStorage(requireContext())

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        mypageViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            binding.signupNicknameInput.setText(nickname)
        }

        binding.signupNicknameBackBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_profile_to_mypage_fragment)
        }

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

        binding.signupNicknameBtn.setOnClickListener{
            viewModel.changeNickname()
            viewModel.changeStatus()
            navController.navigate(R.id.action_navigation_profile_to_mypage_fragment)
            Toast.makeText(requireContext(), "프로필 수정이 완료되었습니다", Toast.LENGTH_SHORT).show()
        }

        binding.signupAgreeNicknameDoublecheckBtn.setOnClickListener {
            viewModel.checkDuplicate()
            hideKeyboard()
        }

        mypageViewModel.status.observe(viewLifecycleOwner) { status ->
            when (status) {
                "SCHOOL" -> {
                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
                }
                "COLLEGE" -> {
                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
                }
                "OFFICE" -> {
                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_input)
                }
                "ETC" -> {
                    binding.signupStatusOption1.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption2.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption3.setBackgroundResource(R.drawable.signupnickname_input)
                    binding.signupStatusOption4.setBackgroundResource(R.drawable.signupnickname_doublecheck_activate)
                }
            }
        }

        val buttonList = listOf(
            binding.signupStatusOption1,
            binding.signupStatusOption2,
            binding.signupStatusOption3,
            binding.signupStatusOption4
        )

        buttonList.forEach { button ->
            button.setOnClickListener {
                viewModel.onButtonClicked(button.id)
                updateButtonBackgrounds()
            }
        }

        viewModel.selectedStatusButtonId.observe(viewLifecycleOwner) {
            updateButtonBackgrounds()
        }

        // 완료 버튼 활성화
        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupNicknameBtn.isEnabled = isEnabled
        }

        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyboard()
                v.performClick()
            }
            false
        }

        binding.root.setOnClickListener {
            hideKeyboard()
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
            button.setBackgroundResource(viewModel.getButtonBackground(button.id))
        }
    }

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}