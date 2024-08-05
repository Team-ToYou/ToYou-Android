package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCreateWriteBinding
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class CreateWriteFragment: Fragment() {

    private var _binding : FragmentCreateWriteBinding? = null
    private val binding : FragmentCreateWriteBinding get() = requireNotNull(_binding){"CreateWriteFragment is null"}
    private lateinit var cardViewModel: CardViewModel

    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateWriteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backBtn.setOnClickListener {
            navController.popBackStack()
        }
        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_createWriteFragment_to_previewFragment)
        }
    }
}