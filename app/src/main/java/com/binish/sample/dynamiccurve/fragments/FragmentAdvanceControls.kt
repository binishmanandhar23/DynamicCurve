package com.binish.sample.dynamiccurve.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.AdvanceControlColorAdapter
import com.binish.sample.dynamiccurve.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_advance_controls.*

class FragmentAdvanceControls(private val listener: AdvanceControlInteraction) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_advance_controls, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val curve = requireActivity().dynamicCurve
        checkboxReverse.isChecked = curve.reverse
        checkboxReverse.setOnCheckedChangeListener { _, isChecked ->
            curve.isReversed(isChecked)
        }
        checkboxMirror.isChecked = curve.mirror
        checkboxMirror.setOnCheckedChangeListener { _, isChecked ->
            curve.isMirrored(isChecked)
        }
        checkboxMirror.isChecked = curve.upsideDown
        checkboxUpsideDown.setOnCheckedChangeListener { _, isChecked ->
            curve.isInverted(isChecked)
        }

        recyclerViewCurveColor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerViewCurveColor.adapter = AdvanceControlColorAdapter(
            requireContext(),
            Utils.getColors(),
            getSelectedIndex(Utils.getColors())
        ) {
            requireActivity().dynamicCurve.changeCurveColor(
                ContextCompat.getColor(
                    requireContext(),
                    it
                )
            )
            listener.onColorPicked(it)
        }

        seekBarDecreaseHeightBy.progress = Utils.getValueInInt(-curve.decreaseHeightBy)
        seekBarDecreaseHeightBy.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    curve.decreaseHeightBy("-${Utils.getConvertedValue(progress)}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun getSelectedIndex(colorList: List<Int>): Int {
        var selectedIndex = 0
        for (i in colorList.indices) {
            val color = colorList[i]
            if (requireActivity().dynamicCurve.paintColor == ContextCompat.getColor(
                    requireContext(),
                    color
                )
            )
                selectedIndex = i
        }
        return selectedIndex
    }

    fun interface AdvanceControlInteraction {
        fun onColorPicked(color: Int)
    }

    companion object {
        fun newInstance(listener: AdvanceControlInteraction) =
            FragmentAdvanceControls(listener).apply {
                arguments = Bundle().apply {

                }
            }
    }
}