package com.toyou.toyouandroid.presentation.fragment.record.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.data.record.service.RecordService
import com.toyou.toyouandroid.databinding.FragmentFriendCardContainerBinding
import com.toyou.toyouandroid.domain.record.RecordRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.presentation.viewmodel.RecordViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class FriendCardContainerFragment : Fragment() {
    private lateinit var navController: NavController
    private var _binding: FragmentFriendCardContainerBinding? = null
    private val binding: FragmentFriendCardContainerBinding
        get() = requireNotNull(_binding){"FragmentFriendCardContainerBinding -> null"}

    private lateinit var friendCardViewModel: FriendCardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentFriendCardContainerBinding.inflate(inflater, container, false)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val recordService = AuthNetworkModule.getClient().create(RecordService::class.java)
        val recordRepository = RecordRepository(recordService)

        friendCardViewModel = ViewModelProvider(
            requireActivity(),
            RecordViewModelFactory(recordRepository, tokenManager)
        )[FriendCardViewModel::class.java]

        if (savedInstanceState == null) {
            val fragment = FriendCardDetailFragment()

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
                friendCardViewModel.clearAllData()
                navController.popBackStack()
            }
        })

        val cardId = arguments?.getInt("cardId")
        val date = arguments?.getString("date")
        Timber.d("Received cardId: $cardId $date")

        if (cardId != null) {
            friendCardViewModel.getCardDetail(cardId.toLong())
            friendCardViewModel.setCardId(cardId)
        }

        binding.closeBtn.setOnClickListener {
            friendCardViewModel.clearAllData()

            navController.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}