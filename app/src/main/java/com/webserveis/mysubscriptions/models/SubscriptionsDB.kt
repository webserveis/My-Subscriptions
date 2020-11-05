package com.webserveis.mysubscriptions.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URL
import java.util.*

@Entity(tableName = "subscriptions_table")
data class SubscriptionsDB(
    @PrimaryKey val uid: String,
    var name: String?,
    var description: String?,
    var color: Int,
    var price: Float,
    var currencyCode: String,
    @SubscriptionStatusBill.Companion.Status
    var renewalStatus: Int = SubscriptionStatusBill.AUTO_RENEW,
    var circleValue: Int = 1,
    @SubscriptionPeriodBill.Companion.PeriodBill
    var circleUnits: Int = SubscriptionPeriodBill.MONTHS,
    var firstPayment: Date,
    var previousPayment: Date,
    var nextPayment: Date,
    var webSite : String?,
    var cancelSub : String?,
    var renewSub : String?,
    var isDeleted : Boolean = false
)