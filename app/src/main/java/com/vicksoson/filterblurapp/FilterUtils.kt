package com.vicksoson.filterblurapp

import android.content.Context
import android.graphics.*
import android.renderscript.*

object FilterUtils {

    // ✅ GREYSCALE
    fun applyGreyscale(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val matrix = ColorMatrix()
        matrix.setSaturation(1f - intensity)  // Reduce saturation to greyscale

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ NEGATIVE
    fun applyNegative(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val matrix = ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ SEPIA
    fun applySepia(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val matrix = ColorMatrix(
            floatArrayOf(
                0.393f + (1 - intensity), 0.769f, 0.189f, 0f, 0f,
                0.349f, 0.686f + (1 - intensity), 0.168f, 0f, 0f,
                0.272f, 0.534f, 0.131f + (1 - intensity), 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ BRIGHTNESS
    fun applyBrightness(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val brightness = 255 * (intensity - 1f)

        val matrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, brightness,
                0f, 1f, 0f, 0f, brightness,
                0f, 0f, 1f, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ CONTRAST
    fun applyContrast(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val scale = intensity + 1f
        val translate = (-0.5f * scale + 0.5f) * 255f

        val matrix = ColorMatrix(
            floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ BLUR (Gaussian)
    fun applyGaussianBlur(context: Context, bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap
        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius(intensity * 25f)  // Max intensity = 25
        script.setInput(input)
        script.forEach(output)
        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }
}
