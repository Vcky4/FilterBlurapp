package com.vicksoson.filterblurapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var btnSelectImage: Button
    private lateinit var btnReset: Button
    private lateinit var filterList: ListView
    private lateinit var imageView: ImageView
    private lateinit var seekBar: SeekBar

    private var selectedBitmap: Bitmap? = null
    private val filters = mutableListOf<Filter>()
    private var appliedFilters = mutableListOf<Filter>()
    private val REQUEST_IMAGE_PICK = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSelectImage = findViewById(R.id.btnSelectImage)
        btnReset = findViewById(R.id.btnReset)
        filterList = findViewById(R.id.filterList)
        imageView = findViewById(R.id.imageView)
        seekBar = findViewById(R.id.seekBar)

        btnSelectImage.setOnClickListener { selectImage() }
        btnReset.setOnClickListener { resetImage() }

        setupFilters()

        filterList.setOnItemClickListener { _, _, position, _ ->
            val filter = filters[position]
            seekBar.progress = (filter.intensity * 100).toInt()
            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        filter.intensity = progress / 100f
                        applyFilters()
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }
    }

    private fun setupFilters() {
        filters.add(Filter("Contrast", 0f) { bitmap, intensity -> FilterUtils.applyContrast(bitmap, intensity) })
        filters.add(Filter("Greyscale", 0f) { bitmap, intensity -> FilterUtils.applyGreyscale(bitmap, intensity) })
        filters.add(Filter("Sepia", 0f) { bitmap, intensity -> FilterUtils.applySepia(bitmap, intensity) })
        filters.add(Filter("Negative", 0f) { bitmap, intensity -> FilterUtils.applyNegative(bitmap, intensity) })
        filters.add(Filter("Brightness", 1f) { bitmap, intensity -> FilterUtils.applyBrightness(bitmap, intensity) })
//        filters.add(Filter("Gaussian Blur", 0f) { bitmap, intensity -> FilterUtils.applyGaussianBlur(this, bitmap, intensity) })

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filters.map { it.name })
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
                appliedFilters.clear()
            }
        }
    }

    private fun applyFilters() {
        selectedBitmap?.let { original ->
            var bitmap = original.config?.let { original.copy(it, true) }
            for (filter in filters) {
                bitmap = bitmap?.let { filter.apply(it, filter.intensity) }
            }
            imageView.setImageBitmap(bitmap)
        }
    }

    private fun resetImage() {
        selectedBitmap?.let {
            imageView.setImageBitmap(it)
            appliedFilters.clear()

            // Reset intensity of all filters
            filters.forEach { filter -> filter.intensity = 0f }
            seekBar.progress = 0
        }
    }
}
