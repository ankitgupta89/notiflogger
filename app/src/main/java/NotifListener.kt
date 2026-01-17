package com.example.notiflogger

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotifListener : NotificationListenerService() {

    companion object {
        const val ACTION_NEW_LOG_LINE = "com.example.notiflogger.NEW_LOG_LINE"
        const val EXTRA_LINE = "line"
    }

    private val tsFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)

    override fun onListenerConnected() {
        appendLine("""{"event":"listener_connected","time":"${tsFormat.format(Date())}"}""")
        Log.d("NotifListener", "Listener connected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            val extras = sbn.notification.extras
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

            val line = obj.toString()
            appendLine(line)

            val i = Intent(ACTION_NEW_LOG_LINE).apply {
                setPackage(packageName)
                putExtra(EXTRA_LINE, line)
            }
            sendBroadcast(i)
        } catch (t: Throwable) {
            Log.e("NotifListener", "onNotificationPosted failed", t)
        }
    }

    private fun appendLine(line: String) {
        try {
            val f = File(filesDir, "notifications.jsonl")
            f.appendText(line + "\n")
        } catch (t: Throwable) {
            Log.e("NotifListener", "WRITE FAILED", t)
        }
    }
}
