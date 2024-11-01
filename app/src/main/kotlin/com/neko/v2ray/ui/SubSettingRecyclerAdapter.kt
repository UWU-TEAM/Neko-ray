package com.neko.v2ray.ui

import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.recyclerview.widget.RecyclerView
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ItemQrcodeBinding
import com.neko.v2ray.databinding.ItemRecyclerSubSettingBinding
import com.neko.v2ray.extension.toast
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.handler.SettingsManager
import com.neko.v2ray.helper.ItemTouchHelperAdapter
import com.neko.v2ray.helper.ItemTouchHelperViewHolder
import com.neko.v2ray.util.QRCodeDecoder
import com.neko.v2ray.util.Utils

class SubSettingRecyclerAdapter(val activity: SubSettingActivity) : RecyclerView.Adapter<SubSettingRecyclerAdapter.MainViewHolder>(), ItemTouchHelperAdapter {

    private var mActivity: SubSettingActivity = activity

    private val share_method: Array<out String> by lazy {
        mActivity.resources.getStringArray(R.array.share_sub_method)
    }

    override fun getItemCount() = mActivity.subscriptions.size

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val subId = mActivity.subscriptions[position].first
        val subItem = mActivity.subscriptions[position].second
        holder.itemSubSettingBinding.tvName.text = subItem.remarks
        holder.itemSubSettingBinding.tvUrl.text = subItem.url
        holder.itemSubSettingBinding.chkEnable.isChecked = subItem.enabled
        holder.itemView.setBackgroundColor(Color.TRANSPARENT)

        holder.itemSubSettingBinding.layoutEdit.setOnClickListener {
            mActivity.startActivity(
                Intent(mActivity, SubEditActivity::class.java)
                    .putExtra("subId", subId)
            )
        }

        holder.itemSubSettingBinding.chkEnable.setOnCheckedChangeListener { it, isChecked ->
            if (!it.isPressed) return@setOnCheckedChangeListener
            subItem.enabled = isChecked
            MmkvManager.encodeSubscription(subId, subItem)

        }

        if (TextUtils.isEmpty(subItem.url)) {
            holder.itemSubSettingBinding.layoutShare.visibility = View.INVISIBLE
        } else {
            holder.itemSubSettingBinding.layoutShare.setOnClickListener {
                MaterialAlertDialogBuilder(mActivity)
                    .setItems(share_method.asList().toTypedArray()) { _, i ->
                        try {
                            when (i) {
                                0 -> {
                                    val ivBinding =
                                        ItemQrcodeBinding.inflate(LayoutInflater.from(mActivity))
                                    ivBinding.ivQcode.setImageBitmap(
                                        QRCodeDecoder.createQRCode(
                                            subItem.url

                                        )
                                    )
                                    MaterialAlertDialogBuilder(mActivity).setView(ivBinding.root).show()
                                }

                                1 -> {
                                    Utils.setClipboard(mActivity, subItem.url)
                                }

                                else -> mActivity.toast("else")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ItemRecyclerSubSettingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class MainViewHolder(val itemSubSettingBinding: ItemRecyclerSubSettingBinding) :
        BaseViewHolder(itemSubSettingBinding.root), ItemTouchHelperViewHolder

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onItemSelected() {
            itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        SettingsManager.swapSubscriptions(fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemMoveCompleted() {
        mActivity.refreshData()
    }

    override fun onItemDismiss(position: Int) {
    }
}
