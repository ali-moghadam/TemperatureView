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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        showCurrentValue(binding.temperatureView.getValue())
        showMinValue(binding.temperatureView.getMinValue())
        showMaxValue(binding.temperatureView.getMaxValue())


        binding.buttonRandomConfig.setOnClickListener {
            val value = (-5..15).random()
            val minValue = (-10..-5).random()
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
            showCurrentValue(currentValue)
        }

        binding.fabPlusCurrentValue.setOnClickListener {
            var currentValue = binding.temperatureView.getValue()
            binding.temperatureView.setValue(++currentValue)

            // show value in textView
            showCurrentValue(currentValue)
        }


        binding.fabMinusMinValue.setOnClickListener {
            var minValue = binding.temperatureView.getMinValue()
            binding.temperatureView.setMinValue(--minValue)

            // show value in textView
            showMinValue(minValue)
        }

        binding.fabPlusMinValue.setOnClickListener {
            var minValue = binding.temperatureView.getMinValue()
            binding.temperatureView.setMinValue(++minValue)

            // show value in textView
            showMinValue(minValue)
        }

        binding.fabMinusMaxValue.setOnClickListener {
            var maxValue = binding.temperatureView.getMaxValue()
            binding.temperatureView.setMaxValue(--maxValue)

            // show value in textView
            showMaxValue(maxValue)
        }

        binding.fabPlusMaxValue.setOnClickListener {
            var maxValue = binding.temperatureView.getMaxValue()
            binding.temperatureView.setMaxValue(++maxValue)

            // show value in textView
            showMaxValue(maxValue)
        }

        binding.temperatureView.setOnSeekChangeListener(object : OnSeekChangeListener {
            override fun onSeekChange(value: Int) {
                binding.textView.text = "onSeekChange"

                // show value in textView
                showCurrentValue(value)
            }
        })
        binding.temperatureView.setOnSeekCompleteListener(object : OnSeekCompleteListener {
            override fun onSeekComplete(value: Int) {
                binding.textView.text = "onSeekComplete"
            }
        })

        binding.temperatureView.setOnMoveListener(object : OnMoveListener {
            override fun isMoving(isMoving: Boolean) {
                //  textView.text = "isMoving $isMoving"
            }
        })
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
}