package com.example.messager.fragments.SocialScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.messager.R
import com.example.messager.adapters.SocialPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_social.*


class SocialFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_social, container, false)

        val tabLayout = view.findViewById<TabLayout>(R.id.tablayout_social)
        val viewPager2 = view.findViewById<ViewPager2>(R.id.viewpager2_social)

        val adapter = SocialPagerAdapter(this.requireActivity().supportFragmentManager,lifecycle)

        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab , position->
            when(position){
                0->{
                    tab.text = "Friend"
                }
                1->{
                    tab.text = "Group"
                }
            }
        }.attach()
        return view
    }


}