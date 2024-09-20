package com.toyou.toyouandroid.presentation.fragment.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeBinding
import com.toyou.toyouandroid.model.NoticeItem
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeRepository
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeService
import com.toyou.toyouandroid.presentation.fragment.notice.network.NoticeViewModelFactory
import com.toyou.toyouandroid.utils.SwipeToDeleteNotice
import timber.log.Timber

class NoticeFragment : Fragment(), NoticeAdapterListener {

    lateinit var navController: NavController

    private var _binding: FragmentNoticeBinding? = null

    private var noticeDialog: NoticeDialog? = null

    private val binding: FragmentNoticeBinding
        get() = requireNotNull(_binding){"FragmentNoticeBinding -> null"}

    private val viewModel: NoticeViewModel by viewModels {
        NoticeViewModelFactory(NoticeRepository(AuthNetworkModule.getClient().create(NoticeService::class.java)))
    }

    private val noticeDialogViewModel: NoticeDialogViewModel by activityViewModels()
    private val listener: NoticeAdapterListener
        get() {
            TODO()
        }

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

        viewModel.noticeItems.observe(viewLifecycleOwner) { items ->
            if (items != null) {
                setupRecyclerView(items)
            }
        }

        viewModel.fetchNotices()

        binding.noticeBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_notice_to_home_fragment)
        }
    }

    private fun setupRecyclerView(items: List<NoticeItem>) {
        val adapter = NoticeAdapter(items.toMutableList(), viewModel)
        binding.noticeRv.layoutManager = GridLayoutManager(context, 1)
        binding.noticeRv.adapter = adapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        binding.noticeRv.addItemDecoration(NoticeItemDecoration(verticalSpaceHeight))

        val swipeToDeleteNotice = SwipeToDeleteNotice().apply {
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
            }
        }
    }

    override fun onShowDialog() {
        noticeDialogViewModel.setDialogData(
            title = "존재하지 않는 \n 사용자입니다",
            subTitle = "",
            leftButtonText = "확인",
            rightButtonText = "",
            leftButtonTextColor = R.color.black,
            rightButtonTextColor = R.color.black,
            leftButtonClickAction = { checkUserNone() },
            rightButtonClickAction = { dismissDialog() }
        )
        noticeDialog = NoticeDialog()
        noticeDialog?.show(parentFragmentManager, "CustomDialog")
    }

    override fun onDeleteNotice(alarmId: Int, position: Int) {
        viewModel.deleteNotice(alarmId, position)
    }

    // 회원 로그아웃
    private fun checkUserNone() {
        Timber.tag("handleLogout").d("handleWithdraw")
        noticeDialog?.dismiss()
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        noticeDialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}