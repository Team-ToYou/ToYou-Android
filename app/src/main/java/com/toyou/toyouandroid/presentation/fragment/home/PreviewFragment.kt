package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage

class PreviewFragment : Fragment(){

    private var _binding : FragmentPreviewBinding? = null
    private val binding: FragmentPreviewBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var cardViewModel : CardViewModel
    private lateinit var userViewModel: UserViewModel

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        cardViewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(tokenManager)
        )[CardViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(tokenManager)
        )[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)

        if (savedInstanceState == null) {
            // 프래그먼트 인스턴스 생성
            val fragment = CardFragment()

            // FragmentTransaction을 사용하여 프래그먼트를 추가
            childFragmentManager.beginTransaction()
                .add(R.id.card_container, fragment)
                .commit()
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backFrame.setOnClickListener {
            navController.popBackStack()

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navController.popBackStack()
            }

        })

        binding.nextBtn.setOnClickListener {
            val previewCards = cardViewModel.previewCards.value ?: emptyList()
            Log.d("수정", previewCards.toString())
            val exposure = cardViewModel.exposure.value ?: true

            if (userViewModel.cardId.value == null) {
                cardViewModel.sendData(previewCards, exposure)
                userViewModel.updateCardIdFromOtherViewModel(cardViewModel)
                cardViewModel.disableLock(true)
                Toast.makeText(requireContext(), "일기카드가 저장되었습니다", Toast.LENGTH_SHORT).show()
            }
            else {
                cardViewModel.patchCard(previewCards, exposure, userViewModel.cardId.value!!)
                Toast.makeText(requireContext(), "일기카드가 수정되었습니다", Toast.LENGTH_SHORT).show()
            }
            cardViewModel.toastShow = false

            navController.navigate(R.id.action_previewFragment_to_navigation_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}