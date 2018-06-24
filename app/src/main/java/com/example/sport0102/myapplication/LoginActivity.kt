package com.example.sport0102.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val mFirebaseAuth = FirebaseAuth.getInstance()
    lateinit var mFirebaseAuthStateListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        var splashBackground = mFirebaseRemoteConfig.getString(getString(R.string.splash_background))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        mFirebaseAuth.signOut()
        login_btn_login.setBackgroundColor(Color.parseColor(splashBackground))
        login_btn_signup.setBackgroundColor(Color.parseColor(splashBackground))
        login_btn_signup.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            finish()
        }
        login_btn_login.setOnClickListener {
            mFirebaseAuth.signInWithEmailAndPassword(login_et_id.text.toString(), login_et_pwd.text.toString()).addOnCompleteListener {
                //로그인 실패시
                if (!it.isSuccessful) {
                    Toast.makeText(applicationContext, "login failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        // 로그인 인터페이스 리스너
        mFirebaseAuthStateListener = FirebaseAuth.AuthStateListener {
            if (it.currentUser == null) {
                return@AuthStateListener
            }
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }


    }

    override fun onStart() {
        super.onStart()
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mFirebaseAuth.removeAuthStateListener(mFirebaseAuthStateListener)
    }
}
