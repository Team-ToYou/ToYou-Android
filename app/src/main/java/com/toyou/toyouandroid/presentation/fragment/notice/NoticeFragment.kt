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
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.domain.notice.NoticeRepository
import com.toyou.toyouandroid.data.notice.service.NoticeService
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.notice.SwipeToDeleteNotice
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class NoticeFragment : Fragment(), NoticeAdapterListener {

    lateinit var navController: NavController

    private var _binding: FragmentNoticeBinding? = null

    private val binding: FragmentNoticeBinding
        get() = requireNotNull(_binding){"FragmentNoticeBinding -> null"}

    private lateinit var viewModel: NoticeViewModel
    private val noticeDialogViewModel: NoticeDialogViewModel by activityViewModels()
    private lateinit var listener: NoticeAdapterListener
    private var noticeAdapter: NoticeAdapter? = null

    private lateinit var userViewModel: UserViewModel
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var cardViewModel: CardViewModel

    private var noticeDialog: NoticeDialog? = null

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
        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(tokenStorage)
        )[SocialViewModel::class.java]

        val noticeService = AuthNetworkModule.getClient().create(NoticeService::class.java)
        val noticeRepository = NoticeRepository(noticeService)
        val authService = NetworkModule.getClient().create(AuthService::class.java)

        viewModel = ViewModelProvider(
            this,
            NoticeViewModelFactory(noticeRepository, authService, tokenStorage)
        )[NoticeViewModel::class.java]

        noticeAdapter = NoticeAdapter(mutableListOf(), viewModel, this)

        listener = object : NoticeAdapterListener {

            override fun onDeleteNotice(alarmId: Int, position: Int) {
                viewModel.deleteNotice(alarmId, position)
            }

            override fun onFriendRequestApprove(name: String, alarmId: Int, position: Int) {
                val myName = userViewModel.nickname.value ?: ""
                Timber.d(myName)
                socialViewModel.patchApproveNotice(name, myName, alarmId, position)
            }

            override fun onFriendRequestAcceptClick(item: NoticeItem.NoticeFriendRequestItem) {
                socialViewModel.approveSuccess.observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        if (result.isSuccess) {
                            navController.navigate(R.id.action_navigation_notice_to_social_fragment)

                            socialViewModel.resetApproveSuccess() // 메서드 호출하여 상태 초기화
                        } else {
                            noticeDialogViewModel.setDialogData(
                                title = "존재하지 않는 \n 사용자입니다",
                                leftButtonText = "확인",
                                leftButtonClickAction = { checkUserNone() },
                            )
                            noticeDialog = NoticeDialog()
                            noticeDialog?.show(parentFragmentManager, "CustomDialog")
                        }
                    }
                }
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

        socialViewModel.approveSuccess.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                if (result.isSuccess) {
                    // API 호출이 성공했으므로 아이템 삭제
                    viewModel.deleteNotice(result.alarmId, result.position) // 알림 삭제
                    noticeAdapter?.removeItem(result.position) // 어댑터에 아이템 삭제 요청
                    navController.navigate(R.id.action_navigation_notice_to_social_fragment)

                    Toast.makeText(requireContext(), "친구 요청을 수락했습니다", Toast.LENGTH_SHORT).show()

                    socialViewModel.resetApproveSuccess() // 메서드 호출하여 상태 초기화
                }
            }
        }

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
        noticeDialog?.dismiss()
    }

    override fun onFriendRequestApprove(name: String, alarmId: Int, position: Int) {}
    override fun onDeleteNotice(alarmId: Int, position: Int) {}
    override fun onFriendRequestAcceptClick(item: NoticeItem.NoticeFriendRequestItem) {}
    override fun onFriendRequestAcceptedItemClick(item: NoticeItem.NoticeFriendRequestAcceptedItem) {}
    override fun onFriendCardItemClick(item: NoticeItem.NoticeCardCheckItem) {}

    override fun onDestroyView() {
        super.onDestroyView()
    }
}