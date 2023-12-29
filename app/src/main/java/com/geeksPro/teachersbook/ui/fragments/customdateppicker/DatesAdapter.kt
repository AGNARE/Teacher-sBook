package com.geeksPro.teachersbook.ui.fragments.customdateppicker

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.databinding.ItemDateBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class DatesAdapter(
    private val days: ArrayList<LocalDate?>,
    private val onItemListener: OnItemListener
) : RecyclerView.Adapter<DatesAdapter.CalendarViewHolder>() {

    private var selectedDay: LocalDate? = null
    private val currentMonth = YearMonth.now()

    inner class CalendarViewHolder(val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {
        val parentView = binding.root

        init {
            binding.root.setOnClickListener(this)
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun onClick(v: View?) {
            selectedDay = days[adapterPosition]
            onItemListener.onItemClick(adapterPosition, selectedDay)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDateBinding.inflate(inflater, parent, false)
        val layoutParams = binding.root.layoutParams
        if (days.size > 15)
            layoutParams.height = (parent.height * 0.166666666).toInt()
        else
            layoutParams.height = parent.height

        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val date = days[position]
        if (date == null) {
            holder.binding.dateTextView.text = ""
        } else {
            holder.binding.dateTextView.text = date.dayOfMonth.toString()

            val isInCurrentMonth = date.month == currentMonth.month

            val isPreviousMonth = date.isBefore(currentMonth.atDay(1)) && !isInCurrentMonth
            val isNextMonth = date.isAfter(currentMonth.atEndOfMonth()) && !isInCurrentMonth
            val isNextYear = date.year > currentMonth.year || (date.year == currentMonth.year && date.month.value > currentMonth.month.value)

            when {
                date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY -> {
                    holder.binding.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
                }
                isPreviousMonth -> {
                    holder.binding.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
                }
                isNextMonth || isNextYear -> {
                    holder.binding.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
                }
                else -> {
                    holder.binding.dateTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
                }
            }

            if (date == selectedDay) {
                holder.parentView.setBackgroundResource(R.drawable.selector_date_day)
            } else {
                holder.parentView.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun getItemCount() = days.size

    interface OnItemListener {
        fun onItemClick(position: Int, date: LocalDate?)
    }
}

