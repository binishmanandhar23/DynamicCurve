package com.binish.sample.dynamiccurve.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.binish.dynamiccurve.enums.XYControls
import com.binish.dynamiccurve.model.CurveProperties
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.databinding.DialogBottomSheetComposeBinding
import com.binish.sample.dynamiccurve.enums.AnimationState
import com.binish.sample.dynamiccurve.viewmodel.DynamicCurveViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class BottomSheetComposeController : BottomSheetDialogFragment() {
    private var _binding: DialogBottomSheetComposeBinding? = null
    val binding get() = _binding!!

    val dynamicCurveViewModel by activityViewModels<DynamicCurveViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogBottomSheetComposeBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener {
            val dialog = it as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).state = BottomSheetBehavior.STATE_HALF_EXPANDED
            onStart()
        }
        return bottomSheetDialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view.parent as View).setBackgroundColor(android.graphics.Color.TRANSPARENT)
        if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            val parent = view.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginStart), // LEFT
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginTop), // Top
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginEnd), // RIGHT
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginBottom) // BOTTOM
            )
            parent.layoutParams = layoutParams
        }

        binding.dialogComposeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                    viewLifecycleOwner
                )
            )
            setContent {
                val curveProperties = dynamicCurveViewModel.curveProperties.collectAsState().value
                val animationList =
                    dynamicCurveViewModel.animationList.observeAsState().value ?: ArrayList()
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 20.dp)
                            .size(height = 6.dp, width = 20.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(30.dp))
                    )
                    run XYControl@{
                        XYControls.values().forEach { xyControl ->
                            if (xyControl == XYControls.X1a)
                                return@XYControl
                            IndividualComponent(
                                curveProperties = curveProperties,
                                xyControls = xyControl,
                                value = dynamicCurveViewModel.getValue(xyControl),
                                animateYValue = dynamicCurveViewModel.getAnimateYControlValue(xyControl),
                                defaultAnimationState = if (animationList.contains(xyControl)) AnimationState.PLAY else AnimationState.PAUSE
                            ) {
                                dynamicCurveViewModel.changeValues(xyControl, it)
                            }
                        }
                    }
                    TimeDuration(
                        curveProperties = curveProperties,
                        enabled = animationList.isEmpty()
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    private fun IndividualComponent(
        curveProperties: CurveProperties,
        xyControls: XYControls,
        defaultAnimationState: AnimationState,
        value: Float,
        animateYValue: Float,
        onValueChange: (value: Float) -> Unit
    ) {
        var animationState by remember { mutableStateOf(defaultAnimationState) }
        var defaultValue by remember { mutableStateOf(value) }
        var animateYDefaultValue by remember { mutableStateOf(animateYValue) }
        val hapticFeedback = LocalHapticFeedback.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = xyControls.type, fontSize = 12.sp, color = Color.White)
                    Spacer(modifier = Modifier.padding(horizontal = 10.dp))
                    if (xyControls == XYControls.Y0 || xyControls == XYControls.Y1 || xyControls == XYControls.Y2 || xyControls == XYControls.Y3) {
                        AnimatedContent(targetState = animationState, transitionSpec = {
                            slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Start,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn() with slideOutOfContainer(
                                towards = AnimatedContentScope.SlideDirection.Start,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeOut()
                        }) { state ->
                            Image(
                                painter = painterResource(id = if (state == AnimationState.PAUSE) R.drawable.ic_play else R.drawable.ic_pause),
                                contentDescription = "Play animation",
                                modifier = Modifier
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                    .size(height = 25.dp, width = 35.dp)
                                    .background(
                                        color = colorResource(
                                            id = R.color.color_white
                                        ), shape = CircleShape
                                    )
                                    .padding(5.dp)
                                    .clickable(MutableInteractionSource(), null) {
                                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                        animationState =
                                            if (state == AnimationState.PAUSE) AnimationState.PLAY else AnimationState.PAUSE
                                        dynamicCurveViewModel.changeAnimationList(
                                            xyControls, animationState
                                        )
                                    },
                                colorFilter = ColorFilter.tint(color = colorResource(id = curveProperties.paintColor))
                            )
                        }
                        AnimatedContent(targetState = animationState, transitionSpec = {
                            slideIntoContainer(
                                towards = AnimatedContentScope.SlideDirection.Start,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeIn() with slideOutOfContainer(
                                towards = AnimatedContentScope.SlideDirection.Start,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ) + fadeOut()
                        }) { state ->
                            if(state == AnimationState.PLAY)
                            Slider(
                                value = animateYDefaultValue, onValueChange = {
                                    animateYDefaultValue = it
                                    dynamicCurveViewModel.changeAnimateYControlValue(xyControls, it)
                                }, colors = SliderDefaults.colors(
                                    thumbColor = colorResource(
                                        id = curveProperties.paintColor
                                    ), activeTrackColor = Color.White
                                ), valueRange = 1f..5f,
                                modifier = Modifier
                                    .padding(horizontal = 10.dp)
                                    .fillMaxWidth(0.7f)
                            )
                        }
                    }
                }
                Text(
                    text = String.format("%.1f", defaultValue),
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            Slider(
                value = defaultValue, onValueChange = {
                    defaultValue = it
                    onValueChange(it)
                }, colors = SliderDefaults.colors(
                    thumbColor = colorResource(
                        id = curveProperties.paintColor
                    ), activeTrackColor = Color.White
                ), valueRange = 0f..10f,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            )
        }
    }

    @Composable
    private fun TimeDuration(curveProperties: CurveProperties, enabled: Boolean) {
        var duration by remember { mutableStateOf(dynamicCurveViewModel.duration.value) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Duration", fontSize = 12.sp, color = Color.White)
                Text(
                    text = "${(duration / 1000)} s",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
            Slider(
                value = duration / 1000f, onValueChange = {
                    duration = (it * 1000).roundToInt()
                    dynamicCurveViewModel.changeDuration(duration = duration)
                }, colors = SliderDefaults.colors(
                    thumbColor = colorResource(
                        id = curveProperties.paintColor
                    ), activeTrackColor = Color.White,
                    activeTickColor = Color.White
                ), valueRange = 1f..10f,
                steps = 10,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                enabled = enabled
            )
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.also {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            lifecycleScope.launch {
                dynamicCurveViewModel.curveProperties.collect {
                    bottomSheet?.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            it.paintColor
                        )
                    )
                }
            }
            bottomSheet?.background?.alpha = 100
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            val layout = _binding?.root?.findViewById(R.id.dialogComposeView) as ComposeView
            layout.viewTreeObserver?.addOnGlobalLayoutListener {
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        try {
                            layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            behavior.peekHeight = layout.height
                            view?.requestLayout()
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
    }
}