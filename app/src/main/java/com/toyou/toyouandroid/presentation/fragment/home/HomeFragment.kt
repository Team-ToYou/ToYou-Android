package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.toyou.core.common.mvi.collectEvent
import com.toyou.core.common.mvi.collectState
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeBinding
import com.toyou.toyouandroid.data.home.dto.response.YesterdayCard
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.home.adapter.HomeBottomSheetAdapter
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeViewModel
import com.toyou.toyouandroid.presentation.fragment.record.CardInfoViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}
    private val noticeViewModel: NoticeViewModel by viewModels()
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val userViewModel: UserViewModel by activityViewModels()
    private val cardViewModel: CardViewModel by activityViewModels()

    private lateinit var listener: HomeBottomSheetClickListener
    private val cardInfoViewModel: CardInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        userViewModel.getHomeEntry()
        noticeViewModel.fetchNotices()

        listener = object : HomeBottomSheetClickListener {
            override fun onDiaryCardClick(cardId: Int?) {
                Timber.tag("HomeFragment").d("$cardId")
                cardId?.let {
                    val bundle = Bundle().apply {
                        putInt("cardId", it)
                    }
                    navController.navigate(R.id.action_navigation_home_to_friend_card_container_fragment, bundle)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        // MVI: State 수집
        viewLifecycleOwner.collectState(viewModel.state) { state ->
            // UI 상태 업데이트는 여기서 처리
            Timber.tag("HomeFragment").d("State updated: $state")
        }

        // MVI: Event 수집
        viewLifecycleOwner.collectEvent(viewModel.event) { event ->
            when (event) {
                is HomeEvent.ShowError -> {
                    Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                }
                is HomeEvent.TokenExpired -> {
                    // Token 만료 처리 (로그인 화면으로 이동 등)
                    Timber.tag("HomeFragment").d("Token expired")
                }
            }
        }

        // MVI: Action 발행 - 일기카드 조회
        viewModel.onAction(HomeAction.LoadYesterdayCards)


        // 질문 개수에 따른 우체통 이미지 변경
        userViewModel.cardNum.observe(viewLifecycleOwner) { cardNum ->

            val imageRes = when {
                cardNum == 0 -> R.drawable.home_mailbox_none
                cardNum in 1..5 -> R.drawable.home_mailbox_single
                cardNum >= 6 -> R.drawable.home_mailbox_multiple
                else -> R.drawable.home_mailbox_none
            }
            binding.homeMailboxIv.setImageResource(imageRes)
        }

        val bottomSheet: ConstraintLayout = binding.homeBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        // 작일 친구 일기카드 존재 여부 판단
//        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
//            if (isEmpty) {
//                binding.homeBottomsheetPseudo.visibility = View.VISIBLE
//                binding.homeBottomSheetRv.visibility = View.GONE
//            } else {
////                binding.homeBottomsheetPseudo.visibility = View.GONE
////                binding.homeBottomSheetRv.visibility = View.VISIBLE
//                binding.homeBottomsheetPseudo.visibility = View.VISIBLE
//                binding.homeBottomSheetRv.visibility = View.GONE
//            }
//        }

        binding.homeBottomsheetPseudo.visibility = View.VISIBLE
        binding.homeBottomSheetRv.visibility = View.GONE

        // 바텀 시트 터치 이벤트 처리
        binding.homeBottomSheet.apply {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // 터치 시작 시 바텀 시트 핸들바 색깔 변경
                        binding.homeBottomSheetTouchBar.setBackgroundResource(R.drawable.next_button_enabled_roundly)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        // 터치 종료 시 기본 색깔로 변경
                        binding.homeBottomSheetTouchBar.setBackgroundResource(R.drawable.next_button_roundly)
                        v.performClick()
                        true
                    }
                    else -> false
                }
            }

            setOnClickListener {}
        }

//        // 홈 화면 바텀 시트 설정
//        viewModel.yesterdayCards.observe(viewLifecycleOwner) { yesterdayCards ->
//            if (yesterdayCards.isNotEmpty()) {
//                binding.homeBottomsheetPseudo.visibility = View.GONE
//                binding.homeBottomSheetRv.visibility = View.VISIBLE
//                setupRecyclerView(yesterdayCards)
//            } else {
//                binding.homeBottomsheetPseudo.visibility = View.VISIBLE
//                binding.homeBottomSheetRv.visibility = View.GONE
//            }
//        }

        // 우체통 클릭시 일기카드 생성 화면으로 전환(임시)
        binding.homeMailboxIv.setOnClickListener {
            userViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
                if (emotion != null){
                    userViewModel.cardId.observe(viewLifecycleOwner) { cardId ->
                        if (cardId == null) {
                            navController.navigate(R.id.action_navigation_home_to_create_fragment)
                            cardViewModel.disableLock(false)
                        }
                        else {
//                            cardViewModel.getCardDetail(cardId.toLong())
                            navController.navigate(R.id.action_navigation_home_to_modifyFragment)
                            cardViewModel.disableLock(true)
                        }
                    }
                }
                else{
                    Toast.makeText(requireContext(), "감정 우표를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 홈 화면 -> 알림 화면
        binding.homeNoticeIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_notice_fragment)
        }

        // 홈 화면 -> 감정 선택 화면
        binding.homeEmotionIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_home_option_fragment)
        }

//        // 홈화면 조회 후 사용자의 당일 감정우표 반영
//        userViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
//            when (emotion) {
//                "HAPPY" -> {
//                    viewModel.updateHomeEmotion(
//                        R.drawable.home_emotion_happy,
//                        getString(R.string.home_emotion_happy_title),
//                        R.color.y01,
//                        R.drawable.background_yellow
//                    )
//                }
//                "EXCITED" -> {
//                    viewModel.updateHomeEmotion(
//                        R.drawable.home_emotion_exciting,
//                        getString(R.string.home_emotion_exciting_title),
//                        R.color.b01,
//                        R.drawable.background_skyblue
//                    )
//                }
//                "NORMAL" -> {
//                    viewModel.updateHomeEmotion(
//                        R.drawable.home_emotion_normal,
//                        getString(R.string.home_emotion_normal_title),
//                        R.color.p01,
//                        R.drawable.background_purple
//                    )
//                }
//                "NERVOUS" -> {
//                    viewModel.updateHomeEmotion(
//                        R.drawable.home_emotion_anxiety,
//                        getString(R.string.home_emotion_anxiety_title),
//                        R.color.g02,
//                        R.drawable.background_green
//                    )
//                }
//                "ANGRY" -> {
//                    viewModel.updateHomeEmotion(
//                        R.drawable.home_emotion_upset,
//                        getString(R.string.home_emotion_upset_title),
//                        R.color.r01,
//                        R.drawable.background_red
//                    )
//                }
//            }
//        }

        // 감정 선택에 따른 홈화면 리소스 변경
//        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
//            binding.homeDateTv.text = date
//        }
//        viewModel.homeEmotion.observe(viewLifecycleOwner) { emotion ->
//            binding.homeEmotionIv.setImageResource(emotion)
//        }
//        viewModel.text.observe(viewLifecycleOwner) { text ->
//            binding.homeEmotionTv.text = text
//        }
//        viewModel.homeDateBackground.observe(viewLifecycleOwner) { date ->
//            binding.homeDateTv.setBackgroundResource(date)
//        }
//        viewModel.homeBackground.observe(viewLifecycleOwner) { background ->
//            binding.layoutHome.setBackgroundResource(background)
//        }

        // 알림 존재할 경우 알림 아이콘 빨간점 표시
        userViewModel.uncheckedAlarm.observe(viewLifecycleOwner) { uncheckedAlarm ->
            if (uncheckedAlarm) {
                binding.homeNoticeNew.visibility = View.VISIBLE
            } else {
                binding.homeNoticeNew.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView(items: List<YesterdayCard>) {

        val adapter = HomeBottomSheetAdapter(items.toMutableList(), listener)
        binding.homeBottomSheetRv.layoutManager = GridLayoutManager(context, 2)
        binding.homeBottomSheetRv.adapter = adapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val horizontalSpaceHeight = verticalSpaceHeight / 2
        binding.homeBottomSheetRv.addItemDecoration(
            HomeBottomSheetItemDecoration(
                horizontalSpaceHeight,
                verticalSpaceHeight
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}