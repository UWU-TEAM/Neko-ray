package com.neko.v2ray.nekonet


import java.text.DateFormat
import java.util.*

object Interval {


    private var parser: DateFormat =
        DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault())

    val today: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis

            return TimeInterval(start, end, parser.format(calendar.time))
        }

    val yesterday: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DATE, -1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis
            return TimeInterval(start, end, parser.format(calendar.time))
        }

    // This will return the last 7 days including today
    val lastWeekDaily: List<TimeInterval>
        get() {
            val calendar = Calendar.getInstance()

            calendar.add(Calendar.DATE, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis
            calendar.add(Calendar.DATE, -1)
            val intervals = mutableListOf<TimeInterval>()
            for (i in 1..7) {
                calendar.add(Calendar.DATE, -1)
                intervals.add(
                    TimeInterval(
                        start - (i * 86400000),
                        end - (i * 86400000), //i * 24 * 60 * 60 * 1000
                        parser.format(calendar.time)
                    )
                )
            }

            return intervals
        }

    //this will return the last 30 days data with date
    val lastMonthDaily: List<TimeInterval>
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 1)
            val end = calendar.timeInMillis
            calendar.add(Calendar.DATE, -1)
            val intervals = mutableListOf<TimeInterval>()
            for (i in 1..30) {
                calendar.add(Calendar.DATE, -1)
                intervals.add(
                    TimeInterval(
                        start - (i * 86400000),
                        end - (i * 86400000),
                        parser.format(calendar.time)
                    )
                )
            }

            return intervals
        }

    val last7days: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            val end = calendar.timeInMillis

            calendar.add(Calendar.DATE, -7)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            return TimeInterval(start, end, parser.format(calendar.time))
        }

    val last30days: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.AM_PM, Calendar.AM)
            val end = calendar.timeInMillis

            calendar.add(Calendar.DATE, -30)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            return TimeInterval(start, end, parser.format(calendar.time))
        }

    val week: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.DATE, 6)
            val end = calendar.timeInMillis

            return TimeInterval(start, end, parser.format(calendar.time))
        }

    val month: TimeInterval
        get() {
            val calendar = Calendar.getInstance()

            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.clear(Calendar.MINUTE)
            calendar.clear(Calendar.SECOND)
            calendar.clear(Calendar.MILLISECOND)
            val start = calendar.timeInMillis

            calendar.add(Calendar.MONTH, 1)
            val end = calendar.timeInMillis

            return TimeInterval(start, end, parser.format(calendar.time))
        }

    fun monthlyPlan(startDay: Int): TimeInterval {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_MONTH, startDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        val start = calendar.timeInMillis

        calendar.add(Calendar.MONTH, 1)
        val end = calendar.timeInMillis

        return TimeInterval(start, end, parser.format(calendar.time))
    }

    fun weeklyPlan(startDay: Int): TimeInterval {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, startDay)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.clear(Calendar.MINUTE)
        calendar.clear(Calendar.SECOND)
        calendar.clear(Calendar.MILLISECOND)
        val start = calendar.timeInMillis

        calendar.add(Calendar.DATE, 6)
        val end = calendar.timeInMillis

        return TimeInterval(start, end, parser.format(calendar.time))
    }
}