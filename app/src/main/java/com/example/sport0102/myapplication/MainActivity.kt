package com.example.sport0102.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.util.Log
import android.widget.Toast
import com.example.sport0102.myapplication.fragment.AccountFragment
import com.example.sport0102.myapplication.fragment.ChatFragment
import com.example.sport0102.myapplication.fragment.PeopleFragment
import com.google.android.gms.common.util.CrashUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().replace(R.id.main_fl, PeopleFragment()).commit()
        passPushTokenToServer()
        main_nv_bottom.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_people -> {
                    fragmentManager.beginTransaction().replace(R.id.main_fl, PeopleFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_chat -> {
                    Log.d("action_chat", "chat")
                    fragmentManager.beginTransaction().replace(R.id.main_fl, ChatFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_account -> {
                    fragmentManager.beginTransaction().replace(R.id.main_fl, AccountFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener true
        }

    }

    fun passPushTokenToServer() {
        var uid = FirebaseAuth.getInstance().currentUser!!.uid
        var token = FirebaseInstanceId.getInstance().getToken()
        var map: HashMap<String, Any> = HashMap()
        map.put("pushToken", token!!)
        FirebaseDatabase.getInstance().reference.child(getString(R.string.db_users)).child(uid).updateChildren(map)
    }
}