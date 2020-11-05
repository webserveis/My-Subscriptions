package com.webserveis.mysubscriptions.usecases

import com.webserveis.mysubscriptions.models.SubscriptionModel

sealed class SubscriptionsListState {
    object FirstLoading : SubscriptionsListState()
    object Loading : SubscriptionsListState()
    object Empty : SubscriptionsListState()
    data class Success(val data: List<SubscriptionModel>) : SubscriptionsListState()
    data class Failure(val exception: String?) : SubscriptionsListState()
}