package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
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
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.item_message.view.*

class MessageActivity : AppCompatActivity() {
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()
    var uid = mFirebaseAuth.currentUser!!.uid
    lateinit var destinationUid: String
    var chatroomUid: String? = null
    val tag = "MessageActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        destinationUid = intent.getStringExtra("destinationUid")
        Log.d(tag, destinationUid)
        message_btn_send.setOnClickListener {
            var chatModel = ChatModel()
            chatModel.users?.put(uid, true)
            chatModel.users?.put(destinationUid, true)
            Log.d(tag, uid)
            Log.d(tag, destinationUid)
            var comments = ChatModel.Companion.Comment()
            comments.uid = uid
            comments.message = message_et_message.text.toString()
            checkChatRoom()
            if (chatroomUid == null) {
                Log.d(tag, "null")
                message_btn_send.isEnabled=false
                mFirebaseDatabase.reference.child("chatrooms").push().setValue(chatModel).addOnCompleteListener {
                    mFirebaseDatabase.reference.child("chatrooms").child(chatroomUid!!).child("commnets").push().setValue(comments).addOnCompleteListener {
                        checkChatRoom()
                    }
                }
            } else {
                Log.d(tag, "message")
                mFirebaseDatabase.reference.child("chatrooms").child(chatroomUid!!).child("commnets").push().setValue(comments)
            }
        }
    }

    fun checkChatRoom() {
        mFirebaseDatabase.reference.child("chatrooms").orderByChild("users/${uid}").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    var chatModel = it.getValue(ChatModel::class.java)
                    Log.d(tag, chatModel.toString())
                    Log.d(tag, chatModel?.users?.keys.toString())
                    if (chatModel?.users?.containsKey(destinationUid)!!) {
                        chatroomUid = it.key
                        message_btn_send.isEnabled=true
                        message_rv.layoutManager = LinearLayoutManager(this@MessageActivity)
                        message_rv.adapter = RecyclerViewAdapter()
                    }
                }
            }


        })
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments : ArrayList<ChatModel.Companion.Comment> = ArrayList()

        init {
            mFirebaseDatabase.reference.child("chatrooms").child(chatroomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    comments.clear()
                    p0.children.forEach {
                        comments.add(it.getValue(ChatModel.Companion.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view : View =LayoutInflater.from(p0.context).inflate(R.layout.item_message,p0,false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            Log.d("comments.get(p1)",comments.get(p1).toString())
            p0.itemView.item_message_tv_message.text = comments.get(p1).toString()
        }
        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        }

    }
}
