package com.example.sport0102.myapplication.fragment

import android.app.ActivityOptions
import android.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.sport0102.myapplication.R
import com.example.sport0102.myapplication.chat.MessageActivity
import com.example.sport0102.myapplication.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_people.view.*
import kotlinx.android.synthetic.main.item_friend.view.*

class PeopleFragment : Fragment() {

    var mFirebaseDatabase = FirebaseDatabase.getInstance()
    var mFirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_people, container, false)
        view.fragment_people_rv.layoutManager = LinearLayoutManager(inflater.context)
        view.fragment_people_rv.adapter = PeopleFragmentRecyclerViewAdapter()
        return view
    }

    inner class PeopleFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var userModels: ArrayList<UserModel>

        init {
            userModels = ArrayList<UserModel>()
            var myUid = mFirebaseAuth.currentUser!!.uid
            mFirebaseDatabase.getReference().child("users").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    userModels.clear()
                    p0.children.forEach {
                        var userModel = it.getValue(UserModel::class.java)!!
                        if(userModel.uid.equals(myUid)){
                            return@forEach
                        }
                        userModels.add(userModel)
                    }
                    notifyDataSetChanged()
                }

            })
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_friend, p0, false)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return userModels.size
        }

        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            Glide.with(p0.itemView.context).load(userModels.get(p1).profileImageUrl).apply(RequestOptions().circleCrop()).into(p0.itemView.item_friend_iv_profileimage)
            p0.itemView.item_friend_tv_id.setText(userModels.get(p1).userName)
            p0.itemView.setOnClickListener {
                var intent = Intent(view.context,MessageActivity::class.java)
                intent.putExtra("destinationUid",userModels.get(p1).uid)
                var activityOptions = ActivityOptions.makeCustomAnimation(view.context,R.anim.fromright,R.anim.toleft)
                startActivity(intent,activityOptions.toBundle())
            }
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var profileImage = view.findViewById<ImageView>(R.id.item_friend_iv_profileimage)
            var profileId = view.findViewById<TextView>(R.id.item_friend_tv_id)
        }

    }
}