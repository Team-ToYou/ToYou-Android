package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.UserDatabase
import com.toyou.toyouandroid.databinding.FragmentHomeBinding
import com.toyou.toyouandroid.model.HomeBottomSheetItem
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.home.adapter.HomeBottomSheetAdapter
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel

class HomeFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}
    private val viewModel: HomeViewModel by activityViewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var userViewModel: UserViewModel
    private lateinit var database: UserDatabase
    private lateinit var cardViewModel: CardViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        cardViewModel = ViewModelProvider(requireActivity()).get(CardViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(false)
        database = UserDatabase.getDatabase(requireContext())


        val bottomSheet: ConstraintLayout = binding.homeBottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)

        val items = listOf(
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "테디"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "승원"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "현정"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "유은"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "테디"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "승원"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "현정"),
            HomeBottomSheetItem(R.drawable.home_bottom_sheet_card, "유은")
        )

        val adapter = HomeBottomSheetAdapter(items)
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

        viewModel.currentDate.observe(viewLifecycleOwner) { date ->
            binding.homeDateTv.text = date
        }

        // 우체통 클릭시 일기카드 생성 화면으로 전환(임시)
        binding.homeMailboxIv.setOnClickListener {

            userViewModel.cardId.observe(viewLifecycleOwner, Observer { cardId ->
                if (cardId == 0)
                    navController.navigate(R.id.action_navigation_home_to_create_fragment)
                else {
                    cardViewModel.getCardDetail(cardId.toLong())
                    navController.navigate(R.id.action_navigation_home_to_modifyFragment)
                }
            })
            Log.d("cardID", userViewModel.cardId.value.toString())
        }

        binding.homeNoticeIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_notice_fragment)
        }

        binding.homeEmotionIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_home_option_fragment)
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
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}