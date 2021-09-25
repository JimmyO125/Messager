package com.example.messager.fragments.SocialScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messager.R
import kotlinx.android.synthetic.main.fragment_friend.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FriendFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_friend, container, false)

        view.imageButton_addFriend.setOnClickListener{
            findNavController().navigate(R.id.action_SocialScreen_to_addFriendFragment)
        }

        view.imageButton_request.setOnClickListener{
            findNavController().navigate(R.id.action_SocialScreen_to_requestFragment)
        }

        return view
    }


}