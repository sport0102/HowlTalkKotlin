package com.example.sport0102.myapplication.chat

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.example.sport0102.myapplication.model.NotificationModel
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.item_message.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MessageActivity : AppCompatActivity() {
    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth = FirebaseAuth.getInstance()
    var uid: String = mFirebaseAuth.currentUser!!.uid
    lateinit var destinationUid: String
    lateinit var userModel: UserModel
    var chatroomUid: String? = null
    val tag = "MessageActivity"
    var simpleDataFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        destinationUid = intent.getStringExtra("destinationUid")
        Log.d(tag, destinationUid)
        Log.d(tag, uid)
        checkChatRoom()
        message_btn_send.setOnClickListener {
            var chatModel = ChatModel()
            chatModel.users?.put(uid, true)
            chatModel.users?.put(destinationUid, true)
            var comments = ChatModel.Companion.Comment()
            comments.uid = uid
            comments.message = message_et_message.text.toString()
            if (chatroomUid == null) {
                message_btn_send.isEnabled = false
                mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).push().setValue(chatModel).addOnCompleteListener {
                    checkChatRoom()
                }
            } else {
                comments.timestamp = ServerValue.TIMESTAMP
                mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).child(chatroomUid!!).child("comments").push().setValue(comments).addOnCompleteListener {
                    sendGcm()
                    message_et_message.setText("")
                }
            }
        }
    }

    private fun sendGcm() {
        var gson: Gson = Gson()
        var notificationModel = NotificationModel().apply {
            this.to = userModel.pushToken
            var userName =mFirebaseAuth.currentUser!!.displayName
            this.notification!!.title = userName
            this.notification!!.text = message_et_message.text.toString()
            this.data!!.title = userName
            this.data!!.text = message_et_message.text.toString()
        }


        var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel))
        var request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyC2vr58VfsUByFLQ1wQTtgEeYN9inN_ypo")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build()
        var okHttpClient =OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
            }

        })
    }

    fun checkChatRoom() {
        Log.d(tag, "${uid} ~~~~~")
        mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).orderByChild("users/${uid}").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.d(tag, p0.key)
                Log.d(tag, p0.children.toString())
                p0.children.forEach {
                    var chatModel = it.getValue(ChatModel::class.java)
                    Log.d(tag, chatModel.toString())
                    Log.d(tag, chatModel!!.users.toString())
                    Log.d(tag, chatModel!!.comments.toString())
                    Log.d(tag, chatModel.toString())
                    Log.d(tag, chatModel?.users?.keys.toString())
                    if (chatModel?.users?.containsKey(destinationUid)!!) {
                        chatroomUid = it.key
                        message_btn_send.isEnabled = true
                        message_rv.layoutManager = LinearLayoutManager(this@MessageActivity)
                        message_rv.adapter = RecyclerViewAdapter()
                    }
                }
            }


        })
    }


    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var comments: ArrayList<ChatModel.Companion.Comment>


        init {
            comments = ArrayList()
            mFirebaseDatabase.reference.child(resources.getString(R.string.db_users)).child(destinationUid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    userModel = p0.getValue(UserModel::class.java)!!
                    getMessageList()
                }

            })

        }

        fun getMessageList() {
            mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).child(chatroomUid!!).child("comments").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    comments.clear()
                    p0.children.forEach {
                        comments.add(it.getValue(ChatModel.Companion.Comment::class.java)!!)
                    }
                    // 메시지가 갱신
                    notifyDataSetChanged()
                    message_rv.scrollToPosition(comments.size - 1)
                }

            })
        }


        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view: View = LayoutInflater.from(p0.context).inflate(R.layout.item_message, p0, false)
            return MessageViewHolder(view)
        }

        override fun getItemCount(): Int {
            return comments.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            if (comments.get(p1).uid == uid) {
                p0.itemView.item_message_tv_message.text = comments.get(p1).message
                p0.itemView.item_message_tv_message.setBackgroundResource(R.drawable.rightbuble)
                p0.itemView.item_message_ll_destination.visibility = View.INVISIBLE
                p0.itemView.item_message_ll_main.gravity = Gravity.RIGHT
            } else {
                Glide.with(applicationContext).load(userModel.profileImageUrl).apply(RequestOptions().circleCrop()).into(p0.itemView.item_message_iv_profile)
                p0.itemView.item_message_tv_name.text = userModel.userName
                p0.itemView.item_message_ll_destination.visibility = View.VISIBLE
                p0.itemView.item_message_tv_message.setBackgroundResource(R.drawable.leftbuble)
                p0.itemView.item_message_tv_message.text = comments.get(p1).message
                p0.itemView.item_message_tv_message.textSize = 25f
                p0.itemView.item_message_ll_main.gravity = Gravity.LEFT
            }
            var unixTime = comments.get(p1).timestamp.toString().toLong()
            var date = Date(unixTime)
            simpleDataFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
            var time = simpleDataFormat.format(date)
            p0.itemView.item_message_tv_timestamp.setText(time)

        }

        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        }

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.fromleft, R.anim.toright)
    }
}
