package com.example.sport0102.myapplication

import android.content.DialogInterface
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import android.widget.Toast
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.support.v7.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener


class SplashActivity : AppCompatActivity() {
    val linearLayout: LinearLayout by lazy { findViewById(R.id.splashacitivity_linearlayout) as LinearLayout }
    var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        mFirebaseRemoteConfig?.setConfigSettings(configSettings)
        mFirebaseRemoteConfig?.setDefaults(R.xml.default_resource);

        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
// will use fetch data from the Remote Config service, rather than cached parameter values,
// if cached parameter values are more than cacheExpiration seconds old.
// See Best Practices in the README for more information.
        mFirebaseRemoteConfig?.fetch(0)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SplashActivity, "Fetch Succeeded",
                                Toast.LENGTH_SHORT).show()
                        mFirebaseRemoteConfig?.activateFetched()
                    } else {
                        Toast.makeText(this@SplashActivity, "Fetch Failed",
                                Toast.LENGTH_SHORT).show()
                    }
                    displayMessage()
                }
    }
    fun displayMessage(){
        var splashBacground = mFirebaseRemoteConfig?.getString("splash_background")
        var caps : Boolean? = mFirebaseRemoteConfig?.getBoolean("splash_message_caps")
        var splashMessage = mFirebaseRemoteConfig?.getString("splash_message")
        linearLayout.setBackgroundColor(Color.parseColor(splashBacground))
        if(caps!!){
            var builder : AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage(splashMessage).setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->
                finish()
            })
            builder.create().show()
        }

    }
}
