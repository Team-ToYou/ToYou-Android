package com.toyou.toyouandroid.presentation.fragment.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toyou.toyouHoandroid.data.create.service.CreateService
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.data.onboarding.service.AuthService
import com.toyou.toyouandroid.presentation.base.MainActivity
import com.toyou.toyouandroid.databinding.FragmentCreateBinding
import com.toyou.toyouandroid.domain.create.repository.CreateRepository
import com.toyou.toyouandroid.network.AuthNetworkModule
import com.toyou.toyouandroid.network.NetworkModule
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardChooseAdapter
import com.toyou.toyouandroid.presentation.fragment.home.adapter.CardShortAdapter
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModel
import com.toyou.toyouandroid.presentation.viewmodel.CardViewModelFactory
import com.toyou.toyouandroid.utils.TokenManager
import com.toyou.toyouandroid.utils.TokenStorage
import timber.log.Timber

class CreateFragment : Fragment(){

    private var _binding: FragmentCreateBinding? = null
    private val binding: FragmentCreateBinding get() = requireNotNull(_binding) { "널" }

    private lateinit var cardAdapter : CardAdapter
    private lateinit var cardShortAdapter : CardShortAdapter
    private lateinit var cardChooseAdapter: CardChooseAdapter
    private lateinit var cardViewModel: CardViewModel

    lateinit var navController: NavController
    var count : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenStorage = TokenStorage(requireContext())
        val authService = NetworkModule.getClient().create(AuthService::class.java)
        val tokenManager = TokenManager(authService, tokenStorage)
        val createService = AuthNetworkModule.getClient().create(CreateService::class.java)
        val createRepository = CreateRepository(createService)


        cardViewModel = ViewModelProvider(
            requireActivity(),
            CardViewModelFactory(createRepository,tokenManager)
        )[CardViewModel::class.java]

        //cardViewModel.loadCardData()
        cardViewModel.getAllData()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCreateBinding.inflate(inflater, container, false)



        cardViewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            cardAdapter.setCards(cards)
        })

        cardViewModel.shortCards.observe(viewLifecycleOwner, Observer { cards ->
            cardShortAdapter.setCards(cards)
        })


        cardViewModel.chooseCards.observe(viewLifecycleOwner, Observer { cards ->
            cardChooseAdapter.setCards(cards)
        })

        cardAdapter = CardAdapter({ position, isSelected ->
            cardViewModel.updateButtonState(position, isSelected)
            cardViewModel.countSelect(isSelected)
        }, cardViewModel)
        cardShortAdapter = CardShortAdapter({ position, isSelected ->
            cardViewModel.updateShortButtonState(position, isSelected)
            cardViewModel.countSelect(isSelected)
        }, cardViewModel)
        cardChooseAdapter = CardChooseAdapter({ position, isSelected ->
            cardViewModel.updateChooseButton(position, isSelected)
            cardViewModel.countSelect(isSelected)
        }, cardViewModel)



        setupRecyclerView(binding.cardRv, cardAdapter)
        setupRecyclerView(binding.cardShortRv, cardShortAdapter)
        setupRecyclerView(binding.cardChooseRv, cardChooseAdapter)

        val mainActivity = activity as MainActivity // casting
        mainActivity.hideBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(view)

        binding.backFrame.setOnClickListener {
            handleBackAction()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handleBackAction()
            }

        })

        cardViewModel.countSelection.observe(viewLifecycleOwner, Observer { count ->
            binding.nextBtn.isEnabled = count > 0
            Log.d("선택9", count.toString())
        })


        binding.nextBtn.setOnClickListener {
            cardViewModel.updateAllPreviews()
            //cardViewModel.initialize()
            cardViewModel.resetSelect()
            cardViewModel.disableLock(false)
            navController.navigate(R.id.action_create_fragment_to_createWriteFragment)
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter

            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            val margin = (screenWidth * 0.05).toInt() // 화면 너비의 5%를 마진으로 사용

            // 아이템 데코레이션 추가
            addItemDecoration(RVMarginItemDecoration(margin, true))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleBackAction() {
        cardViewModel.resetSelect()
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigation(false)
        navController.navigate(R.id.action_create_fragment_to_navigation_home)
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}
