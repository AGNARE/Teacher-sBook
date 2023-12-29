package com.geeksPro.teachersbook.ui.fragments.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.databinding.FragmentSearchBinding
import com.geeksPro.teachersbook.extentions.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragmentSearchBinding, SearchViewModel>(R.layout.fragment_search),
    SearchAdapter.OnItemClickListener {
    override val viewModel: SearchViewModel by viewModels()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var adapter: SearchAdapter
    private var typeClassId = 0
    private var groupNameId = ""
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSearchBinding.inflate(inflater, container, false)

    override fun initUI() {
        super.initUI()
        adapter = SearchAdapter(this)
        binding.recyclerView.adapter = adapter
        binding.etSearch.isIconified = false
    }


    override fun initClick() = with(binding) {
        super.initClick()
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.etSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val search = newText ?: ""
                viewModel.findByNameOrSurname(search = search)
                    .observe(viewLifecycleOwner) { students ->
                        if (students.isEmpty()){
                            binding.tvStudentUnmatching.visibility = View.VISIBLE
                        }else{
                            binding.tvStudentUnmatching.visibility = View.GONE
                            adapter.submitList(students)
                        }
                    }
                return false
            }
        })

    }

    private fun navigateToStudentData(
        studentId: Long?,
        studentName: String,
        studentSurname: String,
        groupName: String,
        typeClass: Long,
    ) {
        val bundle = Bundle().apply {
            putLong(STUDENT_ID, studentId!!)
            putString(STUDENT_NAME, studentName)
            putString(STUDENT_SURNAME, studentSurname)
            putString(GROUP_NAME, groupName)
            putInt(SUBJECT_TYPE_CLASSES, typeClass.toInt())
        }
        sharedViewModel.currentTab.value = 0
        findNavController().navigate(R.id.studentReportsCardFragment, bundle)
    }

    override fun onItemClick(
        studentId: Long?,
        studentName: String,
        studentSurname: String,
        groupName: String,
        typeClass: Long,
        groupId: Long?
    ) {
        val subjectId = arguments?.getLong("subjectId")
        viewModel.getGrouopModel(groupId!!).observe(viewLifecycleOwner) { group ->
            groupNameId = group.nameGroup
            viewModel.getSubjectModel(subjectId!!).observe(viewLifecycleOwner) { subject ->
                typeClassId = subject.typeClasses
                navigateToStudentData(
                    studentId,
                    studentName,
                    studentSurname,
                    groupNameId,
                    typeClassId.toLong()
                )
            }
        }
    }

    companion object {
        const val STUDENT_ID = "student id"
        const val STUDENT_NAME = "student name"
        const val STUDENT_SURNAME = "student surname"
        const val GROUP_NAME = "group name"
        const val SUBJECT_TYPE_CLASSES = "subject type classes"
    }
}
