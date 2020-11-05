package com.webserveis.mysubscriptions.repositories

import androidx.lifecycle.LiveData
import com.webserveis.mysubscriptions.models.SubscriptionModel

interface SubscriptionsRepositoryImp {

    suspend fun getAllSubscriptions(): List<SubscriptionModel>

    fun getSubscriptionsCount(): LiveData<Int>

    fun getSubscriptionById(id: String): SubscriptionModel?

    suspend fun addSubscription(entry: SubscriptionModel)

    suspend fun updateSubscription(entry: SubscriptionModel)

    suspend fun deleteSubscriptionById(id: String) : Int
}