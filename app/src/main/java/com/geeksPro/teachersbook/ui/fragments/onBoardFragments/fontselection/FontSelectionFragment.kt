package com.geeksPro.teachersbook.ui.fragments.onBoardFragments.fontselection

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.geeksPro.teachersbook.R
import com.geeksPro.teachersbook.core.base.BaseFragment
import com.geeksPro.teachersbook.core.di.Listeners
import com.geeksPro.teachersbook.data.local.pref.PreferenceHelper
import com.geeksPro.teachersbook.databinding.FragmentFontSelectionBinding
import com.geeksPro.teachersbook.ui.fragments.onBoardFragments.customanimations.CustomSharedElementTransition

class FontSelectionFragment :
    BaseFragment<FragmentFontSelectionBinding, FontSelectionViewModel>(R.layout.fragment_font_selection) {

    override val viewModel: FontSelectionViewModel by viewModels()
    private lateinit var preferenceHelper: PreferenceHelper
    private var isBigSizeButtonClicked = false
    private var isStandardSizeButtonClicked = false
    private var isAnimationPlayed = false

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) = FragmentFontSelectionBinding.inflate(layoutInflater, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customSharedElementTransition = CustomSharedElementTransition()
        sharedElementEnterTransition = customSharedElementTransition
        sharedElementReturnTransition = customSharedElementTransition
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAnimation()
    }

    private fun initAnimation() {
        val slideRightAnimation: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_start_for_texview)
        binding.exampleTvSize.startAnimation(slideRightAnimation)

        val slideUpAnimation: Animation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up)
        binding.topBackgroundImage.startAnimation(slideUpAnimation)
        binding.bottomBackgroundImage.startAnimation(slideUpAnimation)
    }

    override fun initClick() {
        setOnBackClickListener()
        setOnButtonSizeListener()
        setOnButtonContinueListener()
    }

    private fun setOnBackClickListener() {
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setOnButtonSizeListener() {
        preferenceHelper = PreferenceHelper(requireContext())
        val solidBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounder_for_button_solid)
        val transparentBackground =
            ContextCompat.getDrawable(requireContext(), R.drawable.rounder_for_transparent_button)

        if (isBigSizeButtonClicked) {
            binding.bigSize.background = solidBackground
            binding.bigSize.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.continueButton.visibility = View.VISIBLE

        } else if (isStandardSizeButtonClicked) {
            binding.standardSize.background = solidBackground
            binding.standardSize.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
            binding.continueButton.visibility = View.VISIBLE
        }

        binding.bigSize.setOnClickListener {
            if (!isBigSizeButtonClicked) {
                isBigSizeButtonClicked = true
                isStandardSizeButtonClicked = false

                Listeners.booleanLiveData.value = true


                animateButtonBackgroundChange(binding.bigSize, solidBackground)
                animateButtonBackgroundChange(binding.standardSize, transparentBackground)
                binding.bigSize.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.standardSize.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                animateTextSizeChange(binding.exampleTvSize, binding.exampleTvSize.textSize + 10f)

            }
            playFadeInAnimation()
        }

        binding.standardSize.setOnClickListener {
            if (!isStandardSizeButtonClicked) {
                isStandardSizeButtonClicked = true
                isBigSizeButtonClicked = false

                Listeners.booleanLiveData.value = false

                preferenceHelper.saveBoolean =false

                animateButtonBackgroundChange(binding.standardSize, solidBackground)
                animateButtonBackgroundChange(binding.bigSize, transparentBackground)
                binding.standardSize.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                )
                binding.bigSize.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                animateTextSizeChange(binding.exampleTvSize, binding.exampleTvSize.textSize - 10f)

            }
            playFadeInAnimation()
        }
    }

    private fun setOnButtonContinueListener() {
        binding.continueButton.setOnClickListener {
            findNavController().navigate(
                R.id.readyFragment
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

    private fun animateTextSizeChange(textView: TextView, targetTextSize: Float) {
        val textSizeFrom = textView.textSize

        val textSizeAnimator = ValueAnimator.ofFloat(textSizeFrom, targetTextSize)
        textSizeAnimator.duration = 500

        textSizeAnimator.addUpdateListener { animator ->
            val animatedValue = animator.animatedValue as Float
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animatedValue)
        }
        textSizeAnimator.start()
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