package com.webserveis.mysubscriptions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.webserveis.mysubscriptions.database.SubscriptionsDatabase
import com.webserveis.mysubscriptions.repositories.SubscriptionsRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubscriptionsRepository
    val subscriptionsCount: LiveData<Int>

    init {
        val mDao = SubscriptionsDatabase.getDatabase(application).subscriptionsDao()
        repository = SubscriptionsRepository(mDao)
        subscriptionsCount = repository.getSubscriptionsCount()
    }


}