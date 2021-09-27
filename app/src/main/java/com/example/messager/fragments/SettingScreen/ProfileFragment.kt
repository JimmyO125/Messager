package com.example.messager.fragments.SettingScreen

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.messager.R
import com.example.messager.classes.User
import com.example.messager.fragments.ChatScreen.ChatFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*


class ProfileFragment : Fragment() {


    private var username : String ?= null
    private var email : String ?= null
    private var profileImageUrl : String ?= null

    var selectedPhotoUri : Uri? = null

    companion object {
        val TAG = "Profile Fragment"
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = ChatFragment.currentUser.username
        getUserInfo()


        view.button_updatePhoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }

        return view
    }

    private fun setUpUserInfo(){
        textview_username.text = "Username : $username"
        textview_email.text = "Email : $email"

        if(profileImageUrl != "empty"){
            Picasso.get().load(profileImageUrl).into(imageView_ProfileImage)
        }


    }
    private fun getUserInfo(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance(database).getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentUser = snapshot.getValue(User::class.java)
                username = currentUser?.username
                email = currentUser?.email
                profileImageUrl = currentUser?.profileImageUrl

                Log.d(TAG,"username : "+username)
                Log.d(TAG,"email : "+email)
                Log.d(TAG,"Uid : "+FirebaseAuth.getInstance().currentUser!!.uid)
                setUpUserInfo()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun uploadImageToFirebaseStorage(){
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)

            .addOnSuccessListener {

                Log.d("Profile","Successfully uploaded image : ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("Profile","Successfully upload image : $it")

                    val uid = FirebaseAuth.getInstance().uid
                    val ref = FirebaseDatabase.getInstance(database).getReference("/users/$uid")
                    ref.child("profileImageUrl").setValue(it.toString())

                }
            }
            .addOnFailureListener{
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode ==0 && resultCode == Activity.RESULT_OK && data != null){
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(this.activity?.contentResolver,selectedPhotoUri)

            val circleimageview = view?.findViewById<CircleImageView>(R.id.imageView_ProfileImage)
            //val bitmapDrawable = BitmapDrawable(bitmap)

            circleimageview?.setImageBitmap(bitmap)
            uploadImageToFirebaseStorage()
        }
    }

}