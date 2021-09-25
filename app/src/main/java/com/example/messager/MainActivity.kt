package com.example.messager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.messager.adapters.ViewPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tablayout_main)
        val viewPager2 = findViewById<ViewPager2>(R.id.viewpager2_main)

        val adapter = ViewPagerAdapter(supportFragmentManager,lifecycle)

        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager2){tab , position->
            when(position){
                0->{
                    tab.text = "Login"
                }
                1->{
                    tab.text = "Register"
                }
            }
        }.attach()
    }

}