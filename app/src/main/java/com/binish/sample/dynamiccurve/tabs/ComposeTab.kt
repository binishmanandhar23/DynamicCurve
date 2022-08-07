package com.binish.sample.dynamiccurve.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.binish.dynamiccurve.DynamicCurveCompose
import com.binish.dynamiccurve.enums.XYControls
import com.binish.dynamiccurve.model.AnimateDefaults
import com.binish.dynamiccurve.model.AnimateToCurveValues
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.databinding.FragmentComposeTabBinding
import com.binish.sample.dynamiccurve.fragments.BottomSheetComposeController
import com.binish.sample.dynamiccurve.viewmodel.DynamicCurveViewModel

class ComposeTab : Fragment() {
    private var _binding: FragmentComposeTabBinding? = null
    val binding get() = _binding!!

    private val dynamicCurveViewModel: DynamicCurveViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentComposeTabBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.mainComposeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
            setContent {
                val hapticFeedback = LocalHapticFeedback.current
                Box(modifier = Modifier.fillMaxSize()) {
                    CurveBody()
                    FloatingActionButton(
                        modifier = Modifier
                            .padding(35.dp)
                            .align(Alignment.BottomEnd),
                        onClick = {
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            BottomSheetComposeController().show(
                                childFragmentManager,
                                "Bottom Sheet compose"
                            )
                        },
                        shape = RoundedCornerShape(15.dp),
                        backgroundColor = colorResource(id = R.color.color_grey_dark),
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 10.dp,
                            pressedElevation = 5.dp
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_keyboard_arrow_up),
                            contentDescription = "Expand options",
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun CurveBody() {
        val curveProperties = dynamicCurveViewModel.curveProperties.collectAsState().value
        val curveValues = dynamicCurveViewModel.curveValues.collectAsState().value
        val animationList =
            dynamicCurveViewModel.animationList.observeAsState().value ?: ArrayList()
        val duration = dynamicCurveViewModel.duration.collectAsState().value
        val animateYControlValue = dynamicCurveViewModel.animateYControlValue.collectAsState().value

        val y0 = if (animationList.contains(XYControls.Y0)) ((curveValues.y0 ?: 5.9f) + animateYControlValue.y0).let { if (it > 10f) 10f else it } else curveValues.y0

        val y1 = if (animationList.contains(XYControls.Y1)) ((curveValues.y1 ?: 9.9f) + animateYControlValue.y1).let { if (it > 10f) 10f else it } else curveValues.y1

        val y2 = if (animationList.contains(XYControls.Y2)) ((curveValues.y2 ?: 5.5f) + animateYControlValue.y2).let { if (it > 10f) 10f else it } else curveValues.y2

        val y3 = if (animationList.contains(XYControls.Y3)) ((curveValues.y3 ?: 7.5f) + animateYControlValue.y3).let { if (it > 10f) 10f else it } else curveValues.y3

        DynamicCurveCompose.Curve(
            modifier = Modifier.fillMaxSize(),
            curveValues = curveValues,
            curveProperties = curveProperties.copy(animate = AnimateDefaults(animate = true,
                curveValues = AnimateToCurveValues(
                    y0 = y0,
                    y1 = y1,
                    y2 = y2,
                    y3 = y3,
                ),
                duration = duration
            )),
        )
    }
}