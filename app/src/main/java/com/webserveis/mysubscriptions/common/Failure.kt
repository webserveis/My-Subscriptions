package com.webserveis.mysubscriptions.common

sealed class Failure(val exception: Exception = Exception("Failure")) {
    object None : Failure()
    object NetworkConnection : Failure()
    object ServerError : Failure()
    object NoData: Failure()

    /** * Extend this class for feature specific failures. open not abstract*/
    abstract class FeatureFailure(featureException: Exception = Exception("Feature failure")) : Failure(featureException)

    override fun equals(other: Any?): Boolean {
        return other is Failure
    }
}