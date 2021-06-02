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