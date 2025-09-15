package com.toyou.toyouandroid.presentation.fragment.record.my

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.databinding.FragmentCardInstantBinding
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.record.CalendarDialog
import com.toyou.toyouandroid.presentation.fragment.record.CalendarDialogViewModel
import com.toyou.toyouandroid.presentation.viewmodel.RecordViewModelFactory
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class MyCardContainerFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentCardInstantBinding? = null
    private val binding: FragmentCardInstantBinding
        get() = requireNotNull(_binding){"FragmentCardInstantBinding -> null"}

    private lateinit var userViewModel: UserViewModel

    private val calendarDialogViewModel: CalendarDialogViewModel by activityViewModels()
    private var calendarDialog: CalendarDialog? = null

    private lateinit var myRecordViewModel: MyRecordViewModel
    private lateinit var myCardViewModel: MyCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCardInstantBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val recordService = AuthNetworkModule.getClient().create(RecordService::class.java)
        val recordRepository = RecordRepository(recordService)
        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)


        myRecordViewModel = ViewModelProvider(
            this,
            RecordViewModelFactory(recordRepository, tokenManager)
        )[MyRecordViewModel::class.java]

        myCardViewModel = ViewModelProvider(
            requireActivity(),
            RecordViewModelFactory(recordRepository, tokenManager)
        )[MyCardViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity() ,
            UserViewModelFactory(createRepository,tokenManager)
        )[UserViewModel::class.java]

        if (savedInstanceState == null) {
            val fragment = MyCardDetailFragment()

            childFragmentManager.beginTransaction()
                .add(R.id.card_container, fragment)
                .commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        navController = Navigation.findNavController(view)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                myCardViewModel.clearAllData()
                navController.popBackStack()
            }
        })

        val cardId = arguments?.getInt("cardId")
        val date = arguments?.getString("date")
        Timber.d("Received cardId: $cardId $date")

        userViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            myCardViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
                if (receiver != nickname) {
                    Timber.tag("MyCardContainerFragment").d("$receiver $nickname")

                    binding.diarycardDeleteBtn.visibility = View.INVISIBLE
                } else {
                    binding.diarycardDeleteBtn.visibility = View.VISIBLE
                }
            }
        }

        if (cardId != null) {
            myCardViewModel.getCardDetail(cardId.toLong())
            myCardViewModel.setCardId(cardId)
        }

        binding.closeBtn.setOnClickListener {
            myCardViewModel.clearAllData()

            navController.popBackStack()
        }

        binding.diarycardDeleteBtn.setOnClickListener {
            myCardViewModel.clearAll()

            calendarDialogViewModel.setDialogData(
                title = "정말 일기카드를\n삭제하시겠습니까?",
                leftButtonText = "취소",
                rightButtonText = "삭제",
                leftButtonTextColor = Color.BLACK,
                rightButtonTextColor = Color.RED,
                leftButtonClickAction = { dismissDialog() },
                rightButtonClickAction = { deleteDiaryCards() }
            )
            calendarDialog = CalendarDialog()
            calendarDialog?.show(parentFragmentManager, "CustomDialog")
        }
    }

    private fun dismissDialog() {
        Timber.tag("dismissDialog").d("dismissDialog")

        calendarDialog?.dismiss()
    }

    private fun deleteDiaryCards() {
        val cardId = arguments?.getInt("cardId")

        if (cardId != null) {
            myRecordViewModel.deleteDiaryCard(cardId)
        }

        calendarDialog?.dismiss()

        navController.navigate(R.id.action_navigation_my_card_container_to_record_fragment)

        Toast.makeText(requireContext(), "일기카드가 삭제되었습니다", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}