package com.toyou.toyouandroid.presentation.fragment.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
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
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.SwipeToDeleteNotice
import com.toyou.toyouandroid.utils.TokenStorage
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
    private lateinit var listener: NoticeAdapterListener
    private lateinit var noticeAdapter: NoticeAdapter

    private lateinit var userViewModel: UserViewModel
    private lateinit var cardViewModel: CardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(tokenStorage)
        )[CardViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

        listener = object : NoticeAdapterListener {
            override fun onShowDialog() {
                noticeDialogViewModel.setDialogData(
                    title = "존재하지 않는 \n 사용자입니다",
                    leftButtonText = "확인",
                    leftButtonClickAction = { checkUserNone() },
                )
                noticeDialog = NoticeDialog()
                noticeDialog?.show(parentFragmentManager, "CustomDialog")
            }

            override fun onDeleteNotice(alarmId: Int, position: Int) {
                viewModel.deleteNotice(alarmId, position)
            }

            override fun onFriendRequestItemClick(item: NoticeItem.NoticeFriendRequestItem) {
                navController.navigate(R.id.action_navigation_notice_to_social_fragment)
            }

            override fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem) {
                navController.navigate(R.id.action_navigation_notice_to_social_fragment)
            }

            override fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem) {
                userViewModel.emotion.observe(viewLifecycleOwner) { emotion ->
                    if (emotion != null){
                        userViewModel.cardId.observe(viewLifecycleOwner) { cardId ->
                            if (cardId == null) {
                                navController.navigate(R.id.action_navigation_notice_to_create_fragment)
                            }
                            else {
                                cardViewModel.getCardDetail(cardId.toLong())
                                navController.navigate(R.id.action_navigation_notice_to_modify_fragment)
                            }
                        }
                    } else{
                        Toast.makeText(requireContext(), "감정 우표를 먼저 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

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
        val adapter = NoticeAdapter(items.toMutableList(), viewModel, listener)
        binding.noticeRv.layoutManager = GridLayoutManager(context, 1)
        binding.noticeRv.adapter = adapter

        val swipeToDeleteNotice = SwipeToDeleteNotice().apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        ItemTouchHelper(swipeToDeleteNotice).attachToRecyclerView(binding.noticeRv)

        binding.noticeRv.apply {
            setOnTouchListener { v, _ ->
                swipeToDeleteNotice.removePreviousClamp(this)
                v.performClick()
                invalidateItemDecorations()
                false
            }

            setOnClickListener {
            }
        }
    }

    private fun checkUserNone() {
        Timber.tag("handleLogout").d("handleWithdraw")
        noticeDialog?.dismiss()
    }

    override fun onShowDialog() {
        noticeDialogViewModel.setDialogData(
            title = "존재하지 않는 \n 사용자입니다",
            leftButtonText = "확인",
            leftButtonClickAction = { checkUserNone() },
        )
        noticeDialog = NoticeDialog()
        noticeDialog?.show(parentFragmentManager, "CustomDialog")
    }

    override fun onDeleteNotice(alarmId: Int, position: Int) {
        viewModel.deleteNotice(alarmId, position)
        noticeAdapter.removeItem(position)

        // RecyclerView 간격 재설정
        binding.noticeRv.post {
            binding.noticeRv.invalidateItemDecorations()
        }
    }

    override fun onFriendRequestItemClick(item: NoticeItem.NoticeFriendRequestItem) {
        navController.navigate(R.id.action_navigation_notice_to_social_fragment)
    }

    override fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem) {
        navController.navigate(R.id.action_navigation_notice_to_social_fragment)
    }

    override fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem) {
        navController.navigate(R.id.action_navigation_notice_to_home_fragment)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}