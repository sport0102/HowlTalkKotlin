package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group_message.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.item_message.view.*
import java.text.SimpleDateFormat
import java.util.*

class GroupMessageActivity : AppCompatActivity() {
    var users = HashMap<String, UserModel>()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()
    lateinit var destinationRoom: String
    lateinit var uid: String
    lateinit var dataReference: DatabaseReference
    var valueEventListener: ValueEventListener? = null
    var comments: ArrayList<ChatModel.Companion.Comment> = ArrayList()
    var simpleDataFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_message)
        destinationRoom = intent.getStringExtra("destinationRoom")
        uid = mFirebaseAuth.currentUser!!.uid

        mFirebaseDatabase.getReference().child(resources.getString(R.string.db_users)).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    users.put(it.key!!, it.getValue(UserModel::class.java)!!)
                }
                Log.d("users", users.toString())
                init()
                group_message_rv.adapter = GroupMessageRecyclerViewAdapter()
                group_message_rv.layoutManager = LinearLayoutManager(this@GroupMessageActivity)
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

    inner class GroupMessageRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        init {
            getMessageList()
        }

        fun getMessageList() {
            dataReference = mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).child(destinationRoom!!).child("comments")
            valueEventListener = dataReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    comments.clear()
                    var readUsersMap = HashMap<String, Any>()

                    p0.children.forEach {
                        var key = it.key
                        var commentOrigin = it.getValue(ChatModel.Companion.Comment::class.java)
                        var commentModify = it.getValue(ChatModel.Companion.Comment::class.java)
                        commentModify!!.readUsers.put(uid, true)
                        readUsersMap.put(key!!, commentModify)
                        comments.add(commentOrigin!!)
                    }
                    notifyDataSetChanged()
                    group_message_rv.scrollToPosition(comments.size - 1)
//                    if (!comments.get(comments.size - 1).readUsers.containsKey(uid)) {
//                        mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).child(destinationRoom!!).child("comments").updateChildren(readUsersMap)
//                                .addOnCompleteListener {
//                                    // 메시지가 갱신
//                                    notifyDataSetChanged()
//                                    group_message_rv.scrollToPosition(comments.size - 1)
//                                }
//
//                    } else {
//                        notifyDataSetChanged()
//                        group_message_rv.scrollToPosition(comments.size - 1)
//                    }
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0!!.context).inflate(R.layout.item_message, p0, false)
            return GroupMessageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            if (comments.size > 0) {
                if (comments.get(p1).uid == uid) {
                    p0.itemView.item_message_tv_message.text = comments.get(p1).message
                    p0.itemView.item_message_tv_message.setBackgroundResource(R.drawable.rightbuble)
                    p0.itemView.item_message_ll_destination.visibility = View.INVISIBLE
                    p0.itemView.item_message_ll_main.gravity = Gravity.RIGHT
//                setReadCounter(p1, p0.itemView.item_message_tv_readcounter_left)
                } else {
                    Glide.with(applicationContext).load(users.get(comments.get(p1).uid)!!.profileImageUrl).apply(RequestOptions().circleCrop()).into(p0.itemView.item_message_iv_profile)
                    p0.itemView.item_message_tv_name.text = users.get(comments.get(p1).uid)!!.userName
                    p0.itemView.item_message_ll_destination.visibility = View.VISIBLE
                    p0.itemView.item_message_tv_message.setBackgroundResource(R.drawable.leftbuble)
                    p0.itemView.item_message_tv_message.text = comments.get(p1).message
                    p0.itemView.item_message_tv_message.textSize = 25f
                    p0.itemView.item_message_ll_main.gravity = Gravity.LEFT
//                setReadCounter(p1, p0.itemView.item_message_tv_readcounter_right)
                }
                var unixTime = comments.get(p1).timestamp.toString().toLong()
                var date = Date(unixTime)
                simpleDataFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                var time = simpleDataFormat.format(date)
                p0.itemView.item_message_tv_timestamp.setText(time)
            }
        }

        inner class GroupMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }

    }
}

