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
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import com.toyou.toyouandroid.utils.TokenStorage
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

        val tokenStorage = TokenStorage(requireContext())
        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(tokenStorage)
        )[CardViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity() ,
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]
        
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

        userViewModel.cardId.observe(viewLifecycleOwner, Observer { cardId ->

            if (cardId == null) {
                binding.lockFreeIv.setOnClickListener {
                    cardViewModel.isLockSelected(binding.lockFreeIv)
                }
            }
        })

        userViewModel.nickname.observe(viewLifecycleOwner, Observer { name ->
            binding.itemDetail.setText("To.${name}")
        })

        binding.itemTitle.text = LocalDate.now().toString().replace("-", "")

        cardViewModel.exposure.observe(viewLifecycleOwner, Observer { exposure ->
            if (exposure)
                binding.lockFreeIv.setImageResource(R.drawable.lock_btn2)
            else
                binding.lockFreeIv.setImageResource(R.drawable.lock_free2)
        })

        userViewModel.emotion.observe(viewLifecycleOwner, Observer { emotion ->
            listAdapter.setEmotion(emotion ?: "ANGRY")
            when(emotion){
                "HAPPY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_happy)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF7F3E3"))
                }
                "EXCITED" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_exciting)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFE0EEF6"))
                }
                "NORMAL" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_normal)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFDFE1F1"))
                }
                "NERVOUS" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_anxiety)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFD6E4D9"))

                }
                "ANGRY" -> {
                    binding.itemImage.setImageResource(R.drawable.home_stamp_option_upset)
                    binding.cardView.setCardBackgroundColor(Color.parseColor("#FFF4DDDD"))
                }
                //임의로 감정이 null일때..일수는 없지만
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