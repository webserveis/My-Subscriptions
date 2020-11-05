package com.webserveis.mysubscriptions.usecases

import com.webserveis.mysubscriptions.models.SubscriptionModel

sealed class SubscriptionState {
    object Loading : SubscriptionState()
    object Empty : SubscriptionState()
    data class Success(val data: SubscriptionModel) : SubscriptionState()
    data class Failure(val exception: String?) : SubscriptionState()
}