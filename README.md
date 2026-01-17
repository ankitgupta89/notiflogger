# NotifLogger 📲📝

A simple Android (Kotlin) app that listens to system notifications and logs them to a file, while also displaying them live inside the app UI.

✅ Tested on **Samsung Galaxy S22 Ultra**

---

## ✨ Features

- ✅ Reads notifications using `NotificationListenerService`
- ✅ Logs notifications to a local file (**JSON Lines format**)
- ✅ Displays notifications inside the app UI (RecyclerView)
- ✅ Live auto-refresh when new notifications arrive
- ✅ Manual refresh + clear log buttons

---

## 📦 Log file location for further automation

The app writes notification events to: /data/user/0/com.example.notiflogger/files/notifications.jsonl
Further work is planned to make it configurable. 



