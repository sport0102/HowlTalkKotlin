package com.example.sport0102.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var splashBackground = mFirebaseRemoteConfig.getString(getString(R.string.splash_background))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        login_btn_login.setBackgroundColor(Color.parseColor(splashBackground))
        login_btn_signup.setBackgroundColor(Color.parseColor(splashBackground))
        login_btn_signup.setOnClickListener { startActivity(Intent(this@LoginActivity, SignUpActivity::class.java)) }
    }
}
