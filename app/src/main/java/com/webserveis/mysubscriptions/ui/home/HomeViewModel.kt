package com.webserveis.mysubscriptions.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.webserveis.mysubscriptions.common.Failure
import com.webserveis.mysubscriptions.database.SubscriptionsDatabase
import com.webserveis.mysubscriptions.models.SubscriptionModel
import com.webserveis.mysubscriptions.repositories.SubscriptionsRepository
import com.webserveis.mysubscriptions.usecases.SubscriptionsListState
import com.webserveis.mysubscriptions.usecases.SubscriptionsListUseCase

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubscriptionsRepository
    private val useCase: SubscriptionsListUseCase
    val resultSate = MutableLiveData<SubscriptionsListState>().apply {
        this.value = SubscriptionsListState.Loading
    }

    init {
        val albumDao = SubscriptionsDatabase.getDatabase(application).subscriptionsDao()
        repository = SubscriptionsRepository(albumDao)
        useCase = SubscriptionsListUseCase(repository)
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    fun getSubscriptions() {
        if (resultSate.value != SubscriptionsListState.Loading) resultSate.value = SubscriptionsListState.Loading
        val params = SubscriptionsListUseCase.Params()
        useCase.invoke(viewModelScope, params) {
            it.fold(::handleError, ::handleSuccess)
        }

    }

    private fun handleError(failure: Failure) {
        Log.e(TAG, "ERROR : printStackTrace ")
        failure.exception.printStackTrace()
        resultSate.value = SubscriptionsListState.Failure(failure.exception.localizedMessage)
    }


    private fun handleSuccess(data: List<SubscriptionModel>) {
        Log.d(TAG, "handleSuccess: $data")
        if (data.isEmpty()) {
            resultSate.value = SubscriptionsListState.Empty
        } else {
            resultSate.value = SubscriptionsListState.Success(data)
        }


    }

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }
}