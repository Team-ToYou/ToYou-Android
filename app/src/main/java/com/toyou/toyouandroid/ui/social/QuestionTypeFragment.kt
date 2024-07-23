package com.toyou.toyouandroid.ui.social

import android.graphics.Color
import android.os.Bundle
import android.util.Log
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


        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_questionTypeFragment_to_questionContentFragment)
        }


        binding.char1Iv.setOnClickListener {
            if (binding.char2Iv.isSelected){
                binding.char2Iv.isSelected = !binding.char2Iv.isSelected
                binding.shortTv.isSelected = !binding.shortTv.isSelected
                binding.detail2Tv.isSelected =!binding.detail2Tv.isSelected
            } else if (binding.char3Iv.isSelected){
                binding.char3Iv.isSelected = !binding.char3Iv.isSelected
                binding.longTv.isSelected = !binding.longTv.isSelected
                binding.detail3Tv.isSelected =!binding.detail3Tv.isSelected
            }
            binding.char1Iv.isSelected = !binding.char1Iv.isSelected
            binding.chooseTv.isSelected = !binding.chooseTv.isSelected
            binding.detail1Tv.isSelected = !binding.detail1Tv.isSelected
            binding.nextBtn.isEnabled = binding.char1Iv.isSelected || binding.char2Iv.isSelected || binding.char3Iv.isSelected

        }
        binding.char2Iv.setOnClickListener {
            if (binding.char1Iv.isSelected){
                binding.char1Iv.isSelected = !binding.char1Iv.isSelected
                binding.chooseTv.isSelected = !binding.chooseTv.isSelected
                binding.detail1Tv.isSelected = !binding.detail1Tv.isSelected
            } else if (binding.char3Iv.isSelected){
                binding.char3Iv.isSelected = !binding.char3Iv.isSelected
                binding.longTv.isSelected = !binding.longTv.isSelected
                binding.detail3Tv.isSelected =!binding.detail3Tv.isSelected
            }
            binding.char2Iv.isSelected = !binding.char2Iv.isSelected
            binding.shortTv.isSelected = !binding.shortTv.isSelected
            binding.detail2Tv.isSelected =!binding.detail2Tv.isSelected
            binding.nextBtn.isEnabled = binding.char1Iv.isSelected || binding.char2Iv.isSelected || binding.char3Iv.isSelected

        }
        binding.char3Iv.setOnClickListener {
            if (binding.char1Iv.isSelected){
                binding.char1Iv.isSelected = !binding.char1Iv.isSelected
                binding.chooseTv.isSelected = !binding.chooseTv.isSelected
                binding.detail1Tv.isSelected = !binding.detail1Tv.isSelected
            } else if (binding.char2Iv.isSelected){
                binding.char2Iv.isSelected = !binding.char2Iv.isSelected
                binding.shortTv.isSelected = !binding.shortTv.isSelected
                binding.detail2Tv.isSelected =!binding.detail2Tv.isSelected
            }
            binding.char3Iv.isSelected = !binding.char3Iv.isSelected
            binding.longTv.isSelected = !binding.longTv.isSelected
            binding.detail3Tv.isSelected =!binding.detail3Tv.isSelected
            binding.nextBtn.isEnabled = binding.char1Iv.isSelected || binding.char2Iv.isSelected || binding.char3Iv.isSelected

        }

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



}