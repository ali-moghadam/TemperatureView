package com.alirnp.tempretureview.callback

interface OnMoveListener {

    /**
     * called when user is moving the pointer's temp
     *
     * Ex: you can lock viewPager when temperature is moving
     */
    fun isMoving(isMoving: Boolean)
}