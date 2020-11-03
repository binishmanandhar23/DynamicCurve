package com.binish.sample.dynamiccurve

import android.os.Bundle
import android.view.WindowManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import com.binish.sample.dynamiccurve.fragments.BottomSheetController
import com.binish.sample.dynamiccurve.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        Utils.changeStatusBarColor(this,true, R.color.color_white)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Utils.pushInAnimation(view, this)
            val bottomSheetStickerDialog = BottomSheetController.newInstance(object :
                BottomSheetController.Companion.BottomMapDetailFragmentInteraction {

            }, dynamicCurve.paintColor)
            bottomSheetStickerDialog.show(supportFragmentManager, "BottomSheetController")
        }
    }

}