package com.geeksPro.teachersbook.ui.fragments.editGroup

import android.content.res.Configuration
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
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.models.GroupModel
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.AlertDialogRemoveGroupBinding
import com.geeksPro.teachersbook.databinding.AlertDialogRenameGroupBinding
import com.geeksPro.teachersbook.databinding.FragmentEditGroupBinding
import com.geeksPro.teachersbook.ui.fragments.addingSubject.SubjectsFragment.Companion.SUBJECT_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditGroupFragment :
    BaseFragment<FragmentEditGroupBinding, EditGroupViewModel>(R.layout.fragment_edit_group) {
    override val viewModel: EditGroupViewModel by viewModels()
    private var groupModel = listOf<GroupModel>()
    private lateinit var adapter: EditGroupAdapter
    private var subjectId: Long? = null
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentEditGroupBinding {
        return FragmentEditGroupBinding.inflate(inflater, container, false)
    }

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            val configuration = Configuration(resources.configuration)
            val newValue = 1.2f
            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
            binding.title.textSize = 19f
        } else {
            binding.title.textSize = 16f
        }
    }

    override fun initUI() {
        super.initUI()
        adapter = EditGroupAdapter(this::editNameGroup,
            object : EditGroupAdapter.OnItemSelectedListener {
                override fun onItemSelected() {
                    binding.btnRemove.isEnabled = adapter.isAnyItemSelected()
                }
            })
        binding.recyclerView.adapter = adapter
    }

    override fun initLiveData() {
        super.initLiveData()
        subjectId = arguments?.getLong(SUBJECT_ID)
        val localGroupId = subjectId
        if (subjectId != null) {
            viewModel.loadGroupBySelectingSubject(subjectId = localGroupId!!)
        }

        var wasListNonEmpty = false
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.groupsForSelectedSubject.collect { groups ->
                groupModel = groups
                adapter.submitList(groupModel)
                if (wasListNonEmpty && groupModel.isEmpty()) {
                    navigateBack()
                }
                wasListNonEmpty = groupModel.isNotEmpty()
            }
        }
    }

    override fun initClick() {
        super.initClick()
        binding.btnCancelEditMode.setOnClickListener {
            navigateBack()
        }
        binding.btnRemove.setOnClickListener {
            removeModel()
        }
        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }

    private fun editNameGroup(groupModel: GroupModel) {
        val chooseBinding =
            AlertDialogRenameGroupBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        chooseDialog.show()
        chooseBinding.etName.setText(groupModel.nameGroup)
        editTextListeners(chooseBinding)
        chooseBinding.btnRenameGroup.setOnClickListener {
            val newNameGroup = chooseBinding.etName.text.toString().trim()
            if (newNameGroup.isBlank()) {
                Toast.makeText(context, getString(R.string.tv_if_empty), Toast.LENGTH_SHORT).show()
            } else {
                val updateGroup = groupModel.copy(nameGroup = newNameGroup)
                lifecycleScope.launch {
                    delay(200)
                    viewModel.update(updateGroup)
                    viewModel.loadGroupBySelectingSubject(subjectId = subjectId!!)
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

    private fun editTextListeners(addGroupBindingAD: AlertDialogRenameGroupBinding) {
        setupCapitalizationListener(addGroupBindingAD.etName)
    }

    private fun removeModel() {
        val chooseBinding =
            AlertDialogRemoveGroupBinding.inflate(LayoutInflater.from(requireContext()))
        val chooseDialog = AlertDialog.Builder(requireContext()).create()
        chooseDialog.setView(chooseBinding.root)
        chooseDialog.window?.decorView?.setBackgroundResource(R.drawable.rounded_background)
        chooseDialog.show()

        val selectedCount = groupModel.count { it.isSelectedGroup }
        val ending = getCorrectEndingForGroup(selectedCount)
        val resultString = resources.getQuantityString(
            R.plurals.tv_remove_group_title_plural,
            selectedCount,
            selectedCount,
            ending
        )
        chooseBinding.tvRemoveGroup.text = resultString

        chooseBinding.btnRemoveGr.setOnClickListener {
            val selectedGroups = groupModel.filter { it.isSelectedGroup }
            selectedGroups.forEach {
                viewModel.delete(it)
                lifecycleScope.launch {
                    delay(200)
                    viewModel.loadGroupBySelectingSubject(subjectId = subjectId!!)
                }
            }
            chooseDialog.dismiss()
        }
        chooseBinding.btnCancelRemove.setOnClickListener { chooseDialog.dismiss() }
    }

    private fun getCorrectEndingForGroup(count: Int): String {
        return when {
            count % 10 == 1 && count % 100 != 11 -> " "
            count % 10 in 2..4 && count % 100 !in 12..14 -> " "
            else -> " "
        }
    }

    private fun navigateBack() {
        try {
            findNavController().navigate(R.id.action_editGroupFragment_to_addGroupsFragment)
        } catch (_: Exception) {
        }
    }
}
