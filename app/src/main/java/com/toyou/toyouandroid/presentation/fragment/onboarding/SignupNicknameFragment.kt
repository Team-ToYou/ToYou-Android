package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSignupagreeBinding
import com.toyou.toyouandroid.databinding.FragmentSignupnicknameBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class SignupNicknameFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupnicknameBinding? = null
    private val binding: FragmentSignupnicknameBinding
        get() = requireNotNull(_binding){"FragmentSignupnicknameBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupnicknameBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        binding.signupNicknameBtn.setOnClickListener{
            navController.navigate(R.id.action_navigation_signup_nickname_to_signup_status_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}