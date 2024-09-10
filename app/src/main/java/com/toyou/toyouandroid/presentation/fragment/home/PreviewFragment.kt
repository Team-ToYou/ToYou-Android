package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.ui.home.CardFragment
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter

class PreviewFragment : Fragment(){

    private var _binding : FragmentPreviewBinding? = null
    private val binding: FragmentPreviewBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter
    private lateinit var cardViewModel : CardViewModel
    private lateinit var userViewModel: UserViewModel

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(requireActivity()).get(CardViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        binding.imageButton.setOnClickListener {
            navController.popBackStack()

        }

        binding.nextBtn.setOnClickListener {
            val previewCards = cardViewModel.previewCards.value ?: emptyList()
            val exposure = cardViewModel.exposure.value ?: false

            if (cardViewModel.cardId.value == 0) {
                cardViewModel.sendData(previewCards, exposure)
                userViewModel.updateCardIdFromOtherViewModel(cardViewModel)
            }
            else(cardViewModel.cardId.value)
                cardViewModel.patchCard(previewCards, exposure)

            navController.navigate(R.id.action_previewFragment_to_navigation_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}