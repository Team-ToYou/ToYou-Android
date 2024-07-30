package com.toyou.toyouandroid.presentation.fragment.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.toyou.toyouandroid.R
import com.toyou.toyouandroid.databinding.FragmentCalendarBinding
import com.toyou.toyouandroid.model.CalendarItem

class CalendarFragment : Fragment() {

    lateinit var navController: NavController
    private var _binding: FragmentCalendarBinding? = null
    private val binding: FragmentCalendarBinding
        get() = requireNotNull(_binding){"FragmentCalendarBinding -> null"}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        val items = listOf(
            CalendarItem(R.drawable.home_stamp_option_normal, "태연"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "승원"),
            CalendarItem(R.drawable.home_stamp_option_happy, "현정"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "유은"),
            CalendarItem(R.drawable.home_stamp_option_normal, "태연"),
            CalendarItem(R.drawable.home_stamp_option_upset, "승원"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "현정"),
            CalendarItem(R.drawable.home_stamp_option_normal, "유은"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "태연"),
            CalendarItem(R.drawable.home_stamp_option_happy, "승원"),
            CalendarItem(R.drawable.home_stamp_option_normal, "현정"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "유은"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "태연"),
            CalendarItem(R.drawable.home_stamp_option_normal, "태연킹왕짱"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "승원킹왕짱"),
            CalendarItem(R.drawable.home_stamp_option_happy, "현정킹왕짱"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "유은킹왕짱"),
            CalendarItem(R.drawable.home_stamp_option_normal, "태연"),
            CalendarItem(R.drawable.home_stamp_option_upset, "승원"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "현정"),
            CalendarItem(R.drawable.home_stamp_option_normal, "유은"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "태연킹"),
            CalendarItem(R.drawable.home_stamp_option_happy, "승원킹"),
            CalendarItem(R.drawable.home_stamp_option_normal, "현정킹"),
            CalendarItem(R.drawable.home_stamp_option_anxiety, "유은킹"),
            CalendarItem(R.drawable.home_stamp_option_exciting, "태연"),
        )

        val adapter = CalendarAdapter(items)
        binding.calendarRv.layoutManager = GridLayoutManager(context, 5)
        binding.calendarRv.adapter = adapter

        val verticalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing)
        val horizontalSpaceHeight = resources.getDimensionPixelSize(R.dimen.recycler_item_spacing_side)
        binding.calendarRv.addItemDecoration(CalendarItemDecoration(horizontalSpaceHeight, verticalSpaceHeight))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}