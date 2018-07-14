package com.example.sport0102.myapplication.model

data class ChatModel(var users:HashMap<String, Boolean>?=HashMap(),var comments:HashMap<String, Comment>?=HashMap()) {
    companion object {
        data class Comment(var uid: String?=null, var message:String?=null,var timestamp : Any?=null)
    }

}
