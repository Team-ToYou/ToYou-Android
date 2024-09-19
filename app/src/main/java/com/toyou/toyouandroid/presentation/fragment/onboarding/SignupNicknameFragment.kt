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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSignupnicknameBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.ViewModelManager

class SignupNicknameFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupnicknameBinding? = null
    private val binding: FragmentSignupnicknameBinding
        get() = requireNotNull(_binding){"FragmentSignupnicknameBinding -> null"}
    private val viewModel: SignupNicknameViewModel by activityViewModels()
    private val nicknameViewModel: SignupNicknameViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var viewModelManager: ViewModelManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupnicknameBinding.inflate(inflater, container, false)
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
//            nicknameViewModel.changeNickname(1)
            navController.navigate(R.id.action_navigation_signup_nickname_to_signup_status_fragment)
        }

        binding.signupAgreeNicknameDoublecheckBtn.setOnClickListener {
            viewModel.checkDuplicate()
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

        viewModel.navigationEvent.observe(viewLifecycleOwner) { event ->
            when (event) {
                is SignupNicknameViewModel.NavigationEvent.NavigateToMyPage -> {
                    navController.navigate(R.id.action_navigation_signup_nickname_to_mypage_fragment)
                }
                is SignupNicknameViewModel.NavigationEvent.NavigateToSignupAgree -> {
                    navController.navigate(R.id.action_navigation_signup_nickname_to_signup_agree_fragment)
                }
            }
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