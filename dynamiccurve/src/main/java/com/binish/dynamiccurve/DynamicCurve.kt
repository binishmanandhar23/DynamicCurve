package com.binish.dynamiccurve

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.binish.dynamiccurve.enums.XYControls

class DynamicCurve : ConstraintLayout {
    private var mContext: Context? = null
    var paint: Paint? = null
    var paint2: Paint? = null
    var path: Path? = null
    internal var cx1 = 0f
    internal var cx2 = 0f

    var shadowEnabled = false
    var shadowRadius = 2f
    var shadowDx = 1f
    var shadowDy = 1f
    var shadowColor = R.color.shadow_color
    var reverse: Boolean = false
    var mirror: Boolean = false
    var upsideDown: Boolean = false
    var halfWidth: Boolean = false
    var decreaseHeightBy: Float = 0.0f
    private var isInPx: Boolean = false
    private var deltaDivisible: Float = 10f

    private var x0: Float? = 0.0f
    private var x1: Float? = 0.0f
    private var x2: Float? = 0.0f
    private var x3: Float? = 0.0f
    private var y0: Float? = 0.0f
    private var y1: Float? = 0.0f
    private var y2: Float? = 0.0f
    private var y3: Float? = 0.0f

    private var x1a: Float? = 0.0f
    private var x2a: Float? = 0.0f
    private var x3a: Float? = 0.0f
    private var y1a: Float? = 0.0f
    private var y2a: Float? = 0.0f
    private var y3a: Float? = 0.0f

    private var upsideDownCalculated: Boolean = true
    private var listener: DynamicCurveAdapter? = null

    var paintColor: Int = ContextCompat.getColor(context, R.color.color_orange)

    constructor(context: Context) : super(context) {
        this.mContext = context
        initializeXY(null)
        init()
    }


    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        this.mContext = context
        val attribute = context.obtainStyledAttributes(attributeSet, R.styleable.DynamicCurve)
        val curveColor = attribute.getColor(
            R.styleable.DynamicCurve_curveColor,
            ContextCompat.getColor(context, R.color.color_white)
        )

        shadowEnabled = attribute.getBoolean(R.styleable.DynamicCurve_enableShadow, false)
        shadowRadius = attribute.getFloat(R.styleable.DynamicCurve_shadowRadius, 2f)
        shadowDx = attribute.getFloat(R.styleable.DynamicCurve_shadowDx, 1f)
        shadowDy = attribute.getFloat(R.styleable.DynamicCurve_shadowDy, 1f)
        shadowColor = attribute.getResourceId(R.styleable.DynamicCurve_shadowColor, R.color.shadow_color)

        mirror = attribute.getBoolean(R.styleable.DynamicCurve_mirror, false)
        reverse = attribute.getBoolean(R.styleable.DynamicCurve_reverse, false)
        upsideDown = attribute.getBoolean(R.styleable.DynamicCurve_upsideDown, false)
        upsideDownCalculated = !upsideDown
        decreaseHeightBy = attribute.getFloat(R.styleable.DynamicCurve_decreaseHeightBy, 0f)
        isInPx = attribute.getBoolean(R.styleable.DynamicCurve_isInPx, false)
        paintColor = curveColor
        deltaDivisible = attribute.getFloat(R.styleable.DynamicCurve_deltaDivisible, 10f)

        initializeXY(attribute)

