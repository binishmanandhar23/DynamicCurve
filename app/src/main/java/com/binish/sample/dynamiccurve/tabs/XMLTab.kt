package com.binish.sample.dynamiccurve.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.binish.sample.dynamiccurve.databinding.FragmentXmlTabBinding
import com.binish.sample.dynamiccurve.fragments.BottomSheetController
import com.binish.sample.dynamiccurve.utils.Utils

class XMLTab: Fragment() {
    private var _binding: FragmentXmlTabBinding? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentXmlTabBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener { view ->
            Utils.pushInAnimation(view, requireContext())
            val bottomSheetStickerDialog = BottomSheetController.newInstance(object :
                BottomSheetController.Companion.BottomMapDetailFragmentInteraction {

            }, binding.dynamicCurve.paintColor)
            bottomSheetStickerDialog.show(parentFragmentManager, "BottomSheetController")
        }
    }
}