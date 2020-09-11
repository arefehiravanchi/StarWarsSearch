package com.arefe.starwars.utilities

import java.text.DecimalFormat

class Utils {

    companion object {

        fun getFormattedHeight(centi: String) : String {
            val mInch = convertCmToInch(centi.toInt())
            val feet = (mInch / 12).toInt()
            val inch = mInch % 12
            return "$centi Cm ($feet Feet ${DecimalFormat("##.#").format(inch)} Inch)"
        }

        private fun convertCmToInch(centi: Int):Double {
            return 0.3937 * centi
        }
    }
}