package com.geeksPro.teachersbook.ui.fragments.onBoardFragments.ready

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentReadyBinding
import com.geeksPro.teachersbook.ui.fragments.onBoardFragments.customanimations.CustomSharedElementTransitionFotButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ReadyFragment :
    BaseFragment<FragmentReadyBinding, ReadyViewModel>(R.layout.fragment_ready) {

    override val viewModel: ReadyViewModel by viewModels()

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentReadyBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customSharedElementTransition = CustomSharedElementTransitionFotButton()
        sharedElementEnterTransition = customSharedElementTransition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBoardTextColor()
        initAnimation()
        fontSelectionListener()
    }

    private fun onBoardTextColor() {
        val fullText = resources.getString(R.string.tv_about_this_app)
        val spannableString = SpannableString(fullText)

        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.tv_date_color))
        val startIndexColor = 0
        val endIndexColor = 15
        spannableString.setSpan(colorSpan, startIndexColor, endIndexColor, 0)

        val boldSpan = StyleSpan(Typeface.BOLD)
        val startIndexBold = 0
        val endIndexBold = 15
        spannableString.setSpan(boldSpan, startIndexBold, endIndexBold, 0)

        binding.explainAboutApp.text = spannableString
    }

    private fun initAnimation() {
        val slideUpAnimation: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fast_slide_up)
        binding.topImage.startAnimation(slideUpAnimation)

        val slideDownAnimation: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fast_slide_down)
        binding.bottomImage.startAnimation(slideDownAnimation)
    }

    private fun fontSelectionListener() {
        if (Listeners.booleanLiveData.value == true){
            binding.continueButton.textSize = 20f
            binding.explainAboutApp.textSize = 25f
        }else{
            binding.explainAboutApp.textSize = 20f
            binding.continueButton.textSize = 15f
        }
    }

    override fun initClick() {
        setOnBackClickListener()
        setOnContinueClickListener()
    }

    private fun setOnBackClickListener() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnContinueClickListener() {
        binding.continueButton.setOnClickListener {
            val preferenceHelper = PreferenceHelper(requireContext())
            preferenceHelper.unit(requireContext())
            preferenceHelper.saveBoolean = true
            preferenceHelper.saveSize("big")

            if (Listeners.booleanLiveData.value == true){
                val configuration = Configuration(resources.configuration)
                val newValue = 1.2f

                configuration.fontScale = newValue
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }else{
                val configuration = Configuration(resources.configuration)
                val newValue = 1f

                configuration.fontScale = newValue
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }


            binding.spinKit.visibility = View.VISIBLE
            lifecycleScope.launch {
                delay(3000)
                findNavController().navigate(R.id.action_readyFragment_to_mainFragment)
            }

            fadeOutViews(
                binding.linearWithImage,
                binding.bottomImage,
                binding.topImage,
                binding.buttonBack,
                binding.linearWithIndicators
            )
        }
    }

    private fun fadeOutViews(vararg views: View) {
        for (view in views) {
            view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction {
                    view.visibility = View.GONE
                    view.alpha = 1f
                }
        }
    }
}