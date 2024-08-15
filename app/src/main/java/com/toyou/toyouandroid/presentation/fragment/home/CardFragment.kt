package com.toyou.toyouandroid.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.databinding.CardLayoutBinding
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.presentation.fragment.home.RVMarginItemDecoration
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


        listAdapter = CardPreviewListAdapter()
        setupRecyclerView(binding.cardList, listAdapter)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        cardViewModel.previewCards.observe(viewLifecycleOwner, Observer { previewCards ->
            listAdapter.setCards(previewCards )
            Timber.tag("카드2").d(previewCards.toString())

        })

        binding.lockFreeIv.setOnClickListener {
            cardViewModel.isLockSelected(binding.lockFreeIv)
            Log.d("답변3", cardViewModel.previewChoose.value.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
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