package com.alirnp.tempretureview.callback

fun interface OnSeekChangeListener {
    /**
     * called when change value by seeking the pointer's temp
     */
    fun onSeekChange(value: Int)
}