package app.webserveis.statelayout

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.annotation.LayoutRes

class StateLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        // Build-in states.
        const val STATE_CONTENT = 0
        const val STATE_LOADING = 1
        const val STATE_ERROR = 2
        const val STATE_EMPTY = 3
    }

    private var mAnimate: Boolean
    private var mAlphaAnimator: ObjectAnimator? = null
    private var mAnimDuration: Long

    private var state = STATE_CONTENT
    private var stateMap = HashMap<Int, View>()

    private var loadingRes = -1
    private var errorRes = -1
    private var emptyRes = -1


    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StateLayout)
        loadingRes = ta.getResourceId(R.styleable.StateLayout_view_loading, -1)
        errorRes = ta.getResourceId(R.styleable.StateLayout_view_error, -1)
        emptyRes = ta.getResourceId(R.styleable.StateLayout_view_empty, -1)
        mAnimate = ta.getBoolean(R.styleable.StateLayout_animate, false)
        mAnimDuration = ta.getInt(R.styleable.StateLayout_animDuration, 350).toLong()
        ta.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 1) throw IllegalArgumentException("You must have only one content view.")

        if (childCount == 1) {
            val contentView = getChildAt(0)
            stateMap[STATE_CONTENT] = contentView
        }
        if (loadingRes != -1) setViewForState(STATE_LOADING, loadingRes)
        if (errorRes != -1) setViewForState(STATE_ERROR, errorRes)
        if (emptyRes != -1) setViewForState(STATE_EMPTY, emptyRes)

    }

    fun setViewForState(state: Int, @LayoutRes res: Int) {
        val view = LayoutInflater.from(context).inflate(res, this, false)
        setViewForState(state, view)
    }

    private fun setViewForState(state: Int, view: View) {
        if (stateMap.containsKey(state)) {
            removeView(stateMap[state])
        }
        addView(view)
        view.visibility = View.GONE
        stateMap[state] = view
    }

    fun setState(state: Int, animate: Boolean = false) {
        if (this.state == state) return
        if (!stateMap.containsKey(state)) throw IllegalStateException("Invalid state: $state")

        for (key in stateMap.keys) {
            if (key == state) {
                stateMap[key]?.visibility = View.VISIBLE
                if (animate || mAnimate) execAlphaAnimation(stateMap[key])
            } else {
                stateMap[key]?.visibility = View.GONE
            }
            //stateMap[key]!!.visibility = if (key == state) View.VISIBLE else View.GONE
        }

        this.state = state
    }

    fun getView(state: Int): View? {
        return stateMap[state]
    }

    fun content(animate: Boolean = false) = setState(STATE_CONTENT, animate)
    fun loading(animate: Boolean = false) = setState(STATE_LOADING, animate)
    fun error(animate: Boolean = false) = setState(STATE_ERROR, animate)
    fun empty(animate: Boolean = false) = setState(STATE_EMPTY, animate)

    fun setAnimDuration(value: Long) {
        mAnimDuration = value
    }


    private fun clearTargetViewAnimation() {
        mAlphaAnimator?.let {
            mAlphaAnimator!!.cancel()
        }

    }

    private fun execAlphaAnimation(targetView: View?) {

        if (targetView == null) return
        mAlphaAnimator = ObjectAnimator.ofFloat(targetView, View.ALPHA, 0.0f, 1.0f).apply {
            this.interpolator = AccelerateInterpolator()
            this.duration = mAnimDuration
        }
        mAlphaAnimator?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        clearTargetViewAnimation()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return if (superState == null) {
            superState
        } else {
            SavedState(superState, state)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            setState(state.state)
        } else {
            super.onRestoreInstanceState(state)
        }
    }


    internal class SavedState : BaseSavedState {

        var state: Int

        constructor(superState: Parcelable, state: Int) : super(superState) {
            this.state = state
        }

        constructor(source: Parcel) : super(source) {
            state = source.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(state)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

}