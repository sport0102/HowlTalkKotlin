package com.example.sport0102.myapplication.fragment

import android.app.AlertDialog
import android.app.Fragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sport0102.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.dialog_comment.view.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*

class AccountFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view = inflater!!.inflate(R.layout.fragment_account, container, false)
        view.fragment_account_btn_status.setOnClickListener {
            showDialog(view.context)
        }
        return view
    }

    fun showDialog(context: Context) {
        var builder = AlertDialog.Builder(context)
        var layoutInflater = activity.layoutInflater
        var view = layoutInflater.inflate(R.layout.dialog_comment, null)
        builder.setView(view).setPositiveButton("확인", DialogInterface.OnClickListener({ dialogInterface, i ->
            var map = HashMap<String,Any>()
            map.put("comment",view.dialog_comment_et_status.text.toString())
            FirebaseDatabase.getInstance().reference.child(resources.getString(R.string.db_users)).child(FirebaseAuth.getInstance().currentUser!!.uid).updateChildren(map)
        })).setNegativeButton("취소", DialogInterface.OnClickListener({ dialogInterface, i ->

        }))
        builder.show()
    }
}


