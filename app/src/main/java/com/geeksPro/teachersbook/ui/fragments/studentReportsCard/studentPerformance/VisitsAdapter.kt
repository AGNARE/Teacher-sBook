package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.studentPerformance

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.databinding.ItemGeneralVisitBinding
import com.geeksPro.teachersbook.ui.fragments.customdateppicker.CustomDatePickerFragment
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class VisitsAdapter(
    private val context: Context,
    private val onItemClick: (VisitsModel) -> Unit
) : RecyclerView.Adapter<VisitsAdapter.VisitsViewHolder>() {

    val originalFormat = SimpleDateFormat("dd MMMM yyyy", Locale("ru", "RU"))
    val dateFormat = SimpleDateFormat(CustomDatePickerFragment.DATE_FORMAT_PATTERN, Locale(RU_LANGUAGE))
    val dayMonthFormat = SimpleDateFormat(DAY_MONTH_FORMAT, Locale(RU_LANGUAGE))
    val dayOfWeekFormat = SimpleDateFormat(DAY_WEEK_FORMAT, Locale(RU_LANGUAGE))

    private var visitsModelList = mutableListOf<VisitsModel>()

    fun setVisitsModelList(visitsModelList: List<VisitsModel>) {
        this.visitsModelList = visitsModelList.toMutableList()
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VisitsViewHolder(
        ItemGeneralVisitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = visitsModelList.size

    override fun onBindViewHolder(holder: VisitsViewHolder, position: Int) {
        holder.bind(visitsModelList[position])
    }

    inner class VisitsViewHolder(private val binding: ItemGeneralVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(visitsModel: VisitsModel) {
            try {
                val originalDate = originalFormat.parse(visitsModel.date)
                val convertedDate = dateFormat.format(originalDate)
                val date = dateFormat.parse(convertedDate)
                with(binding) {
                    tvWeekday.text = dayOfWeekFormat.format(date!!).replaceFirstChar { it.uppercase() }
                    tvDate.text = dayMonthFormat.format(date)
                    tvVisits.text = if (visitsModel.visits) {
                        tvVisits.setTextColor(ContextCompat.getColor(context, R.color.green))
                        ONE_TEXT
                    } else {
                        tvVisits.setTextColor(ContextCompat.getColor(context, R.color.red))
                        ZERO_TEXT
                    }
                }
                itemView.setOnClickListener { onItemClick(visitsModel) }
            } catch (e: ParseException) {
                // Handle the exception here
                e.printStackTrace()
            }
        }

    }

    companion object {
        const val ZERO_TEXT = "0"
        const val ONE_TEXT = "1"
        const val RU_LANGUAGE = "ru"
        const val DAY_MONTH_FORMAT = "dd MMMM"
        const val DAY_WEEK_FORMAT = "EE"
    }
}