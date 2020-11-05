package com.webserveis.mysubscriptions.common

import android.text.InputFilter
import android.text.Spanned

/*
https://androidpedia.net/en/knowledge-base/53758285/how-to-set-input-type-and-format-in-edittext-using-kotlin-
https://stacktips.com/tutorials/android/restrict-edittext-min-and-max-value-input-range-using-android-inputfilter
 */
class InputFilterMinMax(private val min: Float, private val max: Float) : InputFilter {

    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        try {
            val input = (dest.subSequence(0, dstart).toString() + source + dest.subSequence(dend, dest.length)).toFloat()
            if (isInRange(min, max, input)) return null
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isInRange(a: Float, b: Float, c: Float): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}