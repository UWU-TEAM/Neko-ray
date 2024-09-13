package com.neko.uwu

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.neko.v2ray.R
import com.neko.v2ray.ui.MainActivity
import com.google.android.material.card.MaterialCardView

class AdapterDatabase(
    private val ctx: Context,
    private val arrID: ArrayList<*>,
    private val arrName: ArrayList<*>,
    private val arrUsername: ArrayList<*>,
    private val arrEmail: ArrayList<*>,
    private val arrAge: ArrayList<*>,
    private val arrHobi: ArrayList<*>,
    private val arrTgl: ArrayList<*>
) : RecyclerView.Adapter<AdapterDatabase.ViewHolderDatabase>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDatabase {
        val view = LayoutInflater.from(ctx).inflate(R.layout.card_item, parent, false)
        return ViewHolderDatabase(view)
    }

    override fun onBindViewHolder(holder: ViewHolderDatabase, position: Int) {
        val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val randomString: String = List(15) { alphabet.random() }.joinToString("")

        holder.tvID.text = arrID[position].toString()
        holder.uwuID.text = randomString
        holder.tvName.text = arrName[position].toString()
        holder.tvUsername.text = arrUsername[position].toString()
        holder.tvEmail.text = arrEmail[position].toString()
        holder.tvAge.text = arrAge[position].toString()
        holder.tvHobi.text = arrHobi[position].toString()
        holder.tvTgl.text = arrTgl[position].toString()
    }

    override fun getItemCount(): Int = arrName.size

    inner class ViewHolderDatabase(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID: TextView = itemView.findViewById(R.id.tv_id)
        val uwuID: TextView = itemView.findViewById(R.id.uwu_id)
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvUsername: TextView = itemView.findViewById(R.id.tv_username)
        val tvEmail: TextView = itemView.findViewById(R.id.tv_email)
        val tvAge: TextView = itemView.findViewById(R.id.tv_age)
        val tvHobi: TextView = itemView.findViewById(R.id.tv_hobi)
        val tvTgl: TextView = itemView.findViewById(R.id.tv_tgl)
        val cvDatabase: MaterialCardView = itemView.findViewById(R.id.cv_database)
    }
}

