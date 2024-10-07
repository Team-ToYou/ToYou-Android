package com.toyou.toyouandroid.presentation.fragment.record

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.CardLayoutBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.fragment.record.my.MyRecordViewModel
import com.toyou.toyouandroid.presentation.fragment.record.network.RecordRepository
import com.toyou.toyouandroid.presentation.fragment.record.network.RecordService
import com.toyou.toyouandroid.presentation.fragment.record.network.RecordViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class CardInfoFragment : Fragment() {
    private var _binding : CardLayoutBinding? = null
    private val binding: CardLayoutBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter

    private lateinit var cardInfoViewModel: CardInfoViewModel
    private lateinit var userViewModel: UserViewModel
    private val myRecordViewModel: MyRecordViewModel by activityViewModels {
        RecordViewModelFactory(RecordRepository(AuthNetworkModule.getClient().create(RecordService::class.java)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())

        cardInfoViewModel = ViewModelProvider(
            requireActivity(),
            CardInfoViewModelFactory(tokenStorage)
        )[CardInfoViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity() ,
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CardLayoutBinding.inflate(inflater, container, false)

        listAdapter = CardPreviewListAdapter()
        setupRecyclerView(binding.cardList, listAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        userViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            cardInfoViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
                if (receiver != nickname) {
                    Timber.tag("CardInfoFragment").d("$receiver $nickname")
                    binding.lockFreeIv.visibility = View.INVISIBLE
                } else {
                    binding.lockFreeIv.visibility = View.VISIBLE
                }
            }
        }

        cardInfoViewModel.previewCards.observe(viewLifecycleOwner) { previewCards ->
            listAdapter.setCards(previewCards)
            Timber.tag("CardInfoFragment").d(previewCards.toString())
        }

        cardInfoViewModel.cardId.observe(viewLifecycleOwner) { cardId ->
            if (cardId != null) {
                binding.lockFreeIv.setOnClickListener {
                    myRecordViewModel.patchDiaryCard(cardId)
                    cardInfoViewModel.isLockSelected()
                }
            }
        }

        cardInfoViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
            binding.itemDetail.text = if (!receiver.isNullOrBlank()) {
                "To.$receiver"
            } else {
                "To. Unknown"
            }
        }

        cardInfoViewModel.date.observe(viewLifecycleOwner) { date ->
            binding.itemTitle.text = date.toString().replace("-", "")
        }

        cardInfoViewModel.exposure.observe(viewLifecycleOwner) { exposure ->
            if (exposure) {
                binding.lockFreeIv.setImageResource(R.drawable.lock_btn2)
                Toast.makeText(requireContext(),"일기카드를 비공개로 설정합니다", Toast.LENGTH_SHORT).show()
            }
            else {
                binding.lockFreeIv.setImageResource(R.drawable.lock_free2)
                Toast.makeText(requireContext(), "일기카드를 공개로 설정합니다", Toast.LENGTH_SHORT).show()
            }
        }

        cardInfoViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
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
                // 감정이 null -> 임시 처리
                else -> {
                    binding.itemImage.setImageResource(R.drawable.char_selected)
                    binding.cardView.setCardBackgroundColor(Color.TRANSPARENT)
                }
            }
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}