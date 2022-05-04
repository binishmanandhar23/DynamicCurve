package com.binish.dynamiccurve

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.binish.dynamiccurve.data.CoreLogic
import com.binish.dynamiccurve.model.CurveProperties
import com.binish.dynamiccurve.model.CurveValues
import com.binish.dynamiccurve.enums.XYControls

class DynamicCurve : ConstraintLayout {
    private var mContext: Context? = null
    var paint: Paint? = null
    var paint2: Paint? = null
    var path: Path? = null
    internal var cx1 = 0f
    internal var cx2 = 0f

    var curveProperties = CurveProperties()
    var curveValue = CurveValues()

    lateinit var coreLogic: CoreLogic


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

        curveProperties = CurveProperties(
            shadowEnabled = attribute.getBoolean(R.styleable.DynamicCurve_enableShadow, false),
            shadowRadius = attribute.getFloat(R.styleable.DynamicCurve_shadowRadius, 2f),
            shadowDx = attribute.getFloat(R.styleable.DynamicCurve_shadowDx, 1f),
            shadowDy = attribute.getFloat(R.styleable.DynamicCurve_shadowDy, 1f),
            shadowColor = attribute.getResourceId(
                R.styleable.DynamicCurve_shadowColor,
                R.color.shadow_color
            ),
            mirror = attribute.getBoolean(R.styleable.DynamicCurve_mirror, false),
            reverse = attribute.getBoolean(R.styleable.DynamicCurve_reverse, false),
            upsideDown = attribute.getBoolean(R.styleable.DynamicCurve_upsideDown, false),
            upsideDownCalculated = !attribute.getBoolean(
                R.styleable.DynamicCurve_upsideDown,
                false
            ),
            decreaseHeightBy = attribute.getFloat(R.styleable.DynamicCurve_decreaseHeightBy, 0f),
            isInPx = attribute.getBoolean(R.styleable.DynamicCurve_isInPx, false),
            paintColor = curveColor,
            deltaDivisible = attribute.getFloat(R.styleable.DynamicCurve_deltaDivisible, 10f)
        )

        initializeXY(attribute)

//        if (curveProperties.mirror) ifMirrored() //For mirrored
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
        if (curveProperties.shadowEnabled) {
            paint!!.setShadowLayer(
                curveProperties.shadowRadius,
                curveProperties.shadowDx,
                curveProperties.shadowDy,
                ContextCompat.getColor(context, curveProperties.shadowColor)
            )
            setLayerType(LAYER_TYPE_SOFTWARE, paint)
        }

        coreLogic.setupCurveValuesAndProperties(curveValues = curveValue, curveProperties = curveProperties)
        coreLogic.initializePath(paint = paint)
        /*path = Path()

        paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
        paint2!!.style = Paint.Style.STROKE
        paint2!!.strokeWidth = 3f
        paint2!!.color = ContextCompat.getColor(context, R.color.color_purple)*/
        /****************************************************/

        setBackgroundResource(R.color.color_transparent)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        /*val height = height
        val width = width
        val delta = width / curveProperties.deltaDivisible
        val deltaHeight = height / curveProperties.deltaDivisible
        ifUpsideDown(height.toFloat(), delta) //For upSideDown
        curveValue.run {
            path?.moveTo(
                if (curveProperties.isInPx) dp2px(x0!!) else x0!! * delta,
                when {
                    y0 == null -> height.toFloat() - (curveProperties.decreaseHeightBy * deltaHeight)
                    curveProperties.isInPx -> dp2px(
                        y0!! - curveProperties.decreaseHeightBy
                    )
                    else -> (y0!! - curveProperties.decreaseHeightBy) * delta
                }
            )
            path?.cubicTo(
                if (curveProperties.isInPx) dp2px(x1!!) else x1!! * delta,
                when {
                    y1 == null -> height.toFloat() - (curveProperties.decreaseHeightBy * deltaHeight)
                    curveProperties.isInPx -> dp2px(y1!! - curveProperties.decreaseHeightBy)
                    else -> (y1!! - curveProperties.decreaseHeightBy) * delta
                },
                if (curveProperties.isInPx) dp2px(x2!!) else x2!! * delta,
                when {
                    y2 == null -> height.toFloat() - (curveProperties.decreaseHeightBy * deltaHeight)
                    curveProperties.isInPx -> dp2px(
                        y2!! - curveProperties.decreaseHeightBy
                    )
                    else -> (y2!! - curveProperties.decreaseHeightBy) * delta
                },
                if (x3 == null && !curveProperties.halfWidth) width.toFloat() else if (x3 == null && curveProperties.halfWidth) width.toFloat() / 2 else if (curveProperties.isInPx) dp2px(
                    x3!!
                ) else x3!! * delta,
                when {
                    y3 == null -> height.toFloat() - (curveProperties.decreaseHeightBy * deltaHeight)
                    curveProperties.isInPx -> dp2px(
                        y3!! - curveProperties.decreaseHeightBy
                    )
                    else -> (y3!! - curveProperties.decreaseHeightBy) * delta
                }
            )

            if (x3 == null && curveProperties.halfWidth) {
                path?.cubicTo(
                    if (curveProperties.isInPx) dp2px(x1a!!) else x1a!! * delta,
                    if (curveProperties.isInPx) dp2px(y1a!! - curveProperties.decreaseHeightBy) else (y1a!! - curveProperties.decreaseHeightBy) * delta,
                    if (curveProperties.isInPx) dp2px(x2a!!) else x2a!! * delta,
                    if (curveProperties.isInPx) dp2px(y2a!! - curveProperties.decreaseHeightBy) else (y2a!! - curveProperties.decreaseHeightBy) * delta,
                    width.toFloat(),
                    if (curveProperties.isInPx) dp2px(y3a!! - curveProperties.decreaseHeightBy) else (y3a!! - curveProperties.decreaseHeightBy) * delta
                )
            }

            if (curveProperties.reverse) {
                path?.lineTo(width.toFloat(), 0f)
                path?.lineTo(0f, 0f)
            } else {
                path?.lineTo(width.toFloat(), height.toFloat())
                path?.lineTo(0f, height.toFloat())
            }

            path?.lineTo(0f, height.toFloat())
            path?.close()

            canvas?.drawPath(path!!, paint!!)
        }*/

