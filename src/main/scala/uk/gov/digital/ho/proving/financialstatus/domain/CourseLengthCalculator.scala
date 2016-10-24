package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.{LocalDate, Period}

object CourseLengthCalculator {

  val MONTHS_IN_YEAR = 12

  def differenceInMonths(firstDate: LocalDate, secondDate: LocalDate): Integer = {
    val (startDate, endDate) = if (secondDate.isAfter(firstDate)) (firstDate, secondDate) else (secondDate, firstDate)
    // Add 1 day to end date as we must include the end date
    val period = Period.between(startDate, endDate.plusDays(1))
    val months = period.getMonths + (MONTHS_IN_YEAR * period.getYears)
    if (period.getDays > 0 ) months + 1 else months
  }

  def calculateCourseLength(courseStartDate: LocalDate, courseEndDate: LocalDate, courseExtensionEndDate: Option[LocalDate], maximumCourseLength: Int = 0): Int = {
    val courseLength = courseExtensionEndDate match {
      case Some(extEndDate) => differenceInMonths(courseEndDate.plusDays(1), extEndDate)
      case None => differenceInMonths(courseStartDate, courseEndDate)
    }
    if (courseLength > maximumCourseLength && maximumCourseLength > 0) maximumCourseLength else courseLength
  }

  def calculateCourseLength(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate],
                            courseExtensionEndDate: Option[LocalDate]): Option[Int] = {
    for {startDate <- courseStartDate
         endDate <- courseEndDate}
      yield {
        calculateCourseLength(startDate, endDate, courseExtensionEndDate)
      }
  }
}
