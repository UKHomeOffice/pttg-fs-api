package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}

object LeaveToRemainCalculator {

  private val ONE_YEAR = 1
  private val SIX_MONTHS = 6

  private def calcWrapUpPeriod(coursePeriod: Period, preSessional: Boolean) = {
    if (coursePeriod.getYears >= ONE_YEAR) Period.ofMonths(4)
    else if (coursePeriod.getMonths >= SIX_MONTHS) Period.ofMonths(2)
    else if (preSessional) Period.ofMonths(1) else Period.ofDays(7)
  }

  private def calculatePeriod(start: LocalDate, end: LocalDate, inclusive: Boolean = true) = {
    Period.between(start, end.plusDays(if (inclusive) 1 else 0))
  }

  private def calculatePeriodInclusive(start: LocalDate, end: LocalDate) = calculatePeriod(start, end)

  private def calculatePeriodExclusive(start: LocalDate, end: LocalDate) = calculatePeriod(start, end, false)


  def calculateLeaveToRemain(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate],
                             originalCourseStartDate: Option[LocalDate], preSessional: Boolean): Option[LocalDate] = {
    for {
      start <- courseStartDate
      end <- courseEndDate
    } yield {

      val startDate = originalCourseStartDate match {
        case Some(originalStart) => originalStart
        case None => start
      }

      val coursePeriod = calculatePeriodInclusive(startDate, end)
      println(s"coursePeriod: $coursePeriod")

      val wrapUpPeriod = calcWrapUpPeriod(coursePeriod, preSessional)
      println(s"wrapUpPeriod: $wrapUpPeriod")
      end.plus(wrapUpPeriod)
    }
  }

  def calculateLeaveToRemain(courseStartDate: LocalDate, courseEndDate: LocalDate,
                             originalCourseStartDate: Option[LocalDate], preSessional: Boolean): LocalDate = {

    val startDate = originalCourseStartDate match {
      case Some(originalStart) => originalStart
      case None => courseStartDate
    }

    val coursePeriod = calculatePeriodInclusive(startDate, courseEndDate)

    val wrapUpPeriod = calcWrapUpPeriod(coursePeriod, preSessional)
    courseEndDate.plus(wrapUpPeriod)
  }

  def calculateFixedLeaveToRemain(courseEndDate: LocalDate, period: Period): LocalDate = {
    courseEndDate.plus(period)
  }

}
