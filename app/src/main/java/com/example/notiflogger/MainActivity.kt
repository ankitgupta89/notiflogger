package com.example.notiflogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: NotifAdapter
    private var receiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == NotifListener.ACTION_NEW_LOG_LINE) {
                val line = intent.getStringExtra(NotifListener.EXTRA_LINE) ?: return
                parseLine(line)?.let {
                    adapter.prepend(it)
                    findViewById<RecyclerView>(R.id.recycler).scrollToPosition(0)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recycler = findViewById<RecyclerView>(R.id.recycler)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = NotifAdapter(mutableListOf())
        recycler.adapter = adapter

        findViewById<Button>(R.id.btnAccess).setOnClickListener {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            adapter.replaceAll(loadAll())
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            File(filesDir, "notifications.jsonl").delete()
            adapter.replaceAll(emptyList())
        }

        adapter.replaceAll(loadAll())
    }

    override fun onStart() {
        super.onStart()

        val filter = IntentFilter(NotifListener.ACTION_NEW_LOG_LINE)

        if (!receiverRegistered) {
            if (Build.VERSION.SDK_INT >= 33) {
                // Android 13+ requires an explicit export flag for dynamic receivers
                registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                @Suppress("DEPRECATION")
                registerReceiver(receiver, filter)
            }
            receiverRegistered = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (receiverRegistered) {
            try {
                unregisterReceiver(receiver)
            } catch (_: Throwable) {
                // ignore if already unregistered
            }
            receiverRegistered = false
        }
    }

    private fun loadAll(): List<NotificationItem> {
        val f = File(filesDir, "notifications.jsonl")
        if (!f.exists()) return emptyList()

        return f.readLines()
            .asReversed() // newest first
            .mapNotNull { parseLine(it) }
    }

    private fun parseLine(line: String): NotificationItem? {
        return try {
            val o = JSONObject(line)
            if (o.optString("event") != "posted") return null
            NotificationItem(
                time = o.optString("time"),
                pkg = o.optString("package"),
                title = o.optString("title").takeIf { it.isNotBlank() && it != "null" },
                text = o.optString("text").takeIf { it.isNotBlank() && it != "null" }
            )
        } catch (_: Throwable) {
            null
        }
    }
}
