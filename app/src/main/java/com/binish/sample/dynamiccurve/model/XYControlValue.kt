package com.binish.sample.dynamiccurve.model

import com.binish.dynamiccurve.enums.XYControls

class XYControlValue(xyControl: XYControls, xyValue: Float) {
    var xyControl: XYControls? = null

    var xyValue: Float = 0.0f

    init {
        this.xyControl = xyControl
        this.xyValue = xyValue
    }
}