package com.vicksoson.filterblurapp

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial

class FilterAdapter(
    private val context: Context,
    private val items: MutableList<Filter>, // Use MutableList to track changes
    private val onFilterChanged: (Filter) -> Unit // Callback function
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.itemText)
        val switchView: SwitchMaterial = view.findViewById(R.id.itemSwitch)
        val seekBar: SeekBar = view.findViewById(R.id.itemSlider)
        val resetBt: ImageView = view.findViewById(R.id.resetIcon)
        val colorSelector: LinearLayout = view.findViewById(R.id.colorSelector)
        val colorDisplay: TextView = view.findViewById(R.id.colorDisplay)
        val seekRed: SeekBar = view.findViewById(R.id.seekRed)
        val seekGreen: SeekBar = view.findViewById(R.id.seekGreen)
        val seekBlue: SeekBar = view.findViewById(R.id.seekBlue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val item = items[position]

        holder.textView.text = item.name
        holder.switchView.setOnCheckedChangeListener(null) // Avoid unwanted triggers
        holder.seekBar.setOnSeekBarChangeListener(null) // Avoid multiple listener calls

        // Restore previous state
        holder.switchView.isChecked = item.isEnabled
        holder.seekBar.progress = (item.intensity * 100).toInt()

        // Show/Hide switch and slider based on filter type
        holder.switchView.visibility =
            if (item.type == FilterType.SWITCH) View.VISIBLE else View.GONE
        holder.seekBar.visibility = if (item.type == FilterType.SLIDER) View.VISIBLE else View.GONE
        holder.colorSelector.visibility =
            if (item.type == FilterType.COLOR_PICKER) View.VISIBLE else View.GONE

        // Handle Reset button click
        holder.resetBt.setOnClickListener {
            item.intensity = 0f
            item.isEnabled = false
            holder.switchView.isChecked = false
            holder.seekBar.progress = 0
            notifyItemChanged(position) // Refresh item UI
            onFilterChanged(item)
        }

        // Handle Switch changes
        holder.switchView.setOnCheckedChangeListener { _, isChecked ->
            item.isEnabled = isChecked
            onFilterChanged(item)
        }

        //handle color changes
        val listener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val color = Color.rgb(
                        holder.seekRed.progress,
                        holder.seekGreen.progress,
                        holder.seekBlue.progress
                    )
                    holder.colorDisplay.setBackgroundColor(color)
                    item.color = color
                    onFilterChanged(item)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        holder.seekRed.setOnSeekBarChangeListener(listener)
        holder.seekGreen.setOnSeekBarChangeListener(listener)
        holder.seekBlue.setOnSeekBarChangeListener(listener)

        // Handle Slider changes
        holder.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    item.intensity = progress / 100f
                    Log.d("FilterAdapter", "onProgressChanged: ${item.name} - ${progress / 100f}")
                    onFilterChanged(item)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun getItemCount(): Int = items.size

}
