package com.toyou.toyouandroid.presentation.fragment.emotionstamp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentHomeResultBinding
import com.toyou.toyouandroid.presentation.base.MainActivity

class HomeResultFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentHomeResultBinding? = null
    private val binding: FragmentHomeResultBinding
        get() = requireNotNull(_binding){"FragmentHomeResultBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeResultBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        (requireActivity() as MainActivity).hideBottomNavigation(true)

        // 번들에서 배경색 값을 가져와 설정
        val backgroundColor = arguments?.getInt("background_color") ?: R.drawable.background_white
        val text = arguments?.getString("text") ?: getString(R.string.home_stamp_result_normal_1)

        binding.root.setBackgroundResource(backgroundColor)
        binding.homeResultTv.text = text

        ObjectAnimator.ofFloat(binding.homeResultTv, "alpha", 0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }

        // 자동 화면 전환 및 클릭 시 전환
        val handler = Handler(Looper.getMainLooper())
        val navigateRunnable = Runnable {
            navController.navigate(R.id.action_navigation_home_result_to_home_fragment)
        }

        handler.postDelayed(navigateRunnable, 1500)

        binding.root.setOnClickListener {
            handler.removeCallbacks(navigateRunnable)
            navController.navigate(R.id.action_navigation_home_result_to_home_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
