package com.webserveis.mysubscriptions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.google.android.material.navigation.NavigationView
import com.webserveis.mysubscriptions.common.AdMobHelper
import com.webserveis.mysubscriptions.common.NavDrawerHelper
import com.webserveis.mysubscriptions.common.toast
import com.webserveis.mysubscriptions.preferences.SettingsActivity
import com.webserveis.mysubscriptions.ui.subscriptions.SubscriptionsNavFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.toolbar
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerLocker {

    private lateinit var fragmentStateManager: NavDrawerHelper
    private lateinit var drawerLayout: DrawerLayout

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "onCreate: ")

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val navHost: FrameLayout = findViewById(R.id.nav_host_fragment)
        fragmentStateManager = object : NavDrawerHelper(navHost, supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (position) {
                    //0 -> HomeFragment()
                    1 -> SubscriptionsNavFragment()
                    //2 -> SlideshowFragment()
                    else -> null
                }

            }

        }


        if (savedInstanceState == null) {
            navView.menu.performIdentifierAction(R.id.nav_subscriptions, 0)
            //onNavigationItemSelected(navView.menu.findItem(R.id.nav_home))
        }

        mainViewModel.subscriptionsCount.observe(this) {
            setMenuCounter(R.id.nav_subscriptions, it)
        }


        /*
        ADMob implementation
         */

        AdMobHelper(this@MainActivity, adView)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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



    private fun getNavPositionFromMenuItem(menuItem: MenuItem): Int {
        return when (menuItem.itemId) {
            //R.id.nav_home -> 0
            R.id.nav_subscriptions -> 1
            //R.id.nav_tools -> 2
            else -> -1
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            //R.id.nav_home,
            //R.id.nav_tools,
            R.id.nav_subscriptions -> {
                item.isChecked = true
                fragmentStateManager.changeFragment(getNavPositionFromMenuItem(item))
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
        }

        return true

    }

    private fun setMenuCounter(@IdRes itemId: Int, count: Int) {
        val view = nav_view.menu.findItem(itemId).actionView as TextView
        view.text = if (count > 0) count.toString() else null
    }


    fun getAllCurrencies(): MutableSet<Currency>? {
        return Currency.getAvailableCurrencies()
    }

    fun getCurrencyByCode(code: String): Currency? {
        return Currency.getInstance(code)
    }

    override fun setDrawerEnabled(enabled: Boolean) {

        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawerLayout.setDrawerLockMode(lockMode)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        toggle.isDrawerIndicatorEnabled = enabled
    }


}

interface DrawerLocker {
    fun setDrawerEnabled(enabled: Boolean)
}