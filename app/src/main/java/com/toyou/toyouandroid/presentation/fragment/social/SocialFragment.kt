package com.toyou.toyouandroid.presentation.fragment.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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

class SocialFragment : Fragment() {

    private var _binding : FragmentSocialBinding? = null
    private val binding : FragmentSocialBinding get() = requireNotNull(_binding){"social fragment null"}

    lateinit var navController: NavController

    private lateinit var socialAdapter: SocialRVAdapter
    private lateinit var socialViewModel : SocialViewModel
    private lateinit var addFriendLinearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        socialViewModel = ViewModelProvider(requireActivity()).get(SocialViewModel::class.java)
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
        socialAdapter = SocialRVAdapter(socialViewModel) { position ->
            navController.navigate(R.id.action_navigation_social_to_questionTypeFragment)
            Log.d("아이템", position.toString())
        }

        binding.socialRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = socialAdapter

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val margin = (screenWidth * 0.02).toInt() // 화면 너비의 5%를 마진으로 사용

            // 아이템 데코레이션 추가
            addItemDecoration(RVMarginItemDecoration(margin,false))
        }

        socialViewModel.friends.observe(viewLifecycleOwner, Observer { friends ->
            socialAdapter.setFriendData(friends)
            socialAdapter.notifyDataSetChanged()
        })

        binding.searchBtn.setOnClickListener {
            var searchName = binding.searchEt.text.toString()
            socialViewModel.getSearchData(searchName)
            socialViewModel.isFriend.value?.let { isFriend ->
                addFriendView(isFriend, searchName)
            } ?: run {
                Log.e("SocialFragment", "isFriend 값이 null입니다.")
            }
        }



        //다이얼로그 띄우기
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

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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
                    stateBtn.setText("친구 요청 받음")
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
            if (isFriend == "NOT_FRIEND"){
                socialViewModel.sendFriendRequest(name)
            }
        }

        addFriendLinearLayout.addView(addFriendView)

    }

}
