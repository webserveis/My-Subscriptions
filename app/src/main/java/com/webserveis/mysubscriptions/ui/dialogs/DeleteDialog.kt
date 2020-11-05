package com.webserveis.mysubscriptions.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.webserveis.mysubscriptions.R

class DeleteDialog : DialogFragment() {

    interface OnDeleteDialogListener {
        fun onDeleteConfirm()
    }

    private var listener: OnDeleteDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val title = getString(R.string.dialog_delete_title)
            val content = getString(R.string.dialog_delete_summary)
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(title).setMessage(content)
                .setPositiveButton(getString(R.string.action_delete)) { _, _ ->
                    listener?.onDeleteConfirm()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ ->

                }

            val dialog = builder.create()

            dialog.setOnKeyListener { _, keyCode, keyEvent ->
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                    dismiss()
                    true
                } else false
            }
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (listener == null) {
            listener = when {
                targetFragment is OnDeleteDialogListener -> {
                    targetFragment as OnDeleteDialogListener
                }
                requireActivity() is OnDeleteDialogListener -> {
                    requireActivity() as OnDeleteDialogListener
                }
                else -> {
                    throw  ClassCastException(" must implement OnDeleteDialogListener")
                }
            }
        }
    }

}