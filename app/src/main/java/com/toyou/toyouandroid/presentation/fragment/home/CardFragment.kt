package com.toyou.toyouandroid.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.toyou.toyouandroid.databinding.CardLayoutBinding
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import timber.log.Timber

class CardFragment : Fragment() {

    private var _binding : CardLayoutBinding? = null
    private val binding: CardLayoutBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter
    private lateinit var cardViewModel: CardViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(requireActivity()).get(CardViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = CardLayoutBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        listAdapter = CardPreviewListAdapter(emptyList())
        binding.cardList.adapter = listAdapter

        cardViewModel.previewCards.observe(viewLifecycleOwner, Observer { previewCards ->
            listAdapter.setCards(previewCards )
            Timber.tag("카드2").d(previewCards.toString())

        })

        binding.lockFreeIv.setOnClickListener {
            cardViewModel.isLockSelected(binding.lockFreeIv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}