package com.example.messager.fragments.ChatScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.example.messager.R
import com.example.messager.classes.ChatMessage
import com.example.messager.classes.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.fragment_chat_log.view.*


class ChatLogFragment : Fragment() {

    companion object{
        val TAG = "ChatLogFragment"
        val database = "https://completemessager-default-rtdb.asia-southeast1.firebasedatabase.app"
    }

    val chatLogAdapter = GroupieAdapter()
    private var toUser : User?= null
    private val args : ChatLogFragmentArgs by navArgs<ChatLogFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_log, container, false)

        val recyclerview_chatlog = view.recyclerview_chatlog
        recyclerview_chatlog.adapter = chatLogAdapter

        (activity as AppCompatActivity).supportActionBar?.title = args.user.username
        toUser = args.user
        listenForMessage(view)
        val send_button = view.button_send_chatlog
        send_button.setOnClickListener{
            Log.d(TAG,"Attempt to send message....")
            performSendMessage(view)
        }


        return view
    }


    private fun listenForMessage(view:View){
        val fromId = FirebaseAuth.getInstance().uid
        val toId = args.user.uid
        val ref = FirebaseDatabase.getInstance(database).getReference("/userMessages/$fromId/$toId")

        //notify every event on /messages
        ref.addChildEventListener(object  : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text.toString())

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser = ChatFragment.currentUser
                        chatLogAdapter.add(ChatFromItem(chatMessage.text,currentUser!!))
                    }else{
                        chatLogAdapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }
                view.recyclerview_chatlog.scrollToPosition(chatLogAdapter.itemCount-1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage(view: View){
        //val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val user   = args.user

        val fromId = FirebaseAuth.getInstance().uid
        val toId = user!!.uid
        val text = view.editText_chatlog.text.toString()

        if(fromId == null) return

        val reference = FirebaseDatabase.getInstance(database).getReference("/userMessages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance(database).getReference("/userMessages/$toId/$fromId").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId!!,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG,"Saved our chat message: ${reference.key}")
                val edittext_chatlog = view.editText_chatlog
                edittext_chatlog.text?.clear()

                val recyclerView_chatlog = view.recyclerview_chatlog
                recyclerView_chatlog.scrollToPosition(chatLogAdapter.itemCount-1)
            }

        toReference.setValue(chatMessage)


        val latestMessageRef = FirebaseDatabase.getInstance(database).getReference(
            "/latestMessages/$fromId/$toId"
        )
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance(database).getReference(
            "/latestMessages/$toId/$fromId"
        )
        latestMessageToRef.setValue(chatMessage)
    }

}


class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(p0: GroupieViewHolder, p1: Int) {
        p0.itemView.findViewById<TextView>(R.id.textView_chatfromRow).text = text.toString()
        val uri = user.profileImageUrl
        val targetImageView = p0.itemView.imageView_chatFromRow
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}


class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>(){
    override fun bind(p0: GroupieViewHolder, p1: Int) {
        p0.itemView.findViewById<TextView>(R.id.textView_chatToRow).text = text.toString()

        //load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = p0.itemView.imageView_chatToRow
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}