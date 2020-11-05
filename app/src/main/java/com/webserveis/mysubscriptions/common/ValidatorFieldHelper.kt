package com.webserveis.mysubscriptions.common

abstract class ValidatorFieldHelper {

    interface ValidatorFieldsListener {
        fun onSuccessfulValidator()
        fun onErrorValidator(assertionList: HashMap<String, AssertionItem>)
    }

    data class AssertionItem(val isValid: Boolean, val error: String? = null)

    private var listener: ValidatorFieldsListener? = null

    open var isValid: Boolean = false

    private val assertionList: HashMap<String, AssertionItem> = hashMapOf()

    //Append new field
    fun addAssertion(key: String) {
        assertionList[key] = AssertionItem(false)
    }

    //Get a validation field
    fun getAssertion(key: String): AssertionItem? {
        return assertionList[key]
    }

    //Update a validation field
    fun setAssertion(key: String, value: AssertionItem) {
        assertionList[key] = value
    }

    //Run validate all fields
    fun validate() {
        isValid = false
        if (assertionList.size == 0) return
        isValid = true
        assertionList.forEach {
            if (!it.value.isValid) isValid = false
        }

        if (isValid) listener?.onSuccessfulValidator() else listener?.onErrorValidator(assertionList)

    }

    fun setOnValidateListener(listener: ValidatorFieldsListener) {
        this.listener = listener
    }

}