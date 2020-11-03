package com.binish.sample.dynamiccurve.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binish.dynamiccurve.DynamicCurve
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.ControlAdapter
import com.binish.sample.dynamiccurve.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_controls.*
import kotlinx.android.synthetic.main.layout_bottom_sheet_sticker_dialog.*

class BottomSheetController(val listener: BottomMapDetailFragmentInteraction) :
    BottomSheetDialogFragment() {
    private var root: View? = null
    private var paintColor: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            paintColor = getInt(PAINT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.layout_bottom_sheet_sticker_dialog, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPagerControls.adapter = ControlViewPagerAdapter(this)
        TabLayoutMediator(tabsControls, viewPagerControls) { tab, position ->
            when (position) {
                0 -> tab.text = "Controls 1"
                1 -> tab.text = "Controls 2"
                2 -> tab.text = "Advance Controls"
            }
        }.attach()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)

        val resources = resources
        if (resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            val parent = view?.parent as View
            val layoutParams = parent.layoutParams as CoordinatorLayout.LayoutParams
            layoutParams.setMargins(
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginStart), // LEFT
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginTop), // Top
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginEnd), // RIGHT
                resources.getDimensionPixelSize(R.dimen.bottomDialogMarginBottom) // BOTTOM
            )
            parent.layoutParams = layoutParams
        }
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

    override fun onStart() {
        super.onStart()
        dialog?.also {
            val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            bottomSheet?.setBackgroundColor(paintColor!!)
            bottomSheet?.background?.alpha = 100
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            val layout = root?.findViewById(R.id.containerStickerDialog) as ConstraintLayout
            layout.viewTreeObserver?.addOnGlobalLayoutListener {
                object : OnGlobalLayoutListener {
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

    companion object {
        private const val PAINT = "PaintColor"
        fun newInstance(listener: BottomMapDetailFragmentInteraction, colorResId: Int) =
            BottomSheetController(listener).apply {
                arguments = Bundle().apply {
                    putInt(PAINT, colorResId)
                }
            }


        interface BottomMapDetailFragmentInteraction {

        }
    }

    inner class ControlViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        var mainListener: DynamicCurve.DynamicCurveAdapter? = null
        var control1Fragment: FragmentControls1? = null
        lateinit var control2Fragment: FragmentControls2
        lateinit var advanceControls: FragmentAdvanceControls
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    control1Fragment = FragmentControls1.newInstance(initializeCurveListener())
                    control1Fragment!!
                }
                1 -> {
                    control2Fragment = FragmentControls2.newInstance(initializeCurveListener())
                    control2Fragment
                }
                2 -> {
                    advanceControls = FragmentAdvanceControls.newInstance(initializeColorListener())
                    advanceControls
                }
                else -> {
                    control1Fragment = FragmentControls1.newInstance(initializeCurveListener())
                    control1Fragment!!
                }
            }
        }

        private fun initializeCurveListener(): DynamicCurve.DynamicCurveAdapter {
            return if (mainListener == null) {
                mainListener = object : DynamicCurve.DynamicCurveAdapter(){
                    override fun isHalfWidth(halfWidth: Boolean) {
                        checkBoxEnableSecondCurve.isChecked = halfWidth
                        (control1Fragment?.recyclerViewControl1?.adapter as ControlAdapter).isHalfWidth(halfWidth)
                    }

                    override fun isReversed(reversed: Boolean) {
                        Utils.changeStatusBarColor(requireActivity(),!reversed, R.color.color_white)
                    }
                }
                mainListener!!
            } else
                mainListener!!
        }

        private fun initializeColorListener(): FragmentAdvanceControls.AdvanceControlInteraction{
            return FragmentAdvanceControls.AdvanceControlInteraction {
                paintColor = ContextCompat.getColor(requireContext(), it)
                onStart()
            }
        }
    }
}