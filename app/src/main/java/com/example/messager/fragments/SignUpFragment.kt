package com.example.messager.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.messager.HomePageActivity
import com.example.messager.R
import com.example.messager.classes.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class SignUpFragment : Fragment() {

    companion object{
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater!!.inflate(R.layout.fragment_sign_up, container, false)

        val register_button = view.findViewById<AppCompatButton>(R.id.button_register)
        register_button.setOnClickListener{
            RegisterUser()
        }

        return view
    }

    private fun RegisterUser(){
        val email = view?.findViewById<TextInputEditText>(R.id.edittext_email)?.text.toString()
        val password = view?.findViewById<TextInputEditText>(R.id.edittext_password)?.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this.activity,"Please enter text in email / password",Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                //else

                Log.d(
                    "Register Fragment",
                    "Success : create user with uid : ${it.result!!.user!!.uid}"
                )
                saveUserToFirebaseDatabase(email)
            }
            .addOnFailureListener {
                Log.d("Register Fragment", "Failed to create user :${it.message}")
            }

    }

        private fun saveUserToFirebaseDatabase(email: String) {
        val uid = FirebaseAuth.getInstance().uid?:""

        val ref = FirebaseDatabase.getInstance(database).getReference("/users/$uid")

        val username = view?.findViewById<TextInputEditText>(R.id.edittext_username)?.text.toString()
        val user = User(uid,email,username,"https://firebasestorage.googleapis.com/v0/b/completemessager.appspot.com/o/images%2Faec1afb3-664f-4a9c-9158-81fb9200c895?alt=media&token=970acdbe-21a4-463f-a79c-16b760786f3f")

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("Register","Saved user to Firebase Database")

                val intent = Intent(this.activity,HomePageActivity::class.java)
                startActivity(intent)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

                //into another activity
            }
    }


}

