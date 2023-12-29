package com.geeksPro.teachersbook.ui.fragments.editSubject

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
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
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.databinding.AlertDialogRemoveSubjectBinding
import com.geeksPro.teachersbook.databinding.AlertDialogRenameSubjectBinding
import com.geeksPro.teachersbook.databinding.FragmentEditSubjectBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditSubjectFragment :
    BaseFragment<FragmentEditSubjectBinding, SubjectEditViewModel>(R.layout.fragment_edit_subject) {

    private var subjectModels = listOf<SubjectModel>()
    override val viewModel: SubjectEditViewModel by viewModels()
    private lateinit var adapter: EditSubjectAdapter
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditSubjectBinding {
        return FragmentEditSubjectBinding.inflate(inflater, container, false)
    }
    override fun initUI() {
        super.initUI()
        adapter = EditSubjectAdapter(this::editNameSubject,
            object : EditSubjectAdapter.OnItemSelectedListener {
                override fun onItemSelected() {
                    binding.btnRemove.isEnabled = adapter.isAnyItemSelected()
                }
            })
        binding.recyclerView.adapter = adapter
    }

    override fun initLiveData() {
        super.initLiveData()
        lifecycleScope.launch {
            viewModel.subjects.collect {
                subjectModels = it
                adapter.submitList(it)
                binding.btnRemove.isEnabled = adapter.isAnyItemSelected()
                if (it.isEmpty()) {
                    try {
                        navigateBack()
                    } catch (_: Exception) {
                    }
                }
            }
        }
    }

    override fun initClick() {
        super.initClick()
        with(binding) {
            btnCancelEditMode.setOnClickListener {
                navigateBack()
            }
            btnRemove.setOnClickListener {
                removeModel()
            }
            btnBack.setOnClickListener {
                navigateBack()
            }
        }
    }

    private fun editNameSubject(subjectModel: SubjectModel) {
        val chooseBinding =
            AlertDialogRenameSubjectBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        chooseDialog.show()
        chooseBinding.etNewName.setText(subjectModel.nameSubject)
        editTextListeners(chooseBinding)
        chooseBinding.btnSelectedRename.setOnClickListener {
            val newNameSubject = chooseBinding.etNewName.text.toString().trim()
            if (newNameSubject.isBlank()) {
                Toast.makeText(context, getString(R.string.tv_if_empty), Toast.LENGTH_SHORT).show()
            } else {
                val updateSubject = subjectModel.copy(nameSubject = newNameSubject)
                lifecycleScope.launch {
                    delay(200)
                    viewModel.update(updateSubject)
                    chooseDialog.dismiss()
                }
            }
        }
        chooseBinding.btnBack.setOnClickListener {
            chooseDialog.dismiss()
        }
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

    private fun editTextListeners(addGroupBindingAD: AlertDialogRenameSubjectBinding) {
        setupCapitalizationListener(addGroupBindingAD.etNewName)
    }

    private fun navigateBack() {
        try {
            findNavController().navigate(R.id.action_editSubjectFragment_to_subjectsFragment)
        } catch (_: Exception) {
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun removeModel() {
        val chooseBinding =
            AlertDialogRemoveSubjectBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        chooseDialog.show()

        val selectedCount = subjectModels.count { it.isSelected }
        val ending = getCorrectEndingForSubject(selectedCount)
        val resultString = resources.getQuantityString(
            R.plurals.tv_remove_subject_title_plural,
            selectedCount,
            selectedCount,
            ending
        )
        chooseBinding.tvRemoveSubject.text = resultString

        chooseBinding.btnRemoveSubject.setOnClickListener {
            val selectedSubjects = subjectModels.filter { it.isSelected }
            selectedSubjects.forEach { viewModel.delete(it) }
            chooseDialog.dismiss()
        }
        chooseBinding.btnCancelRemove.setOnClickListener {
            chooseDialog.dismiss()
        }
    }

    private fun getCorrectEndingForSubject(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> " "
            count % 10 in 2..4 && count % 100 !in 12..14 -> " "
            else -> " "
        }
    }
}