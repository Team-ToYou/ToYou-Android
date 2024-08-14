package com.toyou.toyouandroid.presentation.fragment.home

import ShortCardAdapter
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCreateWriteBinding
import com.toyou.toyouandroid.model.ChooseModel
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardChooseAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.ChooseCardAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.WriteCardAdapter
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel

class CreateWriteFragment: Fragment() {

    private var _binding : FragmentCreateWriteBinding? = null
    private val binding : FragmentCreateWriteBinding get() = requireNotNull(_binding){"CreateWriteFragment is null"}
    private lateinit var cardViewModel: CardViewModel

    lateinit var navController: NavController
    private lateinit var longAdapter : WriteCardAdapter
    private lateinit var shortAdapter: ShortCardAdapter
    private lateinit var chooseAdapter : ChooseCardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(requireActivity()).get(CardViewModel::class.java)

        cardViewModel.getAllData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateWriteBinding.inflate(inflater, container, false)

        cardViewModel.previewCards.observe(viewLifecycleOwner, Observer { cards ->
            longAdapter.setCards(cards)
            shortAdapter.setCards(cards)
        })

        cardViewModel.previewChoose.observe(viewLifecycleOwner, Observer { cards ->
            chooseAdapter.setCards(cards)
        })

        longAdapter = WriteCardAdapter(cardViewModel)
        shortAdapter = ShortCardAdapter(cardViewModel)
        chooseAdapter = ChooseCardAdapter()

        setupRecyclerView(binding.cardRv, longAdapter)
        setupRecyclerView(binding.cardShortRv, shortAdapter)
        setupRecyclerView(binding.cardChooseRv, chooseAdapter)

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

        cardViewModel.isAnyEditTextFilled.observe(viewLifecycleOwner, Observer { isFilled ->
            //binding.nextBtn.isEnabled = isFilled
        })
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
}