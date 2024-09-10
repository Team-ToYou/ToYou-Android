package com.toyou.toyouandroid.ui.home

import android.graphics.Color
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
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.CardLayoutBinding
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.presentation.fragment.home.RVMarginItemDecoration
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import timber.log.Timber
import java.time.LocalDate

class CardFragment : Fragment() {

    private var _binding : CardLayoutBinding? = null
    private val binding: CardLayoutBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter
    private lateinit var cardViewModel: CardViewModel
    private lateinit var userViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(requireActivity()).get(CardViewModel::class.java)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

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
            Log.d("답변3", cardViewModel.previewCards.value.toString())
        }

        binding.itemTitle.text = LocalDate.now().toString().replace("-", "")

        userViewModel.emotion.observe(viewLifecycleOwner, Observer { emotion ->
            when(emotion){
                "HAPPY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_happy)
                    binding.cardView.setCardBackgroundColor(R.color.y01)
                }
                "EXCITED" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_exciting)
                    binding.cardView.setCardBackgroundColor(R.color.b01)
                }
                "NORMAL" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_normal)
                    binding.cardView.setCardBackgroundColor(R.color.p01)
                }
                "NERVOUS" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_anxiety)
                    binding.cardView.setCardBackgroundColor(R.color.gr00)

                }
                "ANGRY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_upset)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF4DDDD"))
                }
                //임의로 null일때..
                else -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_upset)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF4DDDD"))
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter

        }
    }

}