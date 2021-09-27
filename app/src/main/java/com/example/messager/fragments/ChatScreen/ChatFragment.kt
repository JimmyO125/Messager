package com.example.messager.fragments.ChatScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.messager.R
import com.example.messager.classes.ChatMessage
import com.example.messager.classes.User
import com.example.messager.fragments.SettingScreen.ProfileFragment
import com.example.messager.fragments.SocialScreen.UserProfileFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.latest_message_row.view.*


class ChatFragment : Fragment() {

    companion object{
        var currentUser : User = User()
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"
        val TAG = "ChatFragment"
    }

    private val latestAdapter = GroupieAdapter()
    val latestMessagesMap = HashMap<String, ChatMessage>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = currentUser.username
        UserInfo()

        val recyclerView_latest_message = view.recyclerview_latest_message
        recyclerView_latest_message.adapter = latestAdapter
        recyclerView_latest_message.addItemDecoration(DividerItemDecoration(this.requireContext(), DividerItemDecoration.VERTICAL))


        latestAdapter.setOnItemClickListener{item,view->
            val row = item as LatestMessageRow
            val action = ChatFragmentDirections.actionChatScreenToChatLogFragment2(row.chatPartnerUser)
            findNavController().navigate(action)
        }
        listenForLatestMessages()

        return view
    }

    private fun UserInfo(){
        val uid = FirebaseAuth.getInstance().uid
        currentUser.uid = uid.toString()
        val ref = FirebaseDatabase.getInstance(database).getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object  : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val thisUser = snapshot.getValue(User::class.java)
                currentUser.username = thisUser?.username.toString()
                currentUser.email = thisUser?.email.toString()
                currentUser.profileImageUrl = thisUser?.profileImageUrl.toString()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun listenForLatestMessages() {
        val fromId = currentUser.uid
        Log.d(TAG,"From Id : "+fromId)
        val ref = FirebaseDatabase.getInstance(database).getReference("/latestMessages/$fromId")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return

                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return
                latestMessagesMap[snapshot.key!!] = chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun refreshRecyclerViewMessage(){
        latestAdapter.clear()
        latestMessagesMap.values.forEach{
            Log.d(TAG,"Latest Message : "+it.text)
            latestAdapter.add(LatestMessageRow(it))
        }
    }

    class LatestMessageRow(val chatMessage: ChatMessage) : Item<GroupieViewHolder>(){
        var chatPartnerUser : User = User()
        override fun bind(p0: GroupieViewHolder, p1: Int) {
            p0.itemView.textView_message_latest_message.text = chatMessage.text

            val chatPartnerID : String
            if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                chatPartnerID = chatMessage.toId
            }else{
                chatPartnerID = chatMessage.fromId
            }

            val ref = FirebaseDatabase.getInstance(database).getReference("/users/$chatPartnerID")
            ref.addListenerForSingleValueEvent( object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatPartnerUser = snapshot.getValue(User::class.java)!!
                    p0.itemView.textView_username_latest_message.text =chatPartnerUser.username

                    val targetImageView = p0.itemView.imageView_latest_message
                    Picasso.get().load(chatPartnerUser.profileImageUrl).into(targetImageView)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }

        override fun getLayout(): Int {
            return R.layout.latest_message_row
        }

    }




}