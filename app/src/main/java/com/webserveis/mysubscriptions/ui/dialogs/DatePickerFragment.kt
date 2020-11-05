package com.webserveis.mysubscriptions.ui.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    interface OnDatePickerListener {
        fun onDateSelected(year: Int, month: Int, dayOfMonth: Int, tag: String?)
    }

    var listener: OnDatePickerListener? = null


    fun newInstance(date: Date? = null, tag: String? = null): DatePickerFragment? {
        val dialog = DatePickerFragment()

        val args = Bundle()
        if (date != null) {
            args.putLong(ARG_DATE, date.time)
        } else {
            val c = Calendar.getInstance()
            args.putLong(ARG_DATE, c.timeInMillis)
        }
        args.putString(ARG_TAG, tag)

        dialog.arguments = args

        return dialog

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val args = requireArguments()

        val c = Calendar.getInstance()

        val date = args.getLong(ARG_DATE)
        c.timeInMillis = date

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (listener == null) {
            listener = when {
                targetFragment is OnDatePickerListener -> {
                    targetFragment as OnDatePickerListener
                }
                requireActivity() is OnDatePickerListener -> {
                    requireActivity() as OnDatePickerListener
                }
                else -> {
                    throw  ClassCastException(" must implement OnDatePickerListener")
                }
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        listener?.onDateSelected(year, month, dayOfMonth, arguments?.getString(ARG_TAG))
    }

    companion object {
        private val TAG = DatePickerFragment::class.java.simpleName
        const val ARG_DATE = "dialog_date"
        const val ARG_TAG = "dialog_date_tag"
    }


}