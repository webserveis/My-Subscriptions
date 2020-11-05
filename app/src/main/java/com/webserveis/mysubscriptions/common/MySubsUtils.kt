package com.webserveis.mysubscriptions.common

import android.text.format.DateUtils
import com.webserveis.mysubscriptions.models.SubscriptionPeriodBill
import java.util.*

object MySubsUtils {

    fun isToday(whenInMillis: Long): Boolean {
        return DateUtils.isToday(whenInMillis)
    }

    fun isTomorrow(whenInMillis: Long): Boolean {
        return DateUtils.isToday(whenInMillis - DateUtils.DAY_IN_MILLIS)
    }

    fun isYesterday(whenInMillis: Long): Boolean {
        return DateUtils.isToday(whenInMillis + DateUtils.DAY_IN_MILLIS)
    }

    fun getNextPayment(d1: Date, @SubscriptionPeriodBill.Companion.PeriodBill units: Int, value: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = d1.time

        when (units) {
            SubscriptionPeriodBill.DAYS -> cal.add(Calendar.DATE, value)
            SubscriptionPeriodBill.WEEKS -> cal.add(Calendar.DATE, value * 7)
            SubscriptionPeriodBill.MONTHS -> cal.add(Calendar.MONTH, value)
            SubscriptionPeriodBill.YEARS -> cal.add(Calendar.YEAR, value)
        }
        return cal.time
    }

    fun getPreviousPayment(d1: Date, @SubscriptionPeriodBill.Companion.PeriodBill units: Int, value: Int): Date {
        val cal = Calendar.getInstance()
        cal.timeInMillis = d1.time

        when (units) {
            SubscriptionPeriodBill.DAYS -> cal.add(Calendar.DATE, value * -1)
            SubscriptionPeriodBill.WEEKS -> cal.add(Calendar.DATE, value * -7)
            SubscriptionPeriodBill.MONTHS -> cal.add(Calendar.MONTH, value * -1)
            SubscriptionPeriodBill.YEARS -> cal.add(Calendar.YEAR, value * -1)
        }
        return cal.time
    }


    fun yearsBetween(a: Date, b: Date?): Int {
        var b = b
        val cal = Calendar.getInstance()
        if (a.before(b)) {
            cal.time = a
        } else {
            cal.time = b
            b = a
        }
        var c = 0
        while (cal.time.before(b)) {
            cal.add(Calendar.YEAR, 1)
            c++
        }
        return c - 1
    }

    fun monthsBetween(a: Date, b: Date?): Int {
        var b = b
        val cal = Calendar.getInstance()
        if (a.before(b)) {
            cal.time = a
        } else {
            cal.time = b
            b = a
        }
        var c = 0
        while (cal.time.before(b)) {
            cal.add(Calendar.MONTH, 1)
            c++
        }
        return c - 1
    }

    fun daysBetween(startDate: Date, endDate: Date): Int {
        val sDate: Calendar = getDatePart(startDate)
        val eDate: Calendar = getDatePart(endDate)
        var daysBetween = 0
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }

    private fun getDatePart(date: Date): Calendar {
        val cal = Calendar.getInstance() // get calendar instance
        cal.time = date
        cal[Calendar.HOUR_OF_DAY] = 0 // set hour to midnight
        cal[Calendar.MINUTE] = 0 // set minute in hour
        cal[Calendar.SECOND] = 0 // set second in minute
        cal[Calendar.MILLISECOND] = 0 // set millisecond in second
        return cal // return the date part
    }


}