package com.binish.dynamiccurve.model

import com.binish.dynamiccurve.DynamicCurveCompose

data class AnimateDefaults(
    val animate: Boolean = false,
    val curveValues: AnimateToCurveValues = AnimateToCurveValues(),
    val duration: Int = 3000
)
