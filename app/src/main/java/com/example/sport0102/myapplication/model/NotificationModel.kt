package com.example.sport0102.myapplication.model

data class NotificationModel(var to: String? = null, var notification: Notification? = null) {
    companion object {
        data class Notification(var title: String? = null, var text: String? = null)
    }

}