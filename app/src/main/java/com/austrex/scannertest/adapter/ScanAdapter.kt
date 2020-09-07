package com.austrex.scannertest.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.austrex.scannertest.R
import com.austrex.scannertest.model.Tag
import kotlinx.android.synthetic.main.scan_item.view.*

class ScanAdapter(private val context: Context) : RecyclerView.Adapter<ScanAdapter.Holder>() {

    private var mTags: MutableList<Tag> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(LayoutInflater.from(context).inflate(
        R.layout.scan_item, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bindView(mTags[position])
    }

    fun setData(tags: List<Tag>) {
        mTags.clear()
        mTags.addAll(tags)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mTags.size

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        fun bindView(tag: Tag) {
            itemView.tvRfid.text = tag.rfid
        }
    }
}