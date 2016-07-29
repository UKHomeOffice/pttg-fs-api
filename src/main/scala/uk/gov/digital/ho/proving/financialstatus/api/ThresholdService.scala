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
                         @RequestParam(value = "courseLength", defaultValue = "0") courseLength: Optional[Integer],
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: Optional[JBigDecimal],
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer]
                        ): ResponseEntity[ThresholdResponse] = {

    val validatedStudentType = validateStudentType(studentType)

    timer("calculateThresholdForStudentType") {
      val auditMessage = s"calculateThreshold: validatedStudentType = $validatedStudentType, innerLondon = $inLondon, " +
        s"courseLength = $courseLength, tuitionFees = $tuitionFees, tuitionFeesPaid = $tuitionFeesPaid, " +
        s"accommodationFeesPaid = $accommodationFeesPaid, dependants = $dependants"
      audit(auditMessage) {
        calculateThresholdForStudentType(validatedStudentType, inLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
      }
    }
  }

  private def calculateThresholdForStudentType(studentType: StudentType,
                                               innerLondon: Option[Boolean],
                                               courseLength: Option[Int],
                                               tuitionFees: Option[JBigDecimal],
                                               tuitionFeesPaid: Option[JBigDecimal],
                                               accommodationFeesPaid: Option[JBigDecimal],
                                               dependants: Option[Int]): ResponseEntity[ThresholdResponse] = {

    studentType match {

      case NonDoctorate =>
        val courseMinLength = maintenanceThresholdCalculator.nonDoctorateMinCourseLength
        val courseMaxLength = maintenanceThresholdCalculator.nonDoctorateMaxCourseLength

        validateAndCalculateNonDoctorate(innerLondon, courseLength, tuitionFees, tuitionFeesPaid,
          accommodationFeesPaid, dependants, courseMinLength, courseMaxLength)

      case Doctorate | DoctorDentist =>
        val courseMinLength = maintenanceThresholdCalculator.doctorateMinCourseLength
        val courseMaxLength = maintenanceThresholdCalculator.doctorateMaxCourseLength

        validateAndCalculateDoctorDentist(innerLondon, courseLength, accommodationFeesPaid, dependants, courseMinLength, courseMaxLength)

      case Unknown(unknownType) =>
        buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_STUDENT_TYPE(studentTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
    }
  }

  private def calculateDesDoctorDentist(innerLondon: Option[Boolean], courseLength: Option[Int],
                                        accommodationFeesPaid: Option[JBigDecimal], dependants: Option[Int]): Option[ThresholdResponse] = {

    for {inner <- innerLondon
         length <- courseLength
         aFeesPaid <- accommodationFeesPaid
         deps <- dependants
    } yield {
      new ThresholdResponse(
        Some(maintenanceThresholdCalculator.calculateDoctorate(inner, length, aFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP), deps)),
        StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def calculateNonDoctorate(innerLondon: Option[Boolean], courseLength: Option[Int],
                                    tuitionFees: Option[JBigDecimal], tuitionFeesPaid: Option[JBigDecimal],
                                    accommodationFeesPaid: Option[JBigDecimal], dependants: Option[Int]): Option[ThresholdResponse] = {

    for {inner <- innerLondon
         length <- courseLength
         tFees <- tuitionFees
         tFeesPaid <- tuitionFeesPaid
         aFeesPaid <- accommodationFeesPaid
         deps <- dependants
    } yield {
      new ThresholdResponse(
        Some(maintenanceThresholdCalculator.calculateNonDoctorate(inner, length,
          tFees,
          tFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP),
          aFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP), deps)),
        StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  private def validateAndCalculateDoctorDentist(innerLondon: Option[Boolean], courseLength: Option[Int], accommodationFeesPaid: Option[JBigDecimal],
                                                dependants: Option[Int], courseMinLength: Int, courseMaxLength: Int): ResponseEntity[ThresholdResponse] = {
    if (!validateCourseLength(courseLength, courseMinLength)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST)
    } else if (!validateAccommodationFeesPaid(setScale(accommodationFeesPaid))) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST)
    } else if (!validateDependants(dependants)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)
    } else if (!validateInnerLondon(innerLondon)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_IN_LONDON, HttpStatus.BAD_REQUEST)
    } else {
      val thresholdResponse = calculateDesDoctorDentist(innerLondon, courseLength, accommodationFeesPaid, dependants)
      thresholdResponse match {
        case Some(response) => new ResponseEntity[ThresholdResponse](response, HttpStatus.OK)
        case None => buildErrorResponse(headers, TEMP_ERROR_CODE, UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
      }
    }
  }

  private def validateAndCalculateNonDoctorate(innerLondon: Option[Boolean], courseLength: Option[Int], tuitionFees: Option[JBigDecimal],
                                               tuitionFeesPaid: Option[JBigDecimal], accommodationFeesPaid: Option[JBigDecimal], dependants: Option[Int],
                                               courseMinLength: Int, courseMaxLength: Int): ResponseEntity[ThresholdResponse] = {
    if (!validateCourseLength(courseLength, courseMinLength)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH, HttpStatus.BAD_REQUEST)
    } else if (!validateTuitionFees(setScale(tuitionFees))) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_TUITION_FEES, HttpStatus.BAD_REQUEST)
    } else if (!validateTuitionFeesPaid(setScale(tuitionFeesPaid))) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_TUITION_FEES_PAID, HttpStatus.BAD_REQUEST)
    } else if (!validateAccommodationFeesPaid(setScale(accommodationFeesPaid))) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST)
    } else if (!validateDependants(dependants)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)
    } else if (!validateInnerLondon(innerLondon)) {
      buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_IN_LONDON, HttpStatus.BAD_REQUEST)
    } else {
      val thresholdResponse = calculateNonDoctorate(innerLondon, courseLength, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)
      thresholdResponse match {
        case Some(response) => new ResponseEntity[ThresholdResponse](response, HttpStatus.OK)
        case None => buildErrorResponse(headers, TEMP_ERROR_CODE, UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
      }
    }
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)


  private def validateDependants(dependants: Option[Int]): Boolean = dependants.exists(_ >= 0)

  private def validateCourseLength(courseLength: Option[Int], min: Int): Boolean = courseLength.exists(length => min <= length)

  private def validateTuitionFees(tuitionFees: Option[JBigDecimal]): Boolean = tuitionFees.exists(fees => fees.compareTo(JBigDecimal.ZERO) > -1)

  private def validateTuitionFeesPaid(tuitionFeesPaid: Option[JBigDecimal]): Boolean = tuitionFeesPaid.exists(feesPaid => feesPaid.compareTo(JBigDecimal.ZERO) > -1)

  private def validateAccommodationFeesPaid(accommFeesPaid: Option[JBigDecimal]): Boolean = accommFeesPaid.exists(feesPaid => feesPaid.compareTo(JBigDecimal.ZERO) > -1)

  private def validateStudentType(studentType: Option[String]): StudentType = studentTypeChecker.getStudentType(studentType.getOrElse(""))

  private def validateInnerLondon(inLondon: Option[Boolean]): Boolean = inLondon.isDefined

  private def setScale(value: Option[JBigDecimal]): Option[JBigDecimal] = value.map(v => v.setScale(BIG_DECIMAL_SCALE, JBigDecimal.ROUND_HALF_UP))

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)


}
