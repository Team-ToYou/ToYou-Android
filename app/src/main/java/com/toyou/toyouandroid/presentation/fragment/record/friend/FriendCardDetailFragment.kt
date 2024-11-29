package com.toyou.toyouandroid.presentation.fragment.record.friend

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.databinding.CardLayoutRecordBinding
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.RecordViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class FriendCardDetailFragment : Fragment() {
    private var _binding : CardLayoutRecordBinding? = null
    private val binding: CardLayoutRecordBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter

    private lateinit var friendCardViewModel: FriendCardViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val recordService = AuthNetworkModule.getClient().create(RecordService::class.java)
        val recordRepository = RecordRepository(recordService)

        friendCardViewModel = ViewModelProvider(
            requireActivity(),
            RecordViewModelFactory(recordRepository, tokenManager)
        )[FriendCardViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity() ,
            HomeViewModelFactory(tokenManager)
        )[UserViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = CardLayoutRecordBinding.inflate(inflater, container, false)

        listAdapter = CardPreviewListAdapter()
        setupRecyclerView(binding.cardList, listAdapter)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.lockFreeIv.visibility = View.GONE

        // 카드 미리 보기 설정
        friendCardViewModel.previewCards.observe(viewLifecycleOwner) { previewCards ->
            listAdapter.setCards(previewCards)
            Timber.tag("CardInfoFragment").d(previewCards.toString())
        }

        // 카드 주인 정보 설정
        friendCardViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
            binding.itemDetail.text = if (!receiver.isNullOrBlank()) {
                "To.$receiver"
            } else {
                "To. Unknown"
            }
        }

        // 카드 생성 날짜 표시
        friendCardViewModel.date.observe(viewLifecycleOwner) { date ->
            binding.itemTitle.text = date.toString().replace("-", "")
        }

        // 감정 상태에 따른 이미지 및 배경색 설정
        friendCardViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
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