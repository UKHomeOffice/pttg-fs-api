package uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes

import java.time.LocalDate

import org.springframework.stereotype.Service
import uk.gov.digital.ho.proving.financialstatus.domain.BelowDegreeCourse
import uk.gov.digital.ho.proving.financialstatus.domain.CourseLengthCalculator
import uk.gov.digital.ho.proving.financialstatus.domain.CourseType
import uk.gov.digital.ho.proving.financialstatus.domain.DoctorateExtensionStudent
import uk.gov.digital.ho.proving.financialstatus.domain.GeneralStudent
import uk.gov.digital.ho.proving.financialstatus.domain.MainCourse
import uk.gov.digital.ho.proving.financialstatus.domain.PostGraduateDoctorDentistStudent
import uk.gov.digital.ho.proving.financialstatus.domain.PreSessionalCourse
import uk.gov.digital.ho.proving.financialstatus.domain.StudentType
import uk.gov.digital.ho.proving.financialstatus.domain.StudentUnionSabbaticalOfficerStudent
import uk.gov.digital.ho.proving.financialstatus.domain.UnknownStudent

@Service
class ConditionCodesCalculatorProvider() {

  def provide(studentType: StudentType = UnknownStudent("")): ConditionCodesCalculator = studentType match {
    case GeneralStudent => new GeneralConditionCodesCalculator
    case DoctorateExtensionStudent | PostGraduateDoctorDentistStudent | StudentUnionSabbaticalOfficerStudent =>
      new OtherNonGeneralConditionCodesCalculator(studentType)
  }

}

/**
  * Encapsulates logic for calculating condition codes
  */
trait ConditionCodesCalculator {
  def calculateConditionCodes(dependanstOnly: Boolean,
                              dependants: Option[Int],
                              courseStartDate: Option[LocalDate],
                              courseEndDate: Option[LocalDate],
                              courseType: CourseType,
                              recognisedBodyOrHEI: Option[Boolean]): ConditionCodesCalculationResult
}

class GeneralConditionCodesCalculator extends ConditionCodesCalculator {
  override def calculateConditionCodes(dependanstOnly: Boolean,
                                       dependants: Option[Int],
                                       courseStartDate: Option[LocalDate],
                                       courseEndDate: Option[LocalDate],
                                       courseType: CourseType,
                                       recognisedBodyOrHEI: Option[Boolean]): ConditionCodesCalculationResult = {

    def calculateApplicantConditionCode(): Option[ApplicantConditionCode] =
      if (dependanstOnly) None
      else courseType match {
        case MainCourse => recognisedBodyOrHEI match {
          case Some(true) => Some(ApplicantConditionCode ("2"))
          case Some(false) => Some(ApplicantConditionCode ("3"))
        }
        case PreSessionalCourse | BelowDegreeCourse => recognisedBodyOrHEI match {
          case Some(true) => Some(ApplicantConditionCode("2A"))
          case Some(false) => Some(ApplicantConditionCode("3"))
        }
    }

    def calculatePartnerConditionCode(): Option[PartnerConditionCode] = dependants match {
      case None => None
      case Some(noOfDeps) if noOfDeps == 0 => None
      case _ =>

        if (courseType == MainCourse) {
          (courseStartDate, courseEndDate) match {
            case (Some(start), Some(end)) =>
              val courseLengthInMonths = CourseLengthCalculator.differenceInMonths(start, end)
              if (courseLengthInMonths >= 12) Some(PartnerConditionCode("4B"))
              else Some(PartnerConditionCode("3"))
          }
      } else {
          Some(PartnerConditionCode("3"))
      }

    }

    def calculateChildConditionCode(): Option[ChildConditionCode] = dependants match {
      case None => None
      case Some(noOfDeps) if noOfDeps == 0 => None
      case _ => Some(ChildConditionCode("1"))
    }

    ConditionCodesCalculationResult(
      calculateApplicantConditionCode(),
      calculatePartnerConditionCode(),
      calculateChildConditionCode()
    )
  }
}

class OtherNonGeneralConditionCodesCalculator(studentType: StudentType) extends ConditionCodesCalculator {

  override def calculateConditionCodes(dependanstOnly: Boolean,
                                       dependants: Option[Int],
                                       courseStartDate: Option[LocalDate],
                                       courseEndDate: Option[LocalDate],
                                       courseType: CourseType,
                                       recognisedBodyOrHEI: Option[Boolean]): ConditionCodesCalculationResult = {

    def calculateApplicantConditionCode(): Option[ApplicantConditionCode] = studentType match {
      case _ if dependanstOnly => None
      case DoctorateExtensionStudent => Some(ApplicantConditionCode("4E"))
      case PostGraduateDoctorDentistStudent | StudentUnionSabbaticalOfficerStudent => Some(ApplicantConditionCode("2"))
      case _ => None
    }

    def calculatePartnerConditionCode(): Option[PartnerConditionCode] = dependants match {
      case None | Some(0) => None
      case _ => Some(PartnerConditionCode("4B"))
    }


    def calculateChildConditionCode(): Option[ChildConditionCode] = dependants match {
      case None | Some(0) => None
      case _ => Some(ChildConditionCode("1"))
    }

    ConditionCodesCalculationResult(
      calculateApplicantConditionCode(),
      calculatePartnerConditionCode(),
      calculateChildConditionCode()
    )
  }
}
