package com.webserveis.mysubscriptions.common

import kotlinx.coroutines.*

/*
https://medium.com/@cesarmcferreira/how-to-use-the-new-android-viewmodelscope-in-clean-architecture-2a33aac959ee
 */
abstract class BaseUseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>
    private var job: Job = Job()

    open operator fun invoke(
        scope: CoroutineScope,
        params: Params,
        jobCancel: Boolean = false,
        onResult: (Either<Failure, Type>) -> Unit = {}
    ) {
        val backgroundJob = scope.async(Dispatchers.IO) { run(params) }
        if (jobCancel && job.isActive) job.cancel()

        job = scope.launch(Dispatchers.Main) {
            onResult(backgroundJob.await())
        }
    }

}