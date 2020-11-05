package com.webserveis.mysubscriptions.common

import android.util.Log
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/*
https://github.com/okaybroda/FragmentStateManager/blob/master/library/src/main/java/com/viven/fragmentstatemanager/FragmentStateManager.java
 */
abstract class NavBottomBarHelper(private val container: ViewGroup, private val fm: FragmentManager) {

    abstract fun getItem(position: Int): Fragment?

    fun changeFragment(position: Int): Fragment? {
        val tag = makeFragmentName(container.id, getItemId(position))

        val fragmentTransaction = fm.beginTransaction()
        Log.i(TAG, "Fragments size:" + fm.fragments.size)

        var fragment: Fragment? = fm.findFragmentByTag(tag)

        if (fragment == null) {
            Log.w(TAG, "fragment $tag is null")
            getItem(position)?.also {
                fragment = it
                fragmentTransaction.add(container.id, it, tag)
                //Log.d(TAG, "fragment ADD:$fragment.tag")
            }

        } else {
            fragment?.let {
                fragmentTransaction.attach(it)
                //Log.d(TAG, "fragment ATTACH:${it.tag}")
            }

        }

        // Detach existing primary fragment
        val curFrag: Fragment? = fm.primaryNavigationFragment
        if (curFrag != null) {
            fragmentTransaction.detach(curFrag)
            //Log.d(TAG, "fragment DETACH:${curFrag.tag}")
        }
        // Set fragment as primary navigator for child manager back stack to be handled by system
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()
        return fragment
    }


    fun removeFragment(position: Int) {
        val fragmentTransaction: FragmentTransaction = fm.beginTransaction()
        fm.findFragmentByTag(makeFragmentName(container.id, getItemId(position)))?.let {
            fragmentTransaction.remove(it)
        }
        fragmentTransaction.commitNowAllowingStateLoss()
    }

    fun getItemId(position: Int): Long {
        return position.toLong()
    }

    companion object {
        private val TAG = NavBottomBarHelper::class.java.simpleName
        private fun makeFragmentName(viewId: Int, id: Long): String {
            return "android:switcher:$viewId:$id"
        }
    }

}