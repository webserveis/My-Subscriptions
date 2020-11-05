package com.webserveis.mysubscriptions.ui.subscriptions

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.webserveis.mysubscriptions.common.Failure
import com.webserveis.mysubscriptions.database.SubscriptionsDatabase
import com.webserveis.mysubscriptions.models.SubscriptionModel
import com.webserveis.mysubscriptions.models.SubscriptionPeriodBill
import com.webserveis.mysubscriptions.models.SubscriptionStatusBill
import com.webserveis.mysubscriptions.repositories.SubscriptionsRepository
import com.webserveis.mysubscriptions.usecases.SubscriptionState
import com.webserveis.mysubscriptions.usecases.SubscriptionUseCase
import com.webserveis.mysubscriptions.usecases.SubscriptionsListState
import com.webserveis.mysubscriptions.usecases.SubscriptionsListUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class SubscriptionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubscriptionsRepository
    private val listUseCase: SubscriptionsListUseCase
    val listResultSate = MutableLiveData<SubscriptionsListState>().apply {
        this.value = SubscriptionsListState.FirstLoading
    }
    private val useCase: SubscriptionUseCase
    val resultSate = MutableLiveData<SubscriptionState>().apply {
        this.value = SubscriptionState.Loading
    }

    //For Edit View
    var dataItem: SubscriptionModel

    init {
        val mDao = SubscriptionsDatabase.getDatabase(application).subscriptionsDao()
        repository = SubscriptionsRepository(mDao)
        listUseCase = SubscriptionsListUseCase(repository)
        useCase = SubscriptionUseCase(repository)

        dataItem = createNewSubscription()
    }

    fun getSubscriptionsList() {
        if (listResultSate.value != SubscriptionsListState.Loading) listResultSate.value = SubscriptionsListState.Loading
        val params = SubscriptionsListUseCase.Params()
        listUseCase.invoke(viewModelScope, params) {
            it.fold(::listHandleError, ::listHandleSuccess)
        }
    }

    fun getSubscriptionById(id: String?) {
        if (id == null) {
            resultSate.value = SubscriptionState.Empty
            return
        }
        if (resultSate.value != SubscriptionState.Loading) resultSate.value = SubscriptionState.Loading
        val params = SubscriptionUseCase.Params(id)
        useCase.invoke(viewModelScope, params) {
            it.fold(::handleError, ::handleSuccess)
        }

    }

    fun addSubscription(entry: SubscriptionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSubscription(entry)
        }
    }

    fun updateSubscription(entry: SubscriptionModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSubscription(entry)
        }
    }

    fun deleteSubscriptionById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubscriptionById(id)
        }
    }

    fun fetchSubscriptionNew() {
        viewModelScope.launch {
            if (resultSate.value != SubscriptionState.Loading) resultSate.value = SubscriptionState.Loading
            dataItem = createNewSubscription()
            resultSate.value = SubscriptionState.Success(dataItem)
        }
    }

    private fun listHandleError(failure: Failure) {
        Log.e(TAG, "ERROR : printStackTrace ")
        failure.exception.printStackTrace()
        listResultSate.value = SubscriptionsListState.Failure(failure.exception.localizedMessage)
    }

    private fun listHandleSuccess(data: List<SubscriptionModel>) {
        Log.d(TAG, "listHandleSuccess: $data")
        if (data.isEmpty()) {
            listResultSate.value = SubscriptionsListState.Empty
        } else {
            listResultSate.value = SubscriptionsListState.Success(data)
        }
    }

    private fun handleError(failure: Failure) {
        Log.e(TAG, "ERROR : printStackTrace ")
        failure.exception.printStackTrace()

        if (failure is Failure.NoData) {
            resultSate.value = SubscriptionState.Empty
        } else {
            resultSate.value = SubscriptionState.Failure(failure.exception.localizedMessage)
        }
    }

    private fun handleSuccess(data: List<SubscriptionModel>) {
        Log.d(TAG, "handleSuccess: $data")
        dataItem = data[0]
        resultSate.value = SubscriptionState.Success(data[0])
    }

    private fun createNewSubscription(): SubscriptionModel {
        //Create New Subscription item
        return SubscriptionModel(
            UUID.randomUUID().toString(),
            null,
            null,
            Color.parseColor("#f44336"),
            0F,
            Currency.getInstance(Locale.getDefault()).currencyCode,
            SubscriptionStatusBill.AUTO_RENEW,
            1,
            SubscriptionPeriodBill.MONTHS,
            Calendar.getInstance().time,
            Calendar.getInstance().time,
            Calendar.getInstance().also {
                it.add(Calendar.MONTH, 1)
            }.time,
            false
        )
    }

    companion object {
        private val TAG = SubscriptionsViewModel::class.java.simpleName
    }

}