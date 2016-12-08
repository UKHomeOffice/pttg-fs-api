package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}

object LeaveToRemainCalculator {

  private def calcWrapUpPeriod(coursePeriod: Period, preSessional: Boolean) = {
    if (coursePeriod.getYears >= 1) Period.ofMonths(4)
    else if (coursePeriod.getMonths >= 6) Period.ofMonths(1)
    else if (preSessional) Period.ofMonths(1) else Period.ofDays(7)
  }

  private def calculatePeriod(start: LocalDate, end: LocalDate, inclusive: Boolean = true) = {
    Period.between(start, end.plusDays(if (inclusive) 1 else 0))
  }

  private def calculatPeriodInclusive(start: LocalDate, end: LocalDate) = calculatePeriod(start, end)

  private def calculatPeriodExclusive(start: LocalDate, end: LocalDate) = calculatePeriod(start, end, false)


  def calculateLeaveToRemain(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate], originalCourseStartDate: Option[LocalDate], preSessional: Boolean): Option[Period] = {
    for {
      start <- courseStartDate
      end <- courseEndDate
    } yield {

      val startDate = originalCourseStartDate match {
        case Some(originalStart) => originalStart
        case None => start
      }

      val coursePeriod = calculatPeriodInclusive(start, end)
      val wrapUpPeriod = calcWrapUpPeriod(coursePeriod, preSessional)
      coursePeriod.plus(wrapUpPeriod)
    }
  }
}
