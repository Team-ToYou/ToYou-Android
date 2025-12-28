package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeOptionBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.data.emotion.dto.EmotionData
import com.toyou.toyouandroid.data.emotion.dto.EmotionRequest
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.home.repository.HomeRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeDialog
import com.toyou.toyouandroid.presentation.fragment.notice.NoticeDialogViewModel
import com.toyou.toyouandroid.presentation.fragment.home.HomeViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.findNavController

@AndroidEntryPoint
class HomeOptionFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentHomeOptionBinding? = null
    private val binding: FragmentHomeOptionBinding
        get() = requireNotNull(_binding){"FragmentHomeOptionBinding -> null"}

    private val noticeDialogViewModel: NoticeDialogViewModel by activityViewModels()
    private var noticeDialog: NoticeDialog? = null

    private lateinit var userViewModel: UserViewModel
    private val homeOptionViewModel: HomeOptionViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeOptionBinding.inflate(layoutInflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)

        // HomeOptionViewModel과 HomeViewModel은 Hilt로 주입됨

        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(createRepository, tokenManager)
        )[UserViewModel::class.java]

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        binding.homeOptionBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_home_option_to_home_fragment)
        }

        setupEmotionClickListeners()

        // 기본 모달창
        onShowDialog()
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
                if (userViewModel.emotion.value != null) {
                    Toast.makeText(requireContext(),"감정은 하루에 한 번만 선택할 수 있어요", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_navigation_home_option_to_home_fragment)
                } else {
                    updateEmotion(data)
                }
            }
        }
    }

    private fun updateEmotion(emotionData: EmotionData) {
        // 랜덤 숫자 생성
        val randomNumber = (1..3).random()

        // 감정에 따라 리소스 ID를 매핑하는 맵
        val emotionTitlesMap = mapOf(
            "HAPPY" to when(randomNumber) {
                1 -> R.string.home_stamp_result_happy_1
                2 -> R.string.home_stamp_result_happy_2
                3 -> R.string.home_stamp_result_happy_3
                else -> R.string.home_stamp_result_happy_1 // 기본값
            },
            "EXCITED" to when (randomNumber) {
                1 -> R.string.home_stamp_result_exciting_1
                2 -> R.string.home_stamp_result_exciting_2
                3 -> R.string.home_stamp_result_exciting_3
                else -> R.string.home_stamp_result_exciting_1 // 기본값
            },
            "NORMAL" to when (randomNumber) {
                1 -> R.string.home_stamp_result_normal_1
                2 -> R.string.home_stamp_result_normal_2
                3 -> R.string.home_stamp_result_normal_3
                else -> R.string.home_stamp_result_normal_1 // 기본값
            },
            "NERVOUS" to when (randomNumber) {
                1 -> R.string.home_stamp_result_anxiety_1
                2 -> R.string.home_stamp_result_anxiety_2
                3 -> R.string.home_stamp_result_anxiety_3
                else -> R.string.home_stamp_result_anxiety_1 // 기본값
            },
            "ANGRY" to when (randomNumber) {
                1 -> R.string.home_stamp_result_upset_1
                2 -> R.string.home_stamp_result_upset_2
                3 -> R.string.home_stamp_result_upset_3
                else -> R.string.home_stamp_result_upset_1 // 기본값
            }
        )

        // 선택된 감정에 해당하는 리소스 ID 가져오기
        val emotionTitleResId = emotionTitlesMap[emotionData.emotion.emotion]
            ?: R.string.home_stamp_result_normal_1 // 기본값

        val bundle = Bundle().apply {
            putInt("background_color", emotionData.backgroundDrawable)
            putString("text", getString(emotionTitleResId)) // 선택한 텍스트 전달
        }

        homeViewModel.updateHomeEmotion(
            emotionData.homeEmotionDrawable.toString(),
//            emotionData.homeEmotionTitle,
//            emotionData.homeColorRes,
//            emotionData.backgroundDrawable
        )

        // 감정 우표 선택 API 호출
        homeOptionViewModel.updateEmotion(emotionRequest = emotionData.emotion)

        navController.navigate(R.id.action_navigation_home_option_to_home_result_fragment, bundle)
    }

    private fun onShowDialog() {
        noticeDialogViewModel.setDialogData(
            title = "감정은 하루에 한 번만\n선택할 수 있어요",
            leftButtonText = "확인",
            leftButtonClickAction = { dismissDialog() }
        )
        noticeDialog = NoticeDialog()
        noticeDialog?.show(parentFragmentManager, "CustomDialog")
    }

    private fun dismissDialog() {
        noticeDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
