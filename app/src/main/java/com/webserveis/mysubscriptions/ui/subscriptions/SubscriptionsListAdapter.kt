package com.webserveis.mysubscriptions.ui.subscriptions

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.webserveis.mysubscriptions.R
import com.webserveis.mysubscriptions.common.MySubsUtils
import com.webserveis.mysubscriptions.common.isDark
import com.webserveis.mysubscriptions.models.SubscriptionModel
import kotlinx.android.synthetic.main.draw_item_subscription.view.*
import java.text.NumberFormat
import java.util.*

class SubscriptionsListAdapter(private val itemClickListener: (SubscriptionModel) -> Unit) :
    ListAdapter<SubscriptionModel, SubscriptionsListAdapter.MyViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<SubscriptionModel>() {
        override fun areItemsTheSame(oldItem: SubscriptionModel, newItem: SubscriptionModel): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: SubscriptionModel, newItem: SubscriptionModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.draw_item_subscription, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SubscriptionModel, clickListener: (SubscriptionModel) -> Unit) {

            if (item.color.isDark()) {
                itemView.tvName.setTextColor(Color.WHITE)
                itemView.tvPrice.setTextColor(Color.WHITE)
                itemView.tvDescription.setTextColor(ColorUtils.setAlphaComponent(Color.WHITE, 0xB3))
                itemView.tvTime.setTextColor(Color.WHITE)
            } else {
                itemView.tvName.setTextColor(Color.BLACK)
                itemView.tvPrice.setTextColor(Color.BLACK)
                itemView.tvDescription.setTextColor(ColorUtils.setAlphaComponent(Color.BLACK, 0x8A))
                itemView.tvTime.setTextColor(Color.BLACK)
            }

            itemView.tvName.text = item.name

            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.maximumFractionDigits = 2
            format.currency = Currency.getInstance(item.currencyCode)
            itemView.tvPrice.text = format.format(item.price)

            itemView.tvDescription.text = when {
                item.isDeleted -> {
                    Log.d(TAG, "bind: " + item.isDeleted)
                    itemView.context.getString(R.string.time_left_expired)
                }
                MySubsUtils.isToday(item.nextPayment.time) -> {
                    itemView.context.getString(R.string.time_left_today)
                }
                else -> {
                    val cal = Calendar.getInstance()
                    val diffDays = MySubsUtils.daysBetween(cal.time, item.nextPayment)
                    val str = itemView.context.resources.getQuantityString(R.plurals.time_left_days, diffDays)
                    str.format(diffDays)
                }
            }

            itemView.cardViewSubscription.setCardBackgroundColor(item.color)
            itemView.setOnClickListener { clickListener(item) }
        }


    }

    companion object {
        private val TAG = SubscriptionsListAdapter::class.java.simpleName
    }


}