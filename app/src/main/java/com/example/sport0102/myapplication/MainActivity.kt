package com.example.sport0102.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import com.example.sport0102.myapplication.fragment.ChatFragment
import com.example.sport0102.myapplication.fragment.PeopleFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_nv_bottom.setOnNavigationItemSelectedListener({
            when (it.itemId) {
                R.id.action_people -> {
                    fragmentManager.beginTransaction().replace(R.id.main_fl, PeopleFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.action_chat ->{
                    fragmentManager.beginTransaction().replace(R.id.main_fl, ChatFragment()).commit()
                    return@setOnNavigationItemSelectedListener true
                }

            }
            return@setOnNavigationItemSelectedListener true
        })
    }
}