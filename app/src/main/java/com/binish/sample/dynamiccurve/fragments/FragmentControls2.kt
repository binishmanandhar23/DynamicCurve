package com.binish.sample.dynamiccurve.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.binish.dynamiccurve.DynamicCurve
import com.binish.dynamiccurve.enums.X3Type
import com.binish.dynamiccurve.enums.XYControls
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.ControlAdapter
import com.binish.sample.dynamiccurve.databinding.FragmentControlsBinding
import com.binish.sample.dynamiccurve.model.XYControlValue

class FragmentControls2(val listener: DynamicCurve.DynamicCurveAdapter) : Fragment() {
    private var _binding: FragmentControlsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentControlsBinding.inflate(inflater,container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val control2Adapter = ControlAdapter(
            requireContext(),
            getXYList(),
            object : ControlAdapter.ControlAdapterInteraction {
                override fun onValueChanged(xyControlValue: XYControlValue) {
                    getView<DynamicCurve>(R.id.dynamicCurve).changeValues(
                        xyControlValue.xyControl!!,
                        String.format("%.1f", xyControlValue.xyValue)
                    )
                }
            })

        binding.checkBoxEnableSecondCurve.visibility = View.VISIBLE
        binding.checkBoxEnableSecondCurve.isChecked = getView<DynamicCurve>(R.id.dynamicCurve).halfWidth
        control2Adapter.isEnabled(getView<DynamicCurve>(R.id.dynamicCurve).halfWidth)
        getView<DynamicCurve>(R.id.dynamicCurve).setUpListener(listener)
        binding.recyclerViewControl1.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewControl1.adapter = control2Adapter

        binding.checkBoxEnableSecondCurve.setOnCheckedChangeListener { _, isChecked ->
            getView<DynamicCurve>(R.id.dynamicCurve).changeValues(
                XYControls.X3,
                if (isChecked) X3Type.HALF.type else X3Type.FULL.type
            )
            control2Adapter.isEnabled(isChecked)
        }
    }

    private fun getXYList(): ArrayList<XYControlValue> {
        val xyList = ArrayList<XYControlValue>()
        xyList.add(XYControlValue(XYControls.X1a, getValueFromCurve(XYControls.X1a)))
        xyList.add(XYControlValue(XYControls.X2a, getValueFromCurve(XYControls.X2a)))
        xyList.add(XYControlValue(XYControls.X3a, getValueFromCurve(XYControls.X3a)))
        xyList.add(XYControlValue(XYControls.Y1a, getValueFromCurve(XYControls.Y1a)))
        xyList.add(XYControlValue(XYControls.Y2a, getValueFromCurve(XYControls.Y2a)))
        xyList.add(XYControlValue(XYControls.Y3a, getValueFromCurve(XYControls.Y3a)))
        return xyList
    }

    private fun getValueFromCurve(xyType: XYControls): Float {
        return getView<DynamicCurve>(R.id.dynamicCurve).getValue(xyType)
    }

    companion object {
        fun newInstance(listener: DynamicCurve.DynamicCurveAdapter): FragmentControls2 =
            FragmentControls2(listener).apply {
                arguments = Bundle().apply {

                }
            }
    }
    
    private fun <T> getView(viewId: Int): T = requireActivity().findViewById(viewId) as T
}