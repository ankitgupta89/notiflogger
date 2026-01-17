package com.example.notiflogger

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifListener : NotificationListenerService() {

    private val tsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val n = sbn.notification
            val extras = n.extras

            val title = extras.getCharSequence("android.title")?.toString()
            val text = extras.getCharSequence("android.text")?.toString()
            val bigText = extras.getCharSequence("android.bigText")?.toString()

            val obj = JSONObject()
            obj.put("event", "posted")
            obj.put("time", tsFormat.format(Date()))
            obj.put("package", sbn.packageName)
            obj.put("id", sbn.id)
            obj.put("tag", sbn.tag ?: JSONObject.NULL)
            obj.put("title", title ?: JSONObject.NULL)
            obj.put("text", text ?: (bigText ?: JSONObject.NULL))

            appendLine(obj.toString())
        } catch (_: Throwable) { }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        try {
            val obj = JSONObject()
            obj.put("event", "removed")
            obj.put("time", tsFormat.format(Date()))
            obj.put("package", sbn.packageName)
            obj.put("id", sbn.id)
            obj.put("tag", sbn.tag ?: JSONObject.NULL)

            appendLine(obj.toString())
        } catch (_: Throwable) { }
    }

    private fun appendLine(line: String) {
        val f = File(filesDir, "notifications.jsonl")
        f.appendText(line + "\n")
    }
}
