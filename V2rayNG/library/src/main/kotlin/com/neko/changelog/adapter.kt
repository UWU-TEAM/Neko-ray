package com.neko.changelog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neko.R

/**
 * adapter.tk
 * Defines classes and adapters for the changelog dialog view.
 *
 * Copyright (C) 2018 Lucy Linder (derlin)
 *
 * This software may be modified and distributed under the terms
 * of the Apache 2.0 license.  See the LICENSE file for details.
 */

/** Holds information about one "item", i.e. a <change> */
open class ChangelogItem(val text: String)

/** Holds information about one "header", i.e. a <release> */
class ChangelogHeader(version: String, val date: String? = null, val summary: String? = null) : ChangelogItem(version)


/** Base Holder for the [ChangelogAdapter]. Used for [ChangelogItem]. */
open class Holder(v: View) : RecyclerView.ViewHolder(v) {
    val textview = v.findViewById<TextView>(R.id.text)
}

/** Holder for the [ChangelogAdapter]. Used for [ChangelogHeader]. */
class HeaderHolder(v: View) : Holder(v) {
    val dateView: TextView = v.findViewById<TextView>(R.id.date)
    val summaryView: TextView = v.findViewById<TextView>(R.id.summary)

    var summary: String? = null
        set(value) {
            summaryView.text = value
            summaryView.visibility = if (value != null) View.VISIBLE else View.GONE
        }
}

/** An adapter for [ChangelogItem] and [ChangelogHeader]. Nothing special, except that
 * it will create two types of holders, depending on the data type. */
class ChangelogAdapter(private val list: List<ChangelogItem>) : RecyclerView.Adapter<Holder>() {

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int =
            if (list[position] is ChangelogHeader) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = when {
        viewType > 0 -> HeaderHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell_header, parent, false))

        else -> Holder(LayoutInflater.from(parent.context)
                .inflate(R.layout.changelog_cell, parent, false))
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.textview.text = item.text

        if (holder is HeaderHolder) {
            val header = item as ChangelogHeader
            holder.summary = header.summary
            holder.dateView.text = header.date
        }

    }
}