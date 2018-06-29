package com.example.sport0102.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.sport0102.myapplication.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    val mFireBaseAuth = FirebaseAuth.getInstance()
    val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
    val mFirebaseDatabase = FirebaseDatabase.getInstance()
    val mFirebaseStorage = FirebaseStorage.getInstance()
    var imageUrl: Uri? = null
    val PICK_FROM_ALBUM = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        var splashBackground = mFirebaseRemoteConfig.getString(getString(R.string.splash_background))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Color.parseColor(splashBackground))
        }
        sign_up_iv_add_photo.setOnClickListener {
            var intent: Intent = Intent(Intent.ACTION_PICK)
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE)
            startActivityForResult(intent, PICK_FROM_ALBUM)
        }


        sign_up_btn_signup.setBackgroundColor(Color.parseColor(splashBackground))
        sign_up_btn_signup.setOnClickListener {
            if (sign_up_et_email.text == null || sign_up_et_name.text == null || sign_up_btn_signup.text == null||imageUrl==null) {
                return@setOnClickListener
            }
            mFireBaseAuth.createUserWithEmailAndPassword(sign_up_et_email.text.toString(), sign_up_et_pwd.text.toString())
                    .addOnCompleteListener(this@SignUpActivity, OnCompleteListener {
                        var uid = it.getResult().user.uid

                        mFirebaseStorage.getReference().child("userImages").child(uid).putFile(imageUrl!!).addOnCompleteListener { task2: Task<UploadTask.TaskSnapshot> ->
                            var imageUrl = task2.getResult().downloadUrl.toString()
                            var userModel: UserModel = UserModel()
                            userModel.userName = sign_up_et_name.text.toString()
                            userModel.profileImageUrl = imageUrl
                            mFirebaseDatabase.getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener {
                                startActivity(Intent(applicationContext, LoginActivity::class.java))
                                finish()
                            }
                        }
                    })

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            Log.d("onActivityResult", "통과")
            sign_up_iv_add_photo.setImageURI(data?.getData())
            imageUrl = data?.getData()
            Log.d("onActivityResult", imageUrl.toString())
        }
    }
}