        if (mirror) ifMirrored() //For mirrored
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.DynamicCurve)
        initializeXY(attribute)
        init()
    }

    private fun init() {
        setWillNotDraw(false)

        /*********************From init()*******************/
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        paint!!.color = paintColor
        //        paint.setStrokeWidth(4);

        //For Shadows
        if(shadowEnabled) {
            paint!!.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
            setLayerType(LAYER_TYPE_SOFTWARE, paint)
        }


        path = Path()

        paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint2!!.style = Paint.Style.STROKE
        paint2!!.strokeWidth = 3f
        paint2!!.color = resources.getColor(R.color.color_purple)
        /****************************************************/

        setBackgroundResource(R.color.color_transparent)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val height = height
        val width = width
        val delta = width / deltaDivisible
        val deltaHeight = height / deltaDivisible

        ifUpsideDown(height.toFloat(), delta) //For upSideDown

        path?.moveTo(
            if (isInPx) dp2px(x0!!) else x0!! * delta,
            when {
                y0 == null -> height.toFloat() - (decreaseHeightBy * deltaHeight)
                isInPx -> dp2px(
                    y0!! - decreaseHeightBy
                )
                else -> (y0!! - decreaseHeightBy) * delta
            }
        )
        path?.cubicTo(
            if (isInPx) dp2px(x1!!) else x1!! * delta,
            when {
                y1 == null -> height.toFloat() - (decreaseHeightBy * deltaHeight)
                isInPx -> dp2px(y1!! - decreaseHeightBy)
                else -> (y1!! - decreaseHeightBy
                        ) * delta
            },
            if (isInPx) dp2px(x2!!) else x2!! * delta,
            when {
                y2 == null -> height.toFloat() - (decreaseHeightBy * deltaHeight)
                isInPx -> dp2px(
                    y2!! - decreaseHeightBy
                )
                else -> (y2!! - decreaseHeightBy) * delta
            },
            if (x3 == null && !halfWidth) width.toFloat() else if (x3 == null && halfWidth) width.toFloat() / 2 else if (isInPx) dp2px(
                x3!!
            ) else x3!! * delta,
            when {
                y3 == null -> height.toFloat() - (decreaseHeightBy * deltaHeight)
                isInPx -> dp2px(
                    y3!! - decreaseHeightBy
                )
                else -> (y3!! - decreaseHeightBy) * delta
            }
        )

        if (x3 == null && halfWidth) {
            path?.cubicTo(
                if (isInPx) dp2px(x1a!!) else x1a!! * delta,
                if (isInPx) dp2px(y1a!! - decreaseHeightBy) else (y1a!! - decreaseHeightBy) * delta,
                if (isInPx) dp2px(x2a!!) else x2a!! * delta,
                if (isInPx) dp2px(y2a!! - decreaseHeightBy) else (y2a!! - decreaseHeightBy) * delta,
                width.toFloat(),
                if (isInPx) dp2px(y3a!! - decreaseHeightBy) else (y3a!! - decreaseHeightBy) * delta
            )
        }

        if (reverse) {
            path?.lineTo(width.toFloat(), 0f)
            path?.lineTo(0f, 0f)
        } else {
            path?.lineTo(width.toFloat(), height.toFloat())
            path?.lineTo(0f, height.toFloat())
        }

        path?.lineTo(0f, height.toFloat())
        path?.close()

        canvas?.drawPath(path!!, paint!!)
    }

    private fun dp2px(dp: Float): Float {
        return (mContext?.resources?.displayMetrics!!.density * dp + 0.5f)
    }

    private fun ifMirrored() {
        //NOTE: '*' means un-mirrored value
        val dummyX1 = x1
        val dummyX2 = x2
        val dummyY0 = y0
        val dummyY1 = y1
        val dummyY2 = y2
        val dummyY3 = y3
        x1 = if (x2 != null) deltaDivisible - dummyX2!! else x1 //If mirrored -> [width - x2*]
        x2 = if (x1 != null) deltaDivisible - dummyX1!! else x2 //If mirrored -> [width - x1*]
        y0 = if (y3 != null) dummyY3 else y0 //If mirrored -> y3*
        y1 = if (y2 != null) dummyY2 else y1 //If mirrored -> y2*
        y2 = if (y1 != null) dummyY1 else y2 //If mirrored -> y1*
        y3 = if (y0 != null) dummyY0 else y3//If mirrored -> y0*
    }

    private fun ifUpsideDown(height: Float, delta: Float) {
        if (!upsideDownCalculated) {
            val dummyY0 = y0
            val dummyY1 = y1
            val dummyY2 = y2
            val dummyY3 = y3
            y0 = height / delta - (dummyY0 ?: 0f)
            y1 = height / delta - (dummyY1 ?: 0f)
            y2 = height / delta - (dummyY2 ?: 0f)
            y3 = height / delta - (dummyY3 ?: 0f)
            if(x3==null && halfWidth){
                val dummyY1a = y1a
                val dummyY2a = y2a
                val dummyY3a = y3a
                y1a = height / delta - (dummyY1a ?: 0f)
                y2a = height / delta - (dummyY2a ?: 0f)
                y3a = height / delta - (dummyY3a ?: 0f)
            }
            upsideDownCalculated = true
        }
    }

    private fun checkFullHeight(value: String?): Float? {
        return if (value.equals("height", ignoreCase = true))
            null else value?.toFloat()
    }

    private fun initializeXY(attribute: TypedArray?) {
        x0 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x0))
                ?: 0f else 0f
        x1 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x1))
                ?: 0f else 0f
        x2 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x2))
                ?: 0f else 0f
        x3 = if (attribute != null) checkStringValues(
            attribute.getString(R.styleable.DynamicCurve_x3),
            true
        ) else null

        y0 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y0))
                ?: 0f else 0f
        y1 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y1))
                ?: 0f else 0f
        y2 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y2))
                ?: 0f else 0f
        y3 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y3))
                ?: 0f else 0f

        if (x3 == null) {
            x1a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x1a))
                    ?: 0f else 0f
            x2a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x2a))
                    ?: 0f else 0f
            x3a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x3a))
                    ?: 0f else 0f
            y1a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y1a))
                    ?: 0f else 0f
            y2a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y2a))
                    ?: 0f else 0f
            y3a =
                if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y3a))
                    ?: 0f else 0f
        }
    }

    private fun checkStringValues(values: String?): Float? {
        return checkStringValues(values, false)
    }

    private fun checkStringValues(values: String?, isX3: Boolean): Float? {
        return if (isX3)
            when {
                values.equals("width", ignoreCase = true) -> {
                    halfWidth = false
                    listener?.isHalfWidth(false)
                    null
                }
                values.equals("half", ignoreCase = true) -> {
                    halfWidth = true
                    listener?.isHalfWidth(true)
                    null
                }
                else -> {
                    halfWidth = false
                    values?.toFloat()
                }
            } else values?.toFloat()
    }

    fun changeCurveColor(color: Int) {
        paintColor = color
        init()
        invalidate()
    }

    fun changeBackgroundColor(colorResId: Int) {
        setBackgroundResource(colorResId)
    }

    fun changeValues(
        x0: String,
        y0: String,
        x1: String,
        y1: String,
        x2: String,
        y2: String,
        x3: String,
        y3: String
    ) {
        this.x0 = checkStringValues(x0)
        this.x1 = checkStringValues(x1)
        this.x2 = checkStringValues(x2)
        this.x3 = checkStringValues(x3, true)

        this.y0 = checkFullHeight(y0)
        this.y1 = checkFullHeight(y1)
        this.y2 = checkFullHeight(y2)
        this.y3 = checkFullHeight(y3)

        init()
        invalidate()
    }

    fun changeValues(xyType: XYControls, value: String) {
        when (xyType) {
            XYControls.X0 -> this.x0 = checkStringValues(value)
            XYControls.X1 -> this.x1 = checkStringValues(value)
            XYControls.X2 -> this.x2 = checkStringValues(value)
            XYControls.X3 -> this.x3 = checkStringValues(value, true)
            XYControls.Y0 -> this.y0 = checkStringValues(value)
            XYControls.Y1 -> this.y1 = checkStringValues(value)
            XYControls.Y2 -> this.y2 = checkStringValues(value)
            XYControls.Y3 -> this.y3 = checkStringValues(value)
            XYControls.X1a -> this.x1a = checkStringValues(value)
            XYControls.X2a -> this.x2a = checkStringValues(value)
            XYControls.X3a -> this.x3a = checkStringValues(value)
            XYControls.Y1a -> this.y1a = checkStringValues(value)
            XYControls.Y2a -> this.y2a = checkStringValues(value)
            XYControls.Y3a -> this.y3a = checkStringValues(value)
        }
        init()
        invalidate()
    }

    fun getValue(xyType: XYControls): Float {
        return when (xyType) {
            XYControls.X0 -> this.x0
            XYControls.X1 -> this.x1
            XYControls.X2 -> this.x2
            XYControls.X3 -> if (x3 != null) this.x3 else if (halfWidth) deltaDivisible / 2 else deltaDivisible
            XYControls.Y0 -> this.y0
            XYControls.Y1 -> this.y1
            XYControls.Y2 -> this.y2
            XYControls.Y3 -> this.y3
            XYControls.X1a -> this.x1a
            XYControls.X2a -> this.x2a
            XYControls.X3a -> this.x3a
            XYControls.Y1a -> this.y1a
            XYControls.Y2a -> this.y2a
            XYControls.Y3a -> this.y3a
        } ?: 0.0f
    }

    fun isShadow(value: Boolean){
        shadowEnabled = value
        init()
        invalidate()
    }

    fun setCurveShadowRadius(value: Float){
        shadowRadius = value
        init()
        invalidate()
    }

    fun setCurveShadowDx(value: Float){
        shadowDx = value
        init()
        invalidate()
    }

    fun setCurveShadowDy(value: Float){
        shadowDy = value
        init()
        invalidate()
    }

    fun isMirrored(value: Boolean) {
        mirror = value
        ifMirrored()
        init()
        invalidate()
    }

    fun isReversed(value: Boolean) {
        reverse = value
        listener?.isReversed(reverse)
        init()
        invalidate()
    }

    fun isInverted(value: Boolean) {
        upsideDown = value
        upsideDownCalculated = false
        init()
        invalidate()
    }

    fun decreaseHeightBy(value: String) {
        decreaseHeightBy = checkStringValues(value) ?: 0f
        init()
        invalidate()
    }

    fun isInPx(value: Boolean) {
        isInPx = value
        init()
        invalidate()
    }

    fun deltaDivisible(value: String) {
        deltaDivisible = checkStringValues(value) ?: 10f
        init()
        invalidate()
    }

    fun setUpListener(listener: DynamicCurveAdapter) {
        this.listener = listener
    }

    interface DynamicCurveInteraction {
        fun isHalfWidth(halfWidth: Boolean)
        fun isReversed(reversed: Boolean)
    }

    abstract class DynamicCurveAdapter : DynamicCurveInteraction {
        override fun isHalfWidth(halfWidth: Boolean) {}
        override fun isReversed(reversed: Boolean) {}
    }
}