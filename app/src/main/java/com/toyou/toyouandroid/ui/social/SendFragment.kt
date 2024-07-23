package com.toyou.toyouandroid.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionContentBinding
import com.toyou.toyouandroid.databinding.FragmentSendBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class SendFragment: Fragment() {
    private var _binding : FragmentSendBinding? = null
    private val binding : FragmentSendBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSendBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_sendFragment_to_sendFinalFragment)
        }
        binding.backBtn.setOnClickListener {
            navController.popBackStack()

        }
    }
}