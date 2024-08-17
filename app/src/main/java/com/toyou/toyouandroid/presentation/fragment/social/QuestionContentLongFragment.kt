package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentContentLongBinding
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel

class QuestionContentLongFragment: Fragment() {

    private var _binding : FragmentContentLongBinding? = null
    private val binding : FragmentContentLongBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
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
        _binding = FragmentContentLongBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = socialViewModel

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        val wordCount: TextView = binding.limit200

        binding.nextBtn.setOnClickListener {
            navController.navigate(R.id.action_questionContentFragment_to_sendFragment)
        }
        binding.backBtn.setOnClickListener {
            navController.popBackStack()

        }

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
                Log.d("질문", socialViewModel.questionDto.value.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

}