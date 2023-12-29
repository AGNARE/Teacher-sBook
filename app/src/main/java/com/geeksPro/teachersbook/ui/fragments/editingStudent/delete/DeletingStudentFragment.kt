package com.geeksPro.teachersbook.ui.fragments.editingStudent.delete

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.databinding.AlertDialogRemoveStudentBinding
import com.geeksPro.teachersbook.databinding.FragmentDeletingStudentBinding
import com.geeksPro.teachersbook.ui.fragments.addingStudent.AddStudentFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeletingStudentFragment :
    BaseFragment<FragmentDeletingStudentBinding, DeletingStudentViewModel>(R.layout.fragment_deleting_student) {

    override val viewModel: DeletingStudentViewModel by viewModels()
    private val adapter = DeletingStudentAdapter()
    private var groupId: Long? = null

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDeletingStudentBinding {
        return FragmentDeletingStudentBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()
        arguments.let { bundle ->
            groupId = bundle?.getLong(AddStudentFragment.GROUP_ID_KEY)
        }
        binding.recyclerView.adapter = adapter
    }

    override fun initClick() {
        super.initClick()
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnDelete.setOnClickListener {
            if (adapter.getDeletingStudentsCount() != 0) {
                dialogRemoveStudent()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_choose_one_student), Toast.LENGTH_SHORT
                ).show()
            }
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_deletingStudentFragment_to_addStudentFragment)
        }
    }

    private fun dialogRemoveStudent() {
        val dialogBinding =
            AlertDialogRemoveStudentBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        val selectedCount = adapter.getDeletingStudentsCount()
        val ending = getCorrectEndingForStudent(selectedCount)
        val resultString = resources.getQuantityString(
            R.plurals.tv_remove_student_title_plural,
            selectedCount,
            selectedCount,
            ending
        )
        dialogBinding.tvRemoveStudent.text = resultString

        dialogBinding.btnCancelRemove.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.btnRemove.setOnClickListener {
            lifecycleScope.launch {
                viewModel.deleteStudents(adapter.getDeletingStudents())
                viewModel.getStudents(groupId!!)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun getCorrectEndingForStudent(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> " "
            count % 10 in 2..4 && count % 100 !in 12..14 -> " "
            else -> " "
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.student.observe(viewLifecycleOwner) { students ->
            if (students.isNotEmpty()) {
                adapter.addStudentList(students)
            } else {
                navigateBack()
            }
        }
        viewModel.getStudents(groupId!!)
    }

    private fun navigateBack() {
        try {
            findNavController().navigate(R.id.action_deletingStudentFragment_to_addStudentFragment)
        } catch (_: Exception) { }
    }
}