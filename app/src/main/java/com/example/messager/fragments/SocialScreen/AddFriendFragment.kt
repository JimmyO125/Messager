package com.example.messager.fragments.SocialScreen

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.hardware.usb.UsbRequest
import android.os.Bundle
import android.text.Layout
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.example.messager.R
import com.example.messager.classes.User
import com.example.messager.fragments.ChatScreen.ChatFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_add_friend.view.*
import com.google.firebase.database.DataSnapshot





class AddFriendFragment : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_add_friend, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Add Friend"
        view.button_search.setOnClickListener{
                searchFriend()
        }

        return view
    }





    private fun searchFriend() {
        val text = view?.findViewById<TextInputEditText>(R.id.textinputedit_friendEmail)?.text.toString()
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        Log.d("AddFriend Fragment","Current User email : "+currentUserEmail)
        if (text.isEmpty()) {
            Toast.makeText(
                this.activity,
                "Please enter your friend's email!",
                Toast.LENGTH_SHORT
            ).show()
        } else if(currentUserEmail == text){
            Toast.makeText(
                this.activity,
                "You cannot add yourself!",
                Toast.LENGTH_SHORT
            ).show()
        }
        else{
            val usersRef: DatabaseReference = FirebaseDatabase.getInstance(database).getReference("users")
            val friendQuery : Query= usersRef.orderByChild("email").equalTo(text)

            var user : User = User()
            friendQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (friendSnapshot in snapshot.getChildren()) {
                        user.uid = friendSnapshot.key.toString()
                        user.email = friendSnapshot.child("email").getValue<String>(String::class.java).toString()
                        user.username = friendSnapshot.child("username").getValue<String>(String::class.java).toString()
                        user.profileImageUrl = friendSnapshot.child("profileImageUrl").getValue<String>(String::class.java).toString()
                        Log.d("AddFriend Fragment","friend key : "+friendSnapshot.key)
                        Log.d("AddFriend Fragment","friend name : "+friendSnapshot.child("username").getValue<String>(
                            String::class.java))


                        //Passing data
                        val action = AddFriendFragmentDirections.actionAddFriendFragmentToUserProfileFragment(user)
                        findNavController().navigate(action)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    throw error.toException();
                }

            })

        }
    }
}