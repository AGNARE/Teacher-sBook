package com.geeksPro.teachersbook.ui.fragments.studentReportsCard.totalPoints

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.data.local.models.StudentScoreModel
import com.geeksPro.teachersbook.databinding.FragmentTotalPointsBinding
import com.geeksPro.teachersbook.extentions.createAndSharePDF
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment.Companion.GROUP_NAME
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment.Companion.STUDENT_ID
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment.Companion.STUDENT_NAME
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.StudentReportsCardFragment.Companion.STUDENT_SURNAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TotalPointsFragment :
    BaseFragment<FragmentTotalPointsBinding, TotalPointsViewModel>(R.layout.fragment_total_points) {

    override val viewModel: TotalPointsViewModel by viewModels()
    private var studentId: Long? = null
    private var studentName: String = ""
    private var studentSurName: String = ""
    private var groupName: String = ""

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTotalPointsBinding {
        return FragmentTotalPointsBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()

        editTextWatcher()
        gettingArguments()
        val nameAndSurName = "$studentName $studentSurName"
        binding.tvFcsStudent.text = nameAndSurName
        binding.tvNameGroup.text = groupName
    }

    private fun editTextWatcher() {
        addMaxValueTextWatcher(
            binding.etPointModuleFirst, 30,
            getString(R.string.toast_max_points_30)
        )
        addMaxValueTextWatcher(
            binding.etPointModuleSecond,
            30,
            getString(R.string.toast_max_points_30)
        )
        addMaxValueTextWatcher(
            binding.etFinalControlPoint,
            40,
            getString(R.string.toast_max_ponts_40)
        )
        addMaxValueTextWatcher(
            binding.etAdditionallyPoint, 20,
            getString(R.string.toast_max_points_20)
        )
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.getScores(studentId = studentId!!).observe(viewLifecycleOwner) { scores ->
            binding.etPointModuleFirst.setText(scores?.pointModuleFirst).toString()
            binding.etPointModuleSecond.setText(scores?.pointModuleSecond).toString()
            binding.etFinalControlPoint.setText(scores?.finalControlPoint).toString()
            binding.etAdditionallyPoint.setText(scores?.additionallyPoint).toString()
            binding.etTotalScorePoint.setText(scores?.totalScorePoint).toString()
            scores?.estimationPoint?.toIntOrNull()?.let { estimationPoint ->
                makeStudentScore(estimationPoint)
            }
            viewModel.estimationPoint.observe(viewLifecycleOwner) {
                binding.tvEstimationPoint.text = it.toString()
                binding.etTotalScorePoint.text = it.toString()
                makeStudentScore(it.toIntOrNull() ?: 0)
            }
        }
    }

    private fun makeStudentScore(estimationPoint: Int) = with(binding) {
        when {
            estimationPoint <= 60 -> {
                tvEstimationPoint.text = getString(R.string.show_general_unsatisfied)
            }

            estimationPoint in 61..73 -> {
                tvEstimationPoint.text = getString(R.string.show_general_satisfactory)
            }

            estimationPoint in 74..86 -> {
                tvEstimationPoint.text = getString(R.string.show_general_good)
            }

            estimationPoint in 87..100 -> {
                tvEstimationPoint.text = getString(R.string.show_general_very_good)
            }

            else -> {
                tvEstimationPoint.text = getString(R.string.show_general_very_good)
            }
        }
    }

    override fun initClick() {
        super.initClick()
        binding.btnEdit.setOnClickListener {
            unableET()
        }
        binding.btnComplate.setOnClickListener {
            saveStudentScore()
            disableET()
        }
        binding.btnShare.setOnClickListener {
            createAndSharePDF(binding.container, "${studentName}_${studentSurName}",2800)
        }
    }

    private fun unableET() {
        with(binding) {
            btnComplate.visibility = View.VISIBLE
            btnEdit.visibility = View.GONE
            btnShare.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.grey)
            etPointModuleFirst.isEnabled = true
            etPointModuleSecond.isEnabled = true
            etFinalControlPoint.isEnabled = true
            etAdditionallyPoint.isEnabled = true
            etTotalScorePoint.isEnabled = true
        }
    }

    private fun gettingArguments() {
        studentId = arguments?.getLong(STUDENT_ID)
        studentName = arguments?.getString(STUDENT_NAME)!!
        studentSurName = arguments?.getString(STUDENT_SURNAME)!!
        groupName = arguments?.getString(GROUP_NAME)!!
    }

    private fun disableET() {
        with(binding) {
            btnComplate.visibility = View.GONE
            btnEdit.visibility = View.VISIBLE
            btnShare.backgroundTintList =
                ContextCompat.getColorStateList(requireContext(), R.color.blue)
            etPointModuleFirst.isEnabled = false
            etPointModuleSecond.isEnabled = false
            etFinalControlPoint.isEnabled = false
            etAdditionallyPoint.isEnabled = false
            etTotalScorePoint.isEnabled = false
        }
    }

    private fun saveStudentScore() = with(binding) {
        val pointModuleFirst = etPointModuleFirst.text.toString().toIntOrNull() ?: 0
        val pointModuleSecond = etPointModuleSecond.text.toString().toIntOrNull() ?: 0
        val finalControlPoint = etFinalControlPoint.text.toString().toIntOrNull() ?: 0
        val additionallyPoint = etAdditionallyPoint.text.toString().toIntOrNull() ?: 0

        val estimationPoint =
            pointModuleFirst + pointModuleSecond + finalControlPoint + additionallyPoint

        viewModel.saveScores(
            StudentScoreModel(
                id = studentId!!,
                binding.etPointModuleFirst.text.toString(),
                binding.etPointModuleSecond.text.toString(),
                binding.etFinalControlPoint.text.toString(),
                binding.etAdditionallyPoint.text.toString(),
                totalScorePoint = estimationPoint.toString(),
                estimationPoint = estimationPoint.toString()
            )
        )
        viewModel.updateEstimationPoint(estimationPoint.toString())
    }

    private fun addMaxValueTextWatcher(editText: EditText, maxValue: Int, errorMessage: String) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val input = s.toString().toIntOrNull()
                if (input != null && input > maxValue) {
                    s.clear()
                    Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }
}