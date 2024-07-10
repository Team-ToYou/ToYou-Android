package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.toyou.toyouandroid.databinding.FragmentLoginBinding
import com.toyou.toyouandroid.databinding.FragmentSignupagreeBinding

class SignupAgreeFragment : Fragment() {

    private var _binding: FragmentSignupagreeBinding? = null

//    private val binding get() = _binding!!

    private val binding: FragmentSignupagreeBinding
        get() = requireNotNull(_binding){"FragmentSignupagreeBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupagreeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}