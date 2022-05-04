package com.binish.dynamiccurve.model

import com.binish.dynamiccurve.R

data class CurveProperties(
    var shadowEnabled: Boolean = false,
    var shadowRadius: Float = 2f,
    var shadowDx: Float = 1f,
    var shadowDy: Float = 1f,
    var shadowColor: Int = R.color.shadow_color,
    var reverse: Boolean = false,
    var mirror: Boolean = false,
    var upsideDown: Boolean = false,
    var halfWidth: Boolean = false,
    var decreaseHeightBy: Float = 0.0f,
    var isInPx: Boolean = false,
    var deltaDivisible: Float = 10f,
    var upsideDownCalculated: Boolean = true,
    var paintColor: Int = R.color.color_orange
)
