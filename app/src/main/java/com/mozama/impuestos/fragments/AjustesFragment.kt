package com.mozama.impuestos.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SwitchCompat
import com.mozama.impuestos.R

/**
* AjustesFragment, View and register the preferences of users
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2021  Edgar Santiago
 */

class AjustesFragment : Fragment() {
    private var configLocales: Int = 0
    private lateinit var switchLocales: SwitchCompat
    private lateinit var configKeyLocales: String
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        configKeyLocales = resources.getString(R.string.imp_config)
        configLocales = sharedPref?.getInt(configKeyLocales, 0)!!

        activity?.setTitle(R.string.ajustes)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                regresarFragment()
            }
        })
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchLocales = view.findViewById(R.id.switchLocal)
        if(configLocales != 0) switchLocales.isChecked = true
        switchLocales.setOnClickListener {
            saveConfigLocal(switchLocales.isChecked)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_ajustes, menu)
    }

    fun saveConfigLocal(valor: Boolean){
        val editSharePreferences = sharedPref?.edit()
        val valorLocal = if(valor) 1 else 0
        editSharePreferences?.putInt(configKeyLocales, valorLocal)
        editSharePreferences?.apply()
    }


    fun regresarFragment(){
        val tagActual = resources.getString(R.string.ajustes)
        val tagPrincipal = resources.getString(R.string.principal)

        val principalFragment = parentFragmentManager.findFragmentByTag(tagPrincipal)
        val ajustesFragment = parentFragmentManager.findFragmentByTag(tagActual)

        if (principalFragment != null && ajustesFragment != null){
            val transaction = parentFragmentManager.beginTransaction()
            transaction.remove(ajustesFragment).show(principalFragment)
            transaction.commit()
            activity?.setTitle(R.string.app_name)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AjustesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}