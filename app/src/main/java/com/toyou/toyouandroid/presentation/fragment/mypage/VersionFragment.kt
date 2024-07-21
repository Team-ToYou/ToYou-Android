package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.databinding.FragmentVersionBinding

class VersionFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentVersionBinding? = null
    private val binding: FragmentVersionBinding
        get() = requireNotNull(_binding){"FragmentVersionBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentVersionBinding.inflate(inflater, container, false)

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