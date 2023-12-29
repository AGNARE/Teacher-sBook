package com.geeksPro.teachersbook.ui.fragments.settings.language

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
import com.geeksPro.teachersbook.databinding.FragmentLanguageBinding
import java.util.Locale

class LanguageFragment :
    BaseFragment<FragmentLanguageBinding, LanguageViewModel>(R.layout.fragment_language) {
    override val viewModel: LanguageViewModel by viewModels()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentLanguageBinding.inflate(inflater, container, false)

    @SuppressLint("QueryPermissionsNeeded")
    override fun initClick() {
        super.initClick()
        preferenceHelper = PreferenceHelper(requireContext())

        if (preferenceHelper.getLanguage() == "ky"){
            binding.rbKyrgyzLanguage.isChecked = true
            Listeners.languageLiveData.value = true
        }else{
            binding.rbRussianLanguage.isChecked = true
            Listeners.languageLiveData.value = false
        }

        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            rbKyrgyzLanguage.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    changeLanguage("ky")
                    rbRussianLanguage.isChecked = false
                    binding.tvLanguage.text = "Тил"
                    Listeners.languageLiveData.value = true
                }
            }

            rbRussianLanguage.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    changeLanguage("ru")
                    rbKyrgyzLanguage.isChecked = false
                    binding.tvLanguage.text = "Язык"
                    Listeners.languageLiveData.value = false
                }
            }

            tvKyrgyzLanguage.setOnClickListener {
                rbKyrgyzLanguage.isChecked = true
                rbRussianLanguage.isChecked = false
            }

            tvRussianLanguage.setOnClickListener {
                rbKyrgyzLanguage.isChecked = false
                rbRussianLanguage.isChecked = true
            }
            geeksName.setOnClickListener {
                val url = "https://www.instagram.com/geeks_pro/"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }


    private fun changeLanguage(language: String) {
        preferenceHelper.saveLanguage(language)
        updateLocale(language)
    }

    private fun updateLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration().apply {
            setLocale(locale)
        }

        requireContext().resources.updateConfiguration(
            configuration, requireContext().resources.displayMetrics
        )
    }
}