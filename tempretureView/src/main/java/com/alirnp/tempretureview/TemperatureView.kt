package com.alirnp.tempretureview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import com.alirnp.tempretureview.callback.OnSeekChangeListener
import com.alirnp.tempretureview.utils.*
import kotlin.math.*

class TemperatureView constructor(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val sizeConverter by lazy { SizeConvertor(context) }

    private var onSeekChangeListener: OnSeekChangeListener? = null

    private val defaultColorPrimary = Color.parseColor("#1a8dff")
    private val defaultStrokeWidth = sizeConverter.dpToPx(20f).toInt()
    private val defaultTopTextSize = sizeConverter.dpToPx(32f).toInt()
    private val defaultBottomTextSize: Int = sizeConverter.dpToPx(28f).toInt()
    private var mRadiusBackgroundProgress = sizeConverter.dpToPx(80f)

    private val mRadiusCircleValueShadowShader by lazy { mPaintBackgroundProgress.strokeWidth }
    private val mRadiusCircleValueShadow by lazy { mRadiusCircleValueShadowShader / 2f }
    private val mRadiusCircleValue by lazy { mRadiusCircleValueShadow / 1.4f }
    private val mRadiusCircleValueCenter by lazy { mRadiusCircleValue / 2f }

    private var mFloatMaxSweepDegree = DEFAULT_MAX_SWEEP_DEGREE
    private var lengthOfHandClock: Float = 0f
    private var mFloatPointerDegree: Float = 0f
    private var mFloatCenterXCircle = 0f
    private var mFloatCenterYCircle: Float = 0f
    private var mFloatBeginOfClockLines = 0f
    private var mFloatEndOfClockLines: Float = 0f

    private var mColorPrimary = defaultColorPrimary
    private var mIntegerValue = 0
    private var mIntegerMinValue: Int = 0
    private var mIntegerMaxValue: Int = 0
    private var mIntegerStrokeWidth = defaultStrokeWidth
    private var mHeightBackgroundProgress = 0
    private var mWidthBackgroundProgress: Int = 0
    private var mTextSizeTop = 0
    private var mTextSizeBottom: Int = 0

    private var mRectBackground: RectF = RectF()
    private var mRectProgress: RectF = RectF()

    /**
     * when pointer is seeking, accessMoving is ture
     */
    private var accessMoving = false

    private val mPaintBackground by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }

    private val mPaintBackgroundProgress by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = mIntegerStrokeWidth.toFloat()
            color = adjustAlpha(mColorPrimary, 0.08f)
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mPaintProgress by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = (mIntegerStrokeWidth / 3).toFloat()
            color = mColorPrimary
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mPaintValue by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }

    private val mPaintCenter by lazy {
        Paint().apply {
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }


    private val mPaintDegree by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = mPaintBackgroundProgress.strokeWidth / 7f
            color = mColorPrimary
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private val mPaintTopText by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            textSize = mTextSizeTop.toFloat()
        }
    }

    private val mPaintCenterText by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            textSize = mTextSizeTop / 1.5f
        }
    }

    private val mPaintBottomText by lazy {
        Paint().apply {
            isAntiAlias = true
            color = mColorPrimary
            textAlign = Paint.Align.CENTER
            style = Paint.Style.FILL_AND_STROKE
            textSize = mTextSizeBottom.toFloat()
        }
    }

    private val mPaintValueShadow: Paint = Paint()

    private var mStringTextCenter = "Celsius"
    private var mStringTextBottom: String = DEFAULT_TEXT_BOTTOM
    private var mCircleArea: CircleArea = CircleArea()


    /**
     * onSizeChanged holder values
     */
    private var w = -1
    private var h: Int = -1
    private var oldw: Int = -1
    private var oldh: Int = -1

    /**
     *  started before constructor called
     */
    init {
        attrs?.let { attributes ->
            initAttributes(attributes)
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //Width
        val widthMeasureMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec)

        var width = 0
        when (widthMeasureMode) {
            MeasureSpec.UNSPECIFIED -> width = getDesireWidth()
            MeasureSpec.EXACTLY -> width = widthMeasureSize
            MeasureSpec.AT_MOST -> width = min(widthMeasureSize, getDesireWidth())
        }

        //Height
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        when (heightMeasureMode) {
            MeasureSpec.UNSPECIFIED -> height = getDesireHeight()
            MeasureSpec.EXACTLY -> height = heightMeasureSize
            MeasureSpec.AT_MOST -> height = heightMeasureSize.coerceAtMost(getDesireHeight())
        }

        //Radius
        mRadiusBackgroundProgress =
            if (widthMeasureMode == MeasureSpec.EXACTLY && heightMeasureMode == MeasureSpec.EXACTLY) {
                val size = min(widthMeasureSize, heightMeasureSize)
                (size - mPaintBackgroundProgress.strokeWidth) / 2
            } else if (widthMeasureMode == MeasureSpec.AT_MOST || heightMeasureMode == MeasureSpec.AT_MOST) {
                val size: Int = min(width - getHorizontalPadding(), height - getVerticalPadding())
                (size - mPaintBackgroundProgress.strokeWidth) / 2
            } else {
                //Default Value
                sizeConverter.dpToPx(80f)
            }
        mWidthBackgroundProgress = width
        mHeightBackgroundProgress = height


        //Tell to system measured size in px
        setMeasuredDimension(width, height)
    }

    /**
     * return the view's desire width when width size is {wrap_content}
     */
    private fun getDesireWidth(): Int {
        return (mRadiusBackgroundProgress * 2 + mPaintBackgroundProgress.getStrokeWidth() + getHorizontalPadding()).toInt()
    }

    /**
     * return the view's desire height when height size is {wrap_content}
     */
    private fun getDesireHeight(): Int {
        return (mRadiusBackgroundProgress * 2 + mPaintBackgroundProgress.getStrokeWidth() + getVerticalPadding()).toInt()
    }


    /**
     * return sum of paddingTop and paddingBottom
     */
    private fun getVerticalPadding(): Int {
        return paddingTop + paddingBottom
    }

    /**
     * return sum of paddingLeft and paddingRight
     */
    private fun getHorizontalPadding(): Int {
        return paddingLeft + paddingRight
    }


    private fun canCalledOnSizeChanged(): Boolean {
        return w > -1 && w > -1 && oldw > -1 && oldh > -1
    }

    /**
     * don't allow change values when user is seeking the pointer
     */
    private fun allowLayoutChange(): Boolean {
        return !accessMoving
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.w = w
        this.h = h
        this.oldw = oldw
        this.oldh = oldh

        mFloatBeginOfClockLines = mRadiusBackgroundProgress

        mFloatEndOfClockLines = mRadiusBackgroundProgress - mPaintBackgroundProgress.strokeWidth / 2

        lengthOfHandClock = mFloatBeginOfClockLines - mFloatEndOfClockLines

        mRectBackground[mWidthBackgroundProgress.toFloat() / 2 - mRadiusBackgroundProgress + mPaintBackgroundProgress.strokeWidth + lengthOfHandClock, mHeightBackgroundProgress.toFloat() / 2 - mRadiusBackgroundProgress + mPaintBackgroundProgress.strokeWidth + lengthOfHandClock, mWidthBackgroundProgress.toFloat() / 2 + mRadiusBackgroundProgress - mPaintBackgroundProgress.strokeWidth - lengthOfHandClock] =
            mHeightBackgroundProgress.toFloat() / 2 + mRadiusBackgroundProgress - mPaintBackgroundProgress.strokeWidth - lengthOfHandClock

        mRectProgress[mWidthBackgroundProgress.toFloat() / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock), mHeightBackgroundProgress.toFloat() / 2 - mRadiusBackgroundProgress + (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock), mWidthBackgroundProgress.toFloat() / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth - lengthOfHandClock)] =
            mHeightBackgroundProgress.toFloat() / 2 + mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth - lengthOfHandClock)

        val sumValues = (mIntegerMaxValue - mIntegerMinValue).toFloat()

        mFloatPointerDegree = (mIntegerValue - mIntegerMinValue) * mFloatMaxSweepDegree / sumValues

        val colors = intArrayOf(mColorPrimary, Color.RED)

        val positions = floatArrayOf(0.25f, 1f)

        val gradient = SweepGradient(
            (mWidthBackgroundProgress / 2).toFloat(),
            (mHeightBackgroundProgress / 2).toFloat(),
            colors,
            positions
        )

        val gradientMatrix = Matrix()

        gradientMatrix.preRotate(-5f)

        gradient.setLocalMatrix(gradientMatrix)

        mPaintProgress.shader = gradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2
        val centerY = height / 2

        // save and rotate canvas
        canvas.save()
        canvas.rotate(DEFAULT_START_DEGREE, centerX.toFloat(), centerY.toFloat())

        // pointer
        addPointerLocation()
        addPointerArea()

        // arcs
        drawArcs(canvas)

        // pointer
        drawPointer(canvas)

        // hand clock
        drawHandClock(canvas)


        // restore canvas to default to display the text correctly
        canvas.restore()

        //texts
        drawTexts(canvas, centerX)
    }

    /**
     * get center x from degree of view
     */
    private fun getDrawXOnBackgroundProgress(
        degree: Float,
        backgroundRadius: Float,
        backgroundWidth: Float
    ): Float {
        var drawX = cos(Math.toRadians(degree.toDouble())).toFloat()
        drawX *= backgroundRadius
        drawX += backgroundWidth / 2
        return drawX
    }

    /**
     * get center y from degree of view
     */
    private fun getDrawYOnBackgroundProgress(
        degree: Float,
        backgroundRadius: Float,
        backgroundHeight: Float
    ): Float {
        var drawY = sin(Math.toRadians(degree.toDouble())).toFloat()
        drawY *= backgroundRadius
        drawY += backgroundHeight / 2
        return drawY
    }

    /**
     * get angel from x and y
     */
    private fun getAngleFromPoint(
        firstPointX: Double,
        firstPointY: Double,
        secondPointX: Double,
        secondPointY: Double
    ): Double {
        if (secondPointX > firstPointX) {
            return atan2(
                secondPointX - firstPointX,
                firstPointY - secondPointY
            ) * 180 / Math.PI
        } else if (secondPointX < firstPointX) {
            return 360 - atan2(
                firstPointX - secondPointX,
                firstPointY - secondPointY
            ) * 180 / Math.PI
        }
        return atan2(0.0, 0.0)
    }

    /**
     * calculate the temp from angel
     */
    private fun getValueFromAngel(angel: Double): Double {
        return (angel / getDegreePerHand()) + mIntegerMinValue
    }

    /**
     * calculate degree between hand clock
     */
    private fun getDegreePerHand(): Float {
        return mFloatMaxSweepDegree / getLeftValue()
    }

    /**
     * calculate value between min and max value
     */
    private fun getLeftValue(): Float {
        return (mIntegerMaxValue - mIntegerMinValue).toFloat()
    }

    /**
     * draw texts in canvas
     */
    private fun drawTexts(canvas: Canvas, centerX: Int) {
        canvas.drawText(
            String.format("%s°C", mIntegerValue),
            centerX.toFloat(),
            mHeightBackgroundProgress.toFloat() / 2 - mRadiusBackgroundProgress / 5,
            mPaintTopText
        )
        canvas.drawText(
            mStringTextCenter, centerX.toFloat(),
            mHeightBackgroundProgress.toFloat() / 2, mPaintCenterText
        )
        canvas.drawText(
            mStringTextBottom,
            centerX.toFloat(),
            mHeightBackgroundProgress.toFloat() / 2 + mRadiusBackgroundProgress / 3,
            mPaintBottomText
        )
    }

    private fun getCircleArea(centerX: Float, centerY: Float, radius: Float): CircleArea {
        var mRadius = radius

        mRadius += mRadius / 2
        mCircleArea.setXStart(centerX - mRadius)
        mCircleArea.setXEnd(centerX + mRadius)
        mCircleArea.setYStart(centerY - mRadius)
        mCircleArea.setYEnd(centerY + mRadius)
        return mCircleArea
    }

    private fun validateValue(value: Int): Int {
        var mValue = value
        if (mValue > mIntegerMaxValue) mValue = mIntegerMaxValue
        if (mValue < mIntegerMinValue) mValue = mIntegerMinValue
        return mValue
    }

    /**
     * user can hold and seek circle pointer
     */
    private fun addPointerLocation() {
        mFloatCenterXCircle = getDrawXOnBackgroundProgress(
            mFloatPointerDegree,
            mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock),
            mWidthBackgroundProgress.toFloat()
        )
        mFloatCenterYCircle = getDrawYOnBackgroundProgress(
            mFloatPointerDegree,
            mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock),
            mHeightBackgroundProgress.toFloat()
        )
    }

    private fun drawHandClock(canvas: Canvas) {
        var x1: Float
        var y1: Float
        var x2: Float
        var y2: Float
        var angel = 0

        val maxDegree = mFloatMaxSweepDegree + (getDegreePerHand() / 2)

        while (angel < maxDegree) {
            // i = angel
            x1 = cos(Math.toRadians(angel.toDouble()))
                .toFloat() * (mFloatBeginOfClockLines - 0) + (mWidthBackgroundProgress / 2).toFloat()

            y1 = sin(Math.toRadians(angel.toDouble()))
                .toFloat() * (mFloatBeginOfClockLines - 0) + (mHeightBackgroundProgress / 2).toFloat()

            x2 = cos(Math.toRadians(angel.toDouble()))
                .toFloat() * (mFloatEndOfClockLines - 4) + (mWidthBackgroundProgress / 2).toFloat()

            y2 = sin(Math.toRadians(angel.toDouble()))
                .toFloat() * (mFloatEndOfClockLines - 4) + (mHeightBackgroundProgress / 2).toFloat()

            canvas.drawLine(x1, y1, x2, y2, mPaintDegree)
            angel += 12
        }
    }

    private fun drawPointer(canvas: Canvas) {
        val shader = RadialGradient(
            mFloatCenterXCircle,
            mFloatCenterYCircle,
            mRadiusCircleValueShadowShader,
            Color.parseColor("#33000000"),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        mPaintValueShadow.shader = shader
        canvas.drawCircle(
            mFloatCenterXCircle,
            mFloatCenterYCircle,
            mRadiusCircleValueShadow,
            mPaintValueShadow
        ) // shadow
        canvas.drawCircle(mFloatCenterXCircle, mFloatCenterYCircle, mRadiusCircleValue, mPaintValue)
        canvas.drawCircle(
            mFloatCenterXCircle,
            mFloatCenterYCircle,
            mRadiusCircleValueCenter,
            mPaintCenter
        )
    }

    private fun drawArcs(canvas: Canvas) {
        // background
        canvas.drawArc(mRectBackground, 0f, mFloatMaxSweepDegree, false, mPaintBackgroundProgress)

        // progress
        canvas.drawArc(mRectBackground, 0f, mFloatPointerDegree, false, mPaintProgress)
    }

    private fun addPointerArea() {
        //CIRCLE AREA FOR DETECT TOUCH
        val centerXCircleArea = getDrawXOnBackgroundProgress(
            mFloatPointerDegree - 90,
            mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock),
            mWidthBackgroundProgress.toFloat()
        )
        val centerYCircleArea = getDrawYOnBackgroundProgress(
            mFloatPointerDegree - 90,
            mRadiusBackgroundProgress - (mPaintBackgroundProgress.strokeWidth + lengthOfHandClock),
            mHeightBackgroundProgress.toFloat()
        )
        mCircleArea = getCircleArea(
            centerXCircleArea,
            centerYCircleArea,
            mPaintBackgroundProgress.strokeWidth
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val angel: Double
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val found =
                    x >= mCircleArea.getXStart() && x <= mCircleArea.getXEnd() && y >= mCircleArea.getYStart() && y <= mCircleArea.getYEnd()
                if (found) {
                    accessMoving = true
                    onSeekChangeListener?.onMoving(false)
                } else {
                    accessMoving = false
                }
            }
            MotionEvent.ACTION_MOVE -> if (accessMoving) {
                angel = getAngleFromPoint(
                    mWidthBackgroundProgress.toDouble() / 2,
                    mHeightBackgroundProgress.toDouble() / 2,
                    x.toDouble(),
                    y.toDouble()
                )
                if (angel > 0 && angel < mFloatMaxSweepDegree) {
                    mFloatPointerDegree = angel.toFloat()
                    val valueFromAngel = getValueFromAngel(mFloatPointerDegree.toDouble())
                    val currentValue = valueFromAngel.roundToInt() // for successfully convert angel to value
                    val valueChanged = currentValue != mIntegerValue
                    mIntegerValue = currentValue
                    onSeekChangeListener?.let { listener ->
                        if (valueChanged) listener.onSeekChange(
                            mIntegerValue
                        )
                    }

                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                onSeekChangeListener?.onMoving(true)
                onSeekChangeListener?.let { listener ->
                    if (accessMoving) listener.onSeekComplete(
                        mIntegerValue
                    )
                }

                accessMoving = false
                performClick()
            }
        }
        return true
    }


    /**
     * Announce changes
     */
    fun setOnSeekChangeListener(onSeekChangeListener: OnSeekChangeListener?) {
        this.onSeekChangeListener = onSeekChangeListener
    }

    /**
     * Get current value
     */
    fun getValue(): Int {
        return mIntegerValue
    }

    /**
     * Set current value
     *
     * @param value is show current value in view with validation
     */
    fun setValue(value: Int) {
        mIntegerValue = validateValue(value)
        invalidate()
    }

    /**
     * The minimum value you can set to the view
     */
    fun setMinValue(minValue: Int) {
        mIntegerMinValue = minValue
        invalidate()
    }

    /**
     * The maximum value you can set to the view
     */
    fun setMaxValue(maxValue: Int) {
        mIntegerMaxValue = maxValue
        invalidate()
    }

    /**
     * The minimum & maximum value you can set to the view
     */
    fun config(config: Config) {
        if (allowLayoutChange()) {
            mIntegerValue = validateValue(config.value)
            mIntegerMinValue = config.minValue
            mIntegerMaxValue = config.maxValue
            mStringTextBottom = config.text
            if (canCalledOnSizeChanged()) {
                onSizeChanged(w, h, oldw, oldh)
            }
            invalidate()
        }
    }

    @ColorInt
    private fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun initAttributes(attrs: AttributeSet) {

        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TempView, 0, 0)

        try {
            mStringTextBottom =
                a.getString(R.styleable.TempView_tmv_text_bottom) ?: DEFAULT_TEXT_BOTTOM

            mColorPrimary =
                a.getColor(R.styleable.TempView_tmv_color_primary, defaultColorPrimary)

            mFloatMaxSweepDegree = a.getFloat(
                R.styleable.TempView_tmv_value_max_sweep_degree, DEFAULT_MAX_SWEEP_DEGREE

            )
            mIntegerStrokeWidth = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_stroke_width, defaultStrokeWidth
            )

            mTextSizeTop = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_text_top, defaultTopTextSize
            )

            mTextSizeBottom = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_text_bottom, defaultBottomTextSize
            )

            mIntegerMinValue = a.getInteger(
                R.styleable.TempView_tmv_value_min, DEFAULT_MIN_VALUE
            )

            mIntegerMaxValue = a.getInteger(
                R.styleable.TempView_tmv_value_max, DEFAULT_MAX_VALUE
            )

            mIntegerValue = validateValue(
                a.getInteger(
                    R.styleable.TempView_tmv_value, DEFAULT_VALUE
                )
            )
        } finally {
            a.recycle()
        }
    }
}