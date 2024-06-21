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
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.ui.home.adapter.CardAdapter
import com.toyou.toyouandroid.view_model.CardViewModel

class CreateFragment : Fragment(){

    private var _binding: FragmentCreateBinding? = null
    private val binding: FragmentCreateBinding get() = requireNotNull(_binding) { "FragmentCreateBinding should not be null" }

    private lateinit var cardAdapter : CardAdapter
    private lateinit var cardViewModel: CardViewModel


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
        cardAdapter = CardAdapter { position ->
            Log.d("CreateFragment", "Item clicked at position: $position")

        }
        //adapter = cardAdapter


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



        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        Log.d("CreateFragment", "ViewModel created: ${cardViewModel}") // 로그 추가

        cardViewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            Log.d("CardViewModel", "Loading cards: $cards") // 디버그 로그 추가
            cardAdapter.setCards(cards)
        })

        cardViewModel.loadCardData()


        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
