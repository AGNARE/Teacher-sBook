package com.geeksPro.teachersbook.extentions

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.geeksPro.teachersbook.R

fun Fragment.mainNavController() = requireActivity().findNavController(R.id.nav_host_fragment_main)

