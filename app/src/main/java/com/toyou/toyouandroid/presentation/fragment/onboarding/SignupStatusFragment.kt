package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.toyou.toyouandroid.databinding.FragmentSignupnicknameBinding
import com.toyou.toyouandroid.databinding.FragmentSignupstatusBinding

class SignupStatusFragment : Fragment() {

    private var _binding: FragmentSignupstatusBinding? = null

//    private val binding get() = _binding!!

    private val binding: FragmentSignupstatusBinding
        get() = requireNotNull(_binding){"FragmentSignupstatusBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupstatusBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}