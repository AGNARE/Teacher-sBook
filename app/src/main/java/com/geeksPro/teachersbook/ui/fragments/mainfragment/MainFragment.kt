package com.geeksPro.teachersbook.ui.fragments.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>(R.layout.fragment_main) {

    override val viewModel: MainViewModel by viewModels()
    private var navController: NavController? = null
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentMainBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferenceHelper = PreferenceHelper(requireContext())
        setUpNav()
    }

    private fun setUpNav() {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.navController

        val navView: BottomNavigationView = binding.bottomNavigation
        navView.setupWithNavController(navHostFragment.navController)
        val menu: Menu = navView.menu

        Listeners.languageLiveData.observe(viewLifecycleOwner) {
            if (preferenceHelper.getLanguage() == "ky" || Listeners.languageLiveData.value == true) {
                val firstMenuItem: MenuItem = menu.findItem(R.id.subjectsFragment)
                firstMenuItem.title = "Дисциплиналар"
                val secondMenuItem: MenuItem = menu.findItem(R.id.addGroupsFragment)
                secondMenuItem.title = "Топтор"
                navView.invalidate()
            } else {
                val firstMenuItem: MenuItem = menu.findItem(R.id.subjectsFragment)
                firstMenuItem.title = "Дисциплины"
                val secondMenuItem: MenuItem = menu.findItem(R.id.addGroupsFragment)
                secondMenuItem.title = "Группы"
                navView.invalidate()
            }
        }

        Listeners.booleanLiveData.observe(viewLifecycleOwner) { isActive ->
            val customTextAppearance = if (preferenceHelper.getSize() == "big" || isActive) {
                R.style.CustomBottomNavigationViewBig
            } else {
                R.style.CustomBottomNavigationViewStandard
            }

            navView.itemTextAppearanceInactive = customTextAppearance
            navView.itemTextAppearanceActive = customTextAppearance
            navView.invalidate()
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment,
                R.id.languageFragment,
                R.id.fontSizesFragment -> {
                    navView.visibility = View.GONE
                }
                else -> {
                    navView.visibility = View.VISIBLE
                }
            }
        }
    }
}
