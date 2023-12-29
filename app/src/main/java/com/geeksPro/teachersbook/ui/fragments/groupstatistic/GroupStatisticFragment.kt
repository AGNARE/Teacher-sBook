package com.geeksPro.teachersbook.ui.fragments.groupstatistic

import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentGroupStatisticBinding
import com.geeksPro.teachersbook.extentions.createAndSharePDF
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GroupStatisticFragment :
    BaseFragment<FragmentGroupStatisticBinding, GroupStatisticViewModel>(R.layout.fragment_group_statistic) {
    override val viewModel: GroupStatisticViewModel by viewModels()
    private var groupName = ""
    private var groupId: Long = 0
    private var typeClasses = 0
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentGroupStatisticBinding.inflate(inflater, container, false)

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            Listeners.booleanLiveData.value = true
            val configuration = Configuration(resources.configuration)
            val newValue = 1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    override fun initClick() {
        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.addStudentFragment)
            sizeListener()
        }
        binding.buttonShare.setOnClickListener {
            createAndSharePDF(binding.container, groupName, 4000)
        }
    }

    override fun initUI() {
        super.initUI()
        groupId = arguments?.getLong(GROUP_ID_KEY)!!
        groupName = arguments?.getString(GROUP_NAME_KEY)!!
        typeClasses = arguments?.getInt(SUBJECT_TYPE_CLASSES_KEY)!!

        viewModel.setTypeClasses(typeClasses)

        classesUIGone()

        var lecture = false
        var laboratory = false
        var practical = false
        var seminar = false

        val typeClassRevers = typeClasses.toString().reversed()
        for (i in typeClassRevers.indices) {
            if (i == 0 && typeClassRevers[i] == '1') seminar = true
            if (i == 1 && typeClassRevers[i] == '1') practical = true
            if (i == 2 && typeClassRevers[i] == '1') laboratory = true
            if (i == 3 && typeClassRevers[i] == '1') lecture = true
        }
        if (lecture) onlyLectureVisible()

        if (laboratory) laboratoryVisible()

        if (practical) practicalVisible()

        if (seminar) seminarVisible()

        if (lecture && laboratory) showLectureLaboratory()

        if (lecture && practical) showLecturePractical()

        if (lecture && seminar) showLectureSeminar()
    }

    private fun showLectureSeminar() {
        lectureAndCombine()
        seminarVisible()
    }

    private fun showLecturePractical() {
        lectureAndCombine()
        practicalVisible()
    }


    private fun showLectureLaboratory() {
        lectureAndCombine()
        laboratoryVisible()
    }


    private fun lectureAndCombine() = with(binding) {
        tvMiddlePoint.visibility = View.VISIBLE
        tvAveragePointsInGroup.visibility = View.VISIBLE
        tvStudentStats.visibility = View.VISIBLE
        generalStatistic.visibility = View.VISIBLE
    }

    private fun onlyLectureVisible() = with(binding) {
        tvMiddlePoint.visibility = View.GONE
        tvAveragePointsInGroup.visibility = View.GONE
        tvStudentStats.visibility = View.GONE
        generalStatistic.visibility = View.GONE
    }

    private fun laboratoryVisible() {
        with(binding) {
            laboratoryGradesContainer.visibility = View.VISIBLE
            laboratoryPercentContainer.visibility = View.VISIBLE
        }
    }

    private fun practicalVisible() {
        with(binding) {
            practicalGradesContainer.visibility = View.VISIBLE
            practicalPercentContainer.visibility = View.VISIBLE
        }
    }

    private fun seminarVisible() {
        with(binding) {
            seminarGradesContainer.visibility = View.VISIBLE
            seminarPercentContainer.visibility = View.VISIBLE
        }
    }

    private fun classesUIGone() {
        with(binding) {
            laboratoryGradesContainer.visibility = View.GONE
            laboratoryPercentContainer.visibility = View.GONE

            practicalGradesContainer.visibility = View.GONE
            practicalPercentContainer.visibility = View.GONE

            seminarGradesContainer.visibility = View.GONE
            seminarPercentContainer.visibility = View.GONE
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.countStudents.observe(viewLifecycleOwner) {
            binding.tvCountStudents.text = it.toString()
        }
        viewModel.averageAbsencesPercent.observe(viewLifecycleOwner) {
            binding.tvAbsencesPercent.text = String.format("%.1f", it) + "%"
            binding.progressBarAbsences.progress = it.toInt()
        }
        viewModel.averageVisitsPercent.observe(viewLifecycleOwner) {
            binding.tvVisitsPercent.text = String.format("%.1f", it) + "%"
            binding.progressBarVisits.progress = it.toInt()
        }
        viewModel.laboratoryAverageGrades.observe(viewLifecycleOwner) {
            binding.laboratoryGrades.text = it.toString()
        }
        viewModel.practicalAverageGrades.observe(viewLifecycleOwner) {
            binding.practicalGrades.text = it.toString()
        }
        viewModel.seminarAverageGrades.observe(viewLifecycleOwner) {
            binding.seminarGrades.text = it.toString()
        }
        viewModel.laboratoryAveragePercent.observe(viewLifecycleOwner) {
            binding.tvLaboratoryPercent.text = String.format("%.1f", it) + "%"
        }
        viewModel.practicalAveragePercent.observe(viewLifecycleOwner) {
            binding.tvPracticalPercent.text = String.format("%.1f", it) + "%"
        }
        viewModel.seminarAveragePercent.observe(viewLifecycleOwner) {
            binding.tvSeminarPercent.text = String.format("%.1f", it) + "%"
        }
        viewModel.groupPerformancePercentage.observe(viewLifecycleOwner) {
            binding.tvGroupPerformancePercentage.text = String.format("%.1f", it) + "%"
            binding.progressBarMain.progress = it.toInt()
        }
        viewModel.getAllData(groupId)
    }

    companion object {
        const val GROUP_ID_KEY = "group id key"
        const val GROUP_NAME_KEY = "group name key"
        const val SUBJECT_TYPE_CLASSES_KEY = "subject type classes key"
    }
}