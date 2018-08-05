package com.example.sport0102.myapplication.fragment

import android.app.ActivityOptions
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.chat.GroupMessageActivity
import com.example.sport0102.myapplication.chat.MessageActivity
import com.example.sport0102.myapplication.model.ChatModel
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_select_friend.*
import kotlinx.android.synthetic.main.item_friend.view.*
import kotlinx.android.synthetic.main.item_friend_select.view.*

class SelectFriendActivity : AppCompatActivity() {

    var chatModel = ChatModel()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_friend)
        select_friend_rv.adapter = SelectFriendRecyclerViewAdapter()
        select_friend_rv.layoutManager = LinearLayoutManager(this)
        select_friend_btn_start.setOnClickListener {
            chatModel.users!!.put(mFirebaseAuth.currentUser!!.uid, true)
            var key = mFirebaseDatabase.getReference().child(resources.getString(R.string.db_chatrooms)).push().key
            mFirebaseDatabase.getReference().child(resources.getString(R.string.db_chatrooms)).child(key!!).setValue(chatModel).addOnCompleteListener {
                var intent = Intent(applicationContext, GroupMessageActivity::class.java)
                intent.putExtra("destinationRoom", key)
                startActivity(intent)
                finish()
            }

        }
    }

    inner class SelectFriendRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var userModels: ArrayList<UserModel>

        init {
            userModels = ArrayList<UserModel>()
            var myUid = mFirebaseAuth.currentUser!!.uid
            mFirebaseDatabase.getReference().child(resources.getString(R.string.db_users)).addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    userModels.clear()
                    p0.children.forEach {
                        var userModel = it.getValue(UserModel::class.java)!!
                        if (userModel.uid.equals(myUid)) {
                            return@forEach
                        }
                        userModels.add(userModel)
                    }
                    notifyDataSetChanged()
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_friend_select, p0, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return userModels.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            Glide.with(p0.itemView.context).load(userModels.get(p1).profileImageUrl).apply(RequestOptions().circleCrop()).into(p0.itemView.item_friend_select_iv_profileimage)
            p0.itemView.item_friend_select_tv_id.setText(userModels.get(p1).userName)
            p0.itemView.setOnClickListener {
                var intent = Intent(applicationContext, MessageActivity::class.java)
                intent.putExtra("destinationUid", userModels.get(p1).uid)
                var activityOptions = ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fromright, R.anim.toleft)
                startActivity(intent, activityOptions.toBundle())
            }
            if (userModels.get(p1).comment != null) {
                p0.itemView.item_friend_select_tv_status.setText(userModels.get(p1).comment)
            }
            p0.itemView.item_friend_select_cv.setOnCheckedChangeListener { compoundButton, b ->
                if (b == true) {
                    chatModel.users!!.put(userModels.get(p1).uid, true)
                } else {
                    chatModel.users!!.remove(userModels.get(p1).uid)
                }

            }
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        }

    }
}
