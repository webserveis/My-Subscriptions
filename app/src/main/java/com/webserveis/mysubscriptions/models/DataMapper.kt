package com.webserveis.mysubscriptions.models

fun SubscriptionsDB.toAlbumModel(): SubscriptionModel {
    return SubscriptionModel(
        uid,
        name,
        description,
        color,
        price,
        currencyCode,
        renewalStatus,
        circleValue,
        circleUnits,
        firstPayment,
        previousPayment,
        nextPayment,
        isDeleted
    )
}


fun SubscriptionModel.toAlbumModelDB(): SubscriptionsDB {
    return SubscriptionsDB(
        uid,
        name,
        description,
        color,
        price,
        currencyCode,
        status,
        circleValue,
        circleUnits,
        firstPayment,
        previousPayment,
        nextPayment,
        null,
        null,
        null,
        isDeleted
    )
}