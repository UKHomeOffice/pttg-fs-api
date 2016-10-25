package uk.gov.digital.ho.proving.financialstatus.domain

import java.time.LocalDate

object LeaveToRemainCalculator {

  def calculateLeaveToRemain(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate], courseExtensionEndDate: Option[LocalDate],
                             leaveToRemainBoundary: Int, shortLeaveToRemain: Int, longLeaveToRemain: Int): Option[Int] = {
    for {
      start <- courseStartDate
      end <- courseEndDate
    } yield {
      val courseLength = courseExtensionEndDate match {
        case Some(extEnd) => CourseLengthCalculator.differenceInMonths(start,extEnd)
        case None =>CourseLengthCalculator.differenceInMonths(start,end)
      }
      if (courseLength < leaveToRemainBoundary) shortLeaveToRemain + courseLength else longLeaveToRemain + courseLength
    }
  }
}
