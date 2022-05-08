package com.binish.dynamiccurve

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.binish.dynamiccurve.model.CurveProperties
import com.binish.dynamiccurve.model.CurveValues

object DynamicCurveCompose {

    @Composable
    fun Curve(
        modifier: Modifier,
        backgroundColor: Color = colorResource(id = android.R.color.transparent),
        curvePropertiesMain: CurveProperties = CurveProperties(),
        curveValues: CurveValues,
        content: (@Composable BoxScope.() -> Unit)? = null
    ) {
        val context = LocalContext.current
        var newInstanceOfCurveValues = curveValues
        var curveProperties = curvePropertiesMain
        checkX3Value(curveValues.x3String) { x3, halfWidth ->
            newInstanceOfCurveValues.x3 = x3
            curveProperties.halfWidth = halfWidth
        }
        if (curveProperties.mirror)
            ifMirrored(curveValues, curveProperties.deltaDivisible) {
                newInstanceOfCurveValues = it
            }
        val path = Path()
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = PaintingStyle.Fill
        paint.color = colorResource(id = curveProperties.paintColor)

        newInstanceOfCurveValues.run {
            ConstraintLayout {
                val (canvas, innerContent) = createRefs()
                Canvas(modifier = Modifier
                    .background(color = backgroundColor)
                    .constrainAs(canvas) {
                        top.linkTo(innerContent.top)
                        bottom.linkTo(innerContent.bottom)
                        start.linkTo(innerContent.start)
                        end.linkTo(innerContent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints

                    }) {
                    val height = size.height
                    val width = size.width
                    val delta = width / curveProperties.deltaDivisible
                    val deltaHeight = height / curveProperties.deltaDivisible

                    if (curveProperties.upsideDown)
                        ifUpsideDown(
                            curveValues,
                            curveProperties,
                            height,
                            delta
                        ) { curveValues, curvePropertiesNew ->
                            newInstanceOfCurveValues = curveValues
                            curveProperties = curvePropertiesNew
                        } //For upSideDown

                    path.moveTo(
                        if (curveProperties.isInPx) x0!!.dp2px(context) else x0!! * delta,
                        when {
                            y0 == null -> height - (curveProperties.decreaseHeightBy * deltaHeight)
                            curveProperties.isInPx -> (
                                    y0!! - curveProperties.decreaseHeightBy
                                    ).dp2px(context)
                            else -> (y0!! - curveProperties.decreaseHeightBy) * delta
                        }
                    )
                    path.cubicTo(
                        if (curveProperties.isInPx) x1!!.dp2px(context) else x1!! * delta,
                        when {
                            y1 == null -> height - (curveProperties.decreaseHeightBy * deltaHeight)
                            curveProperties.isInPx -> (y1!! - curveProperties.decreaseHeightBy).dp2px(
                                context
                            )
                            else -> (y1!! - curveProperties.decreaseHeightBy
                                    ) * delta
                        },
                        if (curveProperties.isInPx) x2!!.dp2px(context) else x2!! * delta,
                        when {
                            y2 == null ->
                                height - (curveProperties.decreaseHeightBy * deltaHeight)
                            curveProperties.isInPx -> (
                                    y2!! - curveProperties.decreaseHeightBy
                                    ).dp2px(context)
                            else -> (y2!! - curveProperties.decreaseHeightBy) * delta
                        },
                        if (x3 == null && !curveProperties.halfWidth) width else if (x3 == null && curveProperties.halfWidth) width / 2 else if (curveProperties.isInPx)
                            x3!!.dp2px(context)
                        else x3!! * delta,
                        when {
                            y3 == null ->
                                height - (curveProperties.decreaseHeightBy * deltaHeight)
                            curveProperties.isInPx -> (
                                    y3!! - curveProperties.decreaseHeightBy
                                    ).dp2px(context)
                            else -> (y3!! - curveProperties.decreaseHeightBy) * delta
                        }
                    )

                    if (x3 == null && curveProperties.halfWidth) {
                        path.cubicTo(
                            if (curveProperties.isInPx) x1a!!.dp2px(context) else x1a!! * delta,
                            if (curveProperties.isInPx) (y1a!! - curveProperties.decreaseHeightBy).dp2px(
                                context
                            ) else (y1a!! - curveProperties.decreaseHeightBy) * delta,
                            if (curveProperties.isInPx) (x2a!!).dp2px(context) else x2a!! * delta,
                            if (curveProperties.isInPx) (y2a!! - curveProperties.decreaseHeightBy).dp2px(
                                context
                            ) else (y2a!! - curveProperties.decreaseHeightBy) * delta,
                            width,
                            if (curveProperties.isInPx) (y3a!! - curveProperties.decreaseHeightBy).dp2px(
                                context
                            ) else (y3a!! - curveProperties.decreaseHeightBy) * delta
                        )
                    }

                    if (curveProperties.reverse) {
                        path.lineTo(width, 0f)
                        path.lineTo(0f, 0f)
                    } else {
                        path.lineTo(width, height)
                        path.lineTo(0f, height)
                    }

                    path.lineTo(0f, height)
                    path.close()

                    drawPath(path = path, color = paint.color, style = Fill)
                }
                Box(modifier = modifier.constrainAs(innerContent) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
                    content?.invoke(this)
                }
            }
        }

    }

    private fun ifMirrored(
        curveValues: CurveValues,
        deltaDivisible: Float,
        newValues: (curveValue: CurveValues) -> Unit
    ) {
        //NOTE: '*' means un-mirrored value
        curveValues.run {
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
        newValues(curveValues)
    }

    private fun ifUpsideDown(
        curveValues: CurveValues,
        curveProperties: CurveProperties,
        height: Float,
        delta: Float,
        newValues: (curveValue: CurveValues, curveProperties: CurveProperties) -> Unit
    ) {
        curveValues.apply {
            if (!curveProperties.upsideDownCalculated) {
                val dummyY0 = y0
                val dummyY1 = y1
                val dummyY2 = y2
                val dummyY3 = y3
                y0 = height / delta - (dummyY0 ?: 0f)
                y1 = height / delta - (dummyY1 ?: 0f)
                y2 = height / delta - (dummyY2 ?: 0f)
                y3 = height / delta - (dummyY3 ?: 0f)
                if (x3 == null && curveProperties.halfWidth) {
                    val dummyY1a = y1a
                    val dummyY2a = y2a
                    val dummyY3a = y3a
                    y1a = height / delta - (dummyY1a ?: 0f)
                    y2a = height / delta - (dummyY2a ?: 0f)
                    y3a = height / delta - (dummyY3a ?: 0f)
                }
                curveProperties.upsideDownCalculated = true
            }
        }
        newValues(curveValues, curveProperties)
    }

    private fun checkX3Value(values: String?, work: (value: Float?, halfWidth: Boolean) -> Unit) {
        var halfWidth = false
        val returnValue = when {
            values.equals("width", ignoreCase = true) -> {
                halfWidth = false
                null
            }
            values.equals("half", ignoreCase = true) -> {
                halfWidth = true
                null
            }
            else -> values?.toFloat()
        }
        work(returnValue, halfWidth)
    }

    private fun checkFullHeight(value: String?): Float? {
        return if (value.equals("height", ignoreCase = true))
            null else value?.toFloat()
    }

    private fun Float.dp2px(context: Context): Float {
        return (context.resources?.displayMetrics!!.density * this + 0.5f)
    }
}