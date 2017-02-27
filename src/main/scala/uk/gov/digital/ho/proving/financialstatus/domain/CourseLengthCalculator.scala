package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}

object CourseLengthCalculator {

  val MONTHS_IN_YEAR = 12

  def differenceInMonths(firstDate: LocalDate, secondDate: LocalDate): Int = {
    val (startDate, endDate) = if (secondDate.isAfter(firstDate)) (firstDate, secondDate) else (secondDate, firstDate)
    // Add 1 day to end date as we must include the end date
    val period = Period.between(startDate, endDate.plusDays(1))
    val months = period.getMonths + (MONTHS_IN_YEAR * period.getYears)
    if (period.getDays > 0) months + 1 else months
  }

}
