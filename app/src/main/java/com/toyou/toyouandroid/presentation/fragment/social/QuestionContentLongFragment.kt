package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.databinding.FragmentContentLongBinding
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.HomeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage

class QuestionContentLongFragment: Fragment() {

    private var _binding : FragmentContentLongBinding? = null
    private val binding : FragmentContentLongBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
    private lateinit var socialViewModel : SocialViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        socialViewModel = ViewModelProvider(
            requireActivity(),
            HomeViewModelFactory(tokenManager)
        )[SocialViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentLongBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = socialViewModel

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_questionContentLongFragment_to_sendFragment)
        }
        binding.backFrame.setOnClickListener {
            socialViewModel.removeContent()
            navController.popBackStack()

        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                socialViewModel.removeContent()
                navController.popBackStack()
            }

        })

        binding.questionBoxEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                socialViewModel.questionDto.value?.content = s.toString()
                binding.limit200.text = String.format("(%d/50)", s?.length ?: 0)

                binding.nextBtn.isEnabled = !s.isNullOrEmpty()

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        socialViewModel.selectedEmotionMent.observe(viewLifecycleOwner) { ment,  ->
            binding.normalTv.text = ment
        }

        socialViewModel.selectedEmotion.observe(viewLifecycleOwner) { emotion,  ->
            when (emotion) {
                1 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_happy)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_happy)
                    binding.imageView2.setBackgroundResource(R.drawable.balloon_happy2)
                }
                2 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_excited)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_excited)
                    binding.imageView2.setBackgroundResource(R.drawable.balloon_excited2)
                }
                3 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.social_ballon)
                    binding.imogeIv.setBackgroundResource(R.drawable.social_imoge)
                    binding.imageView2.setBackgroundResource(R.drawable.social_balloon)
                }
                4 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_anxiety)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_anxiety)
                    binding.imageView2.setBackgroundResource(R.drawable.balloon_anxiety2)
                }
                5 -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_angry)
                    binding.imogeIv.setBackgroundResource(R.drawable.imoge_angry)
                    binding.imageView2.setBackgroundResource(R.drawable.balloon_angry2)
                }
                else -> {
                    binding.balloonTv.setBackgroundResource(R.drawable.balloon_no)
                    binding.imogeIv.setBackgroundResource(0)
                    binding.imageView2.setBackgroundResource(R.drawable.balloon_no2)
                    binding.normalTv.text = "친구가 아직 감정우표를 선택하지 않았어요"
                }
            }
        }
    }

}