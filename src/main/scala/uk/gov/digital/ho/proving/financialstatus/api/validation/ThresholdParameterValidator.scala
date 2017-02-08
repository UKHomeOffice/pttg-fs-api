package uk.gov.digital.ho.proving.financialstatus.api.validation

import java.time.LocalDate

import org.springframework.http.HttpStatus
import uk.gov.digital.ho.proving.financialstatus.domain._

trait ThresholdParameterValidator {

  val serviceMessages: ServiceMessages

  protected def validateInputs(studentType: StudentType,
                               inLondon: Option[Boolean],
                               tuitionFees: Option[BigDecimal],
                               tuitionFeesPaid: Option[BigDecimal],
                               accommodationFeesPaid: Option[BigDecimal],
                               dependants: Option[Int],
                               courseStartDate: Option[LocalDate],
                               courseEndDate: Option[LocalDate],
                               originalCourseStartDate: Option[LocalDate],
                               courseType: CourseType,
                               dependantsOnly: Option[Boolean]
                              ): Either[Seq[(String, String, HttpStatus)], ValidatedInputs] = {

    var errorList = Vector.empty[(String, String, HttpStatus)]
    val (validCourseStartDate, validCourseEndDate, validOriginalCourseStartDate) = validateDates(courseStartDate, courseEndDate, originalCourseStartDate)
    val isContinuation = validOriginalCourseStartDate && originalCourseStartDate.isDefined
    val validDependants = validateDependants(dependants)
    val validTuitionFees = validateTuitionFees(tuitionFees)
    val validTuitionFeesPaid = validateTuitionFeesPaid(tuitionFeesPaid)
    val validAccommodationFeesPaid = validateAccommodationFeesPaid(accommodationFeesPaid)
    val validInLondon = validateInnerLondon(inLondon)
    val validDependantsOnly = validateDependantsOnly(dependantsOnly)

    studentType match {

      case GeneralStudent =>
        if (courseStartDate.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_START_DATE, HttpStatus.BAD_REQUEST))
        } else if (courseEndDate.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_END_DATE, HttpStatus.BAD_REQUEST))
        } else if (!validCourseStartDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_START_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (!validCourseEndDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_END_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (!validOriginalCourseStartDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ORIGINAL_COURSE_START_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (validTuitionFees.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_TUITION_FEES, HttpStatus.BAD_REQUEST))
        } else if (validTuitionFeesPaid.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_TUITION_FEES_PAID, HttpStatus.BAD_REQUEST))
        } else if (validDependants.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST))
        }
      case StudentUnionSabbaticalOfficerStudent | PostGraduateDoctorDentistStudent =>
        if (courseStartDate.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_START_DATE, HttpStatus.BAD_REQUEST))
        } else if (courseEndDate.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_END_DATE, HttpStatus.BAD_REQUEST))
        } else if (!validCourseStartDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_START_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (!validCourseEndDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_END_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (!validOriginalCourseStartDate) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ORIGINAL_COURSE_START_DATE_VALUE, HttpStatus.BAD_REQUEST))
        } else if (validDependants.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST))
        }
      case DoctorateExtensionStudent =>
      case UnknownStudent(unknownStudentType) => errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_STUDENT_TYPE(unknownStudentType), HttpStatus.BAD_REQUEST))
    }

    if (validAccommodationFeesPaid.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST))
    } else if (validDependants.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST))
    } else if (validInLondon.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_IN_LONDON, HttpStatus.BAD_REQUEST))
    } else if (validDependantsOnly.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_IN_DEPENDANTS_ONLY, HttpStatus.BAD_REQUEST))
    }

    if (errorList.isEmpty) Right(ValidatedInputs(validDependants, validTuitionFees, validTuitionFeesPaid,
      validAccommodationFeesPaid, validInLondon, courseStartDate, courseEndDate, originalCourseStartDate, isContinuation, courseType == PreSessionalCourse, validDependantsOnly))
    else Left(errorList)
  }

  private def validateDependants(dependants: Option[Int]) = dependants.filter(_ >= 0)

  private def validateTuitionFees(tuitionFees: Option[BigDecimal]) = tuitionFees.filter(_ >= 0)

  private def validateTuitionFeesPaid(tuitionFeesPaid: Option[BigDecimal]) = tuitionFeesPaid.filter(_ >= 0)

  private def validateAccommodationFeesPaid(accommodationFeesPaid: Option[BigDecimal]) = accommodationFeesPaid.filter(_ >= 0)

  private def validateInnerLondon(inLondon: Option[Boolean]) = inLondon

  private def validateDependantsOnly(dependantsOnly: Option[Boolean]) = dependantsOnly

  private def validateDates(courseStartDate: Option[LocalDate], courseEndDate: Option[LocalDate], originalCourseStartDate: Option[LocalDate]): (Boolean, Boolean, Boolean) = {

    val validation = for {
      startDate <- courseStartDate
      endDate <- courseEndDate
    } yield {
      val (startOK, endOK) = startDate.isBefore(endDate) match {
        case true => (true, true)
        case false => (false, false)
      }
      val originalStartOk = originalCourseStartDate match {
        case None => true
        case Some(date) => date.isBefore(startDate) && date.isBefore(endDate)
      }
      (startOK, endOK, originalStartOk)
    }
    validation.getOrElse((false, false, false))
  }

  case class ValidatedInputs(dependants: Option[Int], tuitionFees: Option[BigDecimal],
                             tuitionFeesPaid: Option[BigDecimal], accommodationFeesPaid: Option[BigDecimal],
                             inLondon: Option[Boolean],
                             courseStartDate: Option[LocalDate],
                             courseEndDate: Option[LocalDate],
                             originalCourseStartDate: Option[LocalDate],
                             isContinuation: Boolean,
                             isPreSessional: Boolean,
                             dependantsOnly: Option[Boolean])

}
