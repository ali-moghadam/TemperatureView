package com.alirnp.tempretureview.utils

import android.content.Context

class SizeConvertor(private val context: Context) {

    fun spToPx(sp: Float): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }

    fun pxToDp( px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
}