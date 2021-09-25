package com.example.messager.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.example.messager.HomePageActivity
import com.example.messager.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_login,container,false)

        val login = view.findViewById<AppCompatButton>(R.id.button_login)

        login.setOnClickListener{
            val email = view.findViewById<TextInputEditText>(R.id.edittext_email).text.toString()
            val password = view.findViewById<TextInputEditText>(R.id.edittext_password).text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login Fragment" , "Success to login:${it.result!!.user!!.uid}")

                    val intent = Intent(this.activity,HomePageActivity::class.java)
                    startActivity(intent)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Log.d("Login Fragment","Failed to login : ${it.message}")
                }


        }

        return view
    }


}