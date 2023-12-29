package com.geeksPro.teachersbook.ui.fragments.addingSubject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.databinding.ItemSubjectsBinding

class SubjectsAdapter(val navigateListener: (SubjectModel) -> Unit) :
    ListAdapter<SubjectModel, SubjectsAdapter.SubjectViewHolder>(SubjectDiffUtil()) {

    inner class SubjectViewHolder(private val binding: ItemSubjectsBinding) :
        ViewHolder(binding.root) {

        fun bind(model: SubjectModel) {
            itemView.setOnClickListener { navigateListener.invoke(model) }
            binding.tvNameSubjects.text = model.nameSubject
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SubjectViewHolder(
        ItemSubjectsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }
}
class SubjectDiffUtil : DiffUtil.ItemCallback<SubjectModel>() {
    override fun areItemsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SubjectModel, newItem: SubjectModel): Boolean {
        return oldItem == newItem
    }
}
