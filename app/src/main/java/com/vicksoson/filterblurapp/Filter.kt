package com.vicksoson.filterblurapp

import android.graphics.Bitmap

data class Filter(
    val name: String,
    var intensity: Float = 0f,
    var apply: (Bitmap, Float) -> Bitmap
)
