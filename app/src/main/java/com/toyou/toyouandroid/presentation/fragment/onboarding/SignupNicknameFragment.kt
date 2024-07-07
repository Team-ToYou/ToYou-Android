package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.toyou.toyouandroid.databinding.FragmentSignupagreeBinding
import com.toyou.toyouandroid.databinding.FragmentSignupnicknameBinding

class SignupNicknameFragment : Fragment() {

    private var _binding: FragmentSignupnicknameBinding? = null

//    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}