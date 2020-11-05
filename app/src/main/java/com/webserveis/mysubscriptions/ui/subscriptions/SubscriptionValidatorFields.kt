package com.webserveis.mysubscriptions.ui.subscriptions

import android.content.Context
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.ValidatorFieldHelper
import java.text.SimpleDateFormat
import java.util.*

class SubscriptionValidatorFields(private val context: Context) : ValidatorFieldHelper() {

    companion object {
        const val FIELD_NAME = "FIELD_NAME"
        const val FIELD_NEXT_PAYMENT = "FIELD_NEXT_PAYMENT"
    }

    init {
        addAssertion(FIELD_NAME)
        addAssertion(FIELD_NEXT_PAYMENT)
    }

    fun checkName(s: String): AssertionItem? {
        var isValid = true
        var errorMsg: String? = null
        if (s.isEmpty()) {
            isValid = false
            errorMsg = context.getString(R.string.error_field_required)
        }

        setAssertion(FIELD_NAME, AssertionItem(isValid, errorMsg))
        return getAssertion(FIELD_NAME)
    }

    fun checkNextPayment(d1: Date, d2: Date): AssertionItem? {
        val isValid: Boolean
        var errorMsg: String? = null
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val diff = sdf.format(d1).compareTo(sdf.format(d2))

        when {
            diff > 0 -> {
                isValid = true
            }
            diff < 0 -> {
                isValid = false
                errorMsg = context.getString(R.string.error_field_next_payment)
            }
            else -> {
                isValid = false
                errorMsg = context.getString(R.string.error_field_next_payment)

            }
        }

        setAssertion(FIELD_NEXT_PAYMENT, AssertionItem(isValid, errorMsg))
        return getAssertion(FIELD_NEXT_PAYMENT)

    }

}