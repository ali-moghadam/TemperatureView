package com.alirnp.tempretureview.callback


interface OnSeekChangeListener {

    /**
     * called when user is moving the pointer's temp
     */
    fun onMoving(isMoving: Boolean)

    /**
     * called when change value by seeking the pointer's temp
     */
    fun onSeekChange(value: Int)

    /**
     * called when change value by seek is completed
     */
    fun onSeekComplete(value: Int)
}