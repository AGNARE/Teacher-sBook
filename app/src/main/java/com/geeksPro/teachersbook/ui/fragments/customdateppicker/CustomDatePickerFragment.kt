package com.geeksPro.teachersbook.ui.fragments.customdateppicker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.databinding.FragmentCustomDatePickerBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

class CustomDatePickerFragment : DialogFragment(), DatesAdapter.OnItemListener {
    private lateinit var binding: FragmentCustomDatePickerBinding
    private var selectedDate: LocalDate = LocalDate.now()
    private val viewModel: DatePickerViewModel by lazy {
        ViewModelProvider(requireActivity())[DatePickerViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomDatePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarRecyclerView.addItemDecoration(GridSpacingItemDecoration(7, 56, true))

        setMonthView()

        binding.btnPrevMonth.setOnClickListener {
            previousMonthAction()
        }
        binding.btnNextMonth.setOnClickListener {
            nextMonthAction()
        }

    }

    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        val params = window?.attributes

        params?.width = (resources.displayMetrics.widthPixels * 0.95).toInt()
        window?.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_date_picker)
        return dialog
    }

    private fun setMonthView() {
        binding.tvMonthYear.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        val calendarAdapter = DatesAdapter(daysInMonth, this)
        binding.calendarRecyclerView.adapter = calendarAdapter
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale(LOCAL_RU))
        return date.format(formatter)
    }

    private fun previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

    private fun nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<LocalDate?> {
        val daysInMonthArray = ArrayList<LocalDate?>()
        val yearMonth = YearMonth.from(date)

        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val firstDayOfWeek = firstOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val lastOfMonth = selectedDate.withDayOfMonth(yearMonth.lengthOfMonth())
        val lastDayOfWeek = lastOfMonth.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

        var currentDay = firstDayOfWeek
        while (currentDay <= lastDayOfWeek) {
            daysInMonthArray.add(currentDay)
            currentDay = currentDay.plusDays(1)
        }
        return daysInMonthArray
    }

    override fun onItemClick(position: Int, date: LocalDate?) {
        date?.let {
            val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN, Locale(LOCAL_RU))
            val formattedDate = it.format(formatter)
            viewModel.selectedDate.value = formattedDate
            dismiss()
        }
    }

    companion object {
        const val DATE_FORMAT_PATTERN = "dd MMMM yyyy"
        const val LOCAL_RU = "ru"
    }
}