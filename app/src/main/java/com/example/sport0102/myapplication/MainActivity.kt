package com.example.sport0102.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sport0102.myapplication.fragment.PeopleFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fragmentManager.beginTransaction().replace(R.id.main_fl,PeopleFragment()).commit()
    }
}
