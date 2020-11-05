package com.webserveis.mysubscriptions.models

import androidx.annotation.IntDef

class SubscriptionStatusBill {
    companion object {
        @IntDef(AUTO_RENEW, MANUAL_RENEW, NOT_RENEW, ONE_TIME)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Status

        const val AUTO_RENEW = 0
        const val MANUAL_RENEW = 1
        const val NOT_RENEW = 2
        const val ONE_TIME = 3

    }
}