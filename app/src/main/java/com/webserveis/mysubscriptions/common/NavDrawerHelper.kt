package com.webserveis.mysubscriptions.common

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/*
https://gist.github.com/nseidm1/9327674
 */
abstract class NavDrawerHelper(private val container: ViewGroup, private val fm: FragmentManager) {

    abstract fun getItem(position: Int): Fragment?

    fun changeFragment(position: Int): Fragment? {
        val fragmentTag = makeFragmentName(container.id, getItemId(position))

        Log.i(TAG, "Fragments size:" + fm.fragments.size)

        var fragment: Fragment? = fm.findFragmentByTag(fragmentTag)

        //val fragmentTransaction = fragment?.childFragmentManager?.beginTransaction()
        val fragmentTransaction = fm.beginTransaction()
        if (fragment == null) {
            getItem(position)?.also {
                fragment = it
                fragmentTransaction.add(container.id, it, fragmentTag)
                Log.d(TAG, "fragment ADD:$fragment.tag")
            }

        } else {
            fragment?.let {
                fragmentTransaction.attach(it)
                Log.d(TAG, "fragment ATTACH:${it.tag}")
            }

        }

        // Detach existing primary fragment
        val currentFragment: Fragment? = fm.primaryNavigationFragment
        if (currentFragment != null) {
            fragmentTransaction.detach(currentFragment)
            //Log.d(TAG, "fragment DETACH:${curFrag.tag}")
        }

        if (currentFragment?.tag != fragment?.tag) {
            // Set fragment as primary navigator for child manager back stack to be handled by system
            fragmentTransaction.setPrimaryNavigationFragment(fragment)
            fragmentTransaction.setReorderingAllowed(true)
            fragmentTransaction.commitNowAllowingStateLoss()
        } else {
            Log.w(TAG, "Avoid changeFragment: Same fragment $fragmentTag")
        }
        return fragment
    }


    fun removeFragment(position: Int) {
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fm.findFragmentByTag(makeFragmentName(container.id, getItemId(position)))?.let {
            fragmentTransaction.remove(it)
        }
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    private fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private val TAG = NavBottomBarHelper::class.java.simpleName
        private fun makeFragmentName(viewId: Int, id: Long): String {
            return "android:switcher:$viewId:$id"
        }
    }

}