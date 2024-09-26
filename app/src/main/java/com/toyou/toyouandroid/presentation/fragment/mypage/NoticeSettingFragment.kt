package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeSettingBinding

class NoticeSettingFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentNoticeSettingBinding? = null
    private val binding: FragmentNoticeSettingBinding
        get() = requireNotNull(_binding){"FragmentNoticeSettingBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNoticeSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.signupNicknameBackBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_notice_setting_to_mypage_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}