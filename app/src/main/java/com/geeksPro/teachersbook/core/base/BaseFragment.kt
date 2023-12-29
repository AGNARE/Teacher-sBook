package com.geeksPro.teachersbook.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel>(@LayoutRes idLayout: Int) :
    Fragment(idLayout) {


    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    protected abstract val viewModel: VM

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        sizeListener()
        initLiveData()
        initClick()
    }

    open fun initUI() {}

    open fun sizeListener(){}
    open fun initLiveData() {}

    open fun initClick() {}
}