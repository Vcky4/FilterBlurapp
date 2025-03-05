package com.vicksoson.filterblurapp

import android.content.Context
import android.graphics.*
import android.renderscript.*
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object FilterUtils {

    // ✅ GREYSCALE
    fun applyGreyscale(bitmap: Bitmap, isEnabled: Boolean): Bitmap {
        if (!isEnabled) return bitmap
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val matrix = ColorMatrix()
        matrix.setSaturation(1f - 1f)  // Reduce saturation to greyscale

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        output?.let { Canvas(it) }?.drawBitmap(bitmap, 0f, 0f, paint)

        return output!!
    }

    // ✅ NEGATIVE
    fun applyNegative(bitmap: Bitmap, isEnabled: Boolean): Bitmap {
        if (!isEnabled) return bitmap
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
        Log.d("FilterUtils", "applyBrightness: $intensity")
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

    fun applyGaussianBlur(context: Context, bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap  // No effect if intensity = 0

        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
        script.setRadius((intensity * 25).coerceIn(0.1f, 25f)) // Prevent crashing
        script.setInput(input)
        script.forEach(output)

        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }

    fun applyMotionBlur(context: Context, bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap  // No effect if intensity is 0

        val rs = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))

        // Set the blur intensity (motion effect)
        script.setRadius(intensity * 15f)  // Max blur intensity = 15
        script.setInput(input)
        script.forEach(output)

        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }


    fun applySwirlEffect(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val centerX = width / 2
        val centerY = height / 2
        val radius = width.coerceAtMost(height) / 2
        val strength = intensity * 3.0  // Controls swirl strength

        for (i in pixels.indices) {
            val x = i % width
            val y = i / width

            val dx = x - centerX
            val dy = y - centerY
            val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

            if (distance < radius) {
                val angle = (1.0 - (distance / radius)) * strength
                val nx = ((dx * cos(angle) - dy * sin(angle)) + centerX).toInt()
                val ny = ((dx * sin(angle) + dy * cos(angle)) + centerY).toInt()

                if (nx in 0 until width && ny in 0 until height) {
                    pixels[i] = bitmap.getPixel(nx, ny)
                }
            }
        }

        output?.setPixels(pixels, 0, width, 0, 0, width, height)
        return output!!
    }


    fun applyPixelate(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val pixelSize = (intensity * 15).toInt().coerceAtLeast(1)
        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val paint = Paint()
        val scaledBitmap =
            Bitmap.createScaledBitmap(bitmap, width / pixelSize, height / pixelSize, false)

        output?.let { Canvas(it) }?.drawBitmap(
            Bitmap.createScaledBitmap(scaledBitmap, width, height, false),
            0f,
            0f,
            paint
        )
        return output!!
    }


    fun applyVignette(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val radius = (width.coerceAtMost(height.plus(50)) * intensity).toInt()

        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }
        val canvas = output?.let { Canvas(it) }
        val paint = Paint().apply {
            shader = RadialGradient(
                width / 2f, height / 2f, radius.toFloat(),
                intArrayOf(Color.TRANSPARENT, Color.BLACK),
                floatArrayOf(0.2f, 1f),
                Shader.TileMode.CLAMP
            )
        }
        if (canvas != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, null)
            canvas.drawCircle(width / 2f, height / 2f, radius.toFloat(), paint)
        }

        return output!!
    }

    fun applyCartoonEffect(context: Context, bitmap: Bitmap, isEnabled: Boolean): Bitmap {
        if (!isEnabled) return bitmap

        val rs = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        // 1. Edge Detection (softer)
        val edgeScript = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        val edgeKernel = floatArrayOf(
            -2f, -2f, -2f,
            -2f, 9f, -1f, // Softer edges
            -1f, 2f, 0f
        )
        edgeScript.setCoefficients(edgeKernel)
        edgeScript.setInput(input)
        edgeScript.forEach(output)

        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }


    fun applySolarize(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val threshold = (128 + (intensity * 50)).toInt()

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            if (r > threshold) pixels[i] = Color.rgb(255 - r, g, b)
            if (g > threshold) pixels[i] = Color.rgb(r, 255 - g, b)
            if (b > threshold) pixels[i] = Color.rgb(r, g, 255 - b)
        }

        output?.setPixels(pixels, 0, width, 0, 0, width, height)
        return output!!
    }

    fun applyUnsharpMask(context: Context, bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val rs = RenderScript.create(context)

        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val sharpKernel = floatArrayOf(
            -1f, -1f, -1f,
            -1f, intensity * 9, -1f,
            -1f, -1f, -1f
        )

        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        script.setCoefficients(sharpKernel)
        script.setInput(input)
        script.forEach(output)

        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }

    fun applyBoxBlur(context: Context, bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val kernelSize = (intensity * 5).toInt().coerceAtLeast(3)
        val kernel = FloatArray(kernelSize * kernelSize) { 1f / (kernelSize * kernelSize) }

        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, bitmap)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        script.setCoefficients(kernel)
        script.setInput(input)
        script.forEach(output)

        output.copyTo(bitmap)
        rs.destroy()

        return bitmap
    }

    fun applySpinBlur(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val centerX = width / 2
        val centerY = height / 2
        val radius = width.coerceAtMost(height) / 2
        val strength = intensity * 2.0

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val dx = x - centerX
                val dy = y - centerY
                val distance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                if (distance < radius) {
                    val angle = strength * (distance / radius)
                    val nx = ((dx * cos(angle) - dy * sin(angle)) + centerX).toInt()
                    val ny = ((dx * sin(angle) + dy * cos(angle)) + centerY).toInt()

                    if (nx in 0 until width && ny in 0 until height) {
                        output?.setPixel(x, y, bitmap.getPixel(nx, ny))
                    }
                }
            }
        }

        return output!!
    }

    fun applyZoomBlur(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val centerX = width / 2
        val centerY = height / 2
        val zoomStrength = intensity * 2f

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (y in 0 until height) {
            for (x in 0 until width) {
                val dx = x - centerX
                val dy = y - centerY
                val nx = (centerX + dx * zoomStrength).toInt()
                val ny = (centerY + dy * zoomStrength).toInt()

                if (nx in 0 until width && ny in 0 until height) {
                    output?.setPixel(x, y, bitmap.getPixel(nx, ny))
                }
            }
        }

        return output!!
    }

    fun applyHexagonalPixelate(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val pixelSize = (intensity * 20).toInt().coerceAtLeast(5)
        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val paint = Paint()
        val hexBitmap = Bitmap.createScaledBitmap(bitmap, width / pixelSize, height / pixelSize, false)
        val scaledBack = Bitmap.createScaledBitmap(hexBitmap, width, height, false)

        if (output != null) {
            Canvas(output).drawBitmap(scaledBack, 0f, 0f, paint)
        }
        return output!!
    }

    fun applyHue(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val paint = Paint()

        val matrix = ColorMatrix()
        matrix.setRotate(0, intensity * 180) // Rotate Red
        matrix.setRotate(1, intensity * 180) // Rotate Green
        matrix.setRotate(2, intensity * 180) // Rotate Blue

        paint.colorFilter = ColorMatrixColorFilter(matrix)
        if (output != null) {
            Canvas(output).drawBitmap(bitmap, 0f, 0f, paint)
        }

        return output!!
    }

    fun applyDither(bitmap: Bitmap, patternSize: Int): Bitmap {
        val output = bitmap.config?.let { Bitmap.createBitmap(bitmap.width, bitmap.height, it) }
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (i in pixels.indices step patternSize) {
            val color = pixels[i]
            val r = Color.red(color) / 255 * patternSize
            val g = Color.green(color) / 255 * patternSize
            val b = Color.blue(color) / 255 * patternSize

            val ditheredColor = Color.rgb(r * 255 / patternSize, g * 255 / patternSize, b * 255 / patternSize)
            pixels[i] = ditheredColor
        }

        output?.setPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        return output!!
    }

    fun applyDitherRandom(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val rand = java.util.Random()

        for (i in pixels.indices) {
            val color = pixels[i]
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)

            val threshold = rand.nextInt((255 * intensity).toInt()).coerceAtMost(255)

            val newR = if (r > threshold) 255 else 0
            val newG = if (g > threshold) 255 else 0
            val newB = if (b > threshold) 255 else 0

            pixels[i] = Color.rgb(newR, newG, newB)
        }

        output?.setPixels(pixels, 0, width, 0, 0, width, height)
        return output!!
    }

    fun applyHalftone(bitmap: Bitmap, intensity: Float): Bitmap {
        if (intensity == 0f) return bitmap

        val width = bitmap.width
        val height = bitmap.height
        val output = bitmap.config?.let { Bitmap.createBitmap(width, height, it) }

        val paint = Paint()
        paint.isAntiAlias = true

        val dotSize = (intensity * 10).toInt().coerceAtLeast(2)
        val stepSize = dotSize * 2

        for (y in 0 until height step stepSize) {
            for (x in 0 until width step stepSize) {
                val color = bitmap.getPixel(x, y)
                val brightness = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3
                val radius = (dotSize * (1 - brightness / 255f)).coerceAtLeast(1f)

                paint.color = Color.BLACK
                output?.let { Canvas(it) }?.drawCircle(x.toFloat(), y.toFloat(), radius, paint)
            }
        }

        return output!!
    }


}
