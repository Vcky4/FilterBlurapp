package com.vicksoson.filterblurapp

import android.graphics.Bitmap

enum class FilterType {
    SLIDER, SWITCH, COLOR_PICKER
}

data class Filter(
    val name: String,
    val type: FilterType,
    var intensity: Float = 0f,
    var isEnabled: Boolean = false,
    val color: Int? = null,  // Added color input for color filters
    val apply: (Bitmap, Float, Boolean, Int?) -> Bitmap  // Added color input for color filters
)
