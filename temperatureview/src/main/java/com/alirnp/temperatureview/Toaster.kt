package com.alirnp.temperatureview

import android.content.Context
import android.widget.Toast

class Toaster(val context : Context) {

    fun show(message : String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}