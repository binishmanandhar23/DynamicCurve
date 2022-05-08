package com.binish.sample.dynamiccurve.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.binish.dynamiccurve.DynamicCurve
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.AdvanceControlColorAdapter
import com.binish.sample.dynamiccurve.databinding.FragmentAdvanceControlsBinding
import com.binish.sample.dynamiccurve.utils.Utils
import com.binish.sample.dynamiccurve.viewmodel.DynamicCurveViewModel

class FragmentAdvanceControls(private val listener: AdvanceControlInteraction) : Fragment() {
    private var _binding: FragmentAdvanceControlsBinding? = null
    private val binding get() = _binding!!

    val dynamicCurveViewModel by activityViewModels<DynamicCurveViewModel>()

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
        binding.checkboxReverse.isChecked = curve.curveProperties.reverse
        binding.checkboxReverse.setOnCheckedChangeListener { _, isChecked ->
            curve.isReversed(isChecked)
            dynamicCurveViewModel.setReversed(isChecked)
        }
        binding.checkboxMirror.isChecked = curve.curveProperties.mirror
        binding.checkboxMirror.setOnCheckedChangeListener { _, isChecked ->
            curve.isMirrored(isChecked)
            dynamicCurveViewModel.setMirrored(isChecked)
        }
        binding.checkboxUpsideDown.isChecked = curve.curveProperties.upsideDown
        binding.checkboxUpsideDown.setOnCheckedChangeListener { _, isChecked ->
            curve.isInverted(isChecked)
            dynamicCurveViewModel.setInverted(isChecked)
        }

        binding.checkboxEnableShadow.isChecked = curve.curveProperties.shadowEnabled
        binding.checkboxEnableShadow.setOnCheckedChangeListener { _, isChecked ->
            curve.isShadow(isChecked)
            dynamicCurveViewModel.setShadow(isChecked)
            binding.seekBarShadowRadius.visibility = if(isChecked) View.VISIBLE else View.GONE
        }

        binding.seekBarShadowRadius.visibility = if(curve.curveProperties.shadowEnabled) View.VISIBLE else View.GONE
        binding.seekBarDecreaseHeightBy.progress = Utils.getValueInIntForShadows(curve.curveProperties.shadowRadius)
        binding.seekBarShadowRadius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser)
                    curve.setCurveShadowRadius(Utils.getConvertedValueForShadows(progress)).also {
                        dynamicCurveViewModel.setShadowRadius(Utils.getConvertedValueForShadows(progress))
                    }
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

        binding.seekBarDecreaseHeightBy.progress = Utils.getValueInInt(-curve.curveProperties.decreaseHeightBy)
        binding.seekBarDecreaseHeightBy.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    Utils.getConvertedValue(progress).let {
                        curve.decreaseHeightBy("-${it}")
                        dynamicCurveViewModel.decreaseHeightBy(-it)
                    }
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