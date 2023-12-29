package com.geeksPro.teachersbook.ui.fragments.editGroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.databinding.ItemGroupEditBinding

class EditGroupAdapter(
    private val listener: (GroupModel) -> Unit,
    private val itemSelectedListener: OnItemSelectedListener
) : ListAdapter<GroupModel, EditGroupAdapter.EditGroupViewHolder>(EditGroupDiffCallback()) {

    interface OnItemSelectedListener {
        fun onItemSelected()
    }

    inner class EditGroupViewHolder(private val binding: ItemGroupEditBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnCheckbox.setOnClickListener {
                val model = getItem(adapterPosition)
                model.isSelectedGroup = binding.btnCheckbox.isChecked
                itemSelectedListener.onItemSelected()
            }

            binding.btnRenameGroup.setOnClickListener {
                val model = getItem(adapterPosition)
                listener.invoke(model)
            }
        }

        fun bind(model: GroupModel) {
            with(binding) {
                tvNameGroup.text = model.nameGroup
                btnCheckbox.isChecked = model.isSelectedGroup
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditGroupViewHolder {
        return EditGroupViewHolder(
            ItemGroupEditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: EditGroupViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }

    class EditGroupDiffCallback : DiffUtil.ItemCallback<GroupModel>() {
        override fun areItemsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
            return oldItem == newItem
        }
    }

    fun isAnyItemSelected(): Boolean {
        return currentList.any { it.isSelectedGroup }
    }
}
