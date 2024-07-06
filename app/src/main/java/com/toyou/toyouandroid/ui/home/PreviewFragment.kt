package com.toyou.toyouandroid.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.toyou.toyouandroid.MainActivity
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding
import com.toyou.toyouandroid.ui.home.adapter.CardPreviewListAdapter
import com.toyou.toyouandroid.view_model.CardViewModel

class PreviewFragment : Fragment(){

    private var _binding : FragmentPreviewBinding? = null
    private val binding: FragmentPreviewBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    private lateinit var listAdapter : CardPreviewListAdapter
    private lateinit var cardViewModel : CardViewModel

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardViewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        cardViewModel.loadCardData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}