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
import com.toyou.toyouandroid.databinding.FragmentModifyBinding
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.ui.home.CardFragment
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter

class ModifyFragment: Fragment() {
    private var _binding : FragmentModifyBinding? = null
    private val binding: FragmentModifyBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter
    private lateinit var cardViewModel : CardViewModel

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
        _binding = FragmentModifyBinding.inflate(inflater, container, false)

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
            cardViewModel.clearAll()
            navController.navigate(R.id.action_modifyFragment_to_create_fragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}