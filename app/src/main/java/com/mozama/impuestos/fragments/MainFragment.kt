/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */

package com.mozama.impuestos.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.mozama.impuestos.R
import com.mozama.impuestos.adapters.ViewPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Fragment principal para visualizar el ViewPager2 y TabLayout
 */
class MainFragment : Fragment() {

    private lateinit var tabView: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPageAdapter: ViewPageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = activity?.findViewById(R.id.viewPage)!!
        tabView = activity?.findViewById(R.id.tabView)!!

        viewPageAdapter = ViewPageAdapter(this)
        viewPager.adapter = viewPageAdapter

        TabLayoutMediator(tabView, viewPager) { tab, position ->
            when(position){
                0 ->tab.text = resources.getString(R.string.retenciones)
                1 ->tab.text = resources.getString(R.string.iva)
            }
        }.attach()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}