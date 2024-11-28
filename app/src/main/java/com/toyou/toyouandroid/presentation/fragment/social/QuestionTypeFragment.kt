package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.databinding.FragmentQuestionTypeBinding
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage

class QuestionTypeFragment : Fragment(){
    private var _binding : FragmentQuestionTypeBinding? = null
    private val binding : FragmentQuestionTypeBinding get() = requireNotNull(_binding){"질문타입 프래그먼트 널"}
    lateinit var navController: NavController
    private lateinit var socialViewModel : SocialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val socialService = AuthNetworkModule.getClient().create(SocialService::class.java)
        val socialRepository = SocialRepository(socialService)

        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(socialRepository, tokenManager)
        )[SocialViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuestionTypeBinding.inflate(inflater, container, false)

        val mainActivity = activity as MainActivity // casting
        mainActivity.hideBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)


        socialViewModel.selectedChar.observe(viewLifecycleOwner, Observer { charIndex ->
            updateSelection(charIndex)
            binding.nextBtn.setOnClickListener {
                when(charIndex){
                    1 -> {
                        socialViewModel.setTypeFriend("OPTIONAL")
                        navController.navigate(R.id.action_questionTypeFragment_to_questionContentFragment)
                    }
                    2 -> {
                        socialViewModel.setTypeFriend("SHORT_ANSWER")
                        navController.navigate(R.id.action_questionTypeFragment_to_questionContentLongFragment)
                    }
                    else -> {
                        socialViewModel.setTypeFriend("LONG_ANSWER")
                        navController.navigate(R.id.action_questionTypeFragment_to_questionContentLongFragment)
                    }
                }
            }
        })



        socialViewModel.nextBtnEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            binding.nextBtn.isEnabled = isEnabled
        })

        binding.oneFrame.setOnClickListener { socialViewModel.onCharSelected(1) }
        binding.twoFrame.setOnClickListener { socialViewModel.onCharSelected(2) }
        binding.threeFrame.setOnClickListener { socialViewModel.onCharSelected(3) }



        binding.backFrame.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(false)
            navController.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val mainActivity = activity as MainActivity
                mainActivity.hideBottomNavigation(false)
                navController.popBackStack()            }

        })

        socialViewModel.selectedEmotionMent.observe(viewLifecycleOwner) { ment,  ->
            binding.normalTv.text = ment
        }

        socialViewModel.selectedEmotion.observe(viewLifecycleOwner) { emotion,  ->
            when (emotion) {
                1 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_happy)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_happy)
                }
                2 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_excited)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_excited)
                }
                3 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_normal)
                    binding.imogeIv.setBackgroundResource(R.drawable.social_imoge)
                }
                4 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_anxiety)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_anxiety)
                }
                5 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_angry)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_angry)
                }
                else -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_no)
                    binding.imogeIv.setBackgroundResource(0)
                    binding.normalTv.text = "친구가 아직 감정우표를 선택하지 않았어요"
                }
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateSelection(charIndex: Int) {
        binding.char1Iv.isSelected = charIndex == 1
        binding.char2Iv.isSelected = charIndex == 2
        binding.char3Iv.isSelected = charIndex == 3

        binding.chooseTv.isSelected = charIndex == 1
        binding.shortTv.isSelected = charIndex == 2
        binding.longTv.isSelected = charIndex == 3

        binding.detail1Tv.isSelected = charIndex == 1
        binding.detail2Tv.isSelected = charIndex == 2
        binding.detail3Tv.isSelected = charIndex == 3
    }

}