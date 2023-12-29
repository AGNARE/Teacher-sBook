package com.geeksPro.teachersbook.ui.fragments.studentReportsCard

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentStudentReportsCardBinding
import com.geeksPro.teachersbook.extentions.SharedViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StudentReportsCardFragment : BaseFragment<
        FragmentStudentReportsCardBinding, StudentReportsCardViewModel
        >(R.layout.fragment_student_reports_card) {

    override val viewModel: StudentReportsCardViewModel by viewModels()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentStudentReportsCardBinding.inflate(inflater, container, false)

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == TEXT_SIZE || Listeners.booleanLiveData.value == true) {
            val configuration = Configuration(resources.configuration)
            val newValue = 1.1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }

    override fun initUI() {
        super.initUI()
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.viewPager2

        val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)
        val args = Bundle()
        args.putLong(STUDENT_ID, arguments?.getLong(STUDENT_ID)!!)
        args.putString(STUDENT_NAME, arguments?.getString(STUDENT_NAME))
        args.putString(STUDENT_SURNAME, arguments?.getString(STUDENT_SURNAME))
        args.putString(GROUP_NAME, arguments?.getString(GROUP_NAME))
        args.putInt(SUBJECT_TYPE_CLASSES, arguments?.getInt(SUBJECT_TYPE_CLASSES)!!)
        args.putInt(GROUP_ID, arguments?.getInt(GROUP_ID)!!)
        adapter.setArguments(args)

        viewPager2.adapter = adapter

        if (Listeners.restartListener.value == true) {
            sharedViewModel.currentTab.value = 1
            Listeners.restartListener.value = false
        }else{
            sharedViewModel.currentTab.value = 0
        }

        viewPager2.post {
            viewPager2.currentItem = sharedViewModel.currentTab.value ?: 0
        }

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getText(R.string.learning_metrics)
                1 -> tab.text = resources.getText(R.string.general_indicators)
            }
        }.attach()
    }

    companion object {
        const val STUDENT_ID = "student id"
        const val STUDENT_NAME = "student name"
        const val STUDENT_SURNAME = "student surname"
        const val GROUP_NAME = "group name"
        const val GROUP_ID = "group id"
        const val SUBJECT_TYPE_CLASSES = "subject type classes"
        const val TEXT_SIZE = "big"
    }
}