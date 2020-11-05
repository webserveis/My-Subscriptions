package com.webserveis.mysubscriptions.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.webserveis.mysubscriptions.R


/*
https://guides.codepath.com/android/using-dialogfragment
 */
class BillPeriodPickerDialog : DialogFragment() {

    //Declare an Interface
    interface OnBillPeriodPickerListener {
        fun onPositiveClick(billPeriodValue: Int, billPeriodUnit: Int)
    }

    var listener: OnBillPeriodPickerListener? = null
    private var billPeriodValue: Int = 1
    private var billPeriodUnits: Int = 0

    fun newInstance(value: Int, units: Int): BillPeriodPickerDialog? {
        val dialog = BillPeriodPickerDialog()

        val args = Bundle()
        args.putInt(ARG_DIALOG_VALUE, value)
        args.putInt(ARG_DIALOG_UNITS, units)
        dialog.arguments = args

        return dialog

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.retainInstance = true

        billPeriodValue = arguments?.getInt(ARG_DIALOG_VALUE) ?: 1
        billPeriodUnits = arguments?.getInt(ARG_DIALOG_UNITS) ?: 0

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())

            val dialogView: View = layoutInflater.inflate(R.layout.dialog_picker_biill_period, null)

            builder.setView(dialogView)

            val npkDays = dialogView.findViewById(R.id.npkDays) as NumberPicker
            val npkUnits = dialogView.findViewById(R.id.npkPeriodUnits) as NumberPicker

            npkDays.apply {
                maxValue = 365
                minValue = 1
                wrapSelectorWheel = true
                value = billPeriodValue
                setOnValueChangedListener { _, _, newVal ->
                    billPeriodValue = newVal
                }

            }

            val valuesList = resources.getStringArray(R.array.bill_period_units)
            npkUnits.apply {
                maxValue = valuesList.size - 1
                value = billPeriodUnits
                displayedValues = valuesList
                wrapSelectorWheel = false
                setOnValueChangedListener { _, _, newVal ->
                    billPeriodUnits = newVal
                }
            }

            builder
                .setTitle(arguments?.getString(ARG_DIALOG_TITLE))
                //.setMessage(content)
                .setPositiveButton(android.R.string.ok) { _, _ ->

                    billPeriodValue = npkDays.value
                    billPeriodUnits = npkUnits.value
                    listener?.onPositiveClick(billPeriodValue, billPeriodUnits)
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    dismiss()
                    //listener?.onCancelClick()
                }
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = targetFragment as OnBillPeriodPickerListener
        } catch (e: java.lang.ClassCastException) {
            throw java.lang.ClassCastException("Calling fragment must implement Callback interface")
        }
    }

    override fun onDestroyView() {
        val dialog = dialog
        if (dialog != null && retainInstance) {
            dialog.setDismissMessage(null)
        }
        super.onDestroyView()
    }


    companion object {
        private val TAG = BillPeriodPickerDialog::class.java.simpleName
        const val ARG_DIALOG_TITLE = "arg_dialog_title"
        const val ARG_DIALOG_VALUE = "arg_dialog_value_1"
        const val ARG_DIALOG_UNITS = "arg_dialog_value_2"
    }
}