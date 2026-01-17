package com.example.notiflogger

import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotifAdapter(private val items: MutableList<NotificationItem>) :
    RecyclerView.Adapter<NotifAdapter.VH>() {

    class VH(val root: LinearLayout) : RecyclerView.ViewHolder(root) {
        val line1 = TextView(root.context)
        val line2 = TextView(root.context)
        val line3 = TextView(root.context)

        init {
            root.orientation = LinearLayout.VERTICAL
            root.setPadding(16, 12, 16, 12)
            root.gravity = Gravity.START

            line1.textSize = 14f
            line2.textSize = 13f
            line3.textSize = 12f

            root.addView(line1)
            root.addView(line2)
            root.addView(line3)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val layout = LinearLayout(parent.context)
        layout.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return VH(layout)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.line1.text = "${it.time}  •  ${it.pkg}"
        holder.line2.text = it.title ?: "(no title)"
        holder.line3.text = it.text ?: "(no text)"
    }

    override fun getItemCount(): Int = items.size

    fun prepend(newItem: NotificationItem) {
        items.add(0, newItem)
        notifyItemInserted(0)
    }

    fun replaceAll(newItems: List<NotificationItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
