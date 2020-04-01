package com.juansandoval.mychatapp.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.juansandoval.mychatapp.fragments.ChatsFragment
import com.juansandoval.mychatapp.fragments.UsersFragment

class SectionPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return UsersFragment()
            1 -> return ChatsFragment()
        }
        return null!!
    }
}