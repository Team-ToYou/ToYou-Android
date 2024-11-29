package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        binding.signupAgreeBackLayout.setOnClickListener {
            navController.popBackStack()
        }

        binding.signupAgreeDetails3.setOnClickListener{
            termsOfUseLink()
        }
        binding.signupAgreeDetails4.setOnClickListener{
            termsOfUseLink()
        }
        binding.signupAgreeArrow3.setOnClickListener{
            termsOfUseLink()
        }
        binding.signupAgreeArrow4.setOnClickListener{
            termsOfUseLink()
        }

        val checkboxLayouts = listOf(
            binding.checkbox1Layout,
            binding.checkbox2Layout,
            binding.checkbox3Layout,
            binding.checkbox4Layout,
            //binding.checkbox5Layout
        )

        val imageViews = listOf(
            binding.checkbox1,
            binding.checkbox2,
            binding.checkbox3,
            binding.checkbox4
        )

        imageViews.forEachIndexed { _, imageView ->
            imageView.tag = R.drawable.checkbox_uncheck
        }

        checkboxLayouts.forEachIndexed { index, layout ->
            layout.setOnClickListener {
                val imageView = imageViews[index]
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

        viewModel.imageStates.observe(viewLifecycleOwner) { imageStates ->
            imageStates.forEachIndexed { index, state ->
                val newImageResId = if (state) R.drawable.checkbox_checked else R.drawable.checkbox_uncheck
                imageViews[index].setImageResource(newImageResId)
                imageViews[index].tag = newImageResId
            }
        }

        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            binding.signupagreeNextBtn.isEnabled = isEnabled
        }

        binding.signupagreeNextBtn.setOnClickListener{
            navController.navigate(R.id.action_navigation_signup_agree_to_signup_nickname_fragment)
        }
    }

    private fun termsOfUseLink() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse("https://sumptuous-metacarpal-d3a.notion.site/1437c09ca64e80fb88f6d8ab881ffee3")
        startActivity(i)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}