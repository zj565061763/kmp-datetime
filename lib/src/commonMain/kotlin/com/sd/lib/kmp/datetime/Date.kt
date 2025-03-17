package com.sd.lib.kmp.datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn

fun fDate(
  year: Int,
  month: Int,
  dayOfMonth: Int,
): LocalDate {
  val safeYear = year.coerceAtLeast(1)
  val safeMonth = month.coerceIn(1, 12)
  val safeDayOfMonth = dayOfMonth.coerceIn(1, maxDayOfMonth(safeYear, safeMonth))
  return LocalDate(year = safeYear, monthNumber = safeMonth, dayOfMonth = safeDayOfMonth)
}

fun fCurrentDate(): LocalDate {
  return Clock.System.todayIn(TimeZone.currentSystemDefault())
}

fun LocalDate.fCopy(
  year: Int = this.year,
  month: Int = this.monthNumber,
  dayOfMonth: Int = this.dayOfMonth,
): LocalDate {
  return fDate(
    year = year,
    month = month,
    dayOfMonth = dayOfMonth,
  )
}

fun LocalDate.fMaxDayOfMonth(): Int {
  return maxDayOfMonth(year = year, month = monthNumber)
}

private fun maxDayOfMonth(year: Int, month: Int): Int {
  return LocalDate(year, month, 1)
    .plus(1, DateTimeUnit.MONTH)
    .minus(1, DateTimeUnit.DAY)
    .dayOfMonth
}