package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSignupagreeBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class SignupAgreeFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSignupagreeBinding? = null
    private val binding: FragmentSignupagreeBinding
        get() = requireNotNull(_binding){"FragmentSignupagreeBinding -> null"}
    private val viewModel: SignupAgreeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSignupagreeBinding.inflate(inflater, container, false)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = findNavController()

        val imageViews = listOf(
            binding.checkbox1,
            binding.checkbox2,
            binding.checkbox3,
            binding.checkbox4,
            binding.checkbox5
        )

        imageViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                val currentImageResId = imageView.tag as? Int ?: R.drawable.checkbox_uncheck
                val newImageResId = if (currentImageResId == R.drawable.checkbox_uncheck) {
                    R.drawable.checkbox_checked
                } else {
                    R.drawable.checkbox_uncheck
                }
                imageView.setImageResource(newImageResId)
                imageView.tag = newImageResId
                viewModel.onImageClicked(index, newImageResId)
            }
        }

        viewModel.imageStates.observe(viewLifecycleOwner, Observer { imageStates ->
            imageStates.forEachIndexed { index, state ->
                val newImageResId = if (state) R.drawable.checkbox_checked else R.drawable.checkbox_uncheck
                imageViews[index].setImageResource(newImageResId)
                imageViews[index].tag = newImageResId
            }
        })

        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupagreeNextBtn.isEnabled = isEnabled
        }

        binding.signupagreeNextBtn.setOnClickListener{
            navController.navigate(R.id.action_navigation_signup_agree_to_signup_nickname_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}