package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.Optional

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.domain._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
@ControllerAdvice
class ThresholdService @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculator,
                                    val messageSource: ResourceBundleMessageSource,
                                    val studentTypeChecker: StudentTypeChecker
                                   ) extends FinancialStatusBaseController {

  val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
  val courseLengthPattern = """^[1-9]$""".r

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "studentType") studentType: Optional[String],
                         @RequestParam(value = "inLondon") inLondon: Optional[JBoolean],
                         @RequestParam(value = "courseLength", required = false) courseLength: Optional[Integer],
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: Optional[JBigDecimal],
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer]
                        ): ResponseEntity[ThresholdResponse] = {

    val validatedStudentType = validateStudentType(studentType)

    calculateThresholdForStudentType(validatedStudentType, inLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
  }

  private def calculateThresholdForStudentType(studentType: StudentType,
                                               inLondon: Option[Boolean],
                                               courseLength: Option[Int],
                                               tuitionFees: Option[BigDecimal],
                                               tuitionFeesPaid: Option[BigDecimal],
                                               accommodationFeesPaid: Option[BigDecimal],
                                               dependants: Option[Int]): ResponseEntity[ThresholdResponse] = {
    studentType match {

      case NonDoctorate =>
        val courseMinLength = maintenanceThresholdCalculator.nonDoctorateMinCourseLength
        // val courseMaxLength = maintenanceThresholdCalculator.nonDoctorateMaxCourseLength
        val validatedInputs = validateInputs(NonDoctorate, inLondon, courseLength, tuitionFees, tuitionFeesPaid,
          accommodationFeesPaid, dependants, courseMinLength)

        calculateThreshold(validatedInputs, calculateNonDoctorate)

      case Doctorate =>
        val fixedCourseLength = maintenanceThresholdCalculator.doctorateFixedCourseLength
        val validatedInputs = validateInputs(Doctorate, inLondon, None, None, None,
          accommodationFeesPaid, dependants, fixedCourseLength)

        calculateThreshold(validatedInputs, calculateDoctorate)

      case DoctorDentist | StudentSabbaticalOfficer =>
        val courseMinLength = maintenanceThresholdCalculator.pgddSsoMinCourseLength
        // val courseMaxLength = maintenanceThresholdCalculator.pgddSsoMaxCourseLength
        val validatedInputs = validateInputs(DoctorDentist, inLondon, courseLength, None, None,
          accommodationFeesPaid, dependants, courseMinLength)

        calculateThreshold(validatedInputs, calculateDoctorDentistSabbatical)

      case Unknown(unknownType) =>
        buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_STUDENT_TYPE(studentTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
    }
  }

  private def calculateThreshold(validatedInputs: Either[Vector[(String, String, HttpStatus)], ValidatedInputs], calculate: ValidatedInputs => Option[ThresholdResponse]) = {

    validatedInputs match {
      case Right(inputs) =>
        val thresholdResponse = calculate(inputs)
        thresholdResponse match {
          case Some(response) => new ResponseEntity[ThresholdResponse](response, HttpStatus.OK)
          case None => buildErrorResponse(headers, TEMP_ERROR_CODE, UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        }
      case Left(errorList) =>
        buildErrorResponse(headers, errorList(0)._1, errorList(0)._2, errorList(0)._3)
    }
  }

  private def calculateDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDoctorate(inner, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def calculateDoctorDentistSabbatical(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDesPgddSso(inner, length, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def calculateNonDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         tFees <- inputs.tuitionFees
         tFeesPaid <- inputs.tuitionFeesPaid
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateNonDoctorate(inner, length, tFees, tFeesPaid, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, OK))
    }
  }


  private def validateInputs(studentType: StudentType,
                             inLondon: Option[Boolean],
                             courseLength: Option[Int],
                             tuitionFees: Option[BigDecimal],
                             tuitionFeesPaid: Option[BigDecimal],
                             accommodationFeesPaid: Option[BigDecimal],
                             dependants: Option[Int],
                             courseMinLength: Int): Either[Vector[(String, String, HttpStatus)], ValidatedInputs] = {

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
          errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_TUITION_FEES, HttpStatus.BAD_REQUEST))
        } else if (validTuitionFeesPaid.isEmpty) {
          errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_TUITION_FEES_PAID, HttpStatus.BAD_REQUEST))
        } else if (validCourseLength.isEmpty) {
          errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST))
        }
      case DoctorDentist | StudentSabbaticalOfficer =>
        if (validCourseLength.isEmpty) {
          errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST))
        }
      case Doctorate =>

      case Unknown(unknownStudentType) => errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_STUDENT_TYPE(unknownStudentType), HttpStatus.BAD_REQUEST))
    }

    if (validAccommodationFeesPaid.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST))
    } else if (validDependants.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST))
    } else if (validInLondon.isEmpty) {
      errorList = errorList :+ ((TEMP_ERROR_CODE, INVALID_IN_LONDON, HttpStatus.BAD_REQUEST))
    }

    if (errorList.isEmpty) Right(ValidatedInputs(validDependants, validCourseLength, validTuitionFees, validTuitionFeesPaid, validAccommodationFeesPaid, validInLondon))
    else Left(errorList)
  }

  private def validateDependants(dependants: Option[Int]) = dependants.filter(_ >= 0)

  private def validateCourseLength(courseLength: Option[Int], min: Int) = courseLength.filter(length => min <= length)

  private def validateTuitionFees(tuitionFees: Option[BigDecimal]) = tuitionFees.filter(_ >= 0)

  private def validateTuitionFeesPaid(tuitionFeesPaid: Option[BigDecimal]) = tuitionFeesPaid.filter(_ >= 0)

  private def validateAccommodationFeesPaid(accommodationFeesPaid: Option[BigDecimal]) = accommodationFeesPaid.filter(_ >= 0)

  private def validateStudentType(studentType: Option[String]): StudentType = studentTypeChecker.getStudentType(studentType.getOrElse("Unknown"))

  private def validateInnerLondon(inLondon: Option[Boolean]) = inLondon

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)

  case class ValidatedInputs(dependants: Option[Int], courseLength: Option[Int], tuitionFees: Option[BigDecimal],
                             tuitionFeesPaid: Option[BigDecimal], accommodationFeesPaid: Option[BigDecimal], inLondon: Option[Boolean])

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
