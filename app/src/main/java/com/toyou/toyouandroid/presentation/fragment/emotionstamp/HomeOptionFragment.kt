package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeOptionBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.HomeOptionViewModel
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModel

class HomeOptionFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentHomeOptionBinding? = null
    private val binding: FragmentHomeOptionBinding
        get() = requireNotNull(_binding){"FragmentHomeOptionBinding -> null"}
    private val homeOptionViewModel: HomeOptionViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeOptionBinding.inflate(layoutInflater, container, false)
        binding.viewModel = homeOptionViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.homeOptionBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_option_to_home_fragment)
        }

        binding.homeStampOptionAnxietyIv.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("background_color", R.drawable.background_green)
                putString("text", getString(R.string.home_stamp_result_anxiety))
            }

            homeViewModel.updateHomeEmotion(
                R.drawable.home_emotion_anxiety,
                getString(R.string.home_emotion_anxiety_title),
                R.color.g02,
                R.drawable.background_green)

            homeViewModel.updateMypageEmotion(R.drawable.home_stamp_option_anxiety)

            navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
        }

        binding.homeStampOptionExcitingIv.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("background_color", R.drawable.background_skyblue)
                putString("text", getString(R.string.home_stamp_result_exciting))
            }

            homeViewModel.updateHomeEmotion(
                R.drawable.home_emotion_exciting,
                getString(R.string.home_emotion_exciting_title),
                R.color.b01,
                R.drawable.background_skyblue)

            homeViewModel.updateMypageEmotion(R.drawable.home_stamp_option_exciting)

            navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
        }

        binding.homeStampOptionHappyIv.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("background_color", R.drawable.background_yellow)
                putString("text", getString(R.string.home_stamp_result_happy))
            }

            homeViewModel.updateHomeEmotion(
                R.drawable.home_emotion_happy,
                getString(R.string.home_emotion_happy_title),
                R.color.y01,
                R.drawable.background_yellow)

            homeViewModel.updateMypageEmotion(R.drawable.home_stamp_option_happy)

            navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
        }

        binding.homeStampOptionNormalIv.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("background_color", R.drawable.background_purple)
                putString("text", getString(R.string.home_stamp_result_normal))
            }

            homeViewModel.updateHomeEmotion(
                R.drawable.home_emotion_normal,
                getString(R.string.home_emotion_normal_title),
                R.color.p01,
                R.drawable.background_purple)

            homeViewModel.updateMypageEmotion(R.drawable.home_stamp_option_normal)

            navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
        }

        binding.homeStampOptionUpsetIv.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("background_color", R.drawable.background_red)
                putString("text", getString(R.string.home_stamp_result_upset))
            }

            homeViewModel.updateHomeEmotion(
                R.drawable.home_emotion_upset,
                getString(R.string.home_emotion_upset_title),
                R.color.r01,
                R.drawable.background_red)

            homeViewModel.updateMypageEmotion(R.drawable.home_stamp_option_upset)

            navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
