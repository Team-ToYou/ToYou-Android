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
import com.toyou.toyouandroid.databinding.FragmentContentLongBinding

class QuestionContentLongFragment: Fragment() {

    private var _binding : FragmentContentLongBinding? = null
    private val binding : FragmentContentLongBinding get() = requireNotNull(_binding){"널"}

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
        _binding = FragmentContentLongBinding.inflate(inflater, container, false)


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
    }

}