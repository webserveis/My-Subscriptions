package com.webserveis.mysubscriptions.preferences

import android.os.Bundle
import android.view.View
import androidx.core.app.ShareCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.webserveis.mysubscriptions.BuildConfig
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.toast
import java.util.*


class AboutPreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_about, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.findPreference<Preference>("pref_app_name")?.summary = BuildConfig.VERSION_NAME

        val str = String.format(getString(R.string.pref_about_copyright_summary), Calendar.getInstance().get(Calendar.YEAR))
        this.findPreference<Preference>("pref_copyright")?.title = str

        // feedback preference click listener
        val myPref = this.findPreference<Preference>("pref_send_feedback")
        myPref?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ShareCompat.IntentBuilder.from(requireActivity())
                .setType("text/plain")//message/rfc822
                .addEmailTo("webserveis@gmail.com")
                .setSubject("Feedback: " + getText(R.string.app_name))
                .setText("Feedback content") //.setHtmlText(body) //If you are using HTML in your body text
                .setChooserTitle(R.string.pref_about_send_feedback_header)
                .startChooser()
            true
        }


        val easterEgg = this.findPreference<Preference>("pref_copyright")

        easterEgg?.onPreferenceClickListener = EasterEggClickListener(
            callback = object : EasterEggClickListener.Callback {
                override fun easterEggClicked() {
                    context.toast("¯\\_(ツ)_/¯")
                }
            }
        )


    }
}