        coreLogic.drawLogic(canvas = canvas, height = height, width = width)

    }

    private fun checkFullHeight(value: String?): Float? {
        return if (value.equals("height", ignoreCase = true))
            null else value?.toFloat()
    }

    private fun initializeXY(attribute: TypedArray?) {
        curveValue = CurveValues(
            x0 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x0))
                ?: 0f else 0f,
            x1 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x1))
                ?: 0f else 0f,
            x2 =
            if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x2))
                ?: 0f else 0f,
            x3 = if (attribute != null) checkStringValues(
                attribute.getString(R.styleable.DynamicCurve_x3),
                true
            ) else null,

            y0 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y0))
                ?: 0f else 0f,
            y1 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y1))
                ?: 0f else 0f,
            y2 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y2))
                ?: 0f else 0f,
            y3 =
            if (attribute != null) checkFullHeight(attribute.getString(R.styleable.DynamicCurve_y3))
                ?: 0f else 0f
        ).let {
            if (it.x3 == null)
                return@let it.copy(
                    x1a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x1a))
                        ?: 0f else 0f,
                    x2a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x2a))
                        ?: 0f else 0f,
                    x3a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_x3a))
                        ?: 0f else 0f,
                    y1a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y1a))
                        ?: 0f else 0f,
                    y2a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y2a))
                        ?: 0f else 0f,
                    y3a =
                    if (attribute != null) checkStringValues(attribute.getString(R.styleable.DynamicCurve_y3a))
                        ?: 0f else 0f
                )
            return@let it
        }
        coreLogic = CoreLogic.initialize(curveValue, curveProperties)
    }

    private fun checkStringValues(values: String?): Float? {
        return checkStringValues(values, false)
    }

    private fun checkStringValues(values: String?, isX3: Boolean): Float? {
        return if (isX3)
            when {
                values.equals("width", ignoreCase = true) -> {
                    curveProperties.halfWidth = false
                    listener?.isHalfWidth(false)
                    null
                }
                values.equals("half", ignoreCase = true) -> {
                    curveProperties.halfWidth = true
                    listener?.isHalfWidth(true)
                    null
                }
                else -> {
                    curveProperties.halfWidth = false
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
        curveValues: CurveValues
    ) {
        curveValue = curveValues
        init()
        invalidate()
    }

    fun changeValues(xyType: XYControls, value: String) {
        curveValue.apply {
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
        }
        init()
        invalidate()
    }

    fun getValue(xyType: XYControls): Float {
        return when (xyType) {
            XYControls.X0 -> curveValue.x0
            XYControls.X1 -> curveValue.x1
            XYControls.X2 -> curveValue.x2
            XYControls.X3 -> if (curveValue.x3 != null) curveValue.x3 else if (curveProperties.halfWidth) curveProperties.deltaDivisible / 2 else curveProperties.deltaDivisible
            XYControls.Y0 -> curveValue.y0
            XYControls.Y1 -> curveValue.y1
            XYControls.Y2 -> curveValue.y2
            XYControls.Y3 -> curveValue.y3
            XYControls.X1a -> curveValue.x1a
            XYControls.X2a -> curveValue.x2a
            XYControls.X3a -> curveValue.x3a
            XYControls.Y1a -> curveValue.y1a
            XYControls.Y2a -> curveValue.y2a
            XYControls.Y3a -> curveValue.y3a
        } ?: 0.0f
    }

    fun isShadow(value: Boolean) {
        curveProperties.shadowEnabled = value
        init()
        invalidate()
    }

    fun setCurveShadowRadius(value: Float) {
        curveProperties.shadowRadius = value
        init()
        invalidate()
    }

    fun setCurveShadowDx(value: Float) {
        curveProperties.shadowDx = value
        init()
        invalidate()
    }

    fun setCurveShadowDy(value: Float) {
        curveProperties.shadowDy = value
        init()
        invalidate()
    }

    fun isMirrored(value: Boolean) {
        curveProperties.mirror = value
        coreLogic.ifMirrored()
        init()
        invalidate()
    }

    fun isReversed(value: Boolean) {
        curveProperties.reverse = value
        listener?.isReversed(curveProperties.reverse)
        init()
        invalidate()
    }

    fun isInverted(value: Boolean) {
        curveProperties.upsideDown = value
        curveProperties.upsideDownCalculated = false
        init()
        invalidate()
    }

    fun decreaseHeightBy(value: String) {
        curveProperties.decreaseHeightBy = checkStringValues(value) ?: 0f
        init()
        invalidate()
    }

    fun isInPx(value: Boolean) {
        curveProperties.isInPx = value
        init()
        invalidate()
    }

    fun deltaDivisible(value: String) {
        curveProperties.deltaDivisible = checkStringValues(value) ?: 10f
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