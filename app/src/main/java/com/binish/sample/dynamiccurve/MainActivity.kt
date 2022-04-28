package com.binish.sample.dynamiccurve

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.binish.sample.dynamiccurve.databinding.ActivityMainBinding
import com.binish.sample.dynamiccurve.fragments.BottomSheetController
import com.binish.sample.dynamiccurve.utils.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        Utils.changeStatusBarColor(this,true, R.color.color_white)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Utils.pushInAnimation(view, this)
            val bottomSheetStickerDialog = BottomSheetController.newInstance(object :
                BottomSheetController.Companion.BottomMapDetailFragmentInteraction {

            }, binding.dynamicCurve.paintColor)
            bottomSheetStickerDialog.show(supportFragmentManager, "BottomSheetController")
        }
    }

}