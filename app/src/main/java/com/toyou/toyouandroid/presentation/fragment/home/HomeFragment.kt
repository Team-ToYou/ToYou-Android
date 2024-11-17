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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.UserDatabase
import com.toyou.toyouandroid.databinding.FragmentHomeBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.data.emotion.dto.DiaryCard
import com.toyou.toyouandroid.presentation.fragment.home.adapter.HomeBottomSheetAdapter
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeViewModel
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.data.notice.service.NoticeService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.NoticeViewModelFactory
import com.toyou.toyouandroid.presentation.fragment.record.CardInfoViewModel
import com.toyou.toyouandroid.presentation.viewmodel.RecordViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}
    private lateinit var noticeViewModel: NoticeViewModel
    private lateinit var viewModel: HomeViewModel

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: UserDatabase
    private lateinit var cardViewModel: CardViewModel

    private lateinit var listener: HomeBottomSheetClickListener
    private lateinit var cardInfoViewModel: CardInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val noticeService = AuthNetworkModule.getClient().create(NoticeService::class.java)
        val noticeRepository = NoticeRepository(noticeService)

        val recordService = AuthNetworkModule.getClient().create(RecordService::class.java)
        val recordRepository = RecordRepository(recordService)

        noticeViewModel = ViewModelProvider(
            this,
            NoticeViewModelFactory(noticeRepository, tokenManager)
        )[NoticeViewModel::class.java]

        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(tokenManager)
        )[HomeViewModel::class.java]

        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(tokenStorage)
        )[CardViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

        cardInfoViewModel = ViewModelProvider(
            requireActivity(),
            RecordViewModelFactory(recordRepository, tokenManager)
        )[CardInfoViewModel::class.java]

        userViewModel.getHomeEntry()
        noticeViewModel.fetchNotices()

        listener = object : HomeBottomSheetClickListener {
            override fun onDiaryCardClick(cardId: Int?) {
                Timber.tag("HomeFragment").d("$cardId")
                cardId?.let {
                    cardInfoViewModel.getCardDetail(cardId.toLong())
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

        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)

        database = UserDatabase.getDatabase(requireContext())

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
        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.homeBottomsheetPseudo.visibility = View.VISIBLE
                binding.homeBottomSheetRv.visibility = View.GONE
            } else {
                binding.homeBottomsheetPseudo.visibility = View.GONE
                binding.homeBottomSheetRv.visibility = View.VISIBLE
            }
        }

        // 작일 친구 일기 카드 자동 조회
        viewModel.loadYesterdayDiaryCards()

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

        // 홈 화면 바텀 시트 설정
        viewModel.diaryCards.observe(viewLifecycleOwner) { diaryCards ->
            if (diaryCards != null) {
                setupRecyclerView(diaryCards)
            }
        }

        // 우체통 클릭시 일기카드 생성 화면으로 전환(임시)
        binding.homeMailboxIv.setOnClickListener {
            userViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
                if (emotion != null){
                    userViewModel.cardId.observe(viewLifecycleOwner) { cardId ->
                        if (cardId == null) {
                            navController.navigate(R.id.action_navigation_home_to_create_fragment)
                        }
                        else {
                        Timber.tag("mail").d("click")

                        cardViewModel.getCardDetail(cardId.toLong())
                            navController.navigate(R.id.action_navigation_home_to_modifyFragment)
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

        // 홈화면 조회 후 사용자의 당일 감정우표 반영
        userViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
            when (emotion) {
                "HAPPY" -> {
                    viewModel.updateHomeEmotion(
                        R.drawable.home_emotion_happy,
                        getString(R.string.home_emotion_happy_title),
                        R.color.y01,
                        R.drawable.background_yellow
                    )
                }
                "EXCITED" -> {
                    viewModel.updateHomeEmotion(
                        R.drawable.home_emotion_exciting,
                        getString(R.string.home_emotion_exciting_title),
                        R.color.b01,
                        R.drawable.background_skyblue
                    )
                }
                "NORMAL" -> {
                    viewModel.updateHomeEmotion(
                        R.drawable.home_emotion_normal,
                        getString(R.string.home_emotion_normal_title),
                        R.color.p01,
                        R.drawable.background_purple
                    )
                }
                "NERVOUS" -> {
                    viewModel.updateHomeEmotion(
                        R.drawable.home_emotion_anxiety,
                        getString(R.string.home_emotion_anxiety_title),
                        R.color.g02,
                        R.drawable.background_green
                    )
                }
                "ANGRY" -> {
                    viewModel.updateHomeEmotion(
                        R.drawable.home_emotion_upset,
                        getString(R.string.home_emotion_upset_title),
                        R.color.r01,
                        R.drawable.background_red
                    )
                }
            }
        }

        // 감정 선택에 따른 홈화면 변경
        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
            binding.homeDateTv.text = date
        }
        viewModel.homeEmotion.observe(viewLifecycleOwner) { emotion ->
            binding.homeEmotionIv.setImageResource(emotion)
        }
        viewModel.text.observe(viewLifecycleOwner) { text ->
            binding.homeEmotionTv.text = text
        }
        viewModel.homeDateBackground.observe(viewLifecycleOwner) { date ->
            binding.homeDateTv.setBackgroundResource(date)
        }
        viewModel.homeBackground.observe(viewLifecycleOwner) { background ->
            binding.layoutHome.setBackgroundResource(background)
        }

        // 알림 존재할 경우 알림 아이콘 빨간점 표시
        noticeViewModel.hasNotifications.observe(viewLifecycleOwner) { hasNotifications ->
            if (hasNotifications) {
                binding.homeNoticeNew.visibility = View.VISIBLE
            } else {
                binding.homeNoticeNew.visibility = View.GONE
            }
        }
    }

    private fun setupRecyclerView(items: List<DiaryCard>) {

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