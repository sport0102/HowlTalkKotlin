package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupMessageActivity : AppCompatActivity() {
    var users = HashMap<String, UserModel>()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message)
        mFirebaseDatabase.getReference().child("users").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                users = p0.getValue() as HashMap<String, UserModel>

            }

        })
    }
}
