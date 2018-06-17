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

class SignUpActivity : AppCompatActivity() {
    val emailEt by lazy { findViewById<EditText>(R.id.sign_up_et_email) }
    val nameEt by lazy { findViewById<EditText>(R.id.sign_up_et_name) }
    val pwdEt by lazy { findViewById<EditText>(R.id.sign_up_et_pwd) }
    val signUpBtn by lazy { findViewById<Button>(R.id.sign_up_btn_signup) }
    val mFireBaseAuth = FirebaseAuth.getInstance()
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val mFirebaseDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        var splashBackground = mFirebaseRemoteConfig.getString(getString(R.string.rc_color))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        signUpBtn.setBackgroundColor(Color.parseColor(splashBackground))
        signUpBtn.setOnClickListener {
            if(emailEt.text==null||nameEt.text==null||pwdEt.text==null){
                return@setOnClickListener
            }
            mFireBaseAuth.createUserWithEmailAndPassword(emailEt.text.toString(), pwdEt.text.toString())
                .addOnCompleteListener(this@SignUpActivity, OnCompleteListener {
                    var userModel : UserModel = UserModel()
                    userModel.userName=nameEt.text.toString()
                    var uid = it.getResult().user.uid
                    mFirebaseDatabase.getReference().child("users").child(uid).setValue(userModel)
                }) }

    }
}
