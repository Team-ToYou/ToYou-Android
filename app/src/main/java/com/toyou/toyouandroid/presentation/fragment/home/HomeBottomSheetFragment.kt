package com.toyou.toyouandroid.presentation.fragment.home

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeBottomSheetBinding
import com.toyou.toyouandroid.model.HomeBottomSheetItem
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.home.adapter.HomeBottomSheetAdapter

class HomeBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.NoDimBottomSheetDialogTheme)
        dialog.setCanceledOnTouchOutside(false)  // 외부 터치로 닫히지 않게 설정
        return dialog
    }

    private lateinit var binding: FragmentHomeBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
            HomeBottomSheetItem("drawable/send_card.xml"),
        )

        val adapter = HomeBottomSheetAdapter(items)
        binding.homeBottomSheetRv.layoutManager = GridLayoutManager(context, 2)
        binding.homeBottomSheetRv.adapter = adapter

        // BottomSheetBehavior 설정
        val dialog = dialog as BottomSheetDialog
        val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.peekHeight = 200 // 초기 상태에서 보여지는 높이 설정

        // 드래그 가능한 설정
        behavior.isHideable = false // 필요시 숨길 수 있는 옵션
        behavior.skipCollapsed = false // 바로 확장하지 않도록 설정
        behavior.isDraggable = true

        // BottomNavigationView를 최상위로 가져오기
        view.post {
            val activity = activity as? MainActivity
            activity?.bringBottomNavigationViewToFront()
        }
    }
}