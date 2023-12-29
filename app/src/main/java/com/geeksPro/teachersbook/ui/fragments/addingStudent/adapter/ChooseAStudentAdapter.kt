package com.geeksPro.teachersbook.ui.fragments.addingStudent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.ItemChooseAStudentBinding

class ChooseAStudentAdapter(
    private val showGradesAD: (StudentModel) -> Unit,
) : Adapter<ChooseAStudentAdapter.StudentViewHolder>() {

    private var list: MutableList<StudentModel> = mutableListOf()

    private var alertDialog: AlertDialog? = null

    fun addStudentsList(studentsList: List<StudentModel>) {
        list.clear()
        list.addAll(studentsList)
        notifyDataSetChanged()
    }

    fun setAlertDialog(alertDialog: AlertDialog) {
        this.alertDialog = alertDialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(
        ItemChooseAStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class StudentViewHolder(private val binding: ItemChooseAStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(studentModel: StudentModel) {
            binding.tvStudentsFullName.text = "${studentModel.name} ${studentModel.surname}"

            itemView.setOnClickListener {
                showGradesAD(studentModel)
                alertDialog?.dismiss()
            }
        }
    }
}
