package com.dsl.orderrobot.widget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dsl.orderrobot.R

/**
 * @author dsl-abben
 * on 2020/09/23.
 */
class AddTriggerDialog : DialogFragment() {

    companion object {
        fun onNewInstance(): AddTriggerDialog {
            return AddTriggerDialog()
        }
    }

    interface AddTriggerDialogClickListener {
        fun onPositiveClcik(trigger: String, editAddTrigger: String, autoSend: String)
    }

    var onClickListener: AddTriggerDialogClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.CustomDialog)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_add_trigger, null, false)
        val editTrigger = view.findViewById<EditText>(R.id.edit_trigger)
        val editAddTrigger = view.findViewById<EditText>(R.id.edit_and_trigger)
        val editAutoSend = view.findViewById<EditText>(R.id.edit_auto_send)
        view.findViewById<TextView>(R.id.positive).setOnClickListener {
            if (editTrigger.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            if (editAutoSend.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            onClickListener?.onPositiveClcik(
                editTrigger.text.toString(),
                editAddTrigger.text.toString(),
                editAutoSend.text.toString()
            )
            dismiss()
        }
        dialog.setContentView(view)
        return dialog
    }

}