package com.toyou.toyouandroid.presentation.fragment.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.data.notice.service.NoticeService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.NoticeViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.notice.SwipeToDeleteNotice
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

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

    private lateinit var viewModel: NoticeViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var cardViewModel: CardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoticeBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val noticeService = AuthNetworkModule.getClient().create(NoticeService::class.java)
        val noticeRepository = NoticeRepository(noticeService)

        val socialService = AuthNetworkModule.getClient().create(SocialService::class.java)
        val socialRepository = SocialRepository(socialService)
        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)
        val fcmService = AuthNetworkModule.getClient().create(FCMService::class.java)
        val fcmRepository = FCMRepository(fcmService)


        viewModel = ViewModelProvider(
            this,
            NoticeViewModelFactory(noticeRepository, tokenManager)
        )[NoticeViewModel::class.java]

        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(createRepository,tokenManager)
        )[CardViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(createRepository,tokenManager)
        )[UserViewModel::class.java]

        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(socialRepository, tokenManager, fcmRepository)
        )[SocialViewModel::class.java]

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