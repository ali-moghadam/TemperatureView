package com.alirnp.temperatureviewexample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.alirnp.tempretureview.TemperatureView
import com.alirnp.tempretureview.callback.OnMoveListener
import com.alirnp.tempretureview.callback.OnSeekChangeListener
import com.alirnp.tempretureview.callback.OnSeekCompleteListener
import com.alirnp.tempretureview.utils.Config

class MainActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val temperatureView = findViewById<TemperatureView>(R.id.temperatureView)
        val textView = findViewById<TextView>(R.id.textView)

        textView.setOnClickListener {
            val config = Config("text", 5, -2, 11)
            temperatureView.config(config)
        }

        temperatureView.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeekChange(value: Int) {
                textView.text = "onSeekChange $value"
            }
        })
        temperatureView.setOnSeekCompleteListener(object : OnSeekCompleteListener {
            override fun onSeekComplete(value: Int) {
                textView.text = "onSeekComplete $value"
            }
        })

        temperatureView.setOnMoveListener(object : OnMoveListener {
            override fun isMoving(isMoving: Boolean) {
                //  textView.text = "isMoving $isMoving"
            }
        })
    }
}