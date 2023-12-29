package com.geeksPro.teachersbook.ui.fragments.editingStudent

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.ItemListStudentBinding

class EditingStudentAdapter(private val onClick: (StudentModel) -> Unit) :
    RecyclerView.Adapter<EditingStudentAdapter.EditingStudentViewHolder>() {

    private val studentData = arrayListOf<StudentModel>()

    fun addStudentList(students: List<StudentModel>) {
        studentData.clear()
        studentData.addAll(students)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditingStudentViewHolder {
        return EditingStudentViewHolder(
            ItemListStudentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount() = studentData.size
    override fun onBindViewHolder(holder: EditingStudentViewHolder, position: Int) =
        holder.bind(studentData[position])

    inner class EditingStudentViewHolder(private val binding: ItemListStudentBinding) :
        ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(model: StudentModel) {
            binding.tvNameStudent.text = model.surname + " " + model.name
            binding.btnEditingStudent.setOnClickListener {
                onClick(model)
            }
        }
    }
}
