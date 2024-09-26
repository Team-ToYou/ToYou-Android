package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.Observer
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
import com.toyou.toyouandroid.presentation.fragment.home.adapter.HomeBottomSheetAdapter
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeViewModel
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeRepository
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeService
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}
    private val viewModel: HomeViewModel by activityViewModels()
    private val noticeViewModel: NoticeViewModel by viewModels {
        NoticeViewModelFactory(NoticeRepository(AuthNetworkModule.getClient().create(NoticeService::class.java)))
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: UserDatabase
    private lateinit var cardViewModel: CardViewModel

//    private lateinit var diaryAdapter: HomeBottomSheetAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // onBackPressedCallback 등록
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        val tokenStorage = TokenStorage(requireContext())
        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(tokenStorage)
        )[CardViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

        userViewModel.getHomeEntry()
        noticeViewModel.fetchNotices()

        userViewModel.cardId.observe(viewLifecycleOwner) { cardId ->
            Timber.tag("get home").d(cardId.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)
        database = UserDatabase.getDatabase(requireContext())

        userViewModel.cardNum.observe(viewLifecycleOwner) { cardNum ->
            Timber.tag("get home").d(cardNum.toString())

            val imageRes = when {
                cardNum == 0 -> R.drawable.home_mailbox_none
                cardNum in 1..9 -> R.drawable.home_mailbox_single
                cardNum >= 10 -> R.drawable.home_mailbox_multiple
                else -> R.drawable.home_mailbox_none
            }
            binding.homeMailboxIv.setImageResource(imageRes)
        }


        val bottomSheet: ConstraintLayout = binding.homeBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        viewModel.diaryCards.observe(viewLifecycleOwner) { diaryCards ->
//            diaryAdapter.submitList(diaryCards)
        }

        viewModel.isEmpty.observe(viewLifecycleOwner) { isEmpty ->
            if (isEmpty) {
                binding.homeBottomsheetPseudo.visibility = View.VISIBLE
                binding.homeBottomSheetRv.visibility = View.GONE
            } else {
                binding.homeBottomsheetPseudo.visibility = View.GONE
                binding.homeBottomSheetRv.visibility = View.VISIBLE
            }
        }

        viewModel.loadYesterdayDiaryCards()

        binding.homeBottomSheet.apply {
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        // 터치 시작 시 색깔 변경
                        binding.homeBottomSheetTouchBar.setBackgroundResource(R.drawable.next_button_enabled_roundly)
                        true
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        // 터치 종료 시 원래 색깔로 변경
                        binding.homeBottomSheetTouchBar.setBackgroundResource(R.drawable.next_button_roundly)
                        v.performClick() // performClick 호출
                        true
                    }
                    else -> false
                }
            }

            setOnClickListener {
                // performClick 시 수행할 작업
            }
        }

        // 우체통 클릭시 일기카드 생성 화면으로 전환(임시)
        binding.homeMailboxIv.setOnClickListener {
            Log.d("mail", "click")
            userViewModel.emotion.observe(viewLifecycleOwner, Observer { emotion ->
                if (emotion != null){
                    Log.d("mail", "click")
                    userViewModel.cardId.observe(viewLifecycleOwner, Observer { cardId ->
                        if (cardId == null) {
                            Log.d("mail", "click")

                            navController.navigate(R.id.action_navigation_home_to_create_fragment)
                        }
                        else {
                        Log.d("mail", "click")

                        cardViewModel.getCardDetail(cardId.toLong())
                            navController.navigate(R.id.action_navigation_home_to_modifyFragment)
                        }
                    })
                    Log.d("cardID", userViewModel.cardId.value.toString())
                } else{
                    Log.d("mail", "click")
                    Toast.makeText(requireContext(), "감정 우표를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.homeNoticeIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_notice_fragment)
        }

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

        noticeViewModel.hasNotifications.observe(viewLifecycleOwner) { hasNotifications ->
            if (hasNotifications) {
                binding.homeNoticeNew.visibility = View.VISIBLE
            } else {
                binding.homeNoticeNew.visibility = View.GONE
            }
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        val adapter = HomeBottomSheetAdapter()
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