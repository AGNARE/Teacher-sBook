package com.geeksPro.teachersbook.ui.fragments.settings.fontSizes

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentFontSizesBinding

class FontSizesFragment :
    BaseFragment<FragmentFontSizesBinding, FountSizesViewModel>(R.layout.fragment_font_sizes) {
    override val viewModel: FountSizesViewModel by viewModels()
    private lateinit var preferenceHelper: PreferenceHelper

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentFontSizesBinding.inflate(inflater, container, false)

    @SuppressLint("QueryPermissionsNeeded")
    override fun initClick() {
        preferenceHelper = PreferenceHelper(requireContext())
        if (preferenceHelper.getSize() == "big" || Listeners.booleanLiveData.value == true) {
            Listeners.booleanLiveData.value = false
            binding.rbLarge.isChecked = true
        } else {
            binding.rbStandard.isChecked = true
        }

        with(binding) {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            rbLarge.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    changeSize("big")
                    Listeners.booleanLiveData.value = true
                    rbStandard.isChecked = false
                }
            }

            rbStandard.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    changeSize("standard")
                    Listeners.booleanLiveData.value = false
                    rbLarge.isChecked = false
                }
            }

            tvLarge.setOnClickListener {
                rbLarge.isChecked = true
                rbStandard.isChecked = false
            }

            tvStandard.setOnClickListener {
                rbLarge.isChecked = false
                rbStandard.isChecked = true
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

    private fun changeSize(size: String) {
        preferenceHelper.saveSize(size)
        updateSize(size)
    }

    private fun updateSize(size: String) {
        if (size == "big") {
            val configuration = Configuration(resources.configuration)
            val newValue = 1.1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)

            binding.tvFontSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

        } else {
            val configuration = Configuration(resources.configuration)
            val newValue = 1f

            configuration.fontScale = newValue
            resources.updateConfiguration(configuration, resources.displayMetrics)
            val textSizeInSp = 16f

            binding.tvFontSize.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp)
        }
    }
}