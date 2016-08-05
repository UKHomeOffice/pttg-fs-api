package uk.gov.digital.ho.proving.financialstatus.api.validation

import org.springframework.http.HttpStatus
import uk.gov.digital.ho.proving.financialstatus.domain._

trait ThresholdParameterValidator {

  val serviceMessages: ServiceMessages

  protected def validateInputs(studentType: StudentType,
                               inLondon: Option[Boolean],
                               courseLength: Option[Int],
                               tuitionFees: Option[BigDecimal],
                               tuitionFeesPaid: Option[BigDecimal],
                               accommodationFeesPaid: Option[BigDecimal],
                               dependants: Option[Int],
                               courseMinLength: Int): Either[Seq[(String, String, HttpStatus)], ValidatedInputs] = {

    var errorList = Vector.empty[(String, String, HttpStatus)]
    val validDependants = validateDependants(dependants)
    val validCourseLength = validateCourseLength(courseLength, courseMinLength)
    val validTuitionFees = validateTuitionFees(tuitionFees)
    val validTuitionFeesPaid = validateTuitionFeesPaid(tuitionFeesPaid)
    val validAccommodationFeesPaid = validateAccommodationFeesPaid(accommodationFeesPaid)
    val validInLondon = validateInnerLondon(inLondon)

    studentType match {

      case NonDoctorate =>
        if (validTuitionFees.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_TUITION_FEES, HttpStatus.BAD_REQUEST))
        } else if (validTuitionFeesPaid.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_TUITION_FEES_PAID, HttpStatus.BAD_REQUEST))
        } else if (validCourseLength.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST))
        }
      case DoctorDentist | StudentSabbaticalOfficer =>
        if (validCourseLength.isEmpty) {
          errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST))
        }
      case Doctorate =>
      case Unknown(unknownStudentType) => errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_STUDENT_TYPE(unknownStudentType), HttpStatus.BAD_REQUEST))
    }

    if (validAccommodationFeesPaid.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST))
    } else if (validDependants.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST))
    } else if (validInLondon.isEmpty) {
      errorList = errorList :+ ((serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_IN_LONDON, HttpStatus.BAD_REQUEST))
    }

    if (errorList.isEmpty) Right(ValidatedInputs(validDependants, validCourseLength, validTuitionFees, validTuitionFeesPaid, validAccommodationFeesPaid, validInLondon))
    else Left(errorList)
  }

  private def validateDependants(dependants: Option[Int]) = dependants.filter(_ >= 0)
  private def validateCourseLength(courseLength: Option[Int], min: Int) = courseLength.filter(length => min <= length)
  private def validateTuitionFees(tuitionFees: Option[BigDecimal]) = tuitionFees.filter(_ >= 0)
  private def validateTuitionFeesPaid(tuitionFeesPaid: Option[BigDecimal]) = tuitionFeesPaid.filter(_ >= 0)
  private def validateAccommodationFeesPaid(accommodationFeesPaid: Option[BigDecimal]) = accommodationFeesPaid.filter(_ >= 0)
  private def validateInnerLondon(inLondon: Option[Boolean]) = inLondon

  case class ValidatedInputs(dependants: Option[Int], courseLength: Option[Int], tuitionFees: Option[BigDecimal],
                             tuitionFeesPaid: Option[BigDecimal], accommodationFeesPaid: Option[BigDecimal], inLondon: Option[Boolean])

}
