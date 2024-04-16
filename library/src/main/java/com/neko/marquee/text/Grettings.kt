package com.neko.marquee.text

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.Calendar
import com.neko.R

class Grettings : AppCompatTextView {

    constructor(context: Context) : super(context) {
        greeting()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        greeting()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        greeting()
    }

    override fun isFocused(): Boolean {
        return true
    }

    private fun greeting() {
        val calendar = Calendar.getInstance()
        val timeOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val language = resources.configuration.locales.get(0).language

        val sb = StringBuilder()
        when (language) {
            "in" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 Selamat Pagi...")
                    timeOfDay in 9..15 -> sb.append("⛅ Selamat Siang...")
                    timeOfDay in 16..20 -> sb.append("🌥️ Selamat Sore...")
                    timeOfDay in 21..23 -> sb.append("🌙 Selamat Malam...")
                    else -> sb.append("💤 Waktunya Tidur...")
                }
            }
            "ja" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 おはよう...")
                    timeOfDay in 9..15 -> sb.append("⛅ こんにちは...")
                    timeOfDay in 16..20 -> sb.append("🌥️ こんばんは...")
                    timeOfDay in 21..23 -> sb.append("🌙 おやすみ...")
                    else -> sb.append("💤 寝る時間だよ...")
                }
            }
            else -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 Good Morning...")
                    timeOfDay in 9..15 -> sb.append("⛅ Good Afternoon...")
                    timeOfDay in 16..20 -> sb.append("🌥️ Good Evening...")
                    timeOfDay in 21..23 -> sb.append("🌙 Good Night...")
                    else -> sb.append("💤 It's time to go to sleep...")
                }
            }
        }
        text = sb
    }
}
