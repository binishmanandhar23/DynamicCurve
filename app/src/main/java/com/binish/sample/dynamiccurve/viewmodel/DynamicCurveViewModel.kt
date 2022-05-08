package com.binish.sample.dynamiccurve.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binish.dynamiccurve.enums.XYControls
import com.binish.dynamiccurve.model.CurveProperties
import com.binish.dynamiccurve.model.CurveValues
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.enums.AnimationState
import com.binish.sample.dynamiccurve.model.AnimateYControlValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DynamicCurveViewModel : ViewModel() {
    private var _duration = MutableStateFlow(3000)
    val duration = _duration.asStateFlow()

    private var _curveProperties =
        MutableStateFlow(CurveProperties(paintColor = R.color.color_orange))
    val curveProperties = _curveProperties.asStateFlow()

    private var _curveValuesForCompose = MutableStateFlow(
        CurveValues(
            x0 = 0f,
            x1 = 2.8f,
            x2 = 6.5f,
            x3String = "width",
            y0 = 5.6f,
            y1 = 8.6f,
            y2 = 3.0f,
            y3 = 5.6f,
        )
    )
    val curveValues = _curveValuesForCompose.asStateFlow()
    val animationList = MutableLiveData(ArrayList<XYControls>())

    private var _animateYControlValue = MutableStateFlow(AnimateYControlValue())
    val animateYControlValue = _animateYControlValue.asStateFlow()

    fun changeAnimateYControlValue(xyControls: XYControls, value: Float) {
        _animateYControlValue.update {
            when (xyControls) {
                XYControls.Y0 -> it.copy(y0 = value)
                XYControls.Y1 -> it.copy(y1 = value)
                XYControls.Y2 -> it.copy(y2 = value)
                XYControls.Y3 -> it.copy(y3 = value)
                else -> it
            }
        }
    }

    fun getAnimateYControlValue(xyControls: XYControls): Float
        = when (xyControls) {
            XYControls.Y0 -> animateYControlValue.value.y0
            XYControls.Y1 -> animateYControlValue.value.y1
            XYControls.Y2 -> animateYControlValue.value.y2
            XYControls.Y3 -> animateYControlValue.value.y3
            else -> 1f
        }

    fun changeDuration(duration: Int) {
        _duration.update {
            duration
        }
    }

    fun changeAnimationList(xyControls: XYControls, animationState: AnimationState) {
        animationList.value = ArrayList<XYControls>(animationList.value ?: ArrayList()).apply {
            if (animationState == AnimationState.PLAY)
                if (!contains(xyControls))
                    add(xyControls)
            if (animationState == AnimationState.PAUSE)
                if (contains(xyControls))
                    remove(xyControls)
        }
    }

    fun changeValues(xyType: XYControls, value: Float) {
        _curveValuesForCompose.update {
            when (xyType) {
                XYControls.X0 -> it.copy(x0 = value)
                XYControls.X1 -> it.copy(x1 = value)
                XYControls.X2 -> it.copy(x2 = value)
                XYControls.X3 -> it.copy(x3 = value)
                XYControls.Y0 -> it.copy(y0 = value)
                XYControls.Y1 -> it.copy(y1 = value)
                XYControls.Y2 -> it.copy(y2 = value)
                XYControls.Y3 -> it.copy(y3 = value)
                XYControls.X1a -> it.copy(x1a = value)
                XYControls.X2a -> it.copy(x2a = value)
                XYControls.X3a -> it.copy(x3a = value)
                XYControls.Y1a -> it.copy(y1a = value)
                XYControls.Y2a -> it.copy(y2a = value)
                XYControls.Y3a -> it.copy(y3a = value)
            }
        }
    }

    fun getValue(xyType: XYControls): Float {
        return when (xyType) {
            XYControls.X0 -> curveValues.value.x0
            XYControls.X1 -> curveValues.value.x1
            XYControls.X2 -> curveValues.value.x2
            XYControls.X3 -> if (curveValues.value.x3 != null) curveValues.value.x3 else if (curveProperties.value.halfWidth) curveProperties.value.deltaDivisible / 2 else curveProperties.value.deltaDivisible
            XYControls.Y0 -> curveValues.value.y0
            XYControls.Y1 -> curveValues.value.y1
            XYControls.Y2 -> curveValues.value.y2
            XYControls.Y3 -> curveValues.value.y3
            XYControls.X1a -> curveValues.value.x1a
            XYControls.X2a -> curveValues.value.x2a
            XYControls.X3a -> curveValues.value.x3a
            XYControls.Y1a -> curveValues.value.y1a
            XYControls.Y2a -> curveValues.value.y2a
            XYControls.Y3a -> curveValues.value.y3a
        } ?: 0.0f
    }

    internal fun setHalfWidth(halfWidth: Boolean) {
        _curveProperties.update {
            it.copy(halfWidth = halfWidth)
        }
    }

    internal fun setPaintColor(colorResId: Int) {
        _curveProperties.update {
            it.copy(paintColor = colorResId)
        }
    }

    internal fun setReversed(reversed: Boolean) {
        _curveProperties.update {
            it.copy(reverse = reversed)
        }
    }

    internal fun setInverted(inverted: Boolean) {
        _curveProperties.update {
            it.copy(upsideDown = inverted, upsideDownCalculated = false)
        }
    }

    internal fun setMirrored(mirrored: Boolean) {
        _curveProperties.update {
            it.copy(mirror = mirrored)
        }
    }

    internal fun setShadow(shadowEnabled: Boolean) {
        _curveProperties.update {
            it.copy(shadowEnabled = shadowEnabled)
        }
    }

    internal fun setShadowRadius(shadowRadius: Float) {
        _curveProperties.update {
            it.copy(shadowRadius = shadowRadius)
        }
    }

    internal fun decreaseHeightBy(decreaseHeightBy: Float) {
        _curveProperties.update {
            it.copy(decreaseHeightBy = decreaseHeightBy)
        }
    }
}