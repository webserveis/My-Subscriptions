package com.webserveis.mysubscriptions.preferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.webserveis.mysubscriptions.R

class GeneralPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_general, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchDarkMode: SwitchPreferenceCompat? = findPreference("pref_dark_mode")

        // Switch preference change listener
        switchDarkMode?.setOnPreferenceChangeListener { _, _ ->
            (activity as SettingsActivity).needRecreate = true
            true
        }

    }
}