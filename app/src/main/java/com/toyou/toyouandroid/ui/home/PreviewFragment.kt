package com.toyou.toyouandroid.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.toyou.toyouandroid.databinding.FragmentPreviewBinding

class PreviewFragment : Fragment(){

    private var _binding : FragmentPreviewBinding? = null
    private val binding: FragmentPreviewBinding get() = requireNotNull(_binding){"FragmentPreview 널"}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

}