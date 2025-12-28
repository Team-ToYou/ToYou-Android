package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.databinding.FragmentSignupnicknameBinding
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupNicknameFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupnicknameBinding? = null
    private val binding: FragmentSignupnicknameBinding
        get() = requireNotNull(_binding){"FragmentSignupnicknameBinding -> null"}

    private val viewModel: SignupNicknameViewModel by viewModels()
    private val nicknameViewModel: SignupNicknameViewModel by viewModels()

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var viewModelManager: ViewModelManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupnicknameBinding.inflate(inflater, container, false)

        // SignupNicknameViewModel은 Hilt로 주입됨
        // 다른 ViewModel들은 기존 방식 유지할 필요가 있다면 여기에 초기화 코드 추가

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelManager = ViewModelManager(nicknameViewModel, homeViewModel)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        binding.signupNicknameBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_signup_nickname_to_signup_agree_fragment)
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
            navController.navigate(R.id.action_navigation_signup_nickname_to_signup_status_fragment)
        }

        binding.signupAgreeNicknameDoublecheckBtn.setOnClickListener {
            viewModel.checkDuplicate(1)
            hideKeyboard()
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

    private fun hideKeyboard() {
        val imm = requireActivity().getSystemService(InputMethodManager::class.java)
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}