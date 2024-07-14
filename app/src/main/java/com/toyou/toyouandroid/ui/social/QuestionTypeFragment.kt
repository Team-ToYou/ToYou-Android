package com.toyou.toyouandroid.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionTypeBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class QuestionTypeFragment : Fragment(){
    private var _binding : FragmentQuestionTypeBinding? = null
    private val binding : FragmentQuestionTypeBinding get() = requireNotNull(_binding){"질문타입 프래그먼트 널"}
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}