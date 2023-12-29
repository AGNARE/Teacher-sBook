package com.geeksPro.teachersbook.ui.fragments.addingGroup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.databinding.ItemChooseSubjectForGroupBinding

class ChooseSubjectDialogAdapter(private val onChecked: (SubjectModel) -> Unit) :
    Adapter<ChooseSubjectDialogAdapter.ChooseSubjectViewHolder>() {

    private val subjectData = arrayListOf<SubjectModel>()
    private var selectedSubjectIndex = -1

    @SuppressLint("NotifyDataSetChanged")
    fun addSubjects(subjects: List<SubjectModel>) {
        subjectData.clear()
        subjectData.addAll(subjects)
        subjectData.sortByDescending { it.id }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseSubjectViewHolder {
        return ChooseSubjectViewHolder(
            ItemChooseSubjectForGroupBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = subjectData.size

    override fun onBindViewHolder(holder: ChooseSubjectViewHolder, position: Int) {
        holder.bind(subjectData[position])
    }

    inner class ChooseSubjectViewHolder(private val binding: ItemChooseSubjectForGroupBinding) :
        ViewHolder(binding.root) {
        fun bind(model: SubjectModel) {
            binding.tvNameSubject.text = model.nameSubject

            // Remove the existing onCheckedChangeListener
            binding.checkBox.setOnCheckedChangeListener(null)

            // Set the checked state of the CheckBox
            binding.checkBox.isChecked = position == selectedSubjectIndex

            // Set an onClickListener for the CheckBox
            binding.checkBox.setOnClickListener {
                // Toggle the selection based on the new checked state
                if (binding.checkBox.isChecked) {
                    selectSubject(model)
                } else {
                    deselectSubject()
                }
            }

            binding.root.setOnClickListener {
                toggleSelection(model)
                onChecked(model)
            }
        }

        private fun selectSubject(subject: SubjectModel) {
            val oldIndex = selectedSubjectIndex
            selectedSubjectIndex = subjectData.indexOf(subject)
            notifyItemChanged(oldIndex)
            notifyItemChanged(selectedSubjectIndex)
            onChecked(subject)
        }

        private fun deselectSubject() {
            val oldIndex = selectedSubjectIndex
            selectedSubjectIndex = -1
            notifyItemChanged(oldIndex)
        }

        private fun toggleSelection(subject: SubjectModel) {
            if (selectedSubjectIndex == subjectData.indexOf(subject)) {
                deselectSubject()
            } else {
                selectSubject(subject)
            }
        }
    }

    fun getSelectedSubject(): SubjectModel? {
        return if (selectedSubjectIndex != -1) {
            subjectData[selectedSubjectIndex]
        } else {
            null
        }
    }
}