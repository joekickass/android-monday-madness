package com.joekickass.mondaymadness.menu.about

import android.R
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TwoLineListItem
import com.joekickass.mondaymadness.menu.about.AboutItem

/**
 * About menu adapter
 */
class AboutAdapter(private val mData: List<AboutItem>) : RecyclerView.Adapter<AboutAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val li = LayoutInflater.from(parent.context)
        val v = li.inflate(R.layout.simple_list_item_2, parent, false) as TwoLineListItem
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mText1.text = mData[position].title
        holder.mText2.text = mData[position].text
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(v: TwoLineListItem) : RecyclerView.ViewHolder(v) {
        internal var mText1: TextView
        internal var mText2: TextView

        init {
            mText1 = v.findViewById(R.id.text1) as TextView
            mText2 = v.findViewById(R.id.text2) as TextView
        }
    }
}
