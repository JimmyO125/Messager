package com.example.messager.fragments.SocialScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.messager.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_user_profile.*
import kotlinx.android.synthetic.main.fragment_user_profile.view.*
import kotlinx.android.synthetic.main.fragment_profile.*


class UserProfileFragment : Fragment() {


    companion object{
        val TAG = "AddFriend Profile"
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"

    }
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val args : UserProfileFragmentArgs by navArgs<UserProfileFragmentArgs>()
    private var currentState = "init"
    private val cancelRequest = "Cancel Friend Request"
    private val sendRequest = "Send Friend Request"
    private val acceptRequest = "Accept Request"
    private val acceptedRequest = "Accepted Request"
    private val declineRequest = "Decline Request"
    private val declinedRequest = "Declined Request"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        ButtonStatus(view)
        setUpFriendInfo(view)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

        },1000)

        view.button_declineRequest.visibility = View.GONE
        view.button_sendRequest.setOnClickListener{
            if(currentState == sendRequest ){
                sendFriendRequest(view)
            }else if(currentState == cancelRequest){
                cancelFriendRequst()
                ButtonStatus(view)
            }else if(currentState == declineRequest){

            }else if(currentState == declineRequest){

            }else if(currentState == acceptRequest){

            }else if(currentState == acceptRequest){
            }

        }
        return view
    }


    private fun ButtonStatus(view : View){
        val friendId = args.user.uid
        val ref = FirebaseDatabase.getInstance(database).getReference().child("FriendRequests")
        ref.child(uid).addListenerForSingleValueEvent(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(friendId)){
                    val requestStatus = snapshot.child(friendId).child("requestStatus").getValue().toString()
                    Log.d(TAG, "This friend status : $requestStatus")
                    if(requestStatus.equals("Sent")){
                        view.button_sendRequest.setText(cancelRequest)
                        currentState = cancelRequest
                    }else if(requestStatus.equals("Received")){
                        view.button_declineRequest.visibility = View.VISIBLE
                        view.button_sendRequest.visibility = View.GONE
                    }
                }else{
                    view.button_sendRequest.setText(sendRequest)
                    currentState = sendRequest
                    view.button_sendRequest.visibility = View.VISIBLE
                    view.button_declineRequest.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun cancelFriendRequst(){
        val friendId = args.user.uid
        val ref = FirebaseDatabase.getInstance(database).getReference().child("FriendRequests")
        ref.child(uid).child(friendId).removeValue()
        ref.child(friendId).child(uid).removeValue()
        currentState = sendRequest

    }
    private fun sendFriendRequest(view:View){
        val friendId = args.user.uid
        val ref = FirebaseDatabase.getInstance(database).getReference().child("FriendRequests")

        ref.child(uid).child(friendId).child("requestStatus").setValue("Sent")
            .addOnCompleteListener(OnCompleteListener() {
                if(it.isSuccessful){
                    ref.child(friendId).child(uid).child("requestStatus").setValue("Received")
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                currentState = cancelRequest
                                ButtonStatus(view)
                            }
                        }
                }
            })
    }
    private fun setUpFriendInfo(view : View){
        if(args.user.profileImageUrl != "empty"){
            Picasso.get().load(args.user.profileImageUrl).into(view.findViewById<CircleImageView>(R.id.imageView_FriendProfileImage))
        }
        val username = args.user.username
        val email = args.user.email
        val friendUsername = view.findViewById<TextView>(R.id.textview_friendUsername)
        val friendEmail = view.findViewById<TextView>(R.id.textview_friendEmail)
        friendUsername.text = "Username : $username"
        friendEmail.text = "Email : $email"

        Log.d(TAG,"UserID : "+args.user.uid)
        Log.d(TAG,"Username : "+args.user.username)
        Log.d(TAG,"Email: "+args.user.email)
        Log.d(TAG,"ProfileImageUrl : "+args.user.profileImageUrl)
    }

}