package com.webserveis.mysubscriptions.preferences

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.webserveis.mysubscriptions.R
import kotlinx.android.synthetic.main.preference_about_header.view.*

/*
https://github.com/TwidereProject/Twidere-Android/blob/master/twidere/src/main/kotlin/org/mariotaku/twidere/preference/ColorPickerPreference.kt
 https://stackoverflow.com/questions/53834600/custom-preference-android-kotlin
 */

class PreferenceAboutHeader @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {

    init {
        widgetLayoutResource = R.layout.preference_about_header
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        layoutResource = R.layout.preference_about_header
        with(holder.itemView) {
            about_app_icon.setImageResource(R.mipmap.ic_launcher)
        }
    }


}
