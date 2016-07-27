package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.lang.{Boolean => JBoolean}
import java.util.Optional

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
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

  val BIG_DECIMAL_SCALE = 2
  val courseLengthPattern = """^[0-9]$""".r
  val TEMP_ERROR_CODE: String = "0000"
  val headers = new HttpHeaders()

  val INVALID_TUITION_FEES = getMessage("invalid.tuition.fees")
  val INVALID_TUITION_FEES_PAID = getMessage("invalid.tuition.fees.paid")
  val INVALID_ACCOMMODATION_FEES_PAID = getMessage("invalid.accommodation.fees.paid")
  val INVALID_DEPENDANTS = getMessage("invalid.dependants.value")
  val INVALID_IN_LONDON = getMessage("invalid.in.london.value")

  def INVALID_COURSE_LENGTH(params: Int*) = getMessage("invalid.course.length", params)

  def INVALID_STUDENT_TYPE(params: String*) = getMessage("invalid.student.type", params)

  val OK = "OK"

  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

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

  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] = {
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateDependants(dependants: Option[Int]): Boolean = {
    dependants.exists(_ >= 0)
  }

  def validateCourseLength(courseLength: Option[Int], min: Int, max: Int): Boolean = {
    courseLength.exists(length => min <= length && length <= max)
  }

  def validateTuitionFees(tuitionFees: Option[JBigDecimal]): Boolean = {
    tuitionFees.exists(fees => fees.compareTo(JBigDecimal.ZERO) > -1)
  }

  def validateTuitionFeesPaid(tuitionFeesPaid: Option[JBigDecimal]): Boolean = {
    tuitionFeesPaid.exists(feesPaid => feesPaid.compareTo(JBigDecimal.ZERO) > -1)
  }

  def validateAccommodationFeesPaid(accommodationFeesPaid: Option[JBigDecimal]): Boolean = {
    accommodationFeesPaid.exists(feesPaid => feesPaid.compareTo(JBigDecimal.ZERO) > -1 &&
      feesPaid.compareTo(new JBigDecimal(maintenanceThresholdCalculator.maxAccommodation)) < 1)
  }

  def validateStudentType(studentType: Option[String]): StudentType = {
    studentTypeChecker.getStudentType(studentType.getOrElse(""))
  }

  def validateInnerLondon(inLondon: Option[Boolean]): Boolean = {
    inLondon.isDefined
  }

  def setScale(value: Option[JBigDecimal]): Option[JBigDecimal] = value.map(v => v.setScale(BIG_DECIMAL_SCALE, JBigDecimal.ROUND_HALF_UP))

  def calculateThresholdForStudentType(studentType: StudentType,
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
        if (!validateCourseLength(courseLength, courseMinLength, courseMaxLength)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH(courseMinLength, courseMaxLength), HttpStatus.BAD_REQUEST)
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
          new ResponseEntity[ThresholdResponse](thresholdResponse.get, HttpStatus.OK)
        }

      case Doctorate | DoctorDentist =>
        val courseMinLength = maintenanceThresholdCalculator.doctorateMinCourseLength
        val courseMaxLength = maintenanceThresholdCalculator.doctorateMaxCourseLength
        if (!validateCourseLength(courseLength, courseMinLength, courseMaxLength)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_COURSE_LENGTH(courseMinLength, courseMaxLength), HttpStatus.BAD_REQUEST)
        } else if (!validateAccommodationFeesPaid(setScale(accommodationFeesPaid))) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOMMODATION_FEES_PAID, HttpStatus.BAD_REQUEST)
        } else if (!validateDependants(dependants)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)
        } else if (!validateInnerLondon(innerLondon)) {
          buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_IN_LONDON, HttpStatus.BAD_REQUEST)
        } else {
          val thresholdResponse = calculateDoctorate(innerLondon, courseLength, accommodationFeesPaid, dependants)
          new ResponseEntity[ThresholdResponse](thresholdResponse.get, HttpStatus.OK)
        }
      case Unknown(unknownType) =>
        buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_STUDENT_TYPE(studentTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
    }
  }

  def calculateDoctorate(innerLondon: Option[Boolean], courseLength: Option[Int],
                         accommodationFeesPaid: Option[JBigDecimal], dependants: Option[Int]): Option[ThresholdResponse] = {

    for {inner <- innerLondon
         length <- courseLength
         aFeesPaid <- accommodationFeesPaid
         deps <- dependants
    } yield {
      new ThresholdResponse(
        maintenanceThresholdCalculator.calculateDoctorate(inner, length, aFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP), deps),
        StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  def calculateNonDoctorate(innerLondon: Option[Boolean], courseLength: Option[Int],
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
        maintenanceThresholdCalculator.calculateNonDoctorate(inner, length,
          tFees,
          tFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP),
          aFeesPaid.setScale(BIG_DECIMAL_SCALE, BigDecimal.RoundingMode.HALF_UP), deps),
        StatusResponse(HttpStatus.OK.toString, OK))
    }
  }

  override def logStartupInformation(): Unit = {
    LOGGER.info(maintenanceThresholdCalculator.parameters)
  }

}
