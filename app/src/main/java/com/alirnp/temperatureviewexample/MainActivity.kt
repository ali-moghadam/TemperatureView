package com.alirnp.temperatureviewexample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.alirnp.temperatureviewexample.databinding.ActivityMainBinding
import com.alirnp.tempretureview.TemperatureView
import com.alirnp.tempretureview.callback.OnMoveListener
import com.alirnp.tempretureview.callback.OnSeekChangeListener
import com.alirnp.tempretureview.callback.OnSeekCompleteListener
import com.alirnp.tempretureview.utils.Config

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isMoving = false
    private var status = "NONE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showCurrentValue(binding.temperatureView.getValue())
        showMinValue(binding.temperatureView.getMinValue())
        showMaxValue(binding.temperatureView.getMaxValue())
        showStatus()


        binding.buttonRandomConfig.setOnClickListener {
            val minValue = (-10..-5).random()
            val value = (-5..15).random()
            val maxValue = (15..20).random()

            val config = Config("random config!", value, minValue, maxValue)
            binding.temperatureView.config(config)

            // show values in textViews
            showCurrentValue(value)
            showMinValue(minValue)
            showMaxValue(maxValue)
        }

        binding.fabMinusCurrentValue.setOnClickListener {
            var currentValue = binding.temperatureView.getValue()
            binding.temperatureView.setValue(--currentValue)

            // show value in textView
            showCurrentValue(binding.temperatureView.getValue())
        }

        binding.fabPlusCurrentValue.setOnClickListener {
            var currentValue = binding.temperatureView.getValue()
            binding.temperatureView.setValue(++currentValue)

            // show value in textView
            showCurrentValue(binding.temperatureView.getValue())
        }


        binding.fabMinusMinValue.setOnClickListener {
            var minValue = binding.temperatureView.getMinValue()
            binding.temperatureView.setMinValue(--minValue)

            // show value in textView
            showMinValue(binding.temperatureView.getMinValue())
        }

        binding.fabPlusMinValue.setOnClickListener {
            var minValue = binding.temperatureView.getMinValue()
            binding.temperatureView.setMinValue(++minValue)

            // show value in textView
            showMinValue(binding.temperatureView.getMinValue())
        }

        binding.fabMinusMaxValue.setOnClickListener {
            var maxValue = binding.temperatureView.getMaxValue()
            binding.temperatureView.setMaxValue(--maxValue)

            // show value in textView
            showMaxValue(binding.temperatureView.getMaxValue())
        }

        binding.fabPlusMaxValue.setOnClickListener {
            var maxValue = binding.temperatureView.getMaxValue()
            binding.temperatureView.setMaxValue(++maxValue)

            // show value in textView
            showMaxValue(binding.temperatureView.getMaxValue())
        }

        binding.temperatureView.setOnSeekChangeListener { value ->
            this.status = "onSeekChange"
            binding.textViewStatus.text = this.status


            // show value in textView
            showCurrentValue(value)
            showStatus()
        }

        binding.temperatureView.setOnSeekCompleteListener {
            this.status = "onSeekComplete"
            binding.textViewStatus.text = this.status

            showStatus()
        }

        binding.temperatureView.setOnMoveListener { isMoving ->
            //disable scrolling when user seeking pointer
            binding.lockableScrollView.setScrollingEnabled(!isMoving)

            this.isMoving = isMoving
            showStatus()
        }
    }

    private fun showCurrentValue(value: Int) {
        binding.textViewCurrentValue.text = "currentValue = $value"
    }

    private fun showMinValue(value: Int) {
        binding.textViewMinValue.text = "minValue = $value"
    }

    private fun showMaxValue(value: Int) {
        binding.textViewMaxValue.text = "maxValue = $value"
    }

    private fun showStatus() {
        binding.textViewStatus.text = "status = $status\nisMoving = $isMoving"
    }
}