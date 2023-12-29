package com.geeksPro.teachersbook.ui.fragments.addingGroup

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.databinding.ItemGroupBinding

class AddingGroupAdapter(private val onClick: (GroupModel) -> Unit) :
    ListAdapter<GroupModel, AddingGroupAdapter.AddingGroupViewHolder>(GroupDiffCallback()) {

    private val groupData = arrayListOf<GroupModel>()
    @SuppressLint("NotifyDataSetChanged")
    fun addGroup(group: List<GroupModel>){
        groupData.clear()
        groupData.addAll(group)
        groupData.sortByDescending { it.id }
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddingGroupViewHolder {
        return AddingGroupViewHolder(
            ItemGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount() = groupData.size

    override fun onBindViewHolder(holder: AddingGroupViewHolder, position: Int) {
        holder.bind(groupData[position])
    }
    inner class AddingGroupViewHolder(private val binding: ItemGroupBinding) :
        ViewHolder(binding.root) {
        fun bind(model: GroupModel) {
            binding.tvNameGroup.text = model.nameGroup

            itemView.setOnClickListener {
                onClick(model)
            }
        }
    }
    class GroupDiffCallback : DiffUtil.ItemCallback<GroupModel>() {
        override fun areItemsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GroupModel, newItem: GroupModel): Boolean {
            return oldItem == newItem
        }
    }
}


