package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSignupstatusBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class SignupStatusFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupstatusBinding? = null
    private val binding: FragmentSignupstatusBinding
        get() = requireNotNull(_binding){"FragmentSignupstatusBinding -> null"}
    private val viewModel: SignupStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupstatusBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

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
                viewModel.onButtonClicked(button.id)
                updateButtonBackgrounds()
            }
        }

        viewModel.selectedButtonId.observe(viewLifecycleOwner) {
            updateButtonBackgrounds()
        }

        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupStatusCompleteBtn.isEnabled = isEnabled
        }

        binding.signupStatusBackBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_signup_status_to_signup_nickname_fragment)
        }

        binding.signupStatusCompleteBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_signup_status_to_home_fragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}