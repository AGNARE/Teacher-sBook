package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.general.generalStudentPoints

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.models.LaboratoryModel
import com.geeksPro.teachersbook.data.local.models.PracticalModel
import com.geeksPro.teachersbook.data.local.models.SeminarModel
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentGeneralPointsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class GeneralStudentPointFragment :
    BaseFragment<FragmentGeneralPointsBinding, GeneralPointViewModel>(R.layout.fragment_general_points),
    GeneralStudentPointAdapter.OnEditStatusChangeListener {
    override val viewModel: GeneralPointViewModel by viewModels()
    private val adapter = GeneralStudentPointAdapter()
    private lateinit var preferenceHelper: PreferenceHelper

    private var isEditing = false
    var studentId: Long = 0
    var groupId: Long = 0

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentGeneralPointsBinding {
        return FragmentGeneralPointsBinding.inflate(inflater, container, false)
    }

    override fun initUI() = with(binding) {
        super.initUI()
        preferenceHelper = PreferenceHelper(requireContext())

        studentId = arguments?.getLong(STUDENT_ID) ?: -1
        groupId = arguments?.getLong(GROUP_ID) ?: -1
        recyclerView.adapter = adapter
        adapter.onEditStatusChangeListener = this@GeneralStudentPointFragment
    }

    override fun initLiveData() = with(binding) {
        super.initLiveData()
        studentId = arguments?.getLong(STUDENT_ID) ?: -1
        if (studentId != -1L) {
            viewModel.getGradesAndDates(studentId)
        } else {
            Toast.makeText(requireContext(), INVALID_STUDENT_ID_TEXT, Toast.LENGTH_SHORT).show()
        }

        when (arguments?.getInt("id", -1) ?: -1) {
            1 -> {
                txtGeneralLaboratory.visibility = View.VISIBLE
                viewModel.laboratoryGradesAndDates.observe(viewLifecycleOwner) { laboratory ->
                    if (laboratory.isEmpty()) {
                        showErrorAlertDialog()
                    } else {
                        val dateAndGrades = laboratory.map { laboratoryModel ->
                            DateAndGrade(
                                laboratoryModel.id,
                                laboratoryModel.date,
                                laboratoryModel.grades,
                                laboratoryModel.visits
                            )
                        }

                        adapter.submitList(dateAndGrades)
                    }
                }
            }

            2 -> {
                txtGeneralPractice.visibility = View.VISIBLE
                viewModel.practicalGradesAndDates.observe(viewLifecycleOwner) { practicals ->
                    if (practicals.isEmpty()) {
                        showErrorAlertDialog()
                    } else {
                        val dateAndGrades = practicals.map { practicalModel ->
                            DateAndGrade(
                                practicalModel.id,
                                practicalModel.date,
                                practicalModel.grades,
                                visits = practicalModel.visits
                            )
                        }
                        adapter.submitList(dateAndGrades)
                    }
                }
            }

            3 -> {
                txtGeneralSeminary.visibility = View.VISIBLE
                viewModel.seminarGradesAndDates.observe(viewLifecycleOwner) { seminar ->
                    if (seminar.isEmpty()) {
                        showErrorAlertDialog()
                    } else {
                        val dateAndGrades = seminar.map { seminarModel ->
                            DateAndGrade(
                                seminarModel.id,
                                seminarModel.date,
                                seminarModel.grades,
                                visits = seminarModel.visits
                            )
                        }
                        adapter.submitList(dateAndGrades)
                    }
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initClick() = with(binding) {
        super.initClick()
        btnBack.setOnClickListener {
            Listeners.restartListener.value = true
            findNavController().navigateUp()
        }
        btnEdit.setOnClickListener {
            isEditing = true
            disableBtnBack()
            adapter.notifyDataSetChanged()
        }
        btnReady.setOnClickListener {
            isEditing = false
            ableBtnBack()
            complateEditing()
            adapter.notifyDataSetChanged()
        }
    }

    private fun disableBtnBack() = with(binding) {
        btnBack.isEnabled = false
        btnBack.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.grey)
        btnReady.visibility = View.VISIBLE
        btnEdit.visibility = View.GONE
    }

    private fun ableBtnBack() = with(binding) {
        btnBack.isEnabled = true
        btnBack.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), R.color.blue)
        btnReady.visibility = View.GONE
        btnEdit.visibility = View.VISIBLE
    }

    private fun showErrorAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(R.string.error_title)
            .setMessage(R.string.error_if_point)
            .setPositiveButton(R.string.agree) { _, _ ->
                findNavController().navigate(R.id.addStudentFragment)
            }.setCancelable(false).create().show()
    }

    private fun complateEditing() {
        val updatedData = adapter.getGrades()
        when (arguments?.getInt(ID_TEXT, -1) ?: -1) {
            1 -> {
                val laboratoryModels = updatedData.map { dateAndGrade ->
                    LaboratoryModel(
                        dateAndGrade.id,
                        studentId,
                        groupId,
                        dateAndGrade.date,
                        dateAndGrade.grades,
                        dateAndGrade.visits
                    )
                }
                viewModel.updateLaboratoryGrade(laboratoryModels)
            }

            2 -> {
                val practicalModels = updatedData.map { dateAndGrade ->
                    PracticalModel(
                        dateAndGrade.id,
                        studentId,
                        groupId,
                        dateAndGrade.date,
                        dateAndGrade.grades,
                        dateAndGrade.visits
                    )
                }
                viewModel.updatePracticalGrade(practicalModels)
            }

            3 -> {
                val seminarModels = updatedData.map { dateAndGrade ->
                    SeminarModel(
                        dateAndGrade.id,
                        studentId,
                        groupId,
                        dateAndGrade.date,
                        dateAndGrade.grades,
                        dateAndGrade.visits
                    )
                }
                viewModel.updateSeminarGrade(seminarModels)
            }
        }
    }

    override fun onEditStatusChange(): Boolean {
        return isEditing
    }

    companion object {
        const val STUDENT_ID = "student id"
        const val GROUP_ID = "group id"
        const val INVALID_STUDENT_ID_TEXT = "Invalid student ID"
        const val ID_TEXT = "id"
    }
}