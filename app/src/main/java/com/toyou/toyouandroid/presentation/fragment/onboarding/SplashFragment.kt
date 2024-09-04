package com.toyou.toyouandroid.presentation.fragment.onboarding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.UserDatabase
import com.toyou.toyouandroid.data.UserEntity
import com.toyou.toyouandroid.databinding.FragmentSplashBinding
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var navController: NavController
    private var _binding: FragmentSplashBinding? = null
    private lateinit var database: UserDatabase
    private lateinit var userViewModel: UserViewModel


    private val binding: FragmentSplashBinding
        get() = requireNotNull(_binding){"FragmentSplashBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = UserDatabase.getDatabase(requireContext())


        // MainActivity의 메소드를 호출하여 바텀 네비게이션 뷰 숨기기
        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // navController 초기화
        navController = findNavController()

        userViewModel.getHomeEntry()

        userViewModel.cardId.observe(viewLifecycleOwner) { cardId ->

                Log.d("get home", userViewModel.cardId.value.toString())

        }

        // 3초 후에 다음 화면으로 이동
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_navigation_splash_to_login_fragment)
        }, 3000)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*private fun saveUserInfo(){
        CoroutineScope(Dispatchers.IO).launch {
            database.dao().insert(UserEntity(cardId = splashViewModel.cardId.value, emotion = splashViewModel.emotion.value))
            val users = database.dao().getAll()

            for (user in users) {
                Log.d("get home", "cardId: ${user.cardId}, emotion: ${user.emotion}")
            }
        }
    }*/



}