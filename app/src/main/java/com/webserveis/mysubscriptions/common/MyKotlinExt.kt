package com.webserveis.mysubscriptions.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.RequiresPermission
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.webserveis.mysubscriptions.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/*
https://stackoverflow.com/questions/50617598/how-to-declare-startactivityforresult-in-one-line-in-kotlin
 */
fun Context?.toast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(it, text, duration).show() }
fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) = this?.let { Toast.makeText(it, textId, duration).show() }

inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T =
    this.apply {
        arguments = Bundle().apply(argsBuilder)
    }


fun View.slideUp(duration: Long = 250L) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f)
    animate.interpolator = AccelerateInterpolator()
    animate.duration = duration
    animate.fillAfter = true


    this.startAnimation(animate)
}

fun View.slideDown(duration: Long = 250L) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat())
    animate.interpolator = AccelerateInterpolator()
    animate.duration = duration
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun Boolean.toInt() = if (this) 1 else 0


fun TextInputEditText.onChange(cb: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            cb(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}

fun TextInputEditText.onChangeDebounce(duration: Long = 350L, cb: (String) -> Unit) {
    var lastStr = ""
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newStr = s.toString()
            if (newStr == lastStr)
                return
            lastStr = newStr
            GlobalScope.launch(Dispatchers.Main) {
                delay(duration)
                if (newStr != lastStr)
                    return@launch
                if (isAttachedToWindow) cb(s.toString())
            }
        }
    })
}

fun TextInputEditText.onDone(callback: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            callback.invoke()
        }
        false
    }
}

fun TextInputLayout.markRequired() {
    hint = "$hint *"
}

fun TextInputLayout.markRequiredInRed() {
    hint = buildSpannedString {
        append(hint)
        color(Color.RED) { append(" *") } // Mind the space prefix.
    }
}

@RequiresPermission(android.Manifest.permission.VIBRATE)
fun Context.vibrate(pattern: LongArray = longArrayOf(0, 150)) {
    val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator? ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createWaveform(pattern, VibrationEffect.DEFAULT_AMPLITUDE)
        )

    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(pattern, -1)
    }
}

fun @receiver:ColorInt Int.isDark(): Boolean = ColorUtils.calculateLuminance(this) < 0.5
fun @receiver:ColorInt Int.darken(ratio: Float = 0.2f): Int = ColorUtils.blendARGB(this, Color.BLACK, ratio)

fun Toolbar.setNavigationIconColor(@ColorInt color: Int) = navigationIcon?.mutate()?.let {
    it.setTint(color)
    this.navigationIcon = it
}


@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}


fun Context.hasTwoPanels(): Boolean {
    val res: Resources = resources
    return res.getBoolean(R.bool.two_panels)
}


fun getAttrColor(context: Context, @AttrRes attrId: Int): Int {
    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue.data
}

/*
    @ColorInt
    fun getAttrColor(context: Context, @AttrRes attrId: Int): Int {
        val typedValue = TypedValue()
        val theme = context.theme
        theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    fun getColorDrawable(context: Context, @ColorInt color: Int): Int {
        return ColorDrawable(color).color
    }

    fun getColorAttr2ColorDrawable(context:Context,@AttrRes attrId: Int) {
        return getColorDrawable(context,getAttrColor(context,attrId))
    }

 */
