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
            "ar" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 صباح الخير...")
                    timeOfDay in 9..15 -> sb.append("⛅ مساء الخير...")
                    timeOfDay in 16..20 -> sb.append("🌥️ مساء الخير...")
                    timeOfDay in 21..23 -> sb.append("🌙 طاب مساؤك...")
                    else -> sb.append("💤 حان الوقت للذهاب الى النوم...")
                }
            }
            "fa" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 صباح الخير...")
                    timeOfDay in 9..15 -> sb.append("⛅ مساء الخير...")
                    timeOfDay in 16..20 -> sb.append("🌥️ مساء الخير...")
                    timeOfDay in 21..23 -> sb.append("🌙 طاب مساؤك...")
                    else -> sb.append("💤 حان الوقت للذهاب الى النوم...")
                }
            }
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
            "ru" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 Доброе утро...")
                    timeOfDay in 9..15 -> sb.append("⛅ Добрый день...")
                    timeOfDay in 16..20 -> sb.append("🌥️ Добрый день...")
                    timeOfDay in 21..23 -> sb.append("🌙 Спокойной ночи...")
                    else -> sb.append("💤 пора идти спать...")
                }
            }
            "su" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 Wilujeng énjing...")
                    timeOfDay in 9..15 -> sb.append("⛅ Wilujeng sonten...")
                    timeOfDay in 16..20 -> sb.append("🌥️ Wilujeng sonten...")
                    timeOfDay in 21..23 -> sb.append("🌙 Wilujeng wengi...")
                    else -> sb.append("💤 Wanci saré...")
                }
            }
            "vi" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 Chào buổi sáng...")
                    timeOfDay in 9..15 -> sb.append("⛅ Chào buổi chiều...")
                    timeOfDay in 16..20 -> sb.append("🌥️ Chào buổi chiều...")
                    timeOfDay in 21..23 -> sb.append("🌙 Chúc ngủ ngon...")
                    else -> sb.append("💤 Đã đến giờ đi ngủ...")
                }
            }
            "zh", "CN" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 早上好...")
                    timeOfDay in 9..15 -> sb.append("⛅ 下午好...")
                    timeOfDay in 16..20 -> sb.append("🌥️ 下午好...")
                    timeOfDay in 21..23 -> sb.append("🌙 晚安...")
                    else -> sb.append("💤 是时候去睡觉了...")
                }
            }
            "zh", "TW" -> {
                when {
                    timeOfDay in 4..8 -> sb.append("🌤 早安...")
                    timeOfDay in 9..15 -> sb.append("⛅ 午安...")
                    timeOfDay in 16..20 -> sb.append("🌥️ 午安...")
                    timeOfDay in 21..23 -> sb.append("🌙 晚安...")
                    else -> sb.append("💤 是時候去睡覺了...")
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
