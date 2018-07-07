package com.example.sport0102.myapplication.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_people.view.*
import java.util.ArrayList

class ChatFragment : Fragment() {
    var mFirebaseAuth = FirebaseAuth.getInstance()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_chat, container, false)
        view.fragment_chat_rv.layoutManager = LinearLayoutManager(inflater.context)
        view.fragment_chat_rv.adapter = ChatRecyclerViewAdapter()
        return view
    }

    inner class ChatRecyclerViewAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var uid: String
        var chatModels: ArrayList<ChatModel> = ArrayList()

        init {
            uid = mFirebaseAuth.currentUser!!.uid
            mFirebaseDatabase.reference.child("chatrooms").orderByChild("users/${uid}").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        chatModels.clear()
                        chatModels.add(it.getValue(ChatModel::class.java)!!)
                    }
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_chat,p0,false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return chatModels.size
        }


        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

    }
}