package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSendBinding
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SendFragment: Fragment() {
    private var _binding : FragmentSendBinding? = null
    private val binding : FragmentSendBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
    private val socialViewModel: SocialViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSendBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        binding.nextBtn.setOnClickListener {
            val myName = userViewModel.nickname.value ?: ""
            socialViewModel.sendQuestion(myName)

            navController.navigate(R.id.action_sendFragment_to_sendFinalFragment)
        }

        binding.backFrame.setOnClickListener {
            navController.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()

            }

        })


        binding.checkboxBtn.setOnClickListener {
            socialViewModel.isAnonymous(binding.checkboxBtn.isChecked)
        }
    }
}