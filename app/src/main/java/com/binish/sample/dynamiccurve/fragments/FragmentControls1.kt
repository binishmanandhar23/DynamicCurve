package com.binish.sample.dynamiccurve.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.binish.dynamiccurve.DynamicCurve
import com.binish.dynamiccurve.enums.XYControls
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.adapters.ControlAdapter
import com.binish.sample.dynamiccurve.databinding.FragmentControlsBinding
import com.binish.sample.dynamiccurve.model.XYControlValue

class FragmentControls1(private val listener: DynamicCurve.DynamicCurveAdapter) : Fragment() {
    private var _binding: FragmentControlsBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentControlsBinding.inflate(inflater,container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val control1Adapter = ControlAdapter(
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
        getView<DynamicCurve>(R.id.dynamicCurve).setUpListener(listener)
        binding.recyclerViewControl1.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewControl1.adapter = control1Adapter
    }

    private fun getXYList(): ArrayList<XYControlValue> {
        val xyList = ArrayList<XYControlValue>()
        xyList.add(XYControlValue(XYControls.X0, getValueFromCurve(XYControls.X0)))
        xyList.add(XYControlValue(XYControls.X1, getValueFromCurve(XYControls.X1)))
        xyList.add(XYControlValue(XYControls.X2, getValueFromCurve(XYControls.X2)))
        xyList.add(XYControlValue(XYControls.X3, getValueFromCurve(XYControls.X3)))
        xyList.add(XYControlValue(XYControls.Y0, getValueFromCurve(XYControls.Y0)))
        xyList.add(XYControlValue(XYControls.Y1, getValueFromCurve(XYControls.Y1)))
        xyList.add(XYControlValue(XYControls.Y2, getValueFromCurve(XYControls.Y2)))
        xyList.add(XYControlValue(XYControls.Y3, getValueFromCurve(XYControls.Y3)))
        return xyList
    }

    private fun getValueFromCurve(xyType: XYControls): Float {
        return getView<DynamicCurve>(R.id.dynamicCurve).getValue(xyType)
    }

    companion object {
        fun newInstance(listener: DynamicCurve.DynamicCurveAdapter): FragmentControls1 = FragmentControls1(listener).apply {
            arguments = Bundle().apply {

            }
        }
    }

    private fun <T> getView(viewId: Int): T = requireActivity().findViewById(viewId) as T
}