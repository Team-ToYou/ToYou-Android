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
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.network.EmotionData
import com.toyou.toyouandroid.presentation.fragment.emotionstamp.network.EmotionRequest
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

        setupEmotionClickListeners()
    }

    private fun setupEmotionClickListeners() {
        val emotionData = listOf(
            EmotionData(
                imageView = binding.homeStampOptionHappyIv,
                emotion = EmotionRequest("HAPPY"),
                homeEmotionDrawable = R.drawable.home_emotion_happy,
                homeEmotionTitle = getString(R.string.home_emotion_happy_title),
                homeColorRes = R.color.y01,
                backgroundDrawable = R.drawable.background_yellow,
                mypageEmotionDrawable = R.drawable.home_stamp_option_happy
            ),
            EmotionData(
                imageView = binding.homeStampOptionExcitingIv,
                emotion = EmotionRequest("EXCITED"),
                homeEmotionDrawable = R.drawable.home_emotion_exciting,
                homeEmotionTitle = getString(R.string.home_emotion_exciting_title),
                homeColorRes = R.color.b01,
                backgroundDrawable = R.drawable.background_skyblue,
                mypageEmotionDrawable = R.drawable.home_stamp_option_exciting
            ),
            EmotionData(
                imageView = binding.homeStampOptionNormalIv,
                emotion = EmotionRequest("NORMAL"),
                homeEmotionDrawable = R.drawable.home_emotion_normal,
                homeEmotionTitle = getString(R.string.home_emotion_normal_title),
                homeColorRes = R.color.p01,
                backgroundDrawable = R.drawable.background_purple,
                mypageEmotionDrawable = R.drawable.home_stamp_option_normal
            ),
            EmotionData(
                imageView = binding.homeStampOptionAnxietyIv,
                emotion = EmotionRequest("NERVOUS"),
                homeEmotionDrawable = R.drawable.home_emotion_anxiety,
                homeEmotionTitle = getString(R.string.home_emotion_anxiety_title),
                homeColorRes = R.color.g02,
                backgroundDrawable = R.drawable.background_green,
                mypageEmotionDrawable = R.drawable.home_stamp_option_anxiety
            ),
            EmotionData(
                imageView = binding.homeStampOptionUpsetIv,
                emotion = EmotionRequest("ANGRY"),
                homeEmotionDrawable = R.drawable.home_emotion_upset,
                homeEmotionTitle = getString(R.string.home_emotion_upset_title),
                homeColorRes = R.color.r01,
                backgroundDrawable = R.drawable.background_red,
                mypageEmotionDrawable = R.drawable.home_stamp_option_upset
            )
        )

        emotionData.forEach { data ->
            data.imageView.setOnClickListener {
                updateEmotion(data)
            }
        }
    }

    private fun updateEmotion(emotionData: EmotionData) {
        val bundle = Bundle().apply {
            putInt("background_color", emotionData.backgroundDrawable)
            putString("text", emotionData.homeEmotionTitle)
        }

        homeViewModel.updateHomeEmotion(
            emotionData.homeEmotionDrawable,
            emotionData.homeEmotionTitle,
            emotionData.homeColorRes,
            emotionData.backgroundDrawable
        )

        homeViewModel.updateMypageEmotion(emotionData.mypageEmotionDrawable)

        homeOptionViewModel.updateEmotion(userId = 1, emotionRequest = emotionData.emotion)

        navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
