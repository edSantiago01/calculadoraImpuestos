package com.mozama.impuestos.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.mozama.impuestos.R
import com.mozama.impuestos.adapters.ViewPageAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 * Use the [MainFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainFragment : Fragment() {

    lateinit var tabView: TabLayout
    lateinit var viewPager: ViewPager2

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

        val viewPageAdapter = ViewPageAdapter(this)
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