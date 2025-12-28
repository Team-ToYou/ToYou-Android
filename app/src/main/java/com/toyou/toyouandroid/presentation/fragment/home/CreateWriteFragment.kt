package com.toyou.toyouandroid.presentation.fragment.home

import ShortCardAdapter
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCreateWriteBinding
import com.toyou.toyouandroid.presentation.fragment.home.adapter.ChooseCardAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.WriteCardAdapter
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateWriteFragment: Fragment() {

    private var _binding : FragmentCreateWriteBinding? = null
    private val binding : FragmentCreateWriteBinding get() = requireNotNull(_binding){"CreateWriteFragment is null"}
    private val cardViewModel: CardViewModel by activityViewModels()

    lateinit var navController: NavController
    private lateinit var longAdapter : WriteCardAdapter
    private lateinit var shortAdapter: ShortCardAdapter
    private lateinit var chooseAdapter : ChooseCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateWriteBinding.inflate(inflater, container, false)

        cardViewModel.previewCards.observe(viewLifecycleOwner) { cards ->
            longAdapter.setCards(cards)
            shortAdapter.setCards(cards)
        }

        cardViewModel.previewCards.observe(viewLifecycleOwner) { cards ->
            chooseAdapter.setCards(cards)
        }

        longAdapter = WriteCardAdapter(cardViewModel)
        shortAdapter = ShortCardAdapter(cardViewModel)
        chooseAdapter = ChooseCardAdapter(cardViewModel)

        setupRecyclerView(binding.cardRv, longAdapter)
        setupRecyclerView(binding.cardShortRv, shortAdapter)
        setupRecyclerView(binding.cardChooseRv, chooseAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backFrame.setOnClickListener {
            cardViewModel.clearAllData()
            navController.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                cardViewModel.clearAllData()
                navController.popBackStack()
            }
        })
        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_createWriteFragment_to_previewFragment)
        }

        cardViewModel.isAllAnswersFilled.observe(viewLifecycleOwner) { isFilled ->
            binding.nextBtn.isEnabled = isFilled
        }

    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val margin = (screenWidth * 0.05).toInt() // 화면 너비의 5%를 마진으로 사용

            // 아이템 데코레이션 추가
            addItemDecoration(RVMarginItemDecoration(margin, true))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}