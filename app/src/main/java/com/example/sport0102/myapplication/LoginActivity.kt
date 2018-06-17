package com.example.sport0102.myapplication

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.widget.Button
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class LoginActivity : AppCompatActivity() {
    val loginBtn by lazy{findViewById<Button>(R.id.login_btn_login)}
    val signInBtn by lazy{findViewById<Button>(R.id.login_btn_signin)}
    var mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var splashBackground = mFirebaseRemoteConfig.getString("splash_background")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        loginBtn.setBackgroundColor(Color.parseColor(splashBackground))
        signInBtn.setBackgroundColor(Color.parseColor(splashBackground))
    }
}
