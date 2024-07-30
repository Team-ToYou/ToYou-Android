package com.toyou.toyouandroid.presentation.fragment.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeBinding
import com.toyou.toyouandroid.model.NoticeItem
import com.toyou.toyouandroid.utils.SwipeToDeleteNotice

class NoticeFragment : Fragment() {

    lateinit var navController: NavController

    private var _binding: FragmentNoticeBinding? = null

    private val binding: FragmentNoticeBinding
        get() = requireNotNull(_binding){"FragmentNoticeBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val items = mutableListOf(
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeCardCheckItem("승원"),
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeFriendRequestAcceptedItem("유은"),
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeCardCheckItem("현정"),
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeFriendRequestAcceptedItem("승원"),
            NoticeItem.NoticeFriendRequestAcceptedItem("테디"),
            NoticeItem.NoticeCardCheckItem("유은"),
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeCardCheckItem("현정"),
            NoticeItem.NoticeFriendRequestItem("테디"),
            NoticeItem.NoticeCardCheckItem("승원")
        )

        val adapter = NoticeAdapter(items)
        binding.noticeRv.layoutManager = GridLayoutManager(context, 1)
        binding.noticeRv.adapter = adapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        binding.noticeRv.addItemDecoration(NoticeItemDecoration(verticalSpaceHeight))

        val swipeToDeleteNotice = SwipeToDeleteNotice(adapter).apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        ItemTouchHelper(swipeToDeleteNotice).attachToRecyclerView(binding.noticeRv)

        binding.noticeRv.apply {
            setOnTouchListener { v, _ ->
                swipeToDeleteNotice.removePreviousClamp(this)
                v.performClick()
                false
            }

            setOnClickListener {
                // Perform any additional click actions if needed
            }
        }

        binding.noticeBackBtn.setOnClickListener {
            navController.navigate(R.id.action_navigation_notice_to_home_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}