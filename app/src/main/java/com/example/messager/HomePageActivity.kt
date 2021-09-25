package com.example.messager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePageActivity : AppCompatActivity() {

    /*private val chatFragment = ChatFragment()
    private val settingFragment = SettingFragment()
    private val groupFragment = GroupFragment()*/

    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)


        //replaceFragment(chatFragment)


       /* bottom_navigation_homepage.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.chatFragment-> activeFragment = chatFragment
                R.id.groupFragment -> activeFragment = groupFragment
                R.id.settingFragment -> activeFragment = settingFragment
            }
            true
        }*/


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container_homepage) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_homepage)
        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.isItemHorizontalTranslationEnabled = true

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.chatScreen,R.id.SocialScreen,R.id.settingScreen)
        )
        setupActionBarWithNavController(navController,appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }
}