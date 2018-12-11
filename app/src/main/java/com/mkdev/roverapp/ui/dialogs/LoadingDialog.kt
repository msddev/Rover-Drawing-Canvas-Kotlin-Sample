package com.mkdev.roverapp.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.mkdev.roverapp.R
import kotlinx.android.synthetic.main.dialog_loading.*

class LoadingDialog(context: Context,
                    var content: String = context.getString(R.string.please_wait)) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_loading)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(false)
        tvContent.text = content
    }
}


