package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentSocialBinding
import com.toyou.toyouandroid.presentation.fragment.home.RVMarginItemDecoration
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModel
import com.toyou.toyouandroid.presentation.fragment.social.adapter.SocialRVAdapter
import com.toyou.toyouandroid.presentation.viewmodel.SocialViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage

class SocialFragment : Fragment() {

    private var _binding : FragmentSocialBinding? = null
    private val binding : FragmentSocialBinding get() = requireNotNull(_binding){"social fragment null"}

    lateinit var navController: NavController

    private lateinit var socialAdapter: SocialRVAdapter
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var addFriendLinearLayout: LinearLayout
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        socialViewModel = ViewModelProvider(
            requireActivity(),
            SocialViewModelFactory(tokenStorage)
        )[SocialViewModel::class.java]
        userViewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

        socialViewModel.getFriendsData()


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)

        addFriendLinearLayout = binding.addFriendLinear


        //어탭터 초기화 후 뷰모델 데이터 관찰
        socialAdapter = SocialRVAdapter(socialViewModel, { position ->
            navController.navigate(R.id.action_navigation_social_to_questionTypeFragment)
            Log.d("아이템", position.toString())
        }) { friendName ->
            showDeleteDialog(friendName) // 다이얼로그 표시 함수 호출
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

        socialViewModel.friends.observe(viewLifecycleOwner, Observer { friends ->
            socialAdapter.setFriendData(friends)
            socialAdapter.notifyDataSetChanged()
        })


        binding.searchBtn.setOnClickListener {
            val searchName = binding.searchEt.text.toString()
            addFriendLinearLayout.removeAllViews()  // 뷰를 초기화

            // API 호출
            socialViewModel.getSearchData(searchName)
        }

        socialViewModel.isFriend.observe(viewLifecycleOwner, Observer { isFriend ->
            if (isFriend == "400" || isFriend == "401") {
                addNotExist(isFriend)
            }else if (isFriend == "no"){
                Log.d("destroy2", isFriend)
                addFriendLinearLayout.removeAllViews()
            }
            else {
                addFriendView(isFriend, binding.searchEt.text.toString())
            }
        })

        socialViewModel.friendRequestCompleted.observe(viewLifecycleOwner, Observer { isCompleted ->
            if (isCompleted) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                }
                socialViewModel.resetFriendRequest()
                Toast.makeText(requireContext(), "친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()

            }
        })

        socialViewModel.friendRequestCanceled.observe(viewLifecycleOwner, Observer { isCanceled ->
            if (isCanceled) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                }
                socialViewModel.resetFriendRequestCanceled()
                Toast.makeText(requireContext(), "친구 요청을 수락했습니다.", Toast.LENGTH_SHORT).show()

            }
        })

        socialViewModel.friendRequestRemove.observe(viewLifecycleOwner, Observer { isCanceled ->
            if (isCanceled) {
                // 친구 요청이 완료되었을 때 addFriendView 제거
                if (addFriendLinearLayout.childCount > 0) {
                    addFriendLinearLayout.removeViewAt(addFriendLinearLayout.childCount - 1)
                    Toast.makeText(requireContext(), "친구 요청을 취소했습니다.", Toast.LENGTH_SHORT).show()
                }
                socialViewModel.resetFriendRequestRemove()
            }
        })

        val dialog = CustomDialogFragment()
        val btn = arrayOf("취소", "확인")
        dialog.arguments= bundleOf(
            "dialogTitle" to "선택한 친구를\n삭제하시겠습니까?",
            "btnText" to btn
        )
        dialog.setButtonClickListener(object : CustomDialogFragment.OnButtonClickListener {
            override fun onButton1Clicked() {
                //아무것도
            }

            override fun onButton2Clicked() {
                //삭제로직
            }

        })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.searchEt.setOnEditorActionListener { v, actionId, event ->
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
            friendName?.setText(name)
        }

        stateBtn.apply {
            when(isFriend) {
                "REQUEST_SENT" -> {
                    stateBtn.setText("취소하기")
                    stateBtn.setBackgroundResource(R.drawable.r10_red_container)
                }
                "REQUEST_RECEIVED" -> {
                    stateBtn.setText("친구 수락")
                    stateBtn.setBackgroundResource(R.drawable.r10_red_container)
                }
                "FRIEND" -> {
                    stateBtn.setText("친구")
                }
                "NOT_FRIEND" -> {
                    stateBtn.setText("친구 요청")
                }
            }
        }

        stateBtn.setOnClickListener {
            val myName = userViewModel.nickname.value ?: ""

            if (isFriend == "NOT_FRIEND") {
                socialViewModel.sendFriendRequest(name, myName)
            } else if (isFriend == "REQUEST_RECEIVED") {
                socialViewModel.patchApprove(name, myName)
            } else if (isFriend == "REQUEST_SENT") {
                socialViewModel.deleteFriend(name)
            }
        }

        addFriendLinearLayout.addView(addFriendView)

    }

    private fun addNotExist(isFriend : String){
        val notExistFriend = LayoutInflater.from(requireContext()).inflate(R.layout.item_not_exsist, addFriendLinearLayout, false)
        val ment = notExistFriend.findViewById<TextView>(R.id.friendName_tv)

        if (isFriend == "401") {
            ment.apply {
                ment?.setText("스스로에게 요청할 수 없습니다. 다시 입력해주세요")
            }
        }
        else{
            ment.apply {
                ment?.setText("찾으시는 닉네임이 존재하지 않아요. 다시 입력해주세요")
            }
        }

        addFriendLinearLayout.addView(notExistFriend)

    }

    private fun showDeleteDialog(friendName: String) {
        val dialog = CustomDialogFragment()
        val btn = arrayOf("취소", "삭제")
        dialog.arguments = bundleOf(
            "dialogTitle" to "선택한 친구를\n삭제하시겠습니까?",
            "btnText" to btn
        )
        dialog.setButtonClickListener(object : CustomDialogFragment.OnButtonClickListener {
            override fun onButton1Clicked() {
                dialog.dismiss()
            }

            override fun onButton2Clicked() {
                socialViewModel.deleteFriend(friendName)
                socialViewModel.resetFriendRequestRemove()
                Toast.makeText(requireContext(), "선택한 친구가 삭제 되었습니다.", Toast.LENGTH_SHORT).show()

            }
        })
        dialog.show(parentFragmentManager, "CustomDialogFragment")
    }

}
