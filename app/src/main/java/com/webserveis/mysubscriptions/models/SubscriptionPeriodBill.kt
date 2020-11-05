package com.webserveis.mysubscriptions.models

import androidx.annotation.IntDef

class SubscriptionPeriodBill {
    companion object {
        @IntDef(DAYS, WEEKS, MONTHS, YEARS)
        @Retention(AnnotationRetention.SOURCE)
        annotation class PeriodBill

        const val DAYS = 0
        const val WEEKS = 1
        const val MONTHS = 2
        const val YEARS = 3

    }
}