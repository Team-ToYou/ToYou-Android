package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toyou.toyouandroid.databinding.FragmentHomeOptionBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.HomeOptionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeOptionFragment : Fragment() {

    private var _binding: FragmentHomeOptionBinding? = null
    private val binding: FragmentHomeOptionBinding
        get() = requireNotNull(_binding){"FragmentHomeOptionBinding -> null"}
    private val viewModel: HomeOptionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeOptionBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
