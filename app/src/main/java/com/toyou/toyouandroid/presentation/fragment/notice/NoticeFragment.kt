package com.toyou.toyouandroid.presentation.fragment.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeBinding
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.utils.notice.SwipeToDeleteNotice
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NoticeFragment : Fragment(), NoticeAdapterListener {

    lateinit var navController: NavController

    private var _binding: FragmentNoticeBinding? = null

    private val binding: FragmentNoticeBinding
        get() = requireNotNull(_binding){"FragmentNoticeBinding -> null"}

    private val noticeDialogViewModel: NoticeDialogViewModel by activityViewModels()
    private lateinit var listener: NoticeAdapterListener
    private var noticeRequestAdapter: NoticeRequestAdapter? = null
    private var noticeEntireAdapter: NoticeEntireAdapter? = null
    private var noticeDialog: NoticeDialog? = null

    private val viewModel: NoticeViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val socialViewModel: SocialViewModel by activityViewModels()
    private val cardViewModel: CardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)

        noticeRequestAdapter = NoticeRequestAdapter(mutableListOf(), this, socialViewModel)
        noticeEntireAdapter = NoticeEntireAdapter(mutableListOf(), viewModel, this)

        listener = object : NoticeAdapterListener {

            override fun onDeleteNotice(alarmId: Int, position: Int) {
                viewModel.deleteNotice(alarmId, position)
            }

            override fun onFriendRequestApprove(name: String, userId: Int, position: Int) {
                val myName = userViewModel.nickname.value ?: ""
                Timber.d(myName)
                socialViewModel.patchApprove(userId.toLong(), myName)

//                socialViewModel.approveSuccess.observe(viewLifecycleOwner) { result ->
//                    if (result != null) {
//                        if (result.isSuccess) {
//                            navController.navigate(R.id.action_navigation_notice_to_social_fragment)
//
//                            socialViewModel.resetApproveSuccess() // 메서드 호출하여 상태 초기화
//                        } else {
//                            noticeDialogViewModel.setDialogData(
//                                title = "존재하지 않는 \n 사용자입니다",
//                                leftButtonText = "확인",
//                                leftButtonClickAction = { dismissDialog() },
//                            )
//                            noticeDialog = NoticeDialog()
//                            noticeDialog?.show(parentFragmentManager, "CustomDialog")
//
//                            socialViewModel.resetApproveSuccess() // 메서드 호출하여 상태 초기화
//                        }
//                    }
//                }
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
//                                cardViewModel.getCardDetail(cardId.toLong())
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

        viewModel.fetchNotices()
        viewModel.fetchFriendsRequestNotices()

        setupRecyclerView()

        binding.noticeBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_notice_to_home_fragment)
        }
    }

    private fun setupRecyclerView() {
        val friendRequestAdapter = NoticeRequestAdapter(mutableListOf(), listener, socialViewModel)
        val entireNoticeRequestAdapter = NoticeEntireAdapter(mutableListOf(), viewModel, listener)
        val noticeRequestBlankAdapter = NoticeRequestBlankAdapter()
        val noticeEntireBlankAdapter = NoticeEntireBlankAdapter()

        binding.rvNoticeFriendRequest.layoutManager = GridLayoutManager(context, 1)
        binding.rvNoticeFriendRequest.adapter = friendRequestAdapter

        binding.rvNoticeEntire.layoutManager = GridLayoutManager(context, 1)
        binding.rvNoticeEntire.adapter = entireNoticeRequestAdapter

        binding.rvNoticeFriendRequestBlank.layoutManager = GridLayoutManager(context, 1)
        binding.rvNoticeFriendRequestBlank.adapter = noticeRequestBlankAdapter

        binding.rvNoticeEntireBlank.layoutManager = GridLayoutManager(context, 1)
        binding.rvNoticeEntireBlank.adapter = noticeEntireBlankAdapter

        val swipeToDeleteNoticeRequest = SwipeToDeleteNotice().apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        val swipeToDeleteNoticeEntire = SwipeToDeleteNotice().apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        ItemTouchHelper(swipeToDeleteNoticeRequest).attachToRecyclerView(binding.rvNoticeFriendRequest)
        ItemTouchHelper(swipeToDeleteNoticeEntire).attachToRecyclerView(binding.rvNoticeEntire)

        binding.rvNoticeFriendRequest.apply {
            setOnTouchListener { v, _ ->
                swipeToDeleteNoticeRequest.removePreviousClamp(this)
                v.performClick()
                invalidateItemDecorations()
                false
            }

            setOnClickListener {
            }
        }

        binding.rvNoticeEntire.apply {
            setOnTouchListener { v, _ ->
                swipeToDeleteNoticeEntire.removePreviousClamp(this)
                v.performClick()
                invalidateItemDecorations()
                false
            }

            setOnClickListener {
            }
        }

        viewModel.friendRequestNotices.observe(viewLifecycleOwner) { friendRequests ->
            if (friendRequests.isEmpty()) {
                Timber.d("Friend request notices are empty")
                binding.rvNoticeFriendRequestBlank.visibility = View.VISIBLE
            } else {
                friendRequestAdapter.updateItems(friendRequests.toMutableList())
                binding.rvNoticeFriendRequestBlank.visibility = View.GONE
            }
        }

        viewModel.generalNotices.observe(viewLifecycleOwner) { generalNotices ->
            if (generalNotices.isEmpty()) {
                Timber.d("General notices are empty")
                binding.rvNoticeEntireBlank.visibility = View.VISIBLE
            } else {
                entireNoticeRequestAdapter.updateItems(generalNotices.toMutableList())
                binding.rvNoticeEntireBlank.visibility = View.GONE
            }
        }
    }

    private fun dismissDialog() {
        noticeDialog?.dismiss()
    }

    override fun onFriendRequestApprove(name: String, alarmId: Int, position: Int) {}
    override fun onDeleteNotice(alarmId: Int, position: Int) {}
    override fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem) {}
    override fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem) {}

}