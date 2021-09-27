package com.example.messager.fragments.SettingScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

import androidx.navigation.fragment.findNavController
import com.example.messager.MainActivity
import com.example.messager.R
import com.example.messager.fragments.ChatScreen.ChatFragment
import kotlinx.android.synthetic.main.fragment_setting.view.*


class SettingFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_setting, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Setting"
        view.findViewById<AppCompatButton>(R.id.button_profile_settingFragment).setOnClickListener{
            findNavController().navigate(R.id.action_settingScreen_to_profileFragment)
        }
        /*(view.button_profile_settingFragment.setOnClickListener{
            //replaceFragment(testFragment)


        }*/
        view.button_logout_settingFragment.setOnClickListener{
            val intent = Intent(this.activity,MainActivity::class.java)
            startActivity(intent)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK.or(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        return view
    }



}