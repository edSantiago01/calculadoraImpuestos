package com.example.impuestos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.impuestos.fragments.MainFragment
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val transaction = this.supportFragmentManager.beginTransaction()
        val fragmentMain = MainFragment.newInstance()
        transaction.replace(R.id.container_main, fragmentMain)
        transaction.commit()
    }


}