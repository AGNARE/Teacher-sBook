package com.geeksPro.teachersbook.ui.fragments.editingStudent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.data.local.models.StudentModel
import com.geeksPro.teachersbook.databinding.AlertDialogRenameStudentBinding
import com.geeksPro.teachersbook.databinding.FragmentEditingStudentBinding
import com.geeksPro.teachersbook.ui.fragments.addingStudent.AddStudentFragment.Companion.GROUP_ID_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditingStudentFragment :
    BaseFragment<FragmentEditingStudentBinding, EditingStudentViewModel>(R.layout.fragment_editing_student) {
    override val viewModel: EditingStudentViewModel by viewModels()

    private val adapter = EditingStudentAdapter(this::onClick)
    private var groupId: Long? = null

    private fun onClick(model: StudentModel) {
        val dialogBinding =
            AlertDialogRenameStudentBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        dialogBinding.etName.setText(model.name)
        dialogBinding.etSurname.setText(model.surname)

        editTextListeners(dialogBinding)

        dialogBinding.btnEditStudent.setOnClickListener {
            if (dialogBinding.etName.text.toString().trim().isNotEmpty() &&
                dialogBinding.etName.text.toString().trim().isNotEmpty()
            ) {
                val updatedStudent = model.copy(
                    name = dialogBinding.etName.text.toString().trim(),
                    surname = dialogBinding.etSurname.text.toString().trim()
                )
                viewModel.updateStudent(updatedStudent)
                lifecycleScope.launch {
                    delay(50)
                    viewModel.getStudents(groupId!!)
                }
                dialog.dismiss()
            } else Toast.makeText(
                requireContext(),
                getString(R.string.tv_if_empty),
                Toast.LENGTH_SHORT
            ).show()
        }


        dialogBinding.btnBack.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupCapitalizationListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!charSequence.isNullOrEmpty() && Character.isLowerCase(charSequence[0])) {
                    val capitalizedText =
                        charSequence.toString().replaceFirstChar { it.uppercase() }
                    editText.removeTextChangedListener(this)
                    editText.text.replace(
                        0,
                        editText.length(),
                        capitalizedText,
                        0,
                        capitalizedText.length
                    )
                    editText.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun editTextListeners(addGroupBindingAD: AlertDialogRenameStudentBinding) {
        setupCapitalizationListener(addGroupBindingAD.etName)
        setupCapitalizationListener(addGroupBindingAD.etSurname)
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditingStudentBinding {
        return FragmentEditingStudentBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()
        arguments.let { bundle ->
            groupId = bundle?.getLong(GROUP_ID_KEY)
        }
        binding.recyclerView.adapter = adapter
    }

    override fun initClick() {
        super.initClick()
        binding.btnDelete.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(GROUP_ID_KEY, groupId!!)
            findNavController().navigate(
                R.id.action_editingStudentFragment_to_deletingStudentFragment,
                bundle
            )
        }
        binding.btnCheckMark.setOnClickListener {
            findNavController().navigate(R.id.action_editingStudentFragment_to_addStudentFragment)
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_editingStudentFragment_to_addStudentFragment)
        }
    }

    override fun initLiveData() {
        super.initLiveData()
        viewModel.student.observe(viewLifecycleOwner) { students ->
            adapter.addStudentList(students)
        }
        viewModel.getStudents(groupId!!)
    }
}