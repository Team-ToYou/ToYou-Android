package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionTypeBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel

class QuestionTypeFragment : Fragment(){
    private var _binding : FragmentQuestionTypeBinding? = null
    private val binding : FragmentQuestionTypeBinding get() = requireNotNull(_binding){"질문타입 프래그먼트 널"}
    lateinit var navController: NavController
    private lateinit var socialViewModel : SocialViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socialViewModel = ViewModelProvider(requireActivity()).get(SocialViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                if (charIndex ==1 ) navController.navigate(R.id.action_questionTypeFragment_to_questionContentFragment)
                else navController.navigate(R.id.action_questionTypeFragment_to_questionContentLongFragment)
            }
        })

        socialViewModel.nextBtnEnabled.observe(viewLifecycleOwner, Observer { isEnabled ->
            binding.nextBtn.isEnabled = isEnabled
        })

        binding.char1Iv.setOnClickListener { socialViewModel.onCharSelected(1) }
        binding.char2Iv.setOnClickListener { socialViewModel.onCharSelected(2) }
        binding.char3Iv.setOnClickListener { socialViewModel.onCharSelected(3) }



        binding.backBtn.setOnClickListener {
            val mainActivity = activity as MainActivity
            mainActivity.hideBottomNavigation(false)
            navController.popBackStack()

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