package com.alirnp.temperatureviewexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alirnp.tempretureview.TemperatureView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val temperatureView  = TemperatureView(this)
    }
}