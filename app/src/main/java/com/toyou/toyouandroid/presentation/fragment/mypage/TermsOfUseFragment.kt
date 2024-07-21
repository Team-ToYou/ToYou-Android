package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.databinding.FragmentTermsOfUseBinding

class TermsOfUseFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentTermsOfUseBinding? = null
    private val binding: FragmentTermsOfUseBinding
        get() = requireNotNull(_binding){"FragmentTermsOfUseBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTermsOfUseBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}