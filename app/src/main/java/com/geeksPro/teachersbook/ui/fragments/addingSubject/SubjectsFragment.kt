package com.geeksPro.teachersbook.ui.fragments.addingSubject

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.AlertDialogAddSubjectBinding
import com.geeksPro.teachersbook.databinding.AlertDialogChoosingTypeClassesBinding
import com.geeksPro.teachersbook.databinding.FragmentSubjectsBinding
import com.geeksPro.teachersbook.extentions.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SubjectsFragment :
    BaseFragment<FragmentSubjectsBinding, SubjectsViewModel>(R.layout.fragment_subjects) {

    private lateinit var adapter: SubjectsAdapter
    override val viewModel: SubjectsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSubjectsBinding.inflate(inflater, container, false)

    private var typeClassesAddingSubject: Int = 0

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            val configuration = Configuration(resources.configuration)
            val newValue = 1.2f
            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
            binding.tvTitle.textSize = 19f
        } else {
            binding.tvTitle.textSize = 16f
        }

    }

    override fun initClick() {
        super.initClick()
        with(binding) {
            btnEditSubject.setOnClickListener {
                findNavController().navigate(R.id.action_subjectsFragment_to_editSubjectFragment)
            }
            btnAddSubject.setOnClickListener {
                choiceOfClassesDialog()
            }
            btnSettings.setOnClickListener {
                findNavController().navigate(R.id.action_subjectsFragment_to_settingsFragment)
            }
        }
    }

    override fun initUI() {
        super.initUI()
        adapter = SubjectsAdapter(this::navigateToAddGroup)
        binding.recyclerView.adapter = adapter
        checkRcViewIsEmpty()
    }

    override fun initLiveData() {
        super.initLiveData()
        lifecycleScope.launch {
            viewModel.subjects.collect { subject ->
                adapter.submitList(subject)
            }
        }
    }

    private fun choiceOfClassesDialog() {
        typeClassesAddingSubject = 0
        val chooseBinding =
            AlertDialogChoosingTypeClassesBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)

        with(chooseBinding) {

            val maxCheck = 3
            var countChecked = 0

            clickLecture.setOnClickListener {
                chbLecture.isChecked = !chbLecture.isChecked
            }
            chbLecture.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (maxCheck <= countChecked) {
                        chbLecture.isChecked = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_max_choose_type_lesson), Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        typeClassesAddingSubject += LECTURE
                        countChecked++
                    }
                } else {
                    typeClassesAddingSubject -= LECTURE
                    countChecked--
                }
            }

            clickLaboratory.setOnClickListener {
                chbLobaratory.isChecked = !chbLobaratory.isChecked
            }
            chbLobaratory.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (maxCheck <= countChecked) {
                        chbLobaratory.isChecked = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_max_choose_type_lesson), Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        typeClassesAddingSubject += LABORATORY
                        countChecked++
                    }
                } else {
                    typeClassesAddingSubject -= LABORATORY
                    countChecked--
                }
            }

            clickPratical.setOnClickListener {
                chbPractical.isChecked = !chbPractical.isChecked
            }
            chbPractical.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (maxCheck <= countChecked) {
                        chbPractical.isChecked = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_max_choose_type_lesson), Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        typeClassesAddingSubject += PRACTICAL
                        countChecked++
                    }
                } else {
                    typeClassesAddingSubject -= PRACTICAL
                    countChecked--
                }
            }

            clickSeminar.setOnClickListener {
                chbSeminar.isChecked = !chbSeminar.isChecked
            }
            chbSeminar.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (maxCheck <= countChecked) {
                        chbSeminar.isChecked = false
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_max_choose_type_lesson), Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        typeClassesAddingSubject += SEMINAR
                        countChecked++
                    }
                } else {
                    typeClassesAddingSubject -= SEMINAR
                    countChecked--
                }
            }

            btnCancel.setOnClickListener {
                chooseDialog.dismiss()
            }

            btnContinue.setOnClickListener {
                if (typeClassesAddingSubject > 0) {
                    chooseDialog.dismiss()
                    addNewSubject()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.toast_choose_type_classes), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        chooseDialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun addNewSubject() {
        val chooseBinding =
            AlertDialogAddSubjectBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        chooseBinding.btnExit.setOnClickListener {
            chooseDialog.dismiss()
        }

        editTextListeners(chooseBinding)

        chooseBinding.btnAddSubject.setOnClickListener {
            val newSubject = chooseBinding.etNameSubject.text.toString().trim()
            if (newSubject.isNotEmpty() && !newSubject.contains("\n")) {
                val subjectModel = SubjectModel(
                    nameSubject = newSubject,
                    typeClasses = typeClassesAddingSubject
                )
                viewModel.insert(subjectModel)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.toast_subjes_added), Toast.LENGTH_SHORT
                ).show()
                checkRcViewIsEmpty()
                chooseDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), R.string.tv_if_empty, Toast.LENGTH_SHORT).show()
            }
        }
        chooseDialog.show()
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

    private fun editTextListeners(addSubjectBindingAD: AlertDialogAddSubjectBinding) {
        setupCapitalizationListener(addSubjectBindingAD.etNameSubject)
    }

    private fun checkRcViewIsEmpty() {
        lifecycleScope.launch {
            viewModel.subjects.collect { subjects ->
                if (subjects.isEmpty()) {
                    binding.imgMenuBook.visibility = View.VISIBLE
                    binding.tvEmptyMessege.visibility = View.VISIBLE
                    binding.btnEditSubject.visibility = View.GONE
                } else {
                    binding.imgMenuBook.visibility = View.GONE
                    binding.tvEmptyMessege.visibility = View.GONE
                    binding.btnEditSubject.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToAddGroup(subjectId: SubjectModel) {
        sharedViewModel.subjectId.value = subjectId.id
        val navController = findNavController()
        val bundle = Bundle().apply {
            putLong(SUBJECT_ID, subjectId.id!!)
            putBoolean(SUBJECT_BOOLEAN, false)
        }
        navController.navigate(R.id.action_subjectsFragment_to_addGroupsFragment, bundle)
    }

    companion object {
        const val SUBJECT_ID = "subjectId"
        const val SUBJECT_BOOLEAN = "subjectBoolean"

        const val LECTURE = 1000
        const val LABORATORY = 100
        const val PRACTICAL = 10
        const val SEMINAR = 1
    }
}