package com.toyou.toyouandroid.ui.home

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyou.toyouandroid.MainActivity
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.ui.home.adapter.CardAdapter
import com.toyou.toyouandroid.view_model.CardViewModel

class CreateFragment : Fragment(){

    private var _binding: FragmentCreateBinding? = null
    private val binding: FragmentCreateBinding get() = requireNotNull(_binding) { "FragmentCreateBinding should not be null" }

    private lateinit var cardAdapter : CardAdapter
    private lateinit var cardViewModel: CardViewModel

    lateinit var navController: NavController
    var count : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        cardViewModel.loadCardData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
                count=0
            }

        })

        cardAdapter = CardAdapter { position, isSelected ->
            cardViewModel.updateButtonState(position, isSelected)
            Log.d("CreateFragment", "Item clicked at position: $position")
            Log.d("CreateFragment", "Item clicked at position: $isSelected, ${cardViewModel.cards.value}")

        }



        cardViewModel.loadCardData()


        binding.cardRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val margin = (screenWidth * 0.02).toInt() // 화면 너비의 5%를 마진으로 사용

            // 아이템 데코레이션 추가
            addItemDecoration(RVMarginItemDecoration(margin))
        }


        val mainActivity = activity as MainActivity // casting
        mainActivity.hideBottomNavigation(true)


        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backBtn.setOnClickListener {
            binding.backBtn.setOnClickListener {
                navController.popBackStack()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigation(false)
        _binding = null
    }


}
