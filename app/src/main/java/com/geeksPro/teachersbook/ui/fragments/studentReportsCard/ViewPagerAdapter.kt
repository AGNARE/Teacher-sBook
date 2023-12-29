package com.geeksPro.teachersbook.ui.fragments.studentReportsCard

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.studentPerformance.StudentPerformanceFragment
import com.geeksPro.teachersbook.ui.fragments.studentReportsCard.totalPoints.TotalPointsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private var fragmentArguments: Bundle? = null

    fun setArguments(args: Bundle) {
        fragmentArguments = args
    }

    override fun getItemCount(): Int = 2


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = TotalPointsFragment()
                fragment.arguments = fragmentArguments // Передача аргументов в фрагмент
                fragment
            }
            1 -> {
                val fragment = StudentPerformanceFragment()
                fragment.arguments = fragmentArguments // Передача аргументов во второй фрагмент
                fragment
            }
            else -> TotalPointsFragment()
        }
    }
}