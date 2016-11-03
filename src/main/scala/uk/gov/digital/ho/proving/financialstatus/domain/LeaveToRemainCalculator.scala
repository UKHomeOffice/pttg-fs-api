package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

object LeaveToRemainCalculator {

  private def calcWrapUpPeriod(courseLength: Int, leaveToRemainBoundary: Int, shortLeaveToRemain: Int, longLeaveToRemain: Int): Int = {
    if (courseLength < leaveToRemainBoundary) shortLeaveToRemain else longLeaveToRemain
  }

  def calculateLeaveToRemain(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate], courseContinuationEndDate: Option[LocalDate],
                             leaveToRemainBoundary: Int, shortLeaveToRemain: Int, longLeaveToRemain: Int): Option[Int] = {
    for {
      start <- courseStartDate
      end <- courseEndDate
    } yield {
      val leaveToRemain = courseContinuationEndDate match {
        case Some(extEnd) =>
          val courseLength = CourseLengthCalculator.differenceInMonths(start, extEnd)
          val continuationCourseLength = CourseLengthCalculator.differenceInMonths(end.plusDays(1), extEnd)
          continuationCourseLength + calcWrapUpPeriod(courseLength, leaveToRemainBoundary, shortLeaveToRemain, longLeaveToRemain)
        case None =>
          val courseLength = CourseLengthCalculator.differenceInMonths(start, end)
          courseLength + calcWrapUpPeriod(courseLength, leaveToRemainBoundary, shortLeaveToRemain, longLeaveToRemain)
      }
      leaveToRemain
    }
  }
}
