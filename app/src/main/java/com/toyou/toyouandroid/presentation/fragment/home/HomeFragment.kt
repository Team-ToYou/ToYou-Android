package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel

class HomeFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = requireNotNull(_binding){"FragmentHomeBinding -> null"}
    private val viewModel: HomeViewModel by activityViewModels()

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

//        viewModel.selectedStamp.observe(viewLifecycleOwner) { stamp ->
//            (activity as MainActivity).setSelectedStamp(stamp)
//        }

        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(false)

        // 우체통 클릭시 일기카드 생성 화면으로 전환(임시)
        binding.homeMailboxIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_create_fragment)
        }

        binding.homeNoticeIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_notice_fragment)
        }

        binding.homeEmotionIv.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_to_home_option_fragment)
        }

//        val bottomSheetFragment = HomeBottomSheetFragment()
//        bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}