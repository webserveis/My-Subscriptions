package com.webserveis.mysubscriptions.repositories

import androidx.lifecycle.LiveData
import com.webserveis.mysubscriptions.common.SingletonHolder
import com.webserveis.mysubscriptions.database.SubscriptionsDao
import com.webserveis.mysubscriptions.models.SubscriptionModel
import com.webserveis.mysubscriptions.models.toAlbumModel
import com.webserveis.mysubscriptions.models.toAlbumModelDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubscriptionsRepository(private val subscriptionsDao: SubscriptionsDao) : SubscriptionsRepositoryImp {

    companion object : SingletonHolder<SubscriptionsRepository, SubscriptionsDao>(::SubscriptionsRepository) {
        private val TAG = SubscriptionsRepository::class.java.simpleName
    }


    override suspend fun getAllSubscriptions(): List<SubscriptionModel> {
        val result = subscriptionsDao.getSubscriptions()
        return result.map { it.toAlbumModel() }
    }

    override fun getSubscriptionsCount(): LiveData<Int> {
        return subscriptionsDao.getSubscriptionsCount()
    }

    override fun getSubscriptionById(id: String): SubscriptionModel? {
        val result = subscriptionsDao.getSubscriptionById(id)
        return result?.toAlbumModel()
    }

    override suspend fun addSubscription(entry: SubscriptionModel) = withContext(Dispatchers.IO) {
        subscriptionsDao.add(entry.toAlbumModelDB())
    }

    override suspend fun updateSubscription(entry: SubscriptionModel) = withContext(Dispatchers.IO) {
        subscriptionsDao.update(entry.toAlbumModelDB())
    }

    override suspend fun deleteSubscriptionById(id: String): Int {
        val entry = subscriptionsDao.getSubscriptionById(id)
        entry?.let {
            return subscriptionsDao.delete(it)
        }
        
        return 0
    }

}