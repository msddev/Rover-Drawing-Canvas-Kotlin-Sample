package com.mkdev.roverapp

import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast

fun isNetworkConnected(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (manager != null) {
        val networkInfo = manager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected && networkInfo.isAvailable) {
            return true
        }
    }
    return false
}

fun dpToPx(dp: Int) = (dp * Resources.getSystem().displayMetrics.density).toInt()

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun TextView.showMessage(message: String, type: MessageType) {
    val color = when (type) {
        MessageType.SUCCESS -> R.color.color_success
        MessageType.ERROR -> R.color.color_error
        MessageType.WARNING -> R.color.color_warning
        MessageType.PROGRESS -> R.color.color_info
    }
    text = message
    setTextColor(ContextCompat.getColor(context, color))
}

fun vibrate(context: Context) {
    val vib = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    else
        vib.vibrate(500)
}

enum class MessageType {
    SUCCESS,
    ERROR,
    WARNING,
    PROGRESS
}