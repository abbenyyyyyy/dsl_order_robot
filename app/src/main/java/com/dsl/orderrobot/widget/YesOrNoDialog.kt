package com.dsl.orderrobot.widget

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.dsl.orderrobot.R

/**
 * 只有确认和取消的弹框
 * @author dsl-abben
 * on 2019/08/01.
 */
class YesOrNoDialog : DialogFragment() {

    companion object {
        private const val KEY_CONTENT = "KEY_CONTENT"
        private const val KEY_POSITIVE_TEXT = "KEY_POSITIVE_TEXT"
        private const val KEY_NEGATIVE_TEXT = "KEY_NEGATIVE_TEXT"

        fun onNewInstance(
            content: String,
            positiveText: String = "确认",
            negativeText: String = "取消"
        ): YesOrNoDialog {
            val yesOrNoDialog = YesOrNoDialog()
            val bundle = Bundle()
            bundle.putString(KEY_CONTENT, content)
            bundle.putString(KEY_POSITIVE_TEXT, positiveText)
            bundle.putString(KEY_NEGATIVE_TEXT, negativeText)
            yesOrNoDialog.arguments = bundle
            return yesOrNoDialog
        }
    }

    interface YesOrNoDialogClickListener {
        fun onYesOrNoDialogNegative()

        fun onYesOrNoDialogPositive()
    }

    var yesOrNoDialogClickListener: YesOrNoDialogClickListener? = null
    var cancelableSet: Boolean = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.CustomDialog)
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_yes_or_no, null, false)
        val dialogContent = view.findViewById<TextView>(R.id.dialog_content)
        val positiveTextView = view.findViewById<TextView>(R.id.positive)
        val negativeTextView = view.findViewById<TextView>(R.id.negative)
        arguments?.let {
            dialogContent.text = it.getString(KEY_CONTENT)
            positiveTextView.text = it.getString(KEY_POSITIVE_TEXT)
            negativeTextView.text = it.getString(KEY_NEGATIVE_TEXT)
        }
        negativeTextView.setOnClickListener {
            yesOrNoDialogClickListener?.onYesOrNoDialogNegative()
            dismiss()
        }
        positiveTextView.setOnClickListener {
            yesOrNoDialogClickListener?.onYesOrNoDialogPositive()
            dismiss()
        }
        isCancelable = cancelableSet
        dialog.setContentView(view)
        return dialog
    }

}