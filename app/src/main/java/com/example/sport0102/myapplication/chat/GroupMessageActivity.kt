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
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.model.ChatModel
import com.example.sport0102.myapplication.model.NotificationModel
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_group_message.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.item_message.*
import kotlinx.android.synthetic.main.item_message.view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

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
    var peopleCount = 0


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

                    mFirebaseDatabase.getReference().child(getString(R.string.db_chatrooms)).child(destinationRoom).child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            var map = p0.getValue() as? HashMap<String, Boolean>
                            Log.d("gcm map", map.toString())
                            map?.forEach {

                                if (!it.key.equals(uid)) {
                                    sendGcm(users.get(it.key)!!.pushToken)
                                }

                            }
                            group_message_et.setText("")
                        }

                    })
                }
            }
        }
    }

    private fun sendGcm(pushToken: String) {
        var gson: Gson = Gson()
        var notificationModel = NotificationModel().apply {
            this.to = pushToken
            Log.d("pushToken", pushToken.toString())
            var userName = mFirebaseAuth.currentUser!!.displayName
            Log.d("userName", userName)
            this.notification!!.title = userName
            this.notification!!.text = group_message_et.text.toString()
            this.data!!.title = userName
            this.data!!.text = group_message_et.text.toString()
            Log.d("group_message_et.text", group_message_et.text.toString())
        }


        var requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel))
        var request = Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyC2vr58VfsUByFLQ1wQTtgEeYN9inN_ypo")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build()
        var okHttpClient = OkHttpClient()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
            }

            override fun onResponse(call: Call?, response: Response?) {
            }

        })
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
                    Log.d("readUsersMap", readUsersMap.toString())
                    if (comments.size > 0) {
                        if (!comments.get(comments.size - 1).readUsers.containsKey(uid)) {
                            mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).child(destinationRoom!!).child("comments").updateChildren(readUsersMap)
                                    .addOnCompleteListener {
                                        Log.d("updateMap", "map update")
                                        // 메시지가 갱신
                                        notifyDataSetChanged()
                                        group_message_rv.scrollToPosition(comments.size - 1)
                                    }

                        } else {
                            notifyDataSetChanged()
                            group_message_rv.scrollToPosition(comments.size - 1)
                        }
                    }
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
                    p0.itemView.item_message_tv_message.textSize = 25f
                    p0.itemView.item_message_tv_readcounter_left.visibility = View.VISIBLE
                    setReadCounter(p1, p0.itemView.item_message_tv_readcounter_left)
                    p0.itemView.item_message_tv_readcounter_right.visibility = View.GONE
                } else {
                    Glide.with(applicationContext).load(users.get(comments.get(p1).uid)!!.profileImageUrl).apply(RequestOptions().circleCrop()).into(p0.itemView.item_message_iv_profile)
                    p0.itemView.item_message_tv_name.text = users.get(comments.get(p1).uid)!!.userName
                    p0.itemView.item_message_ll_destination.visibility = View.VISIBLE
                    p0.itemView.item_message_tv_message.setBackgroundResource(R.drawable.leftbuble)
                    p0.itemView.item_message_tv_message.text = comments.get(p1).message
                    p0.itemView.item_message_tv_message.textSize = 25f
                    p0.itemView.item_message_ll_main.gravity = Gravity.LEFT
                    p0.itemView.item_message_tv_readcounter_right.visibility = View.VISIBLE
                    setReadCounter(p1, p0.itemView.item_message_tv_readcounter_right)
                    p0.itemView.item_message_tv_readcounter_left.visibility = View.GONE
                }
                var unixTime = comments.get(p1).timestamp.toString().toLong()
                var date = Date(unixTime)
                simpleDataFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                var time = simpleDataFormat.format(date)
                p0.itemView.item_message_tv_timestamp.setText(time)
            }
        }

        fun setReadCounter(position: Int, tv: TextView) {
            if (peopleCount == 0) {
                mFirebaseDatabase.reference.child(applicationContext.getString(R.string.db_chatrooms)).child(destinationRoom!!).child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        var users: HashMap<String, Boolean> = p0!!.getValue() as HashMap<String, Boolean>
                        peopleCount = users.size
                        var count = peopleCount - comments.get(position).readUsers.size
                        if (count > 0) {
                            tv.visibility = View.VISIBLE
                            tv.text = count.toString()
                        } else {
                            tv.visibility = View.INVISIBLE
                        }
                    }

                })
            } else {
                var count = peopleCount - comments.get(position).readUsers.size
                if (count > 0) {
                    tv.visibility = View.VISIBLE
                    tv.text = count.toString()
                } else {
                    tv.visibility = View.INVISIBLE
                }
            }
        }


        inner class GroupMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        }

    }
}

