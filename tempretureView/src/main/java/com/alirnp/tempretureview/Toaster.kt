package com.alirnp.tempretureview

import android.content.Context
import android.widget.Toast

class Toaster(private val context: Context) {

    fun show(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}