package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionContentBinding
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel

class QuestionContentFragment : Fragment() {
    private var _binding : FragmentQuestionContentBinding? = null
    private val binding : FragmentQuestionContentBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
    private var questionEt : String = ""
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
        _binding = FragmentQuestionContentBinding.inflate(inflater, container, false)


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
                wordCount.text = "(0/50)"
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                questionEt = s.toString()
                wordCount.text = "${questionEt.length} / 50"
            }

            override fun afterTextChanged(s: Editable?) {
                wordCount.text = "${questionEt.length} / 50"
            }
        })

        socialViewModel.plusBoxVisibility.observe(viewLifecycleOwner, Observer { visibility ->
            binding.plusBox2Iv.visibility = if (visibility[0]) View.VISIBLE else View.GONE
            binding.plusDelete2Btn.visibility = if (visibility[0]) View.VISIBLE else View.GONE
            binding.plusBox3Iv.visibility = if (visibility[1]) View.VISIBLE else View.GONE
            binding.plusDelete3Btn.visibility = if (visibility[1]) View.VISIBLE else View.GONE
            binding.plusBox4Iv.visibility = if (visibility[2]) View.VISIBLE else View.GONE
            binding.plusDelete4Btn.visibility = if (visibility[2]) View.VISIBLE else View.GONE
        })


        binding.plusBtn.setOnClickListener {
            socialViewModel.togglePlusBoxVisibility()
        }

        binding.plusDelete2Btn.setOnClickListener {
            socialViewModel.hidePlusBox(0)
        }
        binding.plusDelete3Btn.setOnClickListener {
            socialViewModel.hidePlusBox(1)
        }
        binding.plusDelete4Btn.setOnClickListener {
            socialViewModel.hidePlusBox(2)
        }
    }
}