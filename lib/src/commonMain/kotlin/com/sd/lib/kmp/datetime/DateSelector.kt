package com.sd.lib.kmp.datetime

import kotlinx.datetime.LocalDate

class DateSelector(
  startDate: LocalDate = fDate(1900, 1, 1),
  endDate: LocalDate = fCurrentDate(),
  private val onStateChanged: (State) -> Unit,
) {
  data class State(
    val date: LocalDate?,
    val listYear: List<Int>,
    val listMonth: List<Int>,
    val listDayOfMonth: List<Int>,
    val indexOfYear: Int,
    val indexOfMonth: Int,
    val indexOfDayOfMonth: Int,
  ) {
    companion object {
      val Empty = State(
        date = null,
        listYear = emptyList(),
        listMonth = emptyList(),
        listDayOfMonth = emptyList(),
        indexOfYear = -1,
        indexOfMonth = -1,
        indexOfDayOfMonth = -1,
      )
    }
  }

  private val _startDate = startDate
  private val _endDate = endDate.coerceAtLeast(startDate)

  private var _state = State.Empty

  val state: State get() = _state

  fun setDate(date: LocalDate) {
    val safeDate = date.coerceIn(_startDate, _endDate)
    val oldState = _state
    val newState = oldState.newState(safeDate).also { _state = it }
    if (newState != oldState) {
      onStateChanged(newState)
    }
  }

  private fun State.newState(date: LocalDate): State {
    if (this.date == date) return this

    val listYear = listYear.ifEmpty {
      (_startDate.year.._endDate.year).toList()
    }

    val listMonth = run {
      val end = if (date.year == _endDate.year) _endDate.monthNumber else 12
      if (end == listMonth.lastOrNull()) listMonth else (1..end).toList()
    }

    val listDayOfMonth = run {
      val end = if (date.year == _endDate.year && date.month == _endDate.month) {
        _endDate.dayOfMonth
      } else {
        date.fMaxDayOfMonth()
      }
      if (end == listDayOfMonth.lastOrNull()) listDayOfMonth else (1..end).toList()
    }

    return State(
      date = date,
      listYear = listYear,
      listMonth = listMonth,
      listDayOfMonth = listDayOfMonth,
      indexOfYear = date.year - listYear.first(),
      indexOfMonth = date.monthNumber - listMonth.first(),
      indexOfDayOfMonth = date.dayOfMonth - listDayOfMonth.first(),
    )
  }
}

fun DateSelector.selectYearWithIndex(index: Int) {
  val date = state.date ?: return
  val year = state.listYear.getOrNull(index) ?: return
  setDate(date.fCopy(year = year))
}

fun DateSelector.selectMonthWithIndex(index: Int) {
  val date = state.date ?: return
  val month = state.listMonth.getOrNull(index) ?: return
  setDate(date.fCopy(month = month))
}

fun DateSelector.selectDayOfMonthWithIndex(index: Int) {
  val date = state.date ?: return
  val dayOfMonth = state.listDayOfMonth.getOrNull(index) ?: return
  setDate(date.fCopy(dayOfMonth = dayOfMonth))
}