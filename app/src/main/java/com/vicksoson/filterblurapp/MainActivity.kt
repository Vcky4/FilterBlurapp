package com.vicksoson.filterblurapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var btnSelectImage: Button
    private lateinit var filterList: RecyclerView
    private lateinit var imageView: ImageView

    private var selectedBitmap: Bitmap? = null
    private val filters = mutableListOf<Filter>()

    //    private var appliedFilters = mutableListOf<Filter>()
    private val REQUEST_IMAGE_PICK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSelectImage = findViewById(R.id.btnSelectImage)
        filterList = findViewById(R.id.filterList)
        imageView = findViewById(R.id.imageView)

        btnSelectImage.setOnClickListener { selectImage() }

        setupFilters()
    }

    private fun setupFilters() {
        filters.add(Filter("Greyscale", FilterType.SWITCH) { bitmap, _, isEnabled, _ ->
            FilterUtils.applyGreyscale(bitmap, isEnabled)
        })

        filters.add(Filter("Brightness", FilterType.SLIDER, 1f) { bitmap, intensity, _, _ ->
            FilterUtils.applyBrightness(bitmap, intensity)
        })

        filters.add(Filter("Contrast", FilterType.SLIDER, 0f) { bitmap, intensity, _, _ ->
            FilterUtils.applyContrast(bitmap, intensity)
        })

        filters.add(Filter("Motion Blur", FilterType.SLIDER, 0f) { bitmap, intensity, _, _ ->
            FilterUtils.applyMotionBlur(this, bitmap, intensity)
        })

        filters.add(Filter("Solarize", FilterType.SLIDER, 0f) { bitmap, intensity, _, _ ->
            FilterUtils.applySolarize(bitmap, intensity)
        })

        filters.add(Filter("Color Tint", FilterType.COLOR_PICKER) { bitmap,_, _, color ->
            FilterUtils.applyColorTint(bitmap, color ?: Color.RED)
        })
        filters.add(
            Filter(
                "Sepia",
                FilterType.SLIDER, 0f
            ) { bitmap, intensity, _, _ -> FilterUtils.applySepia(bitmap, intensity) })
        filters.add(
            Filter(
                "Negative",
                FilterType.SWITCH
            ) { bitmap, _, isEnabled, _ -> FilterUtils.applyNegative(bitmap, isEnabled) })
        filters.add(
            Filter(
                "Gaussian Blur",
                FilterType.SLIDER, 0f
            ) { bitmap, intensity, _, _ -> FilterUtils.applyGaussianBlur(this, bitmap, intensity) })

        filters.add(Filter(
            "Swirl",
            FilterType.SLIDER, 0f
        ) { bitmap, intensity, _, _ -> FilterUtils.applySwirlEffect(bitmap, intensity) })
        filters.add(Filter(
            "Pixelate",
            FilterType.SLIDER, 0f
        ) { bitmap, intensity, _, _ -> FilterUtils.applyPixelate(bitmap, intensity) })
        filters.add(Filter(
            "Vignette",
            FilterType.SLIDER, 0f
        ) { bitmap, intensity, _, _ -> FilterUtils.applyVignette(bitmap, intensity) })
//        filters.add(Filter("Solarize", 0f) { bitmap, intensity -> FilterUtils.applySolarize(bitmap, intensity) })
//
        filters.add(Filter(
            "Motion Blur",
            FilterType.SLIDER, 0f
        ) { bitmap, intensity, _, _ -> FilterUtils.applyMotionBlur(this, bitmap, intensity) })
        filters.add(Filter(
            "Cartoon", FilterType.SWITCH
        ) { bitmap, _, isEnabled, _ ->
            FilterUtils.applyCartoonEffect(this, bitmap, isEnabled)
        })

        filters.add(Filter("Unsharp Mask", FilterType.SLIDER, 0f) { bitmap, intensity, _, _ ->
            FilterUtils.applyUnsharpMask(this, bitmap, intensity)
        })

        filters.add(Filter("Box Blur", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyBoxBlur(this, bitmap, intensity)
        })

        filters.add(Filter("Spin Blur", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applySpinBlur(bitmap, intensity)
        })

        filters.add(Filter("Zoom Blur", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyZoomBlur(bitmap, intensity)
        })

        filters.add(Filter("Hue", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyHue(bitmap, intensity)
        })

        filters.add(Filter("Dither 1x1", FilterType.SWITCH) { bitmap, _, _, _ ->
            FilterUtils.applyDither(bitmap, 1)
        })

        filters.add(Filter("Dither 2x2", FilterType.SWITCH) { bitmap, _, _, _ ->
            FilterUtils.applyDither(bitmap, 2)
        })

        filters.add(Filter("Dither 3x3", FilterType.SWITCH) { bitmap, _, _, _ ->
            FilterUtils.applyDither(bitmap, 3)
        })

        filters.add(Filter("Dither 4x4", FilterType.SWITCH) { bitmap, _, _, _ ->
            FilterUtils.applyDither(bitmap, 4)
        })

        filters.add(Filter("Dither Random", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyDitherRandom(bitmap, intensity)
        })

        filters.add(Filter("Halftone", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyHalftone(bitmap, intensity)
        })

        filters.add(Filter("Hexagonal Pixelate", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyHexagonalPixelate(bitmap, intensity)
        })

        filters.add(Filter("Smoothness", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applySmoothness(this, bitmap, intensity)
        })

        filters.add(Filter("Saturation", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applySaturation(bitmap, intensity)
        })

        filters.add(Filter("Black & White", FilterType.SWITCH) { bitmap, _, isEnabled, _ ->
            FilterUtils.applyBlackAndWhite(bitmap, isEnabled )
        })

        filters.add(Filter("Sketch", FilterType.SLIDER) { bitmap,  _, isEnabled, _ ->
            FilterUtils.applySketch(this, bitmap, isEnabled)
        })

        filters.add(Filter("Ink Effect", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyInkEffect(bitmap, intensity)
        })

        filters.add(Filter("Transparency", FilterType.SLIDER) { bitmap, intensity, _, _ ->
            FilterUtils.applyTransparency(bitmap, intensity)
        })

        val adapter = FilterAdapter(this, filters) {
            applyFilters()
        }

        filterList.layoutManager = LinearLayoutManager(this)
        filterList.adapter = adapter

    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data?.data
            imageUri?.let {
                selectedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                imageView.setImageBitmap(selectedBitmap)
            }
        }
    }

    private fun applyFilters() {
        selectedBitmap?.let { original ->
            var bitmap = original.config?.let { original.copy(it, true) }
            for (filter in filters) {
                if (filter.intensity > 0 || filter.isEnabled || filter.color != null) {
                    bitmap = bitmap?.let {
                        filter.apply(
                            it,
                            filter.intensity,
                            filter.isEnabled,
                            filter.color
                        )
                    }
                }
            }
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun resetImage() {
        selectedBitmap?.let {
            imageView.setImageBitmap(it)
//            appliedFilters.clear()

            // Reset intensity of all filters
            filters.forEach { filter -> filter.intensity = 0f }
        }
    }
}
