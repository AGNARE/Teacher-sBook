package com.geeksPro.teachersbook.ui.fragments.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentSettingsBinding

class SettingsFragment :
    BaseFragment<FragmentSettingsBinding, SettingsViewModel>(R.layout.fragment_settings) {
    override val viewModel: SettingsViewModel by viewModels()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSettingsBinding.inflate(inflater, container, false)

    @SuppressLint("QueryPermissionsNeeded")
    override fun initClick() {
        super.initClick()
        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            tvFontSize.setOnClickListener {
                findNavController().navigate(R.id.fontSizesFragment)
            }
            tvLanguage.setOnClickListener {
                findNavController().navigate(R.id.languageFragment)
            }
            geeksName.setOnClickListener {
                val uri = Uri.parse("instagram://user?username=geeks_pro")
                val intent = Intent(Intent.ACTION_VIEW, uri)

                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                } else {
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/geeks_pro/"))
                    startActivity(webIntent)
                }
            }
        }
    }

    override fun sizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            val configuration = Configuration(resources.configuration)
            val newValue = 1.1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
        }
    }
}