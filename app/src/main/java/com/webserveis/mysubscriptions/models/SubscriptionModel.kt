package com.webserveis.mysubscriptions.models

import java.util.*

data class SubscriptionModel(
    val uid: String = UUID.randomUUID().toString(),
    var name: String?,
    var description: String?,
    var color: Int,
    var price: Float,
    var currencyCode: String,

    @SubscriptionStatusBill.Companion.Status
    var status: Int = SubscriptionStatusBill.AUTO_RENEW,
    var circleValue: Int = 1,

    @SubscriptionPeriodBill.Companion.PeriodBill
    var circleUnits: Int = SubscriptionPeriodBill.MONTHS,
    var firstPayment: Date,
    var previousPayment: Date,
    var nextPayment: Date,
    var isDeleted: Boolean = false
)