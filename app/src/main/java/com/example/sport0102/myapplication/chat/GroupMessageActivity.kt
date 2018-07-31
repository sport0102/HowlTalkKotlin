package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group_message.*

class GroupMessageActivity : AppCompatActivity() {
    var users = HashMap<String, UserModel>()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()
    lateinit var destinationRoom: String
    lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message)
        destinationRoom = intent.getStringExtra("destinationRoom")
        uid = mFirebaseAuth.currentUser!!.uid

        mFirebaseDatabase.getReference().child(resources.getString(R.string.db_users)).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                users = p0.getValue() as HashMap<String, UserModel>
                init()
            }

        })
    }

    fun init() {
        group_message_btn_send.setOnClickListener {
            if (!TextUtils.isEmpty(group_message_et.text.toString())) {
                var comment = ChatModel.Companion.Comment()
                comment.uid = uid
                comment.message = group_message_et.text.toString()
                comment.timestamp = ServerValue.TIMESTAMP
                mFirebaseDatabase.getReference().child(resources.getString(R.string.db_chatrooms)).child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener {
                    group_message_et.setText("")
                }
            }
        }
    }
}

