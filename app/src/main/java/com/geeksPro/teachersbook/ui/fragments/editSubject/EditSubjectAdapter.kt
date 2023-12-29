package com.geeksPro.teachersbook.ui.fragments.editSubject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.databinding.ItemEditSubjectBinding

class EditSubjectAdapter(
    val listener: (SubjectModel) -> Unit,
    val itemSelectedListener: OnItemSelectedListener
) :
    ListAdapter<SubjectModel, EditSubjectAdapter.EditViewHolder>(EditDiffCallback()) {

    interface OnItemSelectedListener {
        fun onItemSelected()
    }
    inner class EditViewHolder(val binding: ItemEditSubjectBinding) : ViewHolder(binding.root) {
        init {
            binding.btnCheckbox.setOnClickListener {
                val model = getItem(adapterPosition)
                model.isSelected = binding.btnCheckbox.isChecked
                itemSelectedListener.onItemSelected()
            }

            binding.btnRenameSubject.setOnClickListener {
                val model = getItem(adapterPosition)
                listener.invoke(model)
            }
        }

        fun bind(model: SubjectModel) {
            binding.tvEditedNameSubject.text = model.nameSubject
            binding.btnCheckbox.isChecked = model.isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = EditViewHolder(
        ItemEditSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: EditViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }

    class EditDiffCallback : DiffUtil.ItemCallback<SubjectModel>() {
        override fun areItemsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
            return oldItem == newItem
        }
    }

    fun isAnyItemSelected(): Boolean {
        return currentList.any { it.isSelected }
    }
}
