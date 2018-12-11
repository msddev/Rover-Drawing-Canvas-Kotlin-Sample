package com.mkdev.roverapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGetCommand.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v) {
            btnGetCommand -> {

            }
        }
    }
}
