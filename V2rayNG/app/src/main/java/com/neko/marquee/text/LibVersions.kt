package com.neko.marquee.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.neko.v2ray.handler.SpeedtestManager

class LibVersions(context: Context) : AppCompatTextView(context) {
    private var memekVersion: String? = null

    private fun JupokInfoSlur() {
        val sb = StringBuilder()
        sb.append(SpeedtestManager.getLibVersion())
        text = sb.toString()
    }

    override fun isFocused(): Boolean {
        return true
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context) {
        JupokInfoSlur()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs) {
        JupokInfoSlur()
    }

    init {
        JupokInfoSlur()
    }
}

