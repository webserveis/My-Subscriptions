package com.webserveis.mysubscriptions.ui.subscriptions

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.AdMobHelper
import com.webserveis.mysubscriptions.common.replaceFragment
import com.webserveis.mysubscriptions.common.toast
import kotlinx.android.synthetic.main.activity_subscription_detail.*
import kotlinx.android.synthetic.main.app_bar_main.toolbar

class SubscriptionDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //val userId = intent.getStringExtra(ARG_ITEM_ID)
        //requireNotNull(userId) { "no user_id provided in Intent extras" }

        if (savedInstanceState == null) {

            //val userId = intent.getStringExtra(INTENT_USER_ID)

            val fragment : Fragment = when (intent.action) {
                Intent.ACTION_VIEW -> {
                    SubscriptionsViewFragment().apply { arguments = intent.extras }
                }
                Intent.ACTION_EDIT -> {
                    SubscriptionsEditFragment().apply { arguments = intent.extras }
                }
                Intent.ACTION_INSERT -> {
                    SubscriptionsEditFragment()
                }
                else -> SubscriptionsViewFragment()
            }
            replaceFragment(fragment, R.id.frame_master_and_detail)

        }

        /*
        ADMOB IMPLEMENTATION
         */
        AdMobHelper(this, adView)

    }

    override fun onSupportNavigateUp(): Boolean {

        val fm: FragmentManager = supportFragmentManager
        if (fm.backStackEntryCount > 0) {
            fm.popBackStack()
        } else {
            onBackPressed()
        }
        return super.onSupportNavigateUp()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    companion object {
        private val TAG = SubscriptionDetailActivity::class.java.simpleName
    }

}