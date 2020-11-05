package com.webserveis.mysubscriptions.usecases

import android.text.format.DateUtils
import com.webserveis.mysubscriptions.common.BaseUseCase
import com.webserveis.mysubscriptions.common.Either
import com.webserveis.mysubscriptions.common.Failure
import com.webserveis.mysubscriptions.common.MySubsUtils
import com.webserveis.mysubscriptions.models.SubscriptionModel
import com.webserveis.mysubscriptions.models.SubscriptionStatusBill
import com.webserveis.mysubscriptions.repositories.SubscriptionsRepository
import kotlinx.coroutines.delay
import java.util.*

class SubscriptionsListUseCase(
    private val repository: SubscriptionsRepository
) : BaseUseCase<List<SubscriptionModel>, SubscriptionsListUseCase.Params>() {

    override suspend fun run(params: Params): Either<Failure, List<SubscriptionModel>> {

        val data = repository.getAllSubscriptions()

        data.map {
            //recalculate nex payment if nextpaynment is before today
            val dateNow = Calendar.getInstance().time
            when (it.status) {
                SubscriptionStatusBill.AUTO_RENEW, SubscriptionStatusBill.MANUAL_RENEW -> {
                    if (it.nextPayment.before(dateNow)) {
                        while (it.nextPayment.before(dateNow) && !DateUtils.isToday(it.nextPayment.time)) {
                            it.nextPayment = MySubsUtils.getNextPayment(it.nextPayment, it.circleUnits, it.circleValue)
                        }
                        it.previousPayment = MySubsUtils.getPreviousPayment(it.nextPayment, it.circleUnits, it.circleValue)
                        repository.updateSubscription(it)
                    }
                }
                SubscriptionStatusBill.NOT_RENEW, SubscriptionStatusBill.ONE_TIME -> {
                    when (it.isDeleted) {
                        true -> {
                            if (it.nextPayment.after(dateNow) || MySubsUtils.isToday(it.nextPayment.time)) {
                                it.isDeleted = false
                                repository.updateSubscription(it)
                            }
                        }
                        false -> {
                            if (it.nextPayment.before(dateNow) && !MySubsUtils.isToday(it.nextPayment.time)) {
                                it.isDeleted = true
                                repository.updateSubscription(it)
                            }
                        }
                    }
                }
            }

        }
        return Either.Right(data)

    }


    data class Params(val nothing: String? = null)

    data class SubscriptionsUseCaseFailure(val error: Exception) : Failure.FeatureFailure(error)


}