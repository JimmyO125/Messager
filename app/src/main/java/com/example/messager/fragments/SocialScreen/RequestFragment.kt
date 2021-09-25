package com.example.messager.fragments.SocialScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messager.R
import com.example.messager.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_request.view.*
import kotlinx.android.synthetic.main.request_row.view.*
import kotlinx.coroutines.delay


class RequestFragment : Fragment() {


    companion object{
        val TAG = "RequestFragment"
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"

    }

    val sentAdapter = GroupieAdapter()
    val receivedAdapter = GroupieAdapter()

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_request, container, false)

        val recyclerViewSent = view.recyclerview_sentRequest
        val recyclerViewReceived = view.recyclerview_ReceivedRequest

        recyclerViewSent.adapter = sentAdapter
        recyclerViewReceived.adapter = receivedAdapter

        recyclerViewReceived.addItemDecoration(DividerItemDecoration(this.requireContext(),DividerItemDecoration.VERTICAL))
        recyclerViewSent.addItemDecoration(DividerItemDecoration(this.requireContext(),DividerItemDecoration.VERTICAL))


        sentAdapter.setOnItemClickListener{item,view->
            val row = item as RequestRow
            Log.d(TAG,"Uid : "+row.user.uid)
            Log.d(TAG,"Username: "+row.user.username)
            Log.d(TAG,"Email : "+row.user.email)
            Log.d(TAG,"ProfileImageurl : "+row.user.profileImageUrl)
            sentAdapter.clear()
            receivedAdapter.clear()
            val action = RequestFragmentDirections.actionRequestFragmentToUserProfileFragment(row.user)
            findNavController().navigate(action)

        }
        receivedAdapter.setOnItemClickListener{item,view->
            val row = item as RequestRow
            Log.d(TAG,"Uid : "+row.user.uid)
            Log.d(TAG,"Username: "+row.user.username)
            Log.d(TAG,"Email : "+row.user.email)
            Log.d(TAG,"ProfileImageurl : "+row.user.profileImageUrl)
            sentAdapter.clear()
            receivedAdapter.clear()
            val action = RequestFragmentDirections.actionRequestFragmentToUserProfileFragment(row.user)
            findNavController().navigate(action)

        }
        listenForRequest()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val spinner = view.progressBar
            spinner.visibility = View.GONE
        },1000)


        return view
    }


    override fun onResume() {
        super.onResume()

    }
    private fun listenForRequest(){
        val ref = FirebaseDatabase.getInstance(UserProfileFragment.database).getReference().child("FriendRequests")

        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach{
                    //var friendId = snapshot.value.toString()
                    //friendId = friendId.split('=','{')[1]
                    var friendId = it.key.toString()
                    Log.d(TAG,"FriendID : "+friendId)
                    val usersRef: DatabaseReference = FirebaseDatabase.getInstance(database).getReference("users")
                    val friendQuery : Query = usersRef.orderByChild("uid").equalTo(friendId)
                    var user : User = User()
                    friendQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (friendSnapshot in snapshot.getChildren()) {
                                user.uid = friendId
                                user.email = friendSnapshot.child("email").getValue<String>(String::class.java).toString()
                                user.username = friendSnapshot.child("username").getValue<String>(String::class.java).toString()
                                user.profileImageUrl = friendSnapshot.child("profileImageUrl").getValue<String>(String::class.java).toString()
                            }
                            if(it.child("requestStatus").getValue().toString() == "Sent"){
                                sentAdapter.add(RequestRow(user))
                            }
                            else if(it.child("requestStatus").getValue().toString() == "Received"){
                                receivedAdapter.add(RequestRow(user,))
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            throw error.toException();
                        }

                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    class RequestRow(val user : User): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_username_Request.text = user.username
            if(user.profileImageUrl != "empty"){
                val uri = user.profileImageUrl
                val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_Request)
                Picasso.get().load(uri).into(targetImageView)
            }

        }

        override fun getLayout(): Int {
            return R.layout.request_row
        }
    }




}