package com.alirnp.temperatureviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.alirnp.tempretureview.TemperatureView
import com.alirnp.tempretureview.callback.OnSeekChangeListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val temperatureView  = findViewById<TemperatureView>(R.id.temperatureView)
        val textView  = findViewById<TextView>(R.id.textView)

        temperatureView.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onMoving(isMoving: Boolean) {

            }

            override fun onSeekChange(value: Int) {
                textView.text = "onSeekChange $value"
            }

            override fun onSeekComplete(value: Int) {
                textView.text = "onSeekComplete $value"
            }
        })
    }
}