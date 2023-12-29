package com.geeksPro.teachersbook.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.appcompat.app.AppCompatDelegate
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferenceHelper: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_TeachersBook)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val window = window
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.white)
        initialize()

    }

    private fun initialize() {
        preferenceHelper = PreferenceHelper(this)
        val locale = Locale(preferenceHelper.getLanguage().toString())
        Locale.setDefault(locale)
        val configuration = Configuration().apply {
            setLocale(locale)
        }
        this.resources.updateConfiguration(
            configuration,
            this.resources.displayMetrics
        )

        if (preferenceHelper.getSize() == "big"){
            Listeners.booleanLiveData.value = true
            val configurationForFont = Configuration(resources.configuration)
            val newValue = 1.1f

            configurationForFont.fontScale = newValue
            this.resources.updateConfiguration(configurationForFont, this.resources.displayMetrics)

        }else{
            val configurationForFont = Configuration(resources.configuration)
            val newValue = 1f

            configurationForFont.fontScale = newValue
            resources.updateConfiguration(configurationForFont, resources.displayMetrics)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController =
            navHostFragment.navController

        preferenceHelper.unit(this)

        val check = preferenceHelper.saveBoolean
        val navGraph =
            navController.navInflater.inflate(R.navigation.onboard_graph)

        if (check == true) {
            navGraph.setStartDestination(R.id.mainFragment)
        } else {
            navGraph.setStartDestination(R.id.languageSelectionFragment)
        }
        navController.graph = navGraph
    }
}