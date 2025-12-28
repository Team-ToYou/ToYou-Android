package com.toyou.toyouandroid.presentation.fragment.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.core.datastore.NotificationPreferences
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentNoticeSettingBinding
import com.toyou.toyouandroid.fcm.MyFirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class NoticeSettingFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentNoticeSettingBinding? = null
    private val binding: FragmentNoticeSettingBinding
        get() = requireNotNull(_binding){"FragmentNoticeSettingBinding -> null"}

    private lateinit var myFirebaseMessagingService: MyFirebaseMessagingService

    @Inject
    lateinit var notificationPreferences: NotificationPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNoticeSettingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        binding.noticeSettingBackLayout.setOnClickListener {
            navController.navigate(R.id.action_navigation_notice_setting_to_mypage_fragment)
        }

        myFirebaseMessagingService = MyFirebaseMessagingService()

        // 기존 구독 상태를 DataStore에서 불러오기
        val isSubscribed = notificationPreferences.isSubscribed()

        // SwitchCompat 초기 상태 설정
        binding.noticeToggle.isChecked = isSubscribed

        Timber.d("현재 구독 상태: %b", isSubscribed)

        binding.noticeToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                myFirebaseMessagingService.subscribeToTopic()
                Timber.d("구독됨")
                Toast.makeText(context, "알림 수신을 동의하였습니다", Toast.LENGTH_SHORT).show()
            } else {
                myFirebaseMessagingService.unsubscribeFromTopic()
                Timber.d("구독 취소됨")
                Toast.makeText(context, "알림 수신을 거부하였습니다", Toast.LENGTH_SHORT).show()
            }

            notificationPreferences.setSubscribedSync(isChecked)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
