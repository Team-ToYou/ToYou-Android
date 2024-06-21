package com.toyou.toyouandroid.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.ui.home.adapter.CardAdapter
import com.toyou.toyouandroid.view_model.CardViewModel

class CreateFragment : Fragment(){

    lateinit var binding : FragmentCreateBinding

    private lateinit var cardAdapter : CardAdapter
    private lateinit var cardViewModel: CardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateBinding.inflate(inflater, container, false)

        cardAdapter = CardAdapter()

        binding.cardRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cardAdapter
        }

        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        cardViewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            Log.d("CardViewModel", "Loading cards: $cards") // 디버그 로그 추가
            cardAdapter.setCards(cards)
        })


        return binding.root
    }

}