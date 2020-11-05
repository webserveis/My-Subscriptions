package com.webserveis.mysubscriptions.preferences
import androidx.preference.Preference

class EasterEggClickListener(private val doubleClickTimeLimitMills: Long = 350L, private val callback: Callback) : Preference.OnPreferenceClickListener {
    private var lastClicked: Long = -1L
    private var count: Int = 0

    override fun onPreferenceClick(preference: Preference?): Boolean {
        lastClicked = when {
            lastClicked == -1L -> {
                System.currentTimeMillis()
            }
            isDoubleClicked() -> {
                count += 1
                if (count > 3) {
                    callback.easterEggClicked()
                    count = 0
                }
                -1L
            }
            else -> {
                count = 0
                System.currentTimeMillis()
            }
        }
        return true
    }

    private fun getTimeDiff(from: Long, to: Long): Long {
        return to - from
    }

    private fun isDoubleClicked(): Boolean {
        return getTimeDiff(lastClicked, System.currentTimeMillis()) <= doubleClickTimeLimitMills
    }

    interface Callback {
        fun easterEggClicked()
    }

}