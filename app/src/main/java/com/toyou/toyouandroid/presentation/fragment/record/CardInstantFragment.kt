package com.toyou.toyouandroid.presentation.fragment.record

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
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCardInstantBinding
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.fragment.record.my.MyRecordViewModel
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModel
import com.toyou.toyouandroid.presentation.viewmodel.UserViewModelFactory
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class CardInstantFragment : Fragment(){
    private lateinit var navController: NavController
    private var _binding: FragmentCardInstantBinding? = null
    private val binding: FragmentCardInstantBinding
        get() = requireNotNull(_binding){"FragmentCardInstantBinding -> null"}

    private lateinit var cardInfoViewModel: CardInfoViewModel
    private lateinit var userViewModel: UserViewModel

    private val calendarDialogViewModel: CalendarDialogViewModel by activityViewModels()
    private var calendarDialog: CalendarDialog? = null

    private val myRecordViewModel: MyRecordViewModel by activityViewModels {
        RecordViewModelFactory(RecordRepository(AuthNetworkModule.getClient().create(RecordService::class.java)))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentCardInstantBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())

        cardInfoViewModel = ViewModelProvider(
            requireActivity(),
            CardInfoViewModelFactory(tokenStorage)
        )[CardInfoViewModel::class.java]

        userViewModel = ViewModelProvider(
            requireActivity() ,
            UserViewModelFactory(tokenStorage)
        )[UserViewModel::class.java]

        if (savedInstanceState == null) {
            val fragment = CardInfoFragment()

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
                cardInfoViewModel.clearAllData()
                navController.popBackStack()
            }
        })

        val cardId = arguments?.getInt("cardId")
        val date = arguments?.getString("date")
        Timber.d("Received cardId: $cardId $date")

        userViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            cardInfoViewModel.receiver.observe(viewLifecycleOwner) { receiver ->
                if (receiver != nickname) {
                    Timber.tag("CardInfoFragment").d("$receiver $nickname")

                    binding.diarycardDeleteBtn.visibility = View.INVISIBLE
                } else {
                    binding.diarycardDeleteBtn.visibility = View.VISIBLE
                }
            }
        }

        if (cardId != null) {
            cardInfoViewModel.getCardDetail(cardId.toLong())
            cardInfoViewModel.setCardId(cardId)
        }

        binding.closeBtn.setOnClickListener {
            cardInfoViewModel.clearAllData()

            navController.popBackStack()
        }

        binding.diarycardDeleteBtn.setOnClickListener {
            cardInfoViewModel.clearAll()

            calendarDialogViewModel.setDialogData(
                title = "정말 일기카드를\n삭제하시겠습니까?",
                leftButtonText = "취소",
                rightButtonText = "삭제",
                leftButtonTextColor = R.color.black,
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
        val date = arguments?.getString("date")

        if (cardId != null) {
            myRecordViewModel.deleteDiaryCard(cardId)
        }

        calendarDialog?.dismiss()

        navController.navigate(R.id.action_navigation_card_instant_to_record_fragment)

        Toast.makeText(requireContext(), "해당 날짜의 일기카드가 삭제되었습니다. $date", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}