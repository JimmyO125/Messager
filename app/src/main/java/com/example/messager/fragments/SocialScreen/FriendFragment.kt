package com.example.messager.fragments.SocialScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.messager.R
import com.example.messager.classes.User
import com.example.messager.fragments.ChatScreen.ChatFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_friend.view.*
import kotlinx.android.synthetic.main.fragment_friend.view.progressBar_friends
import kotlinx.android.synthetic.main.friends_row.view.*
import kotlinx.android.synthetic.main.request_row.view.*

class FriendFragment : Fragment() {

    val friendAdapter = GroupieAdapter()

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Friends"
        val recyclerViewFriends = view.recyclerview_friends

        recyclerViewFriends.adapter = friendAdapter

        recyclerViewFriends.addItemDecoration(
            DividerItemDecoration(this.requireContext(),
                DividerItemDecoration.VERTICAL)
        )

        listenForFriends()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val spinner = view.progressBar_friends
            spinner.visibility = View.GONE
        },1000)


        view.imageButton_addFriend.setOnClickListener{
            friendAdapter.clear()
            findNavController().navigate(R.id.action_friendFragment_to_addFriendFragment)
        }

        view.imageButton_request.setOnClickListener{
            friendAdapter.clear()
            findNavController().navigate(R.id.action_friendFragment_to_requestFragment)
        }

        friendAdapter.setOnItemClickListener{item,view->
            val row = item as FriendRow
            Log.d(RequestFragment.TAG,"Uid : "+row.user.uid)
            Log.d(RequestFragment.TAG,"Username: "+row.user.username)
            Log.d(RequestFragment.TAG,"Email : "+row.user.email)
            Log.d(RequestFragment.TAG,"ProfileImageurl : "+row.user.profileImageUrl)
            friendAdapter.clear()
            val action = FriendFragmentDirections.actionFriendFragmentToUserProfileFragment(row.user)
            findNavController().navigate(action)

        }


        return view
    }
    private fun listenForFriends(){
        val ref = FirebaseDatabase.getInstance(UserProfileFragment.database).getReference().child("FriendRequests")

        ref.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                snapshot.children.forEach{
                    //var friendId = snapshot.value.toString()
                    //friendId = friendId.split('=','{')[1]
                    var friendId = it.key.toString()
                    Log.d(RequestFragment.TAG,"FriendID : "+friendId)
                    val usersRef: DatabaseReference = FirebaseDatabase.getInstance(RequestFragment.database).getReference("users")
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
                            if(it.child("requestStatus").getValue().toString() == "Accepted"){
                                friendAdapter.add(FriendRow(user))
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
    class FriendRow(val user : User): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textView_username_friend.text = user.username
            if(user.profileImageUrl != "empty"){
                val uri = user.profileImageUrl
                val targetImageView = viewHolder.itemView.findViewById<CircleImageView>(R.id.imageView_friend)
                Picasso.get().load(uri).into(targetImageView)
            }

        }

        override fun getLayout(): Int {
            return R.layout.friends_row
        }
    }


}