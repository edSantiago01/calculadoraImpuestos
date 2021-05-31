package com.example.impuestos

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.impuestos.fragments.IvaFragment
import com.example.impuestos.fragments.RetencionFragment

class ViewPageAdapter (fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> RetencionFragment.newInstance()
            1 -> IvaFragment.newInstance()
            else -> RetencionFragment.newInstance()
        }
    }

}