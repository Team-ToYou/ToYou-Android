package com.toyou.toyouandroid.presentation.fragment.social

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.social.service.SocialService
import com.toyou.toyouandroid.databinding.FragmentSocialBinding
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.social.repostitory.SocialRepository
import com.toyou.toyouandroid.fcm.domain.FCMRepository
import com.toyou.toyouandroid.fcm.service.FCMService
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.fragment.mypage.MyPageLogoutDialog
import com.toyou.toyouandroid.presentation.fragment.record.CalendarDialog
import com.toyou.toyouandroid.presentation.fragment.record.CalendarDialogViewModel
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.fragment.social.adapter.SocialRVAdapter
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class SocialFragment : Fragment() {

    private var _binding : FragmentSocialBinding? = null
    private val binding : FragmentSocialBinding get() = requireNotNull(_binding){"social fragment null"}

    lateinit var navController: NavController

    private lateinit var socialAdapter: SocialRVAdapter
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var addFriendLinearLayout: LinearLayout
    private lateinit var userViewModel: UserViewModel

    private var calendarDialog: CalendarDialog? = null
    private val calendarDialogViewModel: CalendarDialogViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)

        val socialService = AuthNetworkModule.getClient().create(SocialService::class.java)
        val socialRepository = SocialRepository(socialService)
        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)
        val fcmService = AuthNetworkModule.getClient().create(FCMService::class.java)
        val fcmRepository = FCMRepository(fcmService)


        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(socialRepository, tokenManager, fcmRepository)
        )[SocialViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(createRepository,tokenManager)
        )[UserViewModel::class.java]

        socialViewModel.getFriendsData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)

        addFriendLinearLayout = binding.addFriendLinear


        //어탭터 초기화 후 뷰모델 데이터 관찰
        socialAdapter = SocialRVAdapter(socialViewModel, { position ->
            navController.navigate(R.id.action_navigation_social_to_questionTypeFragment)
            Timber.tag("아이템").d(position.toString())
        }) { id ->
            showDeleteDialog(id) // 다이얼로그 표시 함수 호출
        }

        binding.socialRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = socialAdapter

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val margin = (screenWidth * 0.02).toInt() // 화면 너비의 5%를 마진으로 사용

            // 아이템 데코레이션 추가
//            addItemDecoration(RVMarginItemDecoration(margin,false))
        }

        socialViewModel.friends.observe(viewLifecycleOwner) { friends ->
            socialAdapter.setFriendData(friends)
            socialAdapter.notifyDataSetChanged()
        }


        binding.searchBtn.setOnClickListener {
            val searchName = binding.searchEt.text.toString()
            addFriendLinearLayout.removeAllViews()  // 뷰를 초기화

            // API 호출
            socialViewModel.getSearchData(searchName)
        }

        socialViewModel.isFriend.observe(viewLifecycleOwner) { isFriend ->
            if (isFriend == "400" || isFriend == "401") {
                addNotExist(isFriend)
            }else if (isFriend == "no"){
                Timber.tag("destroy2").d(isFriend)
                addFriendLinearLayout.removeAllViews()
            }
            else {
                addFriendView(isFriend, binding.searchEt.text.toString())
            }
        }

        socialViewModel.friendRequestCompleted.observe(viewLifecycleOwner) { isCompleted ->
            if (isCompleted) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                }
                socialViewModel.resetFriendRequest()
                Toast.makeText(requireContext(), "친구 요청을 보냈습니다", Toast.LENGTH_SHORT).show()

            }
        }

        socialViewModel.friendRequestCanceled.observe(viewLifecycleOwner) { isCanceled ->
            if (isCanceled) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                }
                socialViewModel.resetFriendRequestCanceled()
                Toast.makeText(requireContext(), "친구 요청을 수락했습니다", Toast.LENGTH_SHORT).show()

            }
        }

        socialViewModel.friendRequestRemove.observe(viewLifecycleOwner) { isCanceled ->
            if (isCanceled) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                    Toast.makeText(requireContext(), "친구 요청을 취소했습니다.", Toast.LENGTH_SHORT).show()
                }
                socialViewModel.resetFriendRequestRemove()
            }
        }

//        val dialog = CustomDialogFragment()
//        val btn = arrayOf("취소", "확인")
//        dialog.arguments= bundleOf(
//            "dialogTitle" to "선택한 친구를\n삭제하시겠습니까?",
//            "btnText" to btn
//        )
//        dialog.setButtonClickListener(object : CustomDialogFragment.OnButtonClickListener {
//            override fun onButton1Clicked() {
//                //아무것도
//            }
//
//            override fun onButton2Clicked() {
//                //삭제로직
//            }
//
//        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.searchEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val searchName = binding.searchEt.text.toString()
                addFriendLinearLayout.removeAllViews()  // 뷰를 초기화

                // API 호출
                socialViewModel.getSearchData(searchName)
                true
            }
            false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        // 친구 추가 뷰를 제거하도록
        socialViewModel.resetFriendState()
    }

    private fun addFriendView(isFriend : String, name : String){
        val addFriendView = LayoutInflater.from(requireContext()).inflate(R.layout.item_add_friend, addFriendLinearLayout,false)
        val friendName = addFriendView.findViewById<TextView>(R.id.friendName_tv)
        val stateBtn = addFriendView.findViewById<Button>(R.id.state_btn)


        friendName.apply {
            friendName?.text = name
        }

        stateBtn.apply {
            when(isFriend) {
                "REQUEST_SENT" -> {
                    stateBtn.text = "취소하기"
                    stateBtn.setBackgroundResource(R.drawable.r10_red_container)
                }
                "REQUEST_RECEIVED" -> {
                    stateBtn.text = "친구 수락"
                }
                "FRIEND" -> {
                    stateBtn.text = "친구"
                }
                "NOT_FRIEND" -> {
                    stateBtn.text = "친구 요청"
                }
            }
        }

        stateBtn.setOnClickListener {
            val myName = userViewModel.nickname.value ?: ""
            socialViewModel.searchFriendId.observe(viewLifecycleOwner) { friendId ->
                if (isFriend == "NOT_FRIEND") {
                    socialViewModel.sendFriendRequest(friendId, myName)
                } else if (isFriend == "REQUEST_RECEIVED") {
                    socialViewModel.patchApprove(friendId, myName)
                } else if (isFriend == "REQUEST_SENT") {
                    socialViewModel.deleteFriend(friendId)
                }
            }
        }


        addFriendLinearLayout.addView(addFriendView)

    }

    private fun addNotExist(isFriend : String){
        val notExistFriend = LayoutInflater.from(requireContext()).inflate(R.layout.item_not_exsist, addFriendLinearLayout, false)
        val ment = notExistFriend.findViewById<TextView>(R.id.friendName_tv)

        if (isFriend == "401") {
            ment.apply {
                ment?.text = "스스로에게 요청할 수 없습니다. 다시 입력해주세요"
            }
        }
        else{
            ment.apply {
                ment?.text = "찾으시는 닉네임이 존재하지 않아요. 다시 입력해주세요"
            }
        }

        addFriendLinearLayout.addView(notExistFriend)

    }

    private fun showDeleteDialog(friendId: Long) {
        calendarDialogViewModel.setDialogData(
            title = "선택한 친구를\n삭제하시겠습니까?",
            leftButtonText = "취소",
            rightButtonText = "삭제",
            leftButtonTextColor = Color.BLACK,
            rightButtonTextColor = Color.RED,
            leftButtonClickAction = { dismissDialog() },
            rightButtonClickAction = { deleteFriend(friendId) }
        )
        calendarDialog = CalendarDialog()
        calendarDialog?.show(parentFragmentManager, "CustomDialog")
    }

    private fun deleteFriend(friendId: Long) {
        Timber.tag("deleteFriend").d("deleteFriend")

        socialViewModel.deleteFriend(friendId)
        socialViewModel.resetFriendRequestRemove()
        Toast.makeText(requireContext(), "선택한 친구가 삭제 되었습니다.", Toast.LENGTH_SHORT).show()

        calendarDialog?.dismiss()
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")
        calendarDialog?.dismiss()
    }
}
