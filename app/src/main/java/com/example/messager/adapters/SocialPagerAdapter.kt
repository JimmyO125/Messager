package com.example.messager.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.messager.fragments.SocialScreen.FriendFragment
import com.example.messager.fragments.SocialScreen.GroupFragment


class SocialPagerAdapter(fragmentManger : FragmentManager,lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManger,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 ->{
                FriendFragment()
            }
            1->{
                GroupFragment()
            }
            else->{
                FriendFragment()
            }
        }
    }

}
