package com.arunika.studysmart.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
object CurrentOrFutureSelectableDates: SelectableDates {
	override fun isSelectableDate(utcTimeMillis: Long): Boolean {
		val currentDateMillis =
			LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
		return utcTimeMillis >= currentDateMillis
	}

	override fun isSelectableYear(year: Int): Boolean {
		return year >= LocalDate.now().year
	}
}