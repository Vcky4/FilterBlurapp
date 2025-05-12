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
    var color: Int? = null,  // Added color input for color filters
    val apply: (Bitmap, Float, Boolean, Int?) -> Bitmap  // Added color input for color filters
)


data class CurrencyRate(
    var name: String,
    var rate: Double,
)

data class Currency(
    val name: String,
    val rate: List<CurrencyRate>
)

val mockData = listOf(
    Currency(
        "USD", listOf(
            CurrencyRate("CAD", 1.35),
            CurrencyRate("NGN", 1480.50),
            CurrencyRate("ZAR", 18.50),
            CurrencyRate("GHS", 13.10),
        )
    ),
    Currency(
        "CAD", listOf(
            CurrencyRate("USD", 0.74),
            CurrencyRate("NGN", 1096.30),
            CurrencyRate("ZAR", 13.70),
            CurrencyRate("GHS", 9.80),
        )
    ),
    Currency(
        "NGN", listOf(
            CurrencyRate("USD", 0.00068),
            CurrencyRate("CAD", 0.00091),
            CurrencyRate("ZAR", 0.0125),
            CurrencyRate("GHS", 0.009),
        )
    ),
    Currency(
        "ZAR", listOf(
            CurrencyRate("USD", 0.054),
            CurrencyRate("CAD", 0.073),
            CurrencyRate("NGN", 80.00),
            CurrencyRate("GHS", 0.70),
        )
    ),
    Currency(
        "GHS", listOf(
            CurrencyRate("USD", 0.076),
            CurrencyRate("CAD", 0.102),
            CurrencyRate("NGN", 110.00),
            CurrencyRate("ZAR", 1.42),
        )
    ),
)


fun convertCurrency(base: String, target: String, amount: Double): Double{
    val currency = mockData.find{c-> c.name == base}
    if(currency == null) return 0.0
    val targetCurrency = currency.rate.find{t-> t.name == target}?.rate
    if(targetCurrency == null) return 0.0
    val convertedAmount = amount * targetCurrency

    return convertedAmount
}
// {
//  "USD": {
//    "CAD": 1.35,
//    "NGN": 1480.50,
//    "ZAR": 18.50,
//    "GHS": 13.10
//  },
//  "CAD": {
//    "USD": 0.74,
//    "NGN": 1096.30,
//    "ZAR": 13.70,
//    "GHS": 9.80
//  },
//  "NGN": {
//    "USD": 0.00068,
//    "CAD": 0.00091,
//    "ZAR": 0.0125,
//    "GHS": 0.009
//  },
//  "ZAR": {
//    "USD": 0.054,
//    "CAD": 0.073,
//    "NGN": 80.00,
//    "GHS": 0.70
//  },
//  "GHS": {
//    "USD": 0.076,
//    "CAD": 0.102,
//    "NGN": 110.00,
//    "ZAR": 1.42
//  }
// }