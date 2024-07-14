package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.toyou.toyouandroid.databinding.FragmentHomeBinding
import com.toyou.toyouandroid.databinding.FragmentHomeResultBinding
import com.toyou.toyouandroid.presentation.viewmodel.HomeResultViewModel

class HomeResultFragment : Fragment() {

    private var _binding: FragmentHomeResultBinding? = null
    private val binding: FragmentHomeResultBinding
        get() = requireNotNull(_binding){"FragmentHomeResultBinding -> null"}
    private val viewModel: HomeResultViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeResultBinding.inflate(layoutInflater, container, false)
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
