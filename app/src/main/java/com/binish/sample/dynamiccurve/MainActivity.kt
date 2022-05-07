package com.binish.sample.dynamiccurve

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.binish.dynamiccurve.DynamicCurveCompose
import com.binish.sample.dynamiccurve.databinding.ActivityMainBinding
import com.binish.sample.dynamiccurve.enums.TabsEnum
import com.binish.sample.dynamiccurve.fragments.BottomSheetController
import com.binish.sample.dynamiccurve.tabs.ComposeTab
import com.binish.sample.dynamiccurve.tabs.XMLTab
import com.binish.sample.dynamiccurve.utils.Utils
import com.binish.sample.dynamiccurve.viewmodel.DynamicCurveViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    val dynamicCurveViewModel by viewModels<DynamicCurveViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        Utils.changeStatusBarColor(this,true, R.color.color_white)


        val mainPagerAdapter = MainPagerAdapter(this)
        binding.mainViewPager.adapter = mainPagerAdapter
        TabLayoutMediator(binding.mainTabLayout,binding.mainViewPager){ tab, page ->
            tab.text = when(page){
                TabsEnum.XML.position -> "XML"
                TabsEnum.Compose.position -> "Compose"
                else -> ""
            }
        }.attach()
    }


    inner class MainPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        private val fragments = listOf(XMLTab(), ComposeTab())
        override fun getItemCount(): Int = fragments.size

        override fun createFragment(position: Int): Fragment = when(position){
            TabsEnum.XML.position -> fragments[TabsEnum.XML.position]
            TabsEnum.Compose.position -> fragments[TabsEnum.Compose.position]
            else -> XMLTab()
        }

        fun getItem(tabsEnum: TabsEnum) = fragments[tabsEnum.position]
    }
}