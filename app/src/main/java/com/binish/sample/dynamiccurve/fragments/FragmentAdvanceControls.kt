package com.binish.sample.dynamiccurve.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.binish.dynamiccurve.DynamicCurve
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.AdvanceControlColorAdapter
import com.binish.sample.dynamiccurve.databinding.FragmentAdvanceControlsBinding
import com.binish.sample.dynamiccurve.utils.Utils

class FragmentAdvanceControls(private val listener: AdvanceControlInteraction) : Fragment() {
    private var _binding: FragmentAdvanceControlsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvanceControlsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val curve = getView<DynamicCurve>(R.id.dynamicCurve)
        binding.checkboxReverse.isChecked = curve.reverse
        binding.checkboxReverse.setOnCheckedChangeListener { _, isChecked ->
            curve.isReversed(isChecked)
        }
        binding.checkboxMirror.isChecked = curve.mirror
        binding.checkboxMirror.setOnCheckedChangeListener { _, isChecked ->
            curve.isMirrored(isChecked)
        }
        binding.checkboxMirror.isChecked = curve.upsideDown
        binding.checkboxUpsideDown.setOnCheckedChangeListener { _, isChecked ->
            curve.isInverted(isChecked)
        }

        binding.checkboxEnableShadow.isChecked = curve.shadowEnabled
        binding.checkboxEnableShadow.setOnCheckedChangeListener { _, isChecked ->
            curve.isShadow(isChecked)
            binding.seekBarShadowRadius.visibility = if(isChecked) View.VISIBLE else View.GONE
        }

        binding.seekBarShadowRadius.visibility = if(curve.shadowEnabled) View.VISIBLE else View.GONE
        binding.seekBarDecreaseHeightBy.progress = Utils.getValueInIntForShadows(curve.shadowRadius)
        binding.seekBarShadowRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                    curve.setCurveShadowRadius(Utils.getConvertedValueForShadows(progress))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        binding.recyclerViewCurveColor.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewCurveColor.adapter = AdvanceControlColorAdapter(
            requireContext(),
            Utils.getColors(),
            getSelectedIndex(Utils.getColors())
        ) {
            getView<DynamicCurve>(R.id.dynamicCurve).changeCurveColor(
                ContextCompat.getColor(
                    requireContext(),
                    it
                )
            )
            listener.onColorPicked(it)
        }

        binding.seekBarDecreaseHeightBy.progress = Utils.getValueInInt(-curve.decreaseHeightBy)
        binding.seekBarDecreaseHeightBy.setOnSeekBarChangeListener(object :
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
            if (getView<DynamicCurve>(R.id.dynamicCurve).paintColor == ContextCompat.getColor(
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

    private fun <T> getView(viewId: Int): T = requireActivity().findViewById(viewId) as T
}