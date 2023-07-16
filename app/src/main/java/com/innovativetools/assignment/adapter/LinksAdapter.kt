package com.innovativetools.assignment.adapter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.innovativetools.assignment.model.Link
import com.innovativetools.assignment.R

class LinksAdapter(private val links: List<Link>) : BaseAdapter(){
    override fun getCount(): Int {
        return links.size
    }

    override fun getItem(position: Int): Any {
        return links[position]
    }

    override fun getItemId(position: Int): Long {
         return position.toLong()
        }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View
        val viewHolder: ViewHolder


        if (convertView == null) {
            // Inflate the item layout if it's not recycled
            view = LayoutInflater.from(parent?.context).inflate(R.layout.item_link_view, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        } else {
            // Reuse the recycled view
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val link = links[position]


        Glide.with(view.context).load(link.originalImage).into(viewHolder.iv_link)

        viewHolder.link_title.text = link.title
        viewHolder.click_time.text = link.timesAgo
        viewHolder.link_url.text = link.smartLink
        viewHolder.link_Click_Count.text = link.totalClicks.toString()

        viewHolder.link_copy.setOnClickListener{
            val clipboardManager = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Label", viewHolder.link_url.text.toString())
            clipboardManager.setPrimaryClip(clipData)
        }

        return view
    }

    private class ViewHolder(view: View){
        val iv_link = view.findViewById<ImageView>(R.id.iv_link)
        val link_copy = view.findViewById<ImageView>(R.id.iv_link_copy)

        val link_title = view.findViewById<TextView>(R.id.tv_link_title)
        val click_time = view.findViewById<TextView>(R.id.tv_clicks_ago)
        val link_Click_Count = view.findViewById<TextView>(R.id.tv_clicks_count)
        val  link_url = view.findViewById<TextView>(R.id.tv_link_url)

    }

}