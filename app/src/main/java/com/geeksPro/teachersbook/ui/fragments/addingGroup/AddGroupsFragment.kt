package com.geeksPro.teachersbook.ui.fragments.addingGroup

import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.local.models.SubjectModel
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.AlertDialogChooseSubjectBinding
import com.geeksPro.teachersbook.databinding.AlertDialogCreateGroupBinding
import com.geeksPro.teachersbook.databinding.FragmentGroupsBinding
import com.geeksPro.teachersbook.extentions.SharedViewModel
import com.geeksPro.teachersbook.ui.fragments.addingStudent.AddStudentFragment.Companion.GROUP_ID_KEY
import com.geeksPro.teachersbook.ui.fragments.addingStudent.AddStudentFragment.Companion.SUBJECT_ID_KEY
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.SUBJECT_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddGroupsFragment :
    BaseFragment<FragmentGroupsBinding, AddGroupsViewModel>(R.layout.fragment_groups) {

    override val viewModel: AddGroupsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var adapter: AddingGroupAdapter
    private val adapterChoose = ChooseSubjectDialogAdapter { selectedSubject ->
        viewModel.selectSubject(selectedSubject)
    }
    private var selectedSubjectId: Long? = null
    private var selectedSubject: SubjectModel? = null
    private val bundle = Bundle()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentGroupsBinding {
        return FragmentGroupsBinding.inflate(inflater, container, false)
    }

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

    override fun initUI() {
        super.initUI()
        adapter = AddingGroupAdapter(this::onClick)
        binding.recyclerView.adapter = adapter
        checkRcViewIsEmpty()
    }

    private fun onClick(model: GroupModel) {
        if (selectedSubject != null) {
            bundle.putLong(SUBJECT_ID_KEY, selectedSubject?.id!!)
            navigateWithBundle(model)
        } else {
            if (selectedSubjectId != null) {
                bundle.putLong(SUBJECT_ID_KEY, selectedSubjectId!!)
                navigateWithBundle(model)
            }
        }
    }

    private fun navigateWithBundle(model: GroupModel) {
        bundle.putLong(GROUP_ID_KEY, model.id!!)
        val navController = findNavController()
        navController.popBackStack()
        navController.navigate(R.id.addStudentFragment, bundle)
    }

    override fun initClick() {
        binding.btnAddGroup.setOnClickListener {
            chooseSubjectDialog()
        }
        binding.btnSearch.setOnClickListener {
            val subjectId = arguments?.getLong("subjectId")
            val bundle = Bundle()
            if (subjectId != null) {
                bundle.putLong("subjectId", subjectId)
            } else {
                sharedViewModel.subjectId.observe(viewLifecycleOwner) {
                    bundle.putLong("subjectId", it)
                }
            }
            findNavController().popBackStack()
            findNavController().navigate(R.id.searchFragment, bundle)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_addGroupsFragment_to_settingsFragment)
        }
        binding.btnEditGroup.setOnClickListener {
            navigateToEditFragment()
        }
    }

    override fun initLiveData() {
        selectedSubjectId = arguments?.getLong(SUBJECT_ID)
        val sbjDialog = arguments?.getBoolean("subjectBoolean") ?: true
        val localSubjectId = selectedSubjectId
        if (localSubjectId != null) {
            viewModel.loadGroupsForSubject(localSubjectId)
        }

        if (sbjDialog) {
            chooseSubjectDialogWhenItStarts()
        }

        viewModel.groupsForSelectedSubject.observe(viewLifecycleOwner) {
            adapter.addGroup(it)
            if (it.isNotEmpty()) {
                with(binding) {
                    btnEditGroup.visibility = View.VISIBLE
                    btnSearch.visibility = View.VISIBLE
                    ivGroup.visibility = View.GONE
                    tvEmptyMessege.visibility = View.GONE
                }
            }
        }
    }

    private fun chooseSubjectDialog() {
        val dialogBinding =
            AlertDialogChooseSubjectBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        dialog.show()

        val allSubjects = viewModel.allSubjects
        lifecycleScope.launch {
            allSubjects.collect { subjects ->
                adapterChoose.addSubjects(subjects)
                delay(60)
                dialogBinding.recyclerView.adapter = adapterChoose
            }
        }

        dialogBinding.btnContinue.setOnClickListener {
            selectedSubject = adapterChoose.getSelectedSubject()
            selectedSubjectId = selectedSubject?.id ?: -1
            if (selectedSubject != null) {
                viewModel.selectSubject(selectedSubject!!)
                viewModel.loadGroupsForSubject(selectedSubjectId!!)
                dialog.dismiss()
                showCreateGroupDialog()
            } else {
                Toast.makeText(requireContext(), R.string.empty_field_error_msg, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun showCreateGroupDialog() {
        val createBinding =
            AlertDialogCreateGroupBinding.inflate(LayoutInflater.from(requireContext()))
        val createDialog = android.app.AlertDialog.Builder(requireContext()).create()
        createDialog.setView(createBinding.root)
        createDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        createDialog.show()

        editTextListeners(createBinding)

        createBinding.btnAddGroup.setOnClickListener {
            val groupName = createBinding.etNameGroup.text.toString().trim()
            if (groupName.isNotEmpty()) {
                selectedSubject?.let { subject ->
                    subject.id?.let { subjectId ->
                        viewModel.addGroupToSubject(groupName)
                        viewModel.loadGroupsForSubject(subjectId)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toasts_group_added), Toast.LENGTH_SHORT
                        ).show()
                        lifecycleScope.launch {
                            delay(200)
                            viewModel.loadGroupsForSubject(subjectId)
                        }
                        createDialog.dismiss()
                    } ?: run {
                        Toast.makeText(
                            requireContext(),
                            R.string.empty_field_error_msg, Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), R.string.tv_if_empty_group, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        createBinding.btnBack.setOnClickListener {
            createDialog.dismiss()
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

    private fun editTextListeners(addCreateGroupBindingAD: AlertDialogCreateGroupBinding) {
        setupCapitalizationListener(addCreateGroupBindingAD.etNameGroup)
    }

    private fun checkRcViewIsEmpty() {
        lifecycleScope.launch {
            viewModel.groups.collect { groups ->
                if (groups.isEmpty()) {
                    binding.tvEmptyMessege.visibility = View.VISIBLE
                    binding.ivGroup.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun chooseSubjectDialogWhenItStarts() {
        val dialogBinding =
            AlertDialogChooseSubjectBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = AlertDialog.Builder(requireContext()).create()
        dialog.setView(dialogBinding.root)
        dialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        dialog.show()
        val allSubjects = viewModel.allSubjects
        lifecycleScope.launch {
            allSubjects.collect { subjects ->
                adapterChoose.addSubjects(subjects)
                dialogBinding.recyclerView.adapter = adapterChoose
            }
        }

        dialogBinding.btnContinue.setOnClickListener {
            selectedSubject = adapterChoose.getSelectedSubject()
            if (selectedSubject != null) {
                viewModel.selectSubject(selectedSubject!!)
                viewModel.loadGroupsForSubject(selectedSubject?.id!!)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), R.string.empty_field_error_msg, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun navigateToEditFragment() {
        if (selectedSubject?.id != null) {
            navigateToEditGroup(selectedSubject?.id!!)
        } else {
            navigateToEditGroup(selectedSubjectId!!)
        }
    }

    private fun navigateToEditGroup(id: Long) {
        val navController = findNavController()
        bundle.putLong(SUBJECT_ID, id)
        navController.navigate(R.id.action_addGroupsFragment_to_editGroupFragment, bundle)
    }
}