package com.arunika.studysmart.data.local

import androidx.compose.ui.graphics.Color
import androidx.room.TypeConverter

class ColorListConverter {

	@TypeConverter
	fun fromColorList(colors: List<Int>): String {
		return colors.joinToString(",") { it.toString() }
	}

	@TypeConverter
	fun toColorList(colorsString: String): List<Int> {
		return colorsString.split(",").map { it.toInt() }
	}

}