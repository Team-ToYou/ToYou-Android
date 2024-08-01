package com.toyou.toyouandroid.presentation.fragment.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.tabs.TabLayout
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentRecordBinding
import com.toyou.toyouandroid.presentation.fragment.record.friend.CalendarFriendRecordFragment
import com.toyou.toyouandroid.presentation.fragment.record.my.CalendarMyRecordFragment

class RecordFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentRecordBinding? = null
    private val binding: FragmentRecordBinding
        get() = requireNotNull(_binding){"FragmentRecordBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRecordBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        val tabLayout: TabLayout = binding.tabLayout

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> replaceFragment(CalendarMyRecordFragment())
                    1 -> replaceFragment(CalendarFriendRecordFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // 초기 화면 설정
        if (savedInstanceState == null) {
            tabLayout.getTabAt(0)?.select()
            replaceFragment(CalendarMyRecordFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            replace(R.id.record_fragment_Container, fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}