package com.alirnp.tempretureview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.alirnp.tempretureview.callback.OnSeekChangeListener
import com.alirnp.tempretureview.utils.CircleArea
import com.alirnp.tempretureview.utils.SizeConvertor
import kotlin.math.min
import kotlin.math.roundToInt

class TemperatureView : View {


    private val sizeConverter by lazy { SizeConvertor(context) }

    private var onSeekChangeListener: OnSeekChangeListener? = null

    private val DEFAULT_TEXT_CENTER = "Celsius"
    private val DEFAULT_TEXT_BOTTOM = "Freezer Temp"
    private val DEFAULT_COLOR_PRIMARY = Color.parseColor("#1a8dff")
    private val DEFAULT_START_DEGREE = -90f
    private val DEFAULT_MAX_SWEEP_DEGREE = 300f
    private val DEFAULT_STROKE_WIDTH = sizeConverter.dpToPx(20f).toInt()
    private val DEFAULT_VALUE = 0
    private val DEFAULT_MIN_VALUE = -10
    private val DEFAULT_MAX_VALUE = 14
    private val DEFAULT_TOP_TEXT_SIZE = sizeConverter.dpToPx(32f).toInt()
    private val DEFAULT_BOTTOM_TEXT_SIZE: Int = sizeConverter.dpToPx(28f).toInt()

    private var mRadiusBackgroundProgress = sizeConverter.dpToPx(80f)
    private val mRadiusCircleValueShadowShader by lazy { mPaintBackgroundProgress.strokeWidth }
    private val mRadiusCircleValueShadow by lazy { mRadiusCircleValueShadowShader / 2f }
    private val mRadiusCircleValue by lazy { mRadiusCircleValueShadow / 1.4f }
    private val mRadiusCircleValueCenter by lazy { mRadiusCircleValue / 2f }

    private var lengthOfHandClock: Float = 0f
    private var mFloatMaxSweepDegree = DEFAULT_MAX_SWEEP_DEGREE
    private var mFloatPointerDegree: Float = 0f
    private var mFloatCenterXCircle = 0f
    private var mFloatCenterYCircle: Float = 0f
    private var mFloatBeginOfClockLines = 0f
    private var mFloatEndOfClockLines: Float = 0f

    private var mColorPrimary = DEFAULT_COLOR_PRIMARY
    private var mColorProgressBackground: Int = 0
    private var mIntegerValue = 0
    private var mIntegerMinValue: Int = 0
    private var mIntegerMaxValue: Int = 0
    private var mIntegerStrokeWidth = DEFAULT_STROKE_WIDTH
    private var mHeightBackgroundProgress = 0
    private var mWidthBackgroundProgress: Int = 0
    private var mTextSizeTop = 0
    private var mTextSizeBottom: Int = 0

    private var accessMoving = false

    private var mRectBackground: RectF = RectF()
    private var mRectProgress: RectF = RectF()


    private val mPaintBackground by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }

    private val mPaintBackgroundProgress by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = mIntegerStrokeWidth.toFloat()
            color = adjustAlpha(mColorProgressBackground, 0.08f)
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

    private var mStringTextCenter: String = DEFAULT_TEXT_CENTER
    private var mStringTextBottom: String = DEFAULT_TEXT_BOTTOM
    private lateinit var mContext: Context
    private var mCircleArea: CircleArea = CircleArea()


    /**
     * onSizeChanged holder values
     */
    private var w = -1
    private var h: Int = -1
    private var oldw: Int = -1
    private var oldh: Int = -1

    /**
     *  inflating a view Programmatically
     */
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    /**
     *  inflating a view via XML
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    /**
     *  started before constructor called
     */
    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //Width

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
        //Height
        val heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightMeasureSize = MeasureSpec.getSize(heightMeasureSpec)
        var height = 0
        when (heightMeasureMode) {
            MeasureSpec.UNSPECIFIED -> height = getDesireHeight()
            MeasureSpec.EXACTLY -> height = heightMeasureSize
            MeasureSpec.AT_MOST -> height = Math.min(heightMeasureSize, getDesireHeight())
        }
        //Radius
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
        canvas.save()
        canvas.rotate(
            DEFAULT_START_DEGREE,
            centerX.toFloat(),
            centerY.toFloat()
        )
        addPointerLocation()
        addPointerArea()
        drawBackground(canvas, centerX, centerY)
        drawArcs(canvas)
        drawPointer(canvas)
        drawHandClock(canvas)
        canvas.restore() // restore canvas to default to display the text correctly
        drawTexts(canvas, centerX)
    }


    private fun getDrawXOnBackgroundProgress(
        degree: Float,
        backgroundRadius: Float,
        backgroundWidth: Float
    ): Float {
        var drawX = Math.cos(Math.toRadians(degree.toDouble())).toFloat()
        drawX *= backgroundRadius
        drawX += backgroundWidth / 2
        return drawX
    }

    private fun getDrawYOnBackgroundProgress(
        degree: Float,
        backgroundRadius: Float,
        backgroundHeight: Float
    ): Float {
        var drawY = Math.sin(Math.toRadians(degree.toDouble())).toFloat()
        drawY *= backgroundRadius
        drawY += backgroundHeight / 2
        return drawY
    }


    private fun getAngleFromPoint(
        firstPointX: Double,
        firstPointY: Double,
        secondPointX: Double,
        secondPointY: Double
    ): Double {
        if (secondPointX > firstPointX) {
            return Math.atan2(
                secondPointX - firstPointX,
                firstPointY - secondPointY
            ) * 180 / Math.PI
        } else if (secondPointX < firstPointX) {
            return 360 - Math.atan2(
                firstPointX - secondPointX,
                firstPointY - secondPointY
            ) * 180 / Math.PI
        }
        return Math.atan2(0.0, 0.0)
    }

    private fun getValueFromAngel(angel: Double): Double {
        return (angel / getDegreePerHand()) + mIntegerMinValue
    }

    private fun getDegreePerHand(): Float {
        return mFloatMaxSweepDegree / getLeftValue()
    }

    private fun getLeftValue(): Float {
        return (mIntegerMaxValue - mIntegerMinValue).toFloat()
    }

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
        var radius = radius
        radius = radius + radius / 2
        mCircleArea.setXStart(centerX - radius)
        mCircleArea.setXEnd(centerX + radius)
        mCircleArea.setYStart(centerY - radius)
        mCircleArea.setYEnd(centerY + radius)
        return mCircleArea
    }

    private fun validateValue(value: Int): Int {
        var value = value
        if (value > mIntegerMaxValue) value = mIntegerMaxValue
        if (value < mIntegerMinValue) value = mIntegerMinValue
        return value
    }

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
        var i = 0
        while (i < 360) {
            // i = angel
            x1 = Math.cos(Math.toRadians(i.toDouble()))
                .toFloat() * (mFloatBeginOfClockLines - 0) + (mWidthBackgroundProgress / 2).toFloat()
            y1 = Math.sin(Math.toRadians(i.toDouble()))
                .toFloat() * (mFloatBeginOfClockLines - 0) + (mHeightBackgroundProgress / 2).toFloat()
            x2 = Math.cos(Math.toRadians(i.toDouble()))
                .toFloat() * (mFloatEndOfClockLines - 4) + (mWidthBackgroundProgress / 2).toFloat()
            y2 = Math.sin(Math.toRadians(i.toDouble()))
                .toFloat() * (mFloatEndOfClockLines - 4) + (mHeightBackgroundProgress / 2).toFloat()
            canvas.drawLine(x1, y1, x2, y2, mPaintDegree)
            i += 12
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
        canvas.drawArc(mRectBackground, 0f, mFloatMaxSweepDegree, false, mPaintBackgroundProgress)
        canvas.drawArc(mRectBackground, 0f, mFloatPointerDegree, false, mPaintProgress)
    }

    private fun drawBackground(canvas: Canvas, centerX: Int, centerY: Int) {
        canvas.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            mRadiusBackgroundProgress - mPaintBackgroundProgress.strokeWidth,
            mPaintBackground
        )
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
                    // TODO: 7/5/2021    break removed
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
                    val `val` = getValueFromAngel(mFloatPointerDegree.toDouble())
                    val currentValue =
                        Math.round(`val`).toInt() // for successfully convert angel to value
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

    override fun performClick(): Boolean {
        return super.performClick()
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
    fun setValues(textBottom: String, value: Int, minValue: Int, maxValue: Int) {
        if (allowLayoutChange()) {
            mIntegerValue = validateValue(value)
            mIntegerMinValue = minValue
            mIntegerMaxValue = maxValue
            mStringTextBottom = textBottom
            if (canCalledOnSizeChanged()) {
                onSizeChanged(w, h, oldw, oldh)
            }
            requestLayout()
        }
    }

    /**
     * The minimum & maximum value you can set to the view
     */
    fun setValues(@StringRes textBottom: Int, value: Int, minValue: Int, maxValue: Int) {
        val text = mContext.getString(textBottom)
        setValues(text, value, minValue, maxValue)
    }

    @ColorInt
    private fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
        val alpha = (Color.alpha(color) * factor).roundToInt()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        initAttrs(attrs)
    }


    private fun initAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        val a = mContext.theme.obtainStyledAttributes(attrs, R.styleable.TempView, 0, 0)
        try {
            mStringTextBottom =
                a.getString(R.styleable.TempView_tmv_text_bottom) ?: DEFAULT_TEXT_BOTTOM
            mColorPrimary = a.getColor(
                R.styleable.TempView_tmv_color_primary, DEFAULT_COLOR_PRIMARY
            )
            mColorProgressBackground =
                a.getColor(R.styleable.TempView_tmv_color_progress_background, mColorPrimary)
            mFloatMaxSweepDegree = a.getFloat(
                R.styleable.TempView_tmv_value_max_sweep_degree, DEFAULT_MAX_SWEEP_DEGREE
            )
            mIntegerStrokeWidth = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_stroke_width, DEFAULT_STROKE_WIDTH
            )
            mTextSizeTop = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_text_top, DEFAULT_TOP_TEXT_SIZE
            )
            mTextSizeBottom = a.getDimensionPixelSize(
                R.styleable.TempView_tmv_size_text_bottom, DEFAULT_BOTTOM_TEXT_SIZE
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