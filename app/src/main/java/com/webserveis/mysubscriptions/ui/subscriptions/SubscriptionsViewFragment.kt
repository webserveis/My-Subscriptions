package com.webserveis.mysubscriptions.ui.subscriptions

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import app.webserveis.statelayout.StateLayout
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.ARG_ITEM_ID
import com.webserveis.mysubscriptions.common.isDark
import com.webserveis.mysubscriptions.common.launchActivity
import com.webserveis.mysubscriptions.common.replaceFragment
import com.webserveis.mysubscriptions.models.SubscriptionModel
import com.webserveis.mysubscriptions.models.SubscriptionPeriodBill
import com.webserveis.mysubscriptions.models.SubscriptionStatusBill
import com.webserveis.mysubscriptions.usecases.SubscriptionState
import kotlinx.android.synthetic.main.activity_subscription_detail.*
import kotlinx.android.synthetic.main.fragment_subscription_view.*
import kotlinx.android.synthetic.main.fragment_subscriptions_nav.*
import java.text.DateFormat
import java.text.NumberFormat
import java.util.*

class SubscriptionsViewFragment : Fragment() {

    private var itemUID: String? = null

    private val mViewModel: SubscriptionsViewModel by lazy {
        ViewModelProvider(this@SubscriptionsViewFragment).get(SubscriptionsViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mViewModel.resultSate.observe(viewLifecycleOwner, {
            when (it) {
                SubscriptionState.Loading -> {
                    Log.d(TAG, " Loading: ")
                    layout_state_2.loading()
                }
                is SubscriptionState.Failure -> {
                    Log.d(TAG, " Failure: ")
                }
                is SubscriptionState.Success -> {
                    Log.d(TAG, " Success: ")
                    refreshDataUI(it.data)
                    layout_state_2.content()
                    setMenuVisibility(true)

                }
                is SubscriptionState.Empty -> {
                    Log.d(TAG, " Empty: ")
                    //if (requireActivity().frame_detail != null) toolbar.visibility = View.GONE
                    showEmptyUI(getString(R.string.state_empty_view_title), getString(R.string.state_empty_view_summary))
                    itemUID = null
                    //requireActivity().invalidateOptionsMenu()
                }

            }

        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemUID = it.getString(ARG_ITEM_ID)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_subscription_view, container, false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.subscription_view, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.d(TAG, "onPrepareOptionsMenu: ")
        menu.findItem(R.id.action_edit)?.isEnabled = itemUID != null
        super.onPrepareOptionsMenu(menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMenuVisibility(false)

        (activity as AppCompatActivity).let {
            if (it.frame_detail == null) {
                it.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24)
                it.supportActionBar?.title = null
                it.supportActionBar?.setDisplayHomeAsUpEnabled(it.frame_detail == null)
            }
            //it.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        mViewModel.getSubscriptionById(itemUID)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> {
                val fragment = SubscriptionsEditFragment()
                fragment.arguments = arguments

                (activity as AppCompatActivity).let {
                    if (it.frame_detail == null) {
                        it.replaceFragment(fragment, R.id.frame_master_and_detail, true)
                    } else {
                        //it.replaceFragment(fragment, R.id.frame_detail, true)
                        requireActivity().launchActivity<SubscriptionDetailActivity> {
                            putExtra(ARG_ITEM_ID, itemUID)
                            action = Intent.ACTION_EDIT
                        }

                    }
                }
            }


        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshDataUI(item: SubscriptionModel) {

        cardViewSub.setCardBackgroundColor(item.color)

        if (item.color.isDark()) {
            tvName.setTextColor(Color.WHITE)
            tvPrice.setTextColor(Color.WHITE)
        } else {
            tvName.setTextColor(Color.BLACK)
            tvPrice.setTextColor(Color.BLACK)
        }

        tvName.text = item.name

        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.currency = Currency.getInstance(item.currencyCode)
        tvPrice.text = format.format(item.price)

        if (item.description.isNullOrEmpty()) {
            tvDescriptionLabel.visibility = View.GONE
            tvDescription.visibility = View.GONE
        } else {
            tvDescription.text = item.description
        }

        val statusArray: Array<String> = resources.getStringArray(R.array.sub_status_array)
        tvRenewalStatus.text = statusArray[item.status]

        when (item.status) {
            SubscriptionStatusBill.AUTO_RENEW, SubscriptionStatusBill.MANUAL_RENEW -> {
                tvNextPaymentLabel.text = getString(R.string.sub_next_payment)
                tvBillPeriodLabel.visibility = View.VISIBLE
                tvBillPeriod.visibility = View.VISIBLE
                tvPreviousPaymentLabel.visibility = View.VISIBLE
                tvPreviousPayment.visibility = View.VISIBLE
            }

            SubscriptionStatusBill.NOT_RENEW -> {
                tvNextPaymentLabel.text = getString(R.string.sub_expire_date_in)
                tvBillPeriodLabel.visibility = View.GONE
                tvBillPeriod.visibility = View.GONE
                tvPreviousPaymentLabel.visibility = View.VISIBLE
                tvPreviousPayment.visibility = View.VISIBLE
            }
            SubscriptionStatusBill.ONE_TIME -> {
                tvNextPaymentLabel.text = getString(R.string.sub_expire_date_in)
                tvBillPeriodLabel.visibility = View.GONE
                tvBillPeriod.visibility = View.GONE
                tvPreviousPaymentLabel.visibility = View.GONE
                tvPreviousPayment.visibility = View.GONE
            }

        }

        val str = when (item.circleUnits) {
            SubscriptionPeriodBill.DAYS -> requireContext().resources.getQuantityString(R.plurals.sub_bill_period_days, item.circleValue)
            SubscriptionPeriodBill.WEEKS -> requireContext().resources.getQuantityString(R.plurals.sub_bill_period_weeks, item.circleValue)
            SubscriptionPeriodBill.MONTHS -> requireContext().resources.getQuantityString(R.plurals.sub_bill_period_months, item.circleValue)
            SubscriptionPeriodBill.YEARS -> requireContext().resources.getQuantityString(R.plurals.sub_bill_period_years, item.circleValue)
            else -> null
        }
        if (str != null) {
            tvBillPeriod.text = str.format(item.circleValue)
        }

        tvFirstPayment.text = DateFormat.getDateInstance().format(item.firstPayment)
        tvPreviousPayment.text = DateFormat.getDateInstance().format(item.previousPayment)
        tvNextPayment.text = DateFormat.getDateInstance().format(item.nextPayment)

    }

    private fun showErrorUI(title: String, summary: String) {
        val view = layout_state_2.getView(StateLayout.STATE_ERROR)
        view?.findViewById<TextView>(R.id.error_title)?.text = title
        view?.findViewById<TextView>(R.id.error_summary)?.text = summary
        layout_state_2.setState(StateLayout.STATE_ERROR)
    }

    private fun showEmptyUI(title: String, summary: String, @DrawableRes icon: Int? = null) {
        val view = layout_state_2.getView(StateLayout.STATE_EMPTY)
        view?.findViewById<TextView>(R.id.empty_title)?.text = title
        view?.findViewById<TextView>(R.id.empty_summary)?.text = summary
        if (icon != null) view?.findViewById<ImageView>(R.id.empty_icon)?.setImageResource(icon)

        layout_state_2.empty()

    }


    companion object {
        private val TAG = SubscriptionsViewFragment::class.java.simpleName
    }
}