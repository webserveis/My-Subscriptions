package com.webserveis.mysubscriptions.ui.subscriptions

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.webserveis.statelayout.StateLayout
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.ARG_ITEM_ID
import com.webserveis.mysubscriptions.common.launchActivity
import com.webserveis.mysubscriptions.common.replaceFragment
import com.webserveis.mysubscriptions.common.withArgs
import com.webserveis.mysubscriptions.usecases.SubscriptionsListState
import kotlinx.android.synthetic.main.fragment_home.layout_state
import kotlinx.android.synthetic.main.fragment_subscriptions_list.*
import kotlinx.android.synthetic.main.fragment_subscriptions_nav.*

class SubscriptionsListFragment : Fragment() {

    private val mViewModel: SubscriptionsViewModel by lazy {
        ViewModelProvider(this@SubscriptionsListFragment).get(SubscriptionsViewModel::class.java)
    }

    private lateinit var mAdapter: SubscriptionsListAdapter


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel.listResultSate.observe(viewLifecycleOwner, {

            when (it) {
                SubscriptionsListState.FirstLoading -> {
                    layout_state.loading()
                }
                SubscriptionsListState.Loading -> {
                    swipeRefresh.isRefreshing = true
                }
                is SubscriptionsListState.Success -> {
                    Log.d(TAG, " Success: ")
                    mAdapter.submitList(it.data)
                    mAdapter.notifyDataSetChanged()
                    layout_state.content()
                    swipeRefresh.isRefreshing = false
                }
                is SubscriptionsListState.Empty -> {
                    Log.d(TAG, " Empty: ")
                    swipeRefresh.isRefreshing = false
                    showEmptyUI(getString(R.string.state_empty_title), getString(R.string.state_empty_summary))

                }
                is SubscriptionsListState.Failure -> {
                    Log.d(TAG, " Failure: ")
                }
            }

        })

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscriptions_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getText(R.string.menu_subscriptions)

        layout_state.setViewForState(StateLayout.STATE_EMPTY, R.layout.view_empty)

        mAdapter = SubscriptionsListAdapter {
            (activity as AppCompatActivity).let { activity ->
                if (activity.frame_detail == null) {
                    requireActivity().launchActivity<SubscriptionDetailActivity> {
                        putExtra(ARG_ITEM_ID, it.uid)
                        action = Intent.ACTION_VIEW
                    }
                } else {
                    val fragment = SubscriptionsViewFragment()
                    fragment.withArgs {
                        putString(ARG_ITEM_ID, it.uid)
                    }

                    activity.replaceFragment(fragment, R.id.frame_detail, true)
                }
            }


        }
        rvSubscriptionsList.adapter = mAdapter

        swipeRefresh.setOnRefreshListener {
            mViewModel.getSubscriptionsList()
        }

        fab.setOnClickListener {
            requireActivity().launchActivity<SubscriptionDetailActivity> {
                action = Intent.ACTION_INSERT
            }

        }

    }

    override fun onResume() {
        super.onResume()
        mViewModel.getSubscriptionsList()
    }

    private fun showErrorUI(title: String, summary: String) {
        val view = layout_state.getView(StateLayout.STATE_ERROR)
        view?.findViewById<TextView>(R.id.error_title)?.text = title
        view?.findViewById<TextView>(R.id.error_summary)?.text = summary
        layout_state.setState(StateLayout.STATE_ERROR)
    }

    private fun showEmptyUI(title: String, summary: String, @DrawableRes icon: Int? = null) {
        val view = layout_state.getView(StateLayout.STATE_EMPTY)
        view?.findViewById<TextView>(R.id.empty_title)?.text = title
        view?.findViewById<TextView>(R.id.empty_summary)?.text = summary
        if (icon != null) view?.findViewById<ImageView>(R.id.empty_icon)?.setImageResource(icon)

        layout_state.setState(StateLayout.STATE_EMPTY)

    }


    companion object {
        private val TAG = SubscriptionsListFragment::class.java.simpleName
    }
}