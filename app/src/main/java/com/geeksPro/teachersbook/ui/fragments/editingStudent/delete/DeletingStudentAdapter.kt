package com.geeksPro.teachersbook.ui.fragments.editingStudent.delete

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.ItemListStudentDeletingBinding

class DeletingStudentAdapter :
    Adapter<DeletingStudentAdapter.DeletingStudentViewHolder>() {

    private val studentData = arrayListOf<StudentModel>()
    private val deletingStudentsList = arrayListOf<Long>()

    fun addStudentList(students: List<StudentModel>) {
        studentData.clear()
        studentData.addAll(students)
        notifyDataSetChanged()
    }

    fun getDeletingStudents(): ArrayList<Long> {
        val returnList = ArrayList(deletingStudentsList)
        deletingStudentsList.clear()
        return returnList
    }

    fun getDeletingStudentsCount() = deletingStudentsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletingStudentViewHolder {
        return DeletingStudentViewHolder(
            ItemListStudentDeletingBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = studentData.size

    override fun onBindViewHolder(holder: DeletingStudentViewHolder, position: Int) =
        holder.bind(studentData[position])

    inner class DeletingStudentViewHolder(private val binding: ItemListStudentDeletingBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: StudentModel) {
            itemView.setOnClickListener {
                binding.chbCheck.isChecked = !binding.chbCheck.isChecked
            }
            binding.tvNameStudent.text = model.surname + " " + model.name
            binding.chbCheck.isChecked = false
            binding.chbCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deletingStudentsList.add(model.id!!)
                } else {
                    deletingStudentsList.remove(model.id!!)
                }
                Log.d("kiber", "bind: $deletingStudentsList")
            }
        }
    }
}

