package com.example.notiflogger

data class NotificationItem(
    val time: String,
    val pkg: String,
    val title: String?,
    val text: String?
)
