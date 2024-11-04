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
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.data.record.service.RecordService
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

    private var isFirstExposureObservation = true

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

        // 카드 미리 보기 설정
        cardInfoViewModel.previewCards.observe(viewLifecycleOwner) { previewCards ->
            listAdapter.setCards(previewCards)
            Timber.tag("CardInfoFragment").d(previewCards.toString())
        }

        // 카드 주인 정보 설정
        cardInfoViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
            binding.itemDetail.text = if (!receiver.isNullOrBlank()) {
                "To.$receiver"
            } else {
                "To. Unknown"
            }
        }

        // 카드 생성 날짜 표시
        cardInfoViewModel.date.observe(viewLifecycleOwner) { date ->
            binding.itemTitle.text = date.toString().replace("-", "")
        }

        cardInfoViewModel.apply {
            receiver.observe(viewLifecycleOwner) { receiver ->
                userViewModel.nickname.value?.let { nickname ->
                    binding.lockFreeIv.visibility = if (receiver != nickname) View.INVISIBLE else View.VISIBLE
                    Timber.tag("CardInfoFragment").d("$receiver $nickname")
                }
            }
            cardId.observe(viewLifecycleOwner) { cardId ->
                if (cardId != null) {
                    binding.lockFreeIv.setOnClickListener {
                        myRecordViewModel.patchDiaryCard(cardId)
                        cardInfoViewModel.isLockSelected()
                    }
                }
            }
            exposure.observe(viewLifecycleOwner) { exposure ->
                if (isFirstExposureObservation) {
                    isFirstExposureObservation = false
                    return@observe // Skip the first observation
                }
                setLockState(exposure)
            }
        }

        // 감정 상태에 따른 이미지 및 배경색 설정
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

    private fun setLockState(isLocked: Boolean) {
        binding.lockFreeIv.setImageResource(if (isLocked) R.drawable.lock_btn2 else R.drawable.lock_free2)
        val message = if (isLocked) "일기카드를 비공개로 설정합니다" else "일기카드를 공개로 설정합니다"
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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