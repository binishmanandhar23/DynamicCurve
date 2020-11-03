package com.binish.sample.dynamiccurve.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.binish.dynamiccurve.enums.XYControls
import com.binish.sample.dynamiccurve.R
import com.binish.sample.dynamiccurve.model.XYControlValue
import com.binish.sample.dynamiccurve.utils.Utils.getConvertedValue
import com.binish.sample.dynamiccurve.utils.Utils.getValueInInt

class ControlAdapter(
    private val context: Context,
    private val xyTypeList: ArrayList<XYControlValue>,
    val listener: ControlAdapterInteraction
) : RecyclerView.Adapter<ControlAdapter.ViewHolder>() {

    private var halfWidth = false
    private var disabled = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_xy_controls, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val xyType = xyTypeList[position]
        holder.textViewLabel.text = xyType.xyControl?.type
        holder.seekBar.progress =
            when {
                xyType.xyControl == XYControls.X3a -> holder.seekBar.max
                xyType.xyControl == XYControls.X3 && halfWidth -> holder.seekBar.max / 2
                else -> getValueInInt(xyType.xyValue)
            }
        holder.textViewValue.text =
            when {
                xyType.xyControl == XYControls.X3a -> getConvertedValue(holder.seekBar.max).toString()
                xyType.xyControl == XYControls.X3 && halfWidth -> getConvertedValue(holder.seekBar.max / 2).toString()
                else -> xyType.xyValue.toString()
            }
        holder.seekBar.isEnabled =
            !(xyType.xyControl == XYControls.X3a || (xyType.xyControl == XYControls.X3 && halfWidth) || disabled)

        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) {
                    holder.textViewValue.text = String.format("%.1f", getConvertedValue(progress))
                    xyType.xyValue = getConvertedValue(progress)
                    listener.onValueChanged(xyType)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    override fun getItemCount(): Int = xyTypeList.size


    fun isHalfWidth(value: Boolean) {
        halfWidth = value
        notifyDataSetChanged()
    }

    fun isEnabled(value: Boolean){
        disabled = !value
        notifyDataSetChanged()
    }

    interface ControlAdapterInteraction {
        fun onValueChanged(xyControlValue: XYControlValue)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewLabel = itemView.findViewById<TextView>(R.id.textViewLabel)!!
        val textViewValue = itemView.findViewById<TextView>(R.id.textViewValue)!!
        val seekBar = itemView.findViewById<SeekBar>(R.id.seekBarControl1)!!
    }
}