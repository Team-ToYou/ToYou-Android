package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardChooseAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardShortAdapter
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class CreateFragment : Fragment(){

    private var _binding: FragmentCreateBinding? = null
    private val binding: FragmentCreateBinding get() = requireNotNull(_binding) { "널" }

    private lateinit var cardAdapter : CardAdapter
    private lateinit var cardShortAdapter : CardShortAdapter
    private lateinit var cardChooseAdapter: CardChooseAdapter
    private lateinit var cardViewModel: CardViewModel

    lateinit var navController: NavController
    var count : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenStorage = TokenStorage(requireContext())
        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(tokenStorage)
        )[CardViewModel::class.java]

        //cardViewModel.loadCardData()
        cardViewModel.getAllData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)

        cardViewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            Log.d("CreateFragment", "Loading cards: ${cardViewModel.cards.value}") // 디버그 로그 추가
            cardAdapter.setCards(cards)

            cards?.let {
                for (card in it) {
                    if (card.isButtonSelected) {
                        count = 1
                        binding.nextBtn.isEnabled = true
                    }
                }
                if (count == 0)
                    binding.nextBtn.isEnabled = false
                count = 0
            }

        })

        cardViewModel.shortCards.observe(viewLifecycleOwner, Observer { cards ->
            Log.d("CreateFragment", "Loading cards: ${cardViewModel.cards.value}") // 디버그 로그 추가
            cardShortAdapter.setCards(cards)

            cards?.let {
                for (card in it) {
                    if (card.isButtonSelected) {
                        count = 1
                        binding.nextBtn.isEnabled = true
                    }
                }
                if (count == 0)
                    binding.nextBtn.isEnabled = false
                count = 0
            }


        })


        cardViewModel.chooseCards.observe(viewLifecycleOwner, Observer { cards ->
            cardChooseAdapter.setCards(cards)

            cards?.let {
                for (card in it) {
                    if (card.isButtonSelected) {
                        count = 1
                        binding.nextBtn.isEnabled = true
                    }
                }
                if (count == 0)
                    binding.nextBtn.isEnabled = false
                count = 0
            }


        })

        cardAdapter = CardAdapter { position, isSelected ->
            cardViewModel.updateButtonState(position, isSelected)
            cardViewModel.countSelect(isSelected)
            Log.d("버튼", position.toString())

        }
        cardShortAdapter = CardShortAdapter{ position, isSelected ->
            Log.d("선택2", position.toString()+isSelected.toString())
            cardViewModel.updateShortButtonState(position, isSelected)
            cardViewModel.countSelect(isSelected)
        }
        cardChooseAdapter = CardChooseAdapter{ position, isSelected ->
            Log.d("선택1", position.toString()+isSelected.toString())
            cardViewModel.updateChooseButton(position, isSelected)
            cardViewModel.countSelect(isSelected)
        }



        setupRecyclerView(binding.cardRv, cardAdapter)
        setupRecyclerView(binding.cardShortRv, cardShortAdapter)
        setupRecyclerView(binding.cardChooseRv, cardChooseAdapter)

        val mainActivity = activity as MainActivity // casting
        mainActivity.hideBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backBtn.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(false)
            navController.popBackStack()
        }

        binding.nextBtn.setOnClickListener {
            //cardViewModel.updateChooseCard()
            cardViewModel.updateAllPreviews()
            //cardViewModel.updatePreviewShortCard()

            cardViewModel.previewCards.value?.let { previewCards ->
                if (previewCards.isNotEmpty()) {
                    Timber.tag("카드3").d(previewCards[0].question)
                }
            }
            navController.navigate(R.id.action_create_fragment_to_createWriteFragment)
        }

        cardViewModel.countSelection.observe(viewLifecycleOwner, Observer { count ->
            if (count == 5){
                Toast.makeText(requireContext(), "질문은 최대 5개까지 선택할 수 있습니다", Toast.LENGTH_SHORT).show()
            }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
