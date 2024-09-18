package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionContentBinding
import com.toyou.toyouandroid.databinding.FragmentSendBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage

class SendFragment: Fragment() {
    private var _binding : FragmentSendBinding? = null
    private val binding : FragmentSendBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenStorage = TokenStorage(requireContext())
        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(tokenStorage)
        )[SocialViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]
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
            val myName = userViewModel.nickname.value ?: ""
            socialViewModel.sendQuestion(myName)

            navController.navigate(R.id.action_sendFragment_to_sendFinalFragment)
        }

        binding.backBtn.setOnClickListener {
            navController.popBackStack()
        }


        binding.checkboxBtn.setOnClickListener {
            socialViewModel.isAnonymous(binding.checkboxBtn.isChecked)
        }
    }
}