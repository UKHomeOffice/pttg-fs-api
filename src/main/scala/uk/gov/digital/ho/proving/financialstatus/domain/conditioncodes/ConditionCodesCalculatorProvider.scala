package uk.gov.digital.ho.proving.financialstatus.domain.conditioncodes

import java.time.LocalDate

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.syntax.apply._
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

@Service
class ConditionCodesCalculatorProvider() {

  def provide(studentType: StudentType): Validated[ConditionCodesParameterError, ConditionCodesCalculator] = studentType match {
    case GeneralStudent => Valid(new GeneralConditionCodesCalculator)
    case DoctorateExtensionStudent | PostGraduateDoctorDentistStudent | StudentUnionSabbaticalOfficerStudent =>
      Valid(new OtherNonGeneralConditionCodesCalculator(studentType))
    case _ => Invalid(ConditionCodesParameterError(s"student type $studentType is not valid"))
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
                              recognisedBodyOrHEI: Option[Boolean]): Validated[ConditionCodesParameterError, ConditionCodesCalculationResult]
}

class GeneralConditionCodesCalculator extends ConditionCodesCalculator {
  override def calculateConditionCodes(dependanstOnly: Boolean,
                                       dependants: Option[Int],
                                       courseStartDate: Option[LocalDate],
                                       courseEndDate: Option[LocalDate],
                                       courseType: CourseType,
                                       recognisedBodyOrHEI: Option[Boolean]): Validated[ConditionCodesParameterError, ConditionCodesCalculationResult] = {

    def calculateApplicantConditionCode(): Validated[ConditionCodesParameterError, Option[ApplicantConditionCode]] =
      if (dependanstOnly) Valid(None)
      else courseType match {
        case MainCourse => recognisedBodyOrHEI match {
          case Some(true) => Valid(Some(ApplicantConditionCode ("2")))
          case Some(false) => Valid(Some(ApplicantConditionCode ("3")))
          case _ => Invalid(ConditionCodesParameterError("Could not determine whether this is a recognised body or HEI"))
        }
        case PreSessionalCourse | BelowDegreeCourse => recognisedBodyOrHEI match {
          case Some(true) => Valid(Some(ApplicantConditionCode("2A")))
          case Some(false) => Valid(Some(ApplicantConditionCode("3")))
          case _ => Invalid(ConditionCodesParameterError("Could not determine whether this is a recognised body or HEI"))
        }
        case _ => Invalid(ConditionCodesParameterError(s"Course type parameter: $courseType is unrecognised for this calculation"))
      }

    def calculatePartnerConditionCode(): Validated[ConditionCodesParameterError, Option[PartnerConditionCode]] = dependants match {
      case None => Valid(None)
      case Some(noOfDeps) if noOfDeps == 0 => Valid(None)
      case _ =>

        if (courseType == MainCourse) {
          (courseStartDate, courseEndDate) match {
            case (Some(start), Some(end)) =>
              val courseLengthInMonths = CourseLengthCalculator.differenceInMonths(start, end)
              if (courseLengthInMonths >= 12) Valid(Some(PartnerConditionCode("4B")))
              else Valid(Some(PartnerConditionCode("3")))
            case (None, None) => Invalid(ConditionCodesParameterError("Missing course start and end date"))
            case (None, _) => Invalid(ConditionCodesParameterError("Missing course start date"))
            case (_, None) => Invalid(ConditionCodesParameterError("Missing course end date"))
          }
        } else {
          Valid(Some(PartnerConditionCode("3")))
      }

    }

    def calculateChildConditionCode(): Option[ChildConditionCode] = dependants match {
      case None => None
      case Some(noOfDeps) if noOfDeps == 0 => None
      case _ => Some(ChildConditionCode("1"))
    }

    val applicantResult: Validated[ConditionCodesParameterError, Option[ApplicantConditionCode]] = calculateApplicantConditionCode()
    val partnerResult: Validated[ConditionCodesParameterError, Option[PartnerConditionCode]] = calculatePartnerConditionCode()

    (applicantResult, partnerResult) match {
      case (Valid(app), Valid(part)) =>
        Valid(ConditionCodesCalculationResult(
          app,
          part,
          calculateChildConditionCode()
        ))
      case (Invalid(inv), Valid(_)) => Invalid(inv)
      case (Valid(_), Invalid(inv)) => Invalid(inv)
      case (Invalid(inv1), Invalid(inv2)) => Invalid(ConditionCodesParameterError(inv1.message + " ; " + inv2.message))
    }
  }
}

class OtherNonGeneralConditionCodesCalculator(studentType: StudentType) extends ConditionCodesCalculator {

  override def calculateConditionCodes(dependanstOnly: Boolean,
                                       dependants: Option[Int],
                                       courseStartDate: Option[LocalDate],
                                       courseEndDate: Option[LocalDate],
                                       courseType: CourseType,
                                       recognisedBodyOrHEI: Option[Boolean]): Validated[ConditionCodesParameterError, ConditionCodesCalculationResult] = {

    def calculateApplicantConditionCode(): Validated[ConditionCodesParameterError, Option[ApplicantConditionCode]] = studentType match {
      case _ if dependanstOnly => Valid(None)
      case DoctorateExtensionStudent => Valid(Some(ApplicantConditionCode("4E")))
      case PostGraduateDoctorDentistStudent | StudentUnionSabbaticalOfficerStudent => Valid(Some(ApplicantConditionCode("2")))
      case _ => Invalid(ConditionCodesParameterError(s"Student Type: $studentType is not a valid type for this calculation"))
    }

    def calculatePartnerConditionCode(): Option[PartnerConditionCode] = dependants match {
      case None | Some(0) => None
      case _ => Some(PartnerConditionCode("4B"))
    }


    def calculateChildConditionCode(): Option[ChildConditionCode] = dependants match {
      case None | Some(0) => None
      case _ => Some(ChildConditionCode("1"))
    }

    calculateApplicantConditionCode() match {
      case Valid(validated) =>
        Valid(ConditionCodesCalculationResult(
          validated,
          calculatePartnerConditionCode(),
          calculateChildConditionCode()
        ))
      case withProblems@Invalid(_) =>
        withProblems
    }
  }
}

case class ConditionCodesParameterError(message: String)
