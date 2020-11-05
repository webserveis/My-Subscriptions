package com.webserveis.mysubscriptions.ui.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import app.webserveis.statelayout.StateLayout
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.toast
import com.webserveis.mysubscriptions.usecases.SubscriptionsListState
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(this@HomeFragment).get(HomeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        homeViewModel.resultSate.observe(viewLifecycleOwner, Observer {

            when (it) {
                SubscriptionsListState.FirstLoading, SubscriptionsListState.Loading -> {
                    Log.d(TAG, " Loading: ")
                    layout_state.loading()
                }
                is SubscriptionsListState.Failure -> {
                    Log.d(TAG, " Failure: ")
                }
                is SubscriptionsListState.Success -> {
                    Log.d(TAG, " Success: ")
                }
                is SubscriptionsListState.Empty -> {
                    Log.d(TAG, " Empty: ")
                    showEmptyUI("Empty","data not found")
                }

            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = getText(R.string.menu_home)

        layout_state.setViewForState(StateLayout.STATE_EMPTY, R.layout.view_empty)
        //showEmptyUI("Error", "Has a error")
        context.toast("¯\\_(ツ)_/¯")

        val uiHandler = Handler()
        uiHandler.post {
            homeViewModel.getSubscriptions()
        }

        requireActivity().invalidateOptionsMenu()
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
        private val TAG = HomeFragment::class.java.simpleName
    }

}