package com.mkdev.roverapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.WindowManager
import com.mkdev.roverapp.R
import kotlinx.android.synthetic.main.dialog_alert.*

class AlertDialog(private val mContext: Context,
                  private val title: String = mContext.getString(R.string.alert),
                  private val content: String,
                  cancelable: Boolean = true,
                  cancelListener: DialogInterface.OnCancelListener? = null) :
        Dialog(mContext, cancelable, cancelListener) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_alert)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        tvTitle.text = title
        tvContent.text = content

        btnDismiss.setOnClickListener {
            cancel()
        }
    }
}