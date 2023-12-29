package com.geeksPro.teachersbook.ui.fragments.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.ItemSearchBinding

class SearchAdapter(val listenerToId: OnItemClickListener) :
    ListAdapter<StudentModel, SearchAdapter.SearchViewHolder>(SearchDiffCallBack()) {
    inner class SearchViewHolder(private val binding: ItemSearchBinding) :
        ViewHolder(binding.root) {

        fun bind(model: StudentModel) {
            val nameAndSurname = "${model.name} ${model.surname}"

            binding.tvStudentsFullName.text = nameAndSurname

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val student = getItem(position)
                    listenerToId.onItemClick(
                        student.id,
                        student.name,
                        student.surname,
                        student.name,
                        student.id!!,
                        student.groupId!!
                    )
                }
            }
        }

    }

    class SearchDiffCallBack : DiffUtil.ItemCallback<StudentModel>() {
        override fun areItemsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: StudentModel, newItem: StudentModel): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SearchViewHolder(
        ItemSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }

    interface OnItemClickListener {
        fun onItemClick(
            studentId: Long?,
            studentName: String,
            studentSurname: String,
            groupName: String,
            typeClass: Long,
            groupId: Long?=null
        )
    }
}
