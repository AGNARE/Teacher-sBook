package com.geeksPro.teachersbook.ui.fragments.onboardfragments.languageselection

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentLanguageSelectionBinding
import com.geeksPro.teachersbook.ui.fragments.onBoardFragments.languageselection.LanguageSelectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class LanguageSelectionFragment :
    BaseFragment<FragmentLanguageSelectionBinding, LanguageSelectionViewModel>(R.layout.fragment_language_selection) {

    override val viewModel: LanguageSelectionViewModel by viewModels()
    private lateinit var sharedPreferencesManager: PreferenceHelper
    private var delayExecuted = false
    private var animationPlayed = false
    private var isBigSizeButtonClicked = false
    private var isStandardSizeButtonClicked = false
    private var isAnimationPlayed = false

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentLanguageSelectionBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!delayExecuted) {
            lifecycleScope.launch {
                delay(3000)
                binding.downBlurImage.visibility = View.VISIBLE
                binding.rightBlurImage.visibility = View.VISIBLE
                binding.linearContainerAppName.visibility = View.VISIBLE
                binding.spinKit.visibility = View.GONE
                animInitialize()

                delayExecuted = true
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (delayExecuted) {
            binding.downBlurImage.visibility = View.VISIBLE
            binding.rightBlurImage.visibility = View.VISIBLE
            binding.linearContainerAppName.visibility = View.VISIBLE
            binding.spinKit.visibility = View.GONE
            if (Listeners.booleanLiveData.value == true) {
                binding.btnRussian.textSize = 25f
                binding.btnKyrgyz.textSize = 25f
                binding.continueButton.textSize = 20f
                binding.tvChooseLanguage.textSize = 30f
                binding.chooseLanguage.textSize = 30f
                binding.tvExplainAboutApp.textSize = 45f
            } else {
                binding.btnRussian.textSize = 20f
                binding.btnKyrgyz.textSize = 20f
                binding.continueButton.textSize = 15f
                binding.tvChooseLanguage.textSize = 25f
                binding.chooseLanguage.textSize = 25f
                binding.tvExplainAboutApp.textSize = 40f
            }
        }
        sharedPreferencesManager = PreferenceHelper(requireContext())
    }

    private fun animInitialize() {
        if (!animationPlayed) {
            val translateAnimationForAppName = TranslateAnimation(-600f, 0f, 0f, 0f)
            translateAnimationForAppName.duration = 1000
            translateAnimationForAppName.fillAfter = true
            binding.linearContainerAppName.startAnimation(translateAnimationForAppName)

            val translateAnimationChooseLanguage = TranslateAnimation(-600f, 0f, 0f, 0f)
            translateAnimationChooseLanguage.duration = 1000
            translateAnimationChooseLanguage.fillAfter = true
            binding.linearContainerChooseLanguage.startAnimation(translateAnimationChooseLanguage)

            val slideDownAnimation: Animation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
            binding.downBlurImage.startAnimation(slideDownAnimation)

            val slideUpAnimation: Animation =
                AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
            binding.rightBlurImage.startAnimation(slideUpAnimation)

            animationPlayed = true
        }
    }

    override fun initClick() {
        setOnLanguageListener()
        setOnButtonContinueListener()
    }

    private fun setOnLanguageListener() {
        val solidBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounder_for_button_solid)
        val transparentBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounder_for_transparent_button)

        if (isBigSizeButtonClicked) {
            binding.btnKyrgyz.background = solidBackground
            binding.btnKyrgyz.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            )
            binding.continueButton.visibility = View.VISIBLE

        } else if (isStandardSizeButtonClicked) {
            binding.btnRussian.background = solidBackground
            binding.btnRussian.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            )
            binding.continueButton.visibility = View.VISIBLE

        }
        binding.btnKyrgyz.setOnClickListener {
            if (!isBigSizeButtonClicked) {
                isBigSizeButtonClicked = true
                isStandardSizeButtonClicked = false

                changeLanguage("ky")
                Listeners.languageLiveData.value = true
                animateButtonBackgroundChange(binding.btnKyrgyz, solidBackground)
                animateButtonBackgroundChange(binding.btnRussian, transparentBackground)
                binding.btnKyrgyz.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.white
                    )
                )
                binding.btnRussian.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.blue
                    )
                )
                textChange(binding.tvExplainAboutApp, R.string.tv_teacher_electronic_journal)
                binding.continueButton.text = resources.getString(R.string.btn_continue_text)

                playFadeInAnimation()
            }
        }

        binding.btnRussian.setOnClickListener {
            if (!isStandardSizeButtonClicked) {
                isStandardSizeButtonClicked = true
                isBigSizeButtonClicked = false

                changeLanguage("ru")
                Listeners.languageLiveData.value = false
                animateButtonBackgroundChange(binding.btnRussian, solidBackground)
                animateButtonBackgroundChange(binding.btnKyrgyz, transparentBackground)
                binding.btnRussian.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.white
                    )
                )
                binding.btnKyrgyz.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.blue
                    )
                )

                textChange(binding.tvExplainAboutApp, R.string.tv_teacher_electronic_journal)
                textChange(binding.continueButton, R.string.btn_continue_text)
            }
            playFadeInAnimation()
        }
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

    private fun changeLanguage(language: String) {
        sharedPreferencesManager.saveLanguage(language)
        updateLocale(language)
    }

    private fun textChange(textView: TextView, newTextResId: Int) {
        val newText = textView.context.getString(newTextResId)
        textView.text = newText
    }

    private fun setOnButtonContinueListener() {
        binding.continueButton.setOnClickListener {
            val extras = FragmentNavigatorExtras(
                binding.linerWithButtons to "buttons"
            )
            findNavController().navigate(
                R.id.fontSelectionFragment, null, null, extras
            )
        }
    }

    private fun animateButtonBackgroundChange(
        button: AppCompatButton,
        targetBackground: Drawable?,
    ) {
        val currentBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounder_for_transparent_button)
        val transitionDrawable = TransitionDrawable(arrayOf(currentBackground, targetBackground))

        button.background = transitionDrawable
        transitionDrawable.startTransition(500)
    }

    private fun playFadeInAnimation() {
        if (!isAnimationPlayed) {
            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator()
            fadeIn.duration = 1000

            binding.continueButton.visibility = View.VISIBLE
            binding.continueButton.startAnimation(fadeIn)
            isAnimationPlayed = true
        }
    }
}