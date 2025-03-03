package com.sd.lib.kmp.datetime

import kotlinx.datetime.LocalDate

class DateSelector(
  startDate: LocalDate,
  endDate: LocalDate,
  private val onStateChanged: (State) -> Unit,
) {
  data class State(
    val date: LocalDate,
    val listYear: List<Int>,
    val listMonth: List<Int>,
    val listDayOfMonth: List<Int>,
  ) {
    val indexOfYear get() = date.year - listYear.first()
    val indexOfMonth get() = date.monthNumber - listMonth.first()
    val indexOfDayOfMonth get() = date.dayOfMonth - listDayOfMonth.first()
  }

  private val _startDate: LocalDate = startDate
  private val _endDate: LocalDate = endDate.coerceAtLeast(startDate)

  private var _state: State? = null

  val state: State? get() = _state

  fun setDate(date: LocalDate) {
    val safeDate = date.coerceIn(_startDate, _endDate)

    val oldState = _state
    if (oldState == null) {
      val initState = initState(safeDate).also { _state = it }
      onStateChanged(initState)
      return
    }

    val newState = oldState.newState(safeDate).also { _state = it }
    if (newState != oldState) {
      onStateChanged(newState)
    }
  }

  private fun State.newState(date: LocalDate): State {
    if (this.date == date) return this

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
    )
  }

  private fun initState(date: LocalDate): State {
    val listYear = (_startDate.year.._endDate.year).toList()

    val listMonth = run {
      val end = if (date.year == _endDate.year) _endDate.monthNumber else 12
      (1..end).toList()
    }

    val listDayOfMonth = run {
      val end = if (date.year == _endDate.year && date.month == _endDate.month) {
        _endDate.dayOfMonth
      } else {
        date.fMaxDayOfMonth()
      }
      (1..end).toList()
    }

    return State(
      date = date,
      listYear = listYear,
      listMonth = listMonth,
      listDayOfMonth = listDayOfMonth,
    )
  }
}

fun DateSelector.selectYearWithIndex(index: Int) {
  val state = this.state ?: return
  val year = state.listYear.getOrNull(index) ?: return
  setDate(state.date.fCopy(year = year))
}

fun DateSelector.selectMonthWithIndex(index: Int) {
  val state = this.state ?: return
  val month = state.listMonth.getOrNull(index) ?: return
  setDate(state.date.fCopy(month = month))
}

fun DateSelector.selectDayOfMonthWithIndex(index: Int) {
  val state = this.state ?: return
  val dayOfMonth = state.listDayOfMonth.getOrNull(index) ?: return
  setDate(state.date.fCopy(dayOfMonth = dayOfMonth))
}