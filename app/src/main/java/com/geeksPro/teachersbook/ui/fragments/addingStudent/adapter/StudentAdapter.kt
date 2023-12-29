package com.geeksPro.teachersbook.ui.fragments.addingStudent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.ItemStudentsBinding
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LABORATORY
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LECTURE
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.PRACTICAL
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.SEMINAR

class StudentAdapter(
    private val navigateReportCard: (StudentModel) -> Unit,
    private val showToast: () -> Unit
) : Adapter<StudentAdapter.StudentViewHolder>() {

    private var list: MutableList<StudentModel> = mutableListOf()

    private var lectures = mutableListOf<LectureModel>()
    private var laboratories = mutableListOf<LaboratoryModel>()
    private var practicals = mutableListOf<PracticalModel>()
    private var seminars = mutableListOf<SeminarModel>()

    private var date = ""
    private var markBoolean: Boolean = false
    private var selectedClasses = 0

    fun addStudentsList(studentsList: List<StudentModel>) {
        list = studentsList.toMutableList()
        notifyDataSetChanged()
    }

    fun setDate(date: String) {
        this.date = date
        notifyDataSetChanged()
    }

    fun checkMode() {
        markBoolean = !markBoolean
        notifyDataSetChanged()
    }

    fun onResume() {
        markBoolean = false
    }

    fun setSelectedClasses(selectedClasses: Int) {
        this.selectedClasses = selectedClasses
    }

    fun setLectures(lectures: List<LectureModel>) {
        this.lectures = lectures.toMutableList()
        notifyDataSetChanged()
    }

    fun getLectures() = lectures.toList()

    fun setLaboratories(laboratories: List<LaboratoryModel>) {
        this.laboratories = laboratories.toMutableList()
        notifyDataSetChanged()
    }

    fun getLaboratories() = laboratories.toList()

    fun setPracticals(practicals: List<PracticalModel>) {
        this.practicals = practicals.toMutableList()
        notifyDataSetChanged()
    }

    fun getPracticals() = practicals.toList()

    fun setSeminars(seminars: List<SeminarModel>) {
        this.seminars = seminars.toMutableList()
        notifyDataSetChanged()
    }

    fun getSeminars() = seminars.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(
        ItemStudentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class StudentViewHolder(private val binding: ItemStudentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(studentModel: StudentModel) {
            binding.tvNumber.text = (adapterPosition + 1).toString() + "."
            binding.tvStudentsFullName.text = "${studentModel.surname} ${studentModel.name}"
            binding.checkBox.setOnCheckedChangeListener(null)

            var found = false
            for (lecture in lectures) {
                if (lecture.studentId == studentModel.id) {
                    found = true
                    break
                }
            }
            if (found.not()) {
                lectures.add(
                    LectureModel(
                        studentId = studentModel.id!!,
                        groupId = studentModel.groupId!!,
                        date = date,
                        visits = false
                    )
                )
            }

            found = false
            for (laboratory in laboratories) {
                if (laboratory.studentId == studentModel.id) {
                    found = true
                    break
                }
            }
            if (found.not()) {
                laboratories.add(
                    LaboratoryModel(
                        studentId = studentModel.id!!,
                        groupId = studentModel.groupId!!,
                        date = date,
                        grades = 0,
                        visits = false
                    )
                )
            }

            found = false
            for (practical in practicals) {
                if (practical.studentId == studentModel.id) {
                    found = true
                    break
                }
            }
            if (found.not()) {
                practicals.add(
                    PracticalModel(
                        studentId = studentModel.id!!,
                        groupId = studentModel.groupId!!,
                        date = date,
                        grades = 0,
                        visits = false
                    )
                )
            }

            found = false
            for (seminary in seminars) {
                if (seminary.studentId == studentModel.id) {
                    found = true
                    break
                }
            }
            if (found.not()) {
                seminars.add(
                    SeminarModel(
                        studentId = studentModel.id!!,
                        groupId = studentModel.groupId!!,
                        date = date,
                        grades = 0,
                        visits = false
                    )
                )
            }

            itemView.setOnClickListener {
                if (markBoolean) binding.checkBox.isChecked = !binding.checkBox.isChecked
                else navigateReportCard(studentModel)
            }

            when (selectedClasses) {
                LECTURE -> {
                    for (lecture in lectures) {
                        if (lecture.studentId == studentModel.id) {
                            binding.checkBox.isChecked = lecture.visits
                            break
                        }
                    }
                }

                LABORATORY -> {
                    for (laboratory in laboratories) {
                        if (laboratory.studentId == studentModel.id) {
                            binding.checkBox.isChecked = laboratory.visits
                            break
                        }
                    }
                }

                PRACTICAL -> {
                    for (practical in practicals) {
                        if (practical.studentId == studentModel.id) {
                            binding.checkBox.isChecked = practical.visits
                            break
                        }
                    }
                }

                SEMINAR -> {
                    for (seminar in seminars) {
                        if (seminar.studentId == studentModel.id) {
                            binding.checkBox.isChecked = seminar.visits
                            break
                        }
                    }
                }
            }

            binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
                when (selectedClasses) {
                    LECTURE -> {
                        val lecture = lectures.find { it.studentId == studentModel.id }
                        val index = lectures.indexOf(lecture)
                        if (lecture != null) {
                            lectures[index] = lecture.copy(
                                visits = isChecked
                            )
                        } else {
                            showToast()
                        }
                    }

                    LABORATORY -> {
                        val laboratory = laboratories.find { it.studentId == studentModel.id }
                        val index = laboratories.indexOf(laboratory)
                        if (laboratory != null) {
                            laboratories[index] = laboratory.copy(
                                visits = isChecked
                            )
                        } else {
                            showToast()
                        }
                    }

                    PRACTICAL -> {
                        val practical = practicals.find { it.studentId == studentModel.id }
                        val index = practicals.indexOf(practical)
                        if (practical != null) {
                            practicals[index] = practical.copy(
                                visits = isChecked
                            )
                        } else {
                            showToast()
                        }
                    }

                    SEMINAR -> {
                        val seminar = seminars.find { it.studentId == studentModel.id }
                        val index = seminars.indexOf(seminar)
                        if (seminar != null) {
                            seminars[index] = seminar.copy(
                                visits = isChecked
                            )
                        } else showToast()
                    }
                }
            }

            if (markBoolean) {
                binding.icChevronRight.visibility = View.GONE
                binding.checkBox.visibility = View.VISIBLE
            } else {
                binding.icChevronRight.visibility = View.VISIBLE
                binding.checkBox.visibility = View.GONE
            }
        }
    }
}