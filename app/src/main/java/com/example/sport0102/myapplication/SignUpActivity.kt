package com.example.sport0102.myapplication

import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.sport0102.myapplication.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    val mFireBaseAuth = FirebaseAuth.getInstance()
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val mFirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        var splashBackground = mFirebaseRemoteConfig.getString(getString(R.string.splash_background))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        sign_up_btn_signup.setBackgroundColor(Color.parseColor(splashBackground))
        sign_up_btn_signup.setOnClickListener {
            if(sign_up_et_email.text==null||sign_up_et_name.text==null||sign_up_btn_signup.text==null){
                return@setOnClickListener
            }
            mFireBaseAuth.createUserWithEmailAndPassword(sign_up_et_email.text.toString(), sign_up_btn_signup.text.toString())
                .addOnCompleteListener(this@SignUpActivity, OnCompleteListener {
                    var userModel : UserModel = UserModel()
                    userModel.userName=sign_up_et_name.text.toString()
                    var uid = it.getResult().user.uid
                    mFirebaseDatabase.getReference().child("users").child(uid).setValue(userModel)
                }) }

    }
}
