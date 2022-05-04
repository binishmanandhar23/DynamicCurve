package com.binish.dynamiccurve.data

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import com.binish.dynamiccurve.enums.CurveType
import com.binish.dynamiccurve.model.CurveProperties
import com.binish.dynamiccurve.model.CurveValues

class CoreLogic private constructor() {
    private var curveValue = CurveValues()
    private var curveProperties = CurveProperties()
    private var path: Path? = null
    private var paint: Paint? = null

    companion object {
        fun initialize(curveValues: CurveValues, curveProperties: CurveProperties): CoreLogic =
            CoreLogic().apply {
                this.curveValue = curveValues
                this.curveProperties = curveProperties
                if (curveProperties.mirror) ifMirrored()
            }
    }

    internal fun setupCurveValuesAndProperties(
        curveValues: CurveValues,
        curveProperties: CurveProperties
    ) {
        this.curveValue = curveValues
        this.curveProperties = curveProperties
    }

    internal fun initializePath(paint: Paint?) {
        this.paint = paint
        path = Path()
    }

    internal fun drawLogic(canvas: Canvas?, height: Int, width: Int) {
        val delta = width / curveProperties.deltaDivisible
        val deltaHeight = height / curveProperties.deltaDivisible
        ifUpsideDown(height.toFloat(), delta) //For upSideDown
        getMoveToValues(height = height, deltaHeight = deltaHeight, delta = delta) { x, y ->
            path?.moveTo(x, y)
        }
        getCubicToValuesForCurve(
            curveType = CurveType.FIRST,
            height = height,
            width = width,
            deltaHeight = deltaHeight,
            delta = delta
        ) { x1, y1, x2, y2, x3, y3 ->
            path?.cubicTo(x1, y1, x2, y2, x3, y3)
        }
        if (curveValue.x3 == null && curveProperties.halfWidth)
            getCubicToValuesForCurve(
                curveType = CurveType.SECOND,
                height = height,
                width = width,
                deltaHeight = deltaHeight,
                delta = delta
            ) { x1, y1, x2, y2, x3, y3 ->
                path?.cubicTo(x1, y1, x2, y2, x3, y3)
            }


        getLineToValues(
            reverse = curveProperties.reverse,
            height = height,
            width = width
        ) { x1, y1, x2, y2 ->
            path?.lineTo(x1, y1)
            path?.lineTo(x2, y2)
        }

        path?.lineTo(0f, height.toFloat())
        path?.close()

        canvas?.drawPath(path!!, paint!!)
    }

    private inline fun getMoveToValues(
        height: Int,
        delta: Float,
        deltaHeight: Float,
        values: (x: Float, y: Float) -> Unit
    ) {
        curveValue.run {
            values(
                if (curveProperties.isInPx) dp2px(x0!!) else x0!! * delta,
                when {
                    y0 == null -> height.toFloat() - (curveProperties.decreaseHeightBy * deltaHeight)
                    curveProperties.isInPx -> dp2px(
                        y0!! - curveProperties.decreaseHeightBy
                    )
                    else -> (y0!! - curveProperties.decreaseHeightBy) * delta
                }
            )
        }
    }

    private inline fun getCubicToValuesForCurve(
        curveType: CurveType,
        height: Int,
        width: Int,
        delta: Float,
        deltaHeight: Float,
        values: (x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) -> Unit
    ) {
        curveValue.run {
            if (curveType == CurveType.FIRST)
                values(
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
            else if (curveType == CurveType.SECOND)
                values(
                    if (curveProperties.isInPx) dp2px(x1a!!) else x1a!! * delta,
                    if (curveProperties.isInPx) dp2px(y1a!! - curveProperties.decreaseHeightBy) else (y1a!! - curveProperties.decreaseHeightBy) * delta,
                    if (curveProperties.isInPx) dp2px(x2a!!) else x2a!! * delta,
                    if (curveProperties.isInPx) dp2px(y2a!! - curveProperties.decreaseHeightBy) else (y2a!! - curveProperties.decreaseHeightBy) * delta,
                    width.toFloat(),
                    if (curveProperties.isInPx) dp2px(y3a!! - curveProperties.decreaseHeightBy) else (y3a!! - curveProperties.decreaseHeightBy) * delta
                )
        }
    }

    private inline fun getLineToValues(
        reverse: Boolean,
        height: Int,
        width: Int,
        values: (x1: Float, y1: Float, x2: Float, y2: Float) -> Unit
    ) {
        if (reverse)
            values(width.toFloat(), 0f, 0f, 0f)
        else
            values(width.toFloat(), height.toFloat(), 0f, height.toFloat())
    }

    internal fun ifMirrored() {
        //NOTE: '*' means un-mirrored value
        val dummyX1 = curveValue.x1
        val dummyX2 = curveValue.x2
        val dummyY0 = curveValue.y0
        val dummyY1 = curveValue.y1
        val dummyY2 = curveValue.y2
        val dummyY3 = curveValue.y3
        curveValue.x1 =
            if (curveValue.x2 != null) curveProperties.deltaDivisible - dummyX2!! else curveValue.x1 //If mirrored -> [width - x2*]
        curveValue.x2 =
            if (curveValue.x1 != null) curveProperties.deltaDivisible - dummyX1!! else curveValue.x2 //If mirrored -> [width - x1*]
        curveValue.y0 =
            if (curveValue.y3 != null) dummyY3 else curveValue.y0 //If mirrored -> y3*
        curveValue.y1 =
            if (curveValue.y2 != null) dummyY2 else curveValue.y1 //If mirrored -> y2*
        curveValue.y2 =
            if (curveValue.y1 != null) dummyY1 else curveValue.y2 //If mirrored -> y1*
        curveValue.y3 =
            if (curveValue.y0 != null) dummyY0 else curveValue.y3//If mirrored -> y0*
    }

    private fun ifUpsideDown(height: Float, delta: Float) {
        if (!curveProperties.upsideDownCalculated) {
            val dummyY0 = curveValue.y0
            val dummyY1 = curveValue.y1
            val dummyY2 = curveValue.y2
            val dummyY3 = curveValue.y3
            curveValue.y0 = height / delta - (dummyY0 ?: 0f)
            curveValue.y1 = height / delta - (dummyY1 ?: 0f)
            curveValue.y2 = height / delta - (dummyY2 ?: 0f)
            curveValue.y3 = height / delta - (dummyY3 ?: 0f)
            if (curveValue.x3 == null && curveProperties.halfWidth) {
                val dummyY1a = curveValue.y1a
                val dummyY2a = curveValue.y2a
                val dummyY3a = curveValue.y3a
                curveValue.y1a = height / delta - (dummyY1a ?: 0f)
                curveValue.y2a = height / delta - (dummyY2a ?: 0f)
                curveValue.y3a = height / delta - (dummyY3a ?: 0f)
            }
            curveProperties.upsideDownCalculated = true
        }
    }


    private fun dp2px(dp: Float): Float {
        return (Resources.getSystem().displayMetrics!!.density * dp + 0.5f)
    }
}