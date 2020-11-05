package com.webserveis.mysubscriptions.ui.subscriptions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.hasTwoPanels
import com.webserveis.mysubscriptions.common.replaceFragment

class SubscriptionsNavFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriptions_nav, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (requireContext().hasTwoPanels()) {
            val fragmentMaster = SubscriptionsListFragment()
            (activity as AppCompatActivity).replaceFragment(fragmentMaster, R.id.frame_master)

            val fragmentDetail = SubscriptionsViewFragment()
            fragmentDetail.arguments = arguments
            (activity as AppCompatActivity).replaceFragment(fragmentDetail, R.id.frame_detail)
        } else {
            val fragment = SubscriptionsListFragment()
            fragment.arguments = arguments
            (activity as AppCompatActivity).replaceFragment(fragment, R.id.frame_master_and_detail)
        }

    }

    companion object {
        private val TAG = SubscriptionsNavFragment::class.java.simpleName
    }
}