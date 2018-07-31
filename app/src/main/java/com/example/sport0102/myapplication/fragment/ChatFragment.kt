package com.example.sport0102.myapplication.fragment

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.item_chat.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment : Fragment() {
    var mFirebaseAuth = FirebaseAuth.getInstance()
    var mFirebaseDatabase = FirebaseDatabase.getInstance()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_chat, container, false)
        view.fragment_chat_rv.adapter = ChatRecyclerViewAdapter()
        view.fragment_chat_rv.layoutManager = LinearLayoutManager(inflater.context)
        return view
    }

    inner class ChatRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        var uid: String
        var chatModels: ArrayList<ChatModel>? = ArrayList()
        var destinationUsers : ArrayList<String> = ArrayList()
        init {
            uid = mFirebaseAuth.currentUser!!.uid

            mFirebaseDatabase.reference.child(resources.getString(R.string.db_chatrooms)).orderByChild("users/${uid}").equalTo(true).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    chatModels!!.clear()
                    p0.children.forEach {
                        Log.d("getValue", it.getValue(ChatModel::class.java)!!.toString())
                        chatModels!!.add(it.getValue(ChatModel::class.java)!!)
                    }
                    notifyDataSetChanged()
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_chat, p0, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return if(chatModels==null) 0 else chatModels!!.size
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, p1: Int) {
            var destinationUid: String? = null
            // 유저 챗방에 있는 유저를 체크
            chatModels!!.get(p1).users!!.keys.forEach {
                if (!it.equals(uid)) {
                    destinationUid = it
                    destinationUsers.add(it)
                }

            }
            mFirebaseDatabase.reference.child(resources.getString(R.string.db_users)).child(destinationUid!!).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var userModel = p0.getValue(UserModel::class.java)
                    Glide.with(holder.itemView.context).load(userModel!!.profileImageUrl).apply(RequestOptions().circleCrop()).into(holder.itemView.item_chat_iv)
                    holder.itemView.item_chat_tv_roomname.setText(userModel.userName)
                }
            })
            // 메세지를 내림차순으로 정렬 후 마지막 메세지의 키값을 가져옴
            var commentMap: TreeMap<String, ChatModel.Companion.Comment> = TreeMap(Collections.reverseOrder())
            commentMap.putAll(chatModels!!.get(p1).comments!!)
            if(commentMap.keys.size>0) {
                var lastMessageKey = commentMap.keys.toTypedArray()[0]

                // 시간 설정
                holder.itemView.item_chat_tv_lastmessage.setText(chatModels!!.get(p1).comments!!.get(lastMessageKey)!!.message)
                var simpleDataFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
                var unixTime = chatModels!!.get(p1).comments!!.get(lastMessageKey)!!.timestamp.toString().toLong()
                var date = Date(unixTime)
                simpleDataFormat.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                var time = simpleDataFormat.format(date)
                holder.itemView.item_chat_tv_timestamp.setText(time)

            }
            holder.itemView.setOnClickListener {
                var intent : Intent? = null
                if(chatModels!!.get(p1).users!!.size>2){
                    intent = Intent(holder.itemView.context, GroupMessageActivity::class.java)
                }else{
                    intent = Intent(holder.itemView.context, MessageActivity::class.java)
                    intent.putExtra("destinationUid", destinationUsers.get(p1))
                }
                var activityOptions: ActivityOptions = ActivityOptions.makeCustomAnimation(view.context, R.anim.fromright, R.anim.toleft)
                startActivity(intent, activityOptions.toBundle())
            }

        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    }
}