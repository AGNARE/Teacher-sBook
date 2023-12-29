package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.studentPerformance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.LectureModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.databinding.AlertDialogEditVisitsBinding
import com.geeksPro.teachersbook.databinding.AlertDialogGeneralVisitBinding
import com.geeksPro.teachersbook.databinding.FragmentStudentPerformanceBinding
import com.geeksPro.teachersbook.extentions.createAndSharePDF
import com.geeksPro.teachersbook.extentions.mainNavController
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LABORATORY
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.LECTURE
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.PRACTICAL
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.SEMINAR
import com.geeksPro.teachersbook.ui.fragments.search.SearchFragment.Companion.STUDENT_ID
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@AndroidEntryPoint
class StudentPerformanceFragment :
    BaseFragment<FragmentStudentPerformanceBinding, StudentPerformanceViewModel>(
        R.layout.fragment_student_performance
    ) {

    override val viewModel: StudentPerformanceViewModel by viewModels()
    private var nameAndSurName = ""
    private lateinit var bundle: Bundle
    private val adapter: VisitsAdapter by lazy {
        VisitsAdapter(
            requireContext(),
            this::showEditVisitsAD
        )
    }
    private val formatter = DateTimeFormatter.ofPattern(DATE_TEXT, Locale(RU_LANGUAGE))
    private val currentDate = LocalDate.now()
    private val month: String = currentDate.month.getDisplayName(TextStyle.FULL, Locale(RU_LANGUAGE))
    private var selectedMonth = month
    private var studentId = 0L

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentStudentPerformanceBinding.inflate(inflater, container, false)

    override fun initUI() {
        super.initUI()

        studentId = arguments?.getLong(STUDENT_ID)!!
        val studentName = arguments?.getString(StudentReportsCardFragment.STUDENT_NAME)!!
        val studentSurname = arguments?.getString(StudentReportsCardFragment.STUDENT_SURNAME)!!
        val groupName = arguments?.getString(StudentReportsCardFragment.GROUP_NAME)!!
        val typeClasses = arguments?.getInt(StudentReportsCardFragment.SUBJECT_TYPE_CLASSES)
        val formattedDate = currentDate.format(formatter)

        nameAndSurName = "$studentName $studentSurname"

        val capitalizedDate = capitalizeFirstLetterOfMonth(formattedDate)
        with(binding) {
            tvNameStudent.text = nameAndSurName
            tvNameGroup.text = groupName
            lecturesLessonDate.text = capitalizedDate
            laboratoryLessonsDate.text = capitalizedDate
            practiceLessonsDate.text = capitalizedDate
            seminarLessonsDate.text = capitalizedDate
        }

        viewModel.setTypeClasses(typeClasses!!)

        classesUIGone()

        var lecture = false
        var laboratory = false
        var practical = false
        var seminar = false

        val typeClassesRevers = typeClasses.toString().reversed()
        for (i in typeClassesRevers.indices) {
            if (i == 0 && typeClassesRevers[i] == '1') seminar = true
            if (i == 1 && typeClassesRevers[i] == '1') practical = true
            if (i == 2 && typeClassesRevers[i] == '1') laboratory = true
            if (i == 3 && typeClassesRevers[i] == '1') lecture = true
        }
        if (lecture) lectureVisible()

        if (laboratory) laboratoryVisible()

        if (practical) practicalVisible()

        if (seminar) seminarVisible()

        viewModel.getAllData(studentId, month)
    }

    override fun initClick() {
        super.initClick()
        with(binding) {
            moreLectureVisits.setOnClickListener {
                viewModel.getLectures(studentId, month)
                showGeneralVisitsAD(LECTURE)
            }

            laboratoryMore.setOnClickListener {
                bundle = Bundle().apply {
                    putInt(ID_TEXT, 1)
                    putLong(STUDENT_ID, studentId)
                }
                mainNavController().navigate(R.id.generalStudentPointFragment, bundle)
            }
            moreLaboratoryVisits.setOnClickListener {
                viewModel.getLaboratories(studentId, month)
                showGeneralVisitsAD(LABORATORY)
            }

            practiceMore.setOnClickListener {
                bundle = Bundle().apply {
                    putInt(ID_TEXT, 2)
                    putLong(STUDENT_ID, studentId)
                }
                mainNavController().navigate(R.id.generalStudentPointFragment, bundle)
            }

            morePracticalVisits.setOnClickListener {
                viewModel.getPracticals(studentId, month)
                showGeneralVisitsAD(PRACTICAL)
            }

            seminarMore.setOnClickListener {
                bundle = Bundle().apply {
                    putInt(ID_TEXT, 3)
                    putLong(STUDENT_ID, studentId)
                }
                mainNavController().navigate(R.id.generalStudentPointFragment, bundle)
            }

            moreSeminarVisits.setOnClickListener {
                viewModel.getSeminars(studentId, month)
                showGeneralVisitsAD(SEMINAR)
            }
            buttonShare.setOnClickListener {
                createAndSharePDF(binding.container, nameAndSurName,4000)
            }
        }
    }

    private fun classesUIGone() {
        with(binding) {
            tvLectureTitle.visibility = View.GONE
            containerLecture.visibility = View.GONE

            tvLaboratoryTitle.visibility = View.GONE
            containerLaboratory.visibility = View.GONE
            laboratoryPercentContainer.visibility = View.GONE
            laboratoryGradesContainer.visibility = View.GONE

            tvPracticalTitle.visibility = View.GONE
            containerPractical.visibility = View.GONE
            practicalPercentContainer.visibility = View.GONE
            practicalGradesContainer.visibility = View.GONE

            tvSeminarTitle.visibility = View.GONE
            containerSeminar.visibility = View.GONE
            seminarPercentContainer.visibility = View.GONE
            seminarGradesContainer.visibility = View.GONE
        }
    }

    private fun lectureVisible() {
        with(binding) {
            tvLectureTitle.visibility = View.VISIBLE
            containerLecture.visibility = View.VISIBLE
            tvStudentSucces.visibility = View.GONE
            generalStatistic.visibility= View.GONE
            points.visibility = View.GONE
            pointCont.visibility = View.GONE
        }
    }

    private fun laboratoryVisible() {
        with(binding) {
            tvLaboratoryTitle.visibility = View.VISIBLE
            containerLaboratory.visibility = View.VISIBLE
            laboratoryPercentContainer.visibility = View.VISIBLE
            laboratoryGradesContainer.visibility = View.VISIBLE
            onlyLectureCase()
        }
    }

    private fun practicalVisible() {
        with(binding) {
            tvPracticalTitle.visibility = View.VISIBLE
            containerPractical.visibility = View.VISIBLE
            practicalPercentContainer.visibility = View.VISIBLE
            practicalGradesContainer.visibility = View.VISIBLE
            onlyLectureCase()
        }
    }

    private fun seminarVisible() {
        with(binding) {
            tvSeminarTitle.visibility = View.VISIBLE
            containerSeminar.visibility = View.VISIBLE
            seminarPercentContainer.visibility = View.VISIBLE
            seminarGradesContainer.visibility = View.VISIBLE
            onlyLectureCase()
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.lectureMonthlyCountVisits.observe(viewLifecycleOwner) {
            binding.lecturesLessonsGeneralVisits.text = it.toString()
        }
        viewModel.lectureCountVisits.observe(viewLifecycleOwner) {
            binding.tvLecturesVisits.text = it.toString()
        }
        viewModel.lectureCountAbsence.observe(viewLifecycleOwner) {
            binding.tvLecturesAbsences.text = it.toString()
        }
        viewModel.percentageLectureAbsence.observe(viewLifecycleOwner) {
            binding.progressBarLecturesAbsences.progress = it
        }
        viewModel.percentageLectureVisits.observe(viewLifecycleOwner) {
            binding.progressBarLecturesVisits.progress = it
        }
        viewModel.laboratoryMonthlyCountVisits.observe(viewLifecycleOwner) {
            binding.laboratoryLessonsGeneralVisits.text = it.toString()
        }
        viewModel.laboratoryCountVisits.observe(viewLifecycleOwner) {
            binding.tvLaboratoryVisits.text = it.toString()
        }
        viewModel.laboratoryCountAbsence.observe(viewLifecycleOwner) {
            binding.tvLaboratoryAbsence.text = it.toString()
        }
        viewModel.laboratorySumGrades.observe(viewLifecycleOwner) {
            viewModel.getAllData(studentId,month)
            binding.laboratoryGrades.text = getString(R.string.points) +" "+ it.toString()
        }
        viewModel.laboratoryPercentageGrades.observe(viewLifecycleOwner) {
            binding.tvLaboratoryPresent.text = String.format("%.1f", it) + "%"
        }
        viewModel.percentageLaboratoryAbsence.observe(viewLifecycleOwner) {
            binding.progressBarLaboratoryAbsences.progress = it
        }
        viewModel.percentageLaboratoryVisits.observe(viewLifecycleOwner) {
            binding.progressBarLaboratoryVisits.progress = it
        }
        viewModel.practicalMonthlyCountVisits.observe(viewLifecycleOwner) {
            binding.practiceGeneralVisits.text = it.toString()
        }
        viewModel.practicalCountVisits.observe(viewLifecycleOwner) {
            binding.tvPracticeVisits.text = it.toString()
        }
        viewModel.practicalCountAbsence.observe(viewLifecycleOwner) {
            binding.tvPracticeAbsences.text = it.toString()
        }
        viewModel.practicalSumGrades.observe(viewLifecycleOwner) {
            binding.practicalGrades.text = getString(R.string.points) +" " +it.toString()
        }
        viewModel.practicalPercentageGrades.observe(viewLifecycleOwner) {
            binding.practicePercent.text = String.format("%.1f", it) + "%"
        }
        viewModel.percentagePracticalAbsence.observe(viewLifecycleOwner) {
            binding.progressBarPracticalAbsences.progress = it
        }
        viewModel.percentagePracticalVisits.observe(viewLifecycleOwner) {
            binding.progressBarPracticalVisits.progress = it
        }
        viewModel.seminarMonthlyCountVisits.observe(viewLifecycleOwner) {
            binding.seminarGeneralVisits.text = it.toString()
        }
        viewModel.seminarCountVisits.observe(viewLifecycleOwner) {
            binding.tvSeminarVisits.text = it.toString()
        }
        viewModel.seminarCountAbsence.observe(viewLifecycleOwner) {
            binding.tvSeminarAbsence.text = it.toString()
        }
        viewModel.seminarSumGrades.observe(viewLifecycleOwner) {
            binding.tvSeminarGrades.text = getString(R.string.points) + " " +it.toString()
        }
        viewModel.seminarPercentageGrades.observe(viewLifecycleOwner) {
            binding.seminarPercent.text = String.format("%.1f", it) + "%"
        }
        viewModel.percentageSeminarAbsence.observe(viewLifecycleOwner) {
            binding.progressBarSeminarAbsences.progress = it
        }
        viewModel.percentageSeminarVisits.observe(viewLifecycleOwner) {
            binding.progressBarSeminarVisits.progress = it
        }
        viewModel.studentPerformancePercentage.observe(viewLifecycleOwner) {
            binding.tvGlobalPercentage.text = String.format("%.1f", it) + "%"
            binding.globalPercentageProgressBar.progress = it.toInt()
        }
        viewModel.lectures.observe(viewLifecycleOwner) {
            adapter.setVisitsModelList(it.toLectureVisitsModel(LECTURE))
        }
        viewModel.laboratories.observe(viewLifecycleOwner) {
            adapter.setVisitsModelList(it.toLaboratoryVisitsModel(LABORATORY))
        }
        viewModel.practicals.observe(viewLifecycleOwner) {
            adapter.setVisitsModelList(it.toPracticalVisitsModel(PRACTICAL))
        }
        viewModel.seminars.observe(viewLifecycleOwner) {
            adapter.setVisitsModelList(it.toSeminarVisitsModel(SEMINAR))
        }
    }

    private fun showGeneralVisitsAD(typeClasses: Int) {
        var selectedDate = currentDate
        val formattedSelectedDate = selectedDate.format(formatter)
        val capitalizedSelectedDate = capitalizeFirstLetterOfMonth(formattedSelectedDate)

        val generalVisitBindingAD = AlertDialogGeneralVisitBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(generalVisitBindingAD.root)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        with(generalVisitBindingAD) {
            adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                override fun onChanged() {
                    if (adapter.itemCount == 0) {
                        recyclerView.visibility = View.INVISIBLE
                        tvTitle.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        tvTitle.visibility = View.GONE
                    }
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {}

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {}
            })

            adapter.itemCount

            recyclerView.adapter = adapter

            tvDate.text = capitalizedSelectedDate

            btnClose.setOnClickListener {
                alertDialog.dismiss()
            }

            btnNextMonth.setOnClickListener {
                selectedDate = selectedDate.plusMonths(1L)
                selectedMonth = selectedDate.month.getDisplayName(TextStyle.FULL, Locale(RU_LANGUAGE))

                updateAlertDialog(typeClasses)
                tvDate.text = selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
            }

            btnPrevMonth.setOnClickListener {
                selectedDate = selectedDate.minusMonths(1L)
                selectedMonth = selectedDate.month.getDisplayName(TextStyle.FULL, Locale(RU_LANGUAGE))
                updateAlertDialog(typeClasses)
                tvDate.text = selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
            }
        }
        alertDialog.setOnDismissListener {
            viewModel.getAllData(studentId, month)
        }
        alertDialog.show()
        lifecycleScope.launch {
            delay(10L)
            adapter.notifyDataSetChanged()
        }
    }

    private fun updateAlertDialog(typeClasses: Int) {
        when (typeClasses) {
            LECTURE -> viewModel.getLectures(studentId, selectedMonth)
            LABORATORY -> viewModel.getLaboratories(studentId, selectedMonth)
            PRACTICAL -> viewModel.getPracticals(studentId, selectedMonth)
            SEMINAR -> viewModel.getSeminars(studentId, selectedMonth)
        }
    }

    private fun showEditVisitsAD(visitsModel: VisitsModel) {
        val editVisitBindingAD = AlertDialogEditVisitsBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(editVisitBindingAD.root)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        editVisitBindingAD.btnVisit.setOnClickListener {
            when (visitsModel.typeClasses) {
                LECTURE -> {
                    viewModel.updateLecture(
                        LectureModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            visits = true
                        )
                    )
                }

                LABORATORY -> {
                    viewModel.updateLaboratory(
                        LaboratoryModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = true
                        )
                    )
                }

                PRACTICAL -> {
                    viewModel.updatePractical(
                        PracticalModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = true
                        )
                    )
                }

                SEMINAR -> {
                    viewModel.updateSeminar(
                        SeminarModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = true
                        )
                    )
                }
            }
            lifecycleScope.launch {
                delay(200)
                updateAlertDialog(visitsModel.typeClasses)
            }
            alertDialog.dismiss()
        }

        editVisitBindingAD.btnAbsent.setOnClickListener {
            when (visitsModel.typeClasses) {
                LECTURE -> {
                    viewModel.updateLecture(
                        LectureModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            visits = false
                        )
                    )
                }

                LABORATORY -> {
                    viewModel.updateLaboratory(
                        LaboratoryModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = false
                        )
                    )
                }

                PRACTICAL -> {
                    viewModel.updatePractical(
                        PracticalModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = false
                        )
                    )
                }

                SEMINAR -> {
                    viewModel.updateSeminar(
                        SeminarModel(
                            id = visitsModel.id,
                            studentId = visitsModel.studentId,
                            groupId = visitsModel.groupId,
                            date = visitsModel.date,
                            grades = visitsModel.grades,
                            visits = false
                        )
                    )
                }
            }
            lifecycleScope.launch {
                delay(200L)
                updateAlertDialog(visitsModel.typeClasses)
            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    companion object {
        const val RU_LANGUAGE = "ru"
        const val ID_TEXT = "id"
        const val DATE_TEXT = "LLLL yyyy"
    }

    fun capitalizeFirstLetterOfMonth(date: String): String {
        return date.replaceFirstChar { it.uppercase() }
    }
    private fun onlyLectureCase() = with(binding){
        tvStudentSucces.visibility = View.VISIBLE
        generalStatistic.visibility = View.VISIBLE
        points.visibility = View.VISIBLE
        pointCont.visibility = View.VISIBLE
    }
}