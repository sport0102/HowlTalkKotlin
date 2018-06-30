package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth= FirebaseAuth.getInstance()
    val tag = "MessageActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        var destinationUid= intent.getStringExtra("destinationUid")
        Log.d("tag",destinationUid)
        message_btn_send.setOnClickListener {
            var chatModel = ChatModel()
            chatModel.uid=mFirebaseAuth.currentUser!!.uid
            chatModel.destinationUid=destinationUid
            mFirebaseDatabase.reference.child("chatrooms").push().setValue(chatModel)
        }
    }
}
