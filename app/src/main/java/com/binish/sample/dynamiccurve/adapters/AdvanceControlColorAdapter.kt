package com.binish.sample.dynamiccurve.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.binish.dynamiccurve.DynamicCurve
import com.binish.sample.dynamiccurve.R

class AdvanceControlColorAdapter(
    private val context: Context,
    private val colorList: ArrayList<Int>,
    private var selectedIndex: Int,
    private val listener: AdvanceControlColorInteraction
) : RecyclerView.Adapter<AdvanceControlColorAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.layout_color_chooser, parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = colorList[position]
        holder.constraintLayoutOuterColor.visibility = View.VISIBLE
        holder.constraintLayoutSelectedColor.visibility =
            if (selectedIndex == position) View.VISIBLE else View.GONE
        holder.constraintLayoutInnerColor.backgroundTintList =
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                ContextCompat.getColorStateList(context, color)
            else
                AppCompatResources.getColorStateList(context, color)

        holder.colorChooserMainContainer.setOnClickListener {
            makeChanges(position)
            listener.onColorPicked(color)
        }
    }

    private fun makeChanges(position: Int){
        val oldPosition = selectedIndex
        selectedIndex = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedIndex)
    }

    override fun getItemCount(): Int = colorList.size

    fun interface AdvanceControlColorInteraction{
        fun onColorPicked(color: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraintLayoutInnerColor =
            itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutInnerColor)!!
        val constraintLayoutOuterColor =
            itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutOuterColor)!!
        val constraintLayoutSelectedColor =
            itemView.findViewById<ConstraintLayout>(R.id.constraintLayoutSelectedColor)!!
        val colorChooserMainContainer =
            itemView.findViewById<ConstraintLayout>(R.id.colorChooserMainContainer)!!
    }
}