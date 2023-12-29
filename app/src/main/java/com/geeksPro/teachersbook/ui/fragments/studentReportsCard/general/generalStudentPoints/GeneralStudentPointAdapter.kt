package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.general.generalStudentPoints

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.databinding.ItemGeneralPointBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class GeneralStudentPointAdapter :
    ListAdapter<DateAndGrade, GeneralStudentPointAdapter.GeneralPointViewHolder>(
        PointDiffCallBack()
    ) {

    var onEditStatusChangeListener: OnEditStatusChangeListener? = null

    interface OnEditStatusChangeListener {
        fun onEditStatusChange(): Boolean
    }

    private fun sortListByDate(list: List<DateAndGrade>): List<DateAndGrade> {
        return list.sortedBy { getDateFromString(it.date) }
    }

    private fun getDateFromString(dateString: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern(FULL_DATE_TEXT, Locale("ru"))
        return LocalDate.parse(dateString, formatter)
    }

    override fun submitList(list: List<DateAndGrade>?) {
        super.submitList(list?.let { sortListByDate(it) })
    }
    inner class GeneralPointViewHolder(private val binding: ItemGeneralPointBinding) :
        ViewHolder(binding.root) {
        private var currentPosition: Int = -1

        fun bind(position: Int, model: DateAndGrade) {
            currentPosition = position
            try {
                val formatter = DateTimeFormatter.ofPattern(FULL_DATE_TEXT, Locale("ru"))
                val date = LocalDate.parse(model.date, formatter)
                val formattedDate = DateTimeFormatter.ofPattern(DATE_TEXT, Locale("ru")).format(date)
                binding.tvDate.text = formattedDate
            } catch (e: Exception) {
                binding.tvDate.text = INVALID_DATE_TEXT
            }
            binding.etGrades.setText(model.grades.toString())
            binding.etGrades.removeTextChangedListener(createTextWatcher(binding.etGrades))
            binding.etGrades.addTextChangedListener(createTextWatcher(binding.etGrades))
            binding.etGrades.isEnabled = onEditStatusChangeListener?.onEditStatusChange() ?: false
        }

        private fun createTextWatcher(etGrades: EditText): TextWatcher {
            var ignoreNextTextChange = false
            return object : TextWatcher {
                override fun afterTextChanged(s: Editable) {
                    if (ignoreNextTextChange) {
                        ignoreNextTextChange = false
                        return
                    }
                    val enteredGrade = s.toString().trim().toIntOrNull()
                    if (enteredGrade != null) {
                        if (enteredGrade > MAX_GRADE) {
                            Toast.makeText(etGrades.context, R.string.possible_only_up_to_10_points, Toast.LENGTH_SHORT).show()
                            ignoreNextTextChange = true
                            etGrades.setText("")
                        } else {
                            updateItemGrade(currentPosition, enteredGrade)
                        }
                    }
                }

                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            }
        }
    }

    class PointDiffCallBack : DiffUtil.ItemCallback<DateAndGrade>() {
        override fun areItemsTheSame(
            oldItem: DateAndGrade, newItem: DateAndGrade
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DateAndGrade, newItem: DateAndGrade
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = GeneralPointViewHolder(
        ItemGeneralPointBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: GeneralPointViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(position, model)
    }

    private fun updateItemGrade(position: Int, newGrade: Int) {
        val currentListCopy = currentList.toMutableList()
        currentListCopy[position].grades = newGrade
        submitList(currentListCopy)
    }

    fun getGrades(): List<DateAndGrade> {
        return currentList
    }

    companion object {
        const val INVALID_DATE_TEXT = "Invalid"
        const val MAX_GRADE = 10
        const val FULL_DATE_TEXT = "dd MMMM yyyy"
        const val DATE_TEXT = "dd MMMM"
    }
}

