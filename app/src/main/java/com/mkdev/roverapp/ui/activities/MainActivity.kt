package com.mkdev.roverapp.ui.activities

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.mkdev.roverapp.R
import com.mkdev.roverapp.api.Api
import com.mkdev.roverapp.api.Controller
import com.mkdev.roverapp.ui.dialogs.LoadingDialog
import com.mkdev.roverapp.utils.CustomToast
import com.mkdev.roverapp.utils.iomain
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var disposable: Disposable? = null
    private val loadingDialog: LoadingDialog by lazy {
        LoadingDialog(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetCommand.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            btnGetCommand -> {
                roverCustomView.stopProcess()
                roverCustomView.reset()
                getRoverCommands()
            }
        }
    }

    private fun getRoverCommands() {
        val service = Controller.getClient(applicationContext).create(Api::class.java)
        disposable = service.getRoverCommand("12856492")
            .iomain()
            .doOnSubscribe { loadingDialog.show() }
            .doAfterTerminate { loadingDialog.hide() }
            .subscribe({ rover ->
                if (linWelcome.visibility == View.VISIBLE) {
                    linWelcome.visibility = View.GONE
                    roverCustomView.visibility = View.VISIBLE
                }

                rover?.let {
                    roverCustomView.reset()
                    roverCustomView.updateLayout(Point(it.startPoint.X, it.startPoint.Y), it.weirs)
                    roverCustomView.processCommand(it.command)
                } ?: run {
                    CustomToast.makeText(
                        this@MainActivity,
                        getString(R.string.no_command_received),
                        CustomToast.WARNING
                    )
                }

            }, {
                Timber.d(it)
                CustomToast.makeText(this@MainActivity, getString(R.string.error), CustomToast.ERROR)
            })
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }
}
