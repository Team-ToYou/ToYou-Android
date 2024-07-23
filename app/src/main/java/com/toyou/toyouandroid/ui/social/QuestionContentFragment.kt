package com.toyou.toyouandroid.ui.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentQuestionContentBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class QuestionContentFragment : Fragment() {
    private var _binding : FragmentQuestionContentBinding? = null
    private val binding : FragmentQuestionContentBinding get() = requireNotNull(_binding){"널"}

    private lateinit var navController: NavController
    private var questionEt : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        binding.plusBtn.setOnClickListener {
            if (binding.plusBox2Iv.visibility == View.GONE)
            {
                binding.plusBox2Iv.visibility = View.VISIBLE
                binding.plusDelete2Btn.visibility = View.VISIBLE
            } else if (binding.plusBox2Iv.visibility == View.VISIBLE && binding.plusBox3Iv.visibility ==View.GONE)
        {
            binding.plusBox3Iv.visibility = View.VISIBLE
            binding.plusDelete3Btn.visibility = View.VISIBLE
        } else if (binding.plusBox2Iv.visibility == View.VISIBLE && binding.plusBox3Iv.visibility ==View.VISIBLE
                && binding.plusBox4Iv.visibility ==View.GONE)
            {
                binding.plusBox4Iv.visibility = View.VISIBLE
                binding.plusDelete4Btn.visibility = View.VISIBLE
            }
        }

        binding.plusDelete2Btn.setOnClickListener {
            binding.plusBox2Iv.visibility = View.GONE
            binding.plusDelete2Btn.visibility = View.GONE
        }
        binding.plusDelete3Btn.setOnClickListener {
            binding.plusBox3Iv.visibility = View.GONE
            binding.plusDelete3Btn.visibility = View.GONE
        }
        binding.plusDelete4Btn.setOnClickListener {
            binding.plusBox4Iv.visibility = View.GONE
            binding.plusDelete4Btn.visibility = View.GONE
        }
    }
}