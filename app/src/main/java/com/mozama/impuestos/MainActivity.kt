/*
 * This is the source code of Calculadora de Impuestos v. 1.x.x.
 * It is licensed under GNU GPL v. 3 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Edgar Santiago, 2021.
 */
package com.mozama.impuestos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.mozama.impuestos.fragments.MainFragment

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = this.supportFragmentManager.beginTransaction()
        val fragmentMain = MainFragment.newInstance()
        transaction.replace(R.id.container_main, fragmentMain)
        transaction.commit()
    }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
                val inflater: MenuInflater = menuInflater
                inflater.inflate(R.menu.menu, menu)
                return true
        }
}