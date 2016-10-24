package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.util.{Optional, UUID}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.PropertySource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.api.validation.{ServiceMessages, ThresholdParameterValidator}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions.{auditEvent, nextId}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventType._
import uk.gov.digital.ho.proving.financialstatus.domain._


@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/maintenance"))
@ControllerAdvice
class ThresholdService @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculator,
                                    val studentTypeChecker: StudentTypeChecker,
                                    val serviceMessages: ServiceMessages,
                                    val auditor: ApplicationEventPublisher,
                                    @Value("${non.doctorate.leave.to.remain.boundary}") val nonDoctorateLeaveToRemainBoundary: Int,
                                    @Value("${non.doctorate.short.leave.to.remain}") val nonDoctorateShortLeaveToRemain: Int,
                                    @Value("${non.doctorate.long.leave.to.remain}") val nonDoctorateLongLeaveToRemain: Int
                                   ) extends FinancialStatusBaseController with ThresholdParameterValidator {

  val LOGGER = LoggerFactory.getLogger(classOf[ThresholdService])
  val courseLengthPattern = """^[1-9]$""".r

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "studentType") studentType: Optional[String],
                         @RequestParam(value = "inLondon") inLondon: Optional[JBoolean],
                         @RequestParam(value = "courseStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) courseStartDate: Optional[LocalDate],
                         @RequestParam(value = "courseEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) courseEndDate: Optional[LocalDate],
                         @RequestParam(value = "courseExtensionEndDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) courseExtentionEndDate: Optional[LocalDate],
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: Optional[JBigDecimal],
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer],
                         @CookieValue(value = "kc-access", defaultValue = "") accessToken: String
                        ): ResponseEntity[ThresholdResponse] = {

    val auditEventId = nextId
    auditSearchParams(auditEventId, studentType, inLondon, courseStartDate, courseEndDate, courseExtentionEndDate,
      tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)

    val validatedStudentType = studentTypeChecker.getStudentType(studentType.getOrElse("Unknown"))
    def threshold = calculateThresholdForStudentType(validatedStudentType, inLondon, courseStartDate, courseEndDate, courseExtentionEndDate,
      tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants)

    auditSearchResult(auditEventId, threshold.getBody)

    threshold
  }

  def auditSearchParams(auditEventId: UUID, studentType: Option[String], inLondon: Option[Boolean],
                        courseStartDate: Optional[LocalDate], courseEndDate: Optional[LocalDate], courseExtentionEndDate: Optional[LocalDate],
                        tuitionFees: Option[BigDecimal], tuitionFeesPaid: Option[BigDecimal],
                        accommodationFeesPaid: Option[BigDecimal], dependants: Option[Int]): Unit = {

    val params = Map(
      "studentType" -> studentType,
      "inLondon" -> inLondon,
      "courseStartDate" -> courseStartDate,
      "courseEndDate" -> courseEndDate,
      "courseExtentionEndDate" -> courseExtentionEndDate,
      "tuitionFees" -> tuitionFees,
      "tuitionFeesPaid" -> tuitionFeesPaid,
      "accommodationFeesPaid" -> accommodationFeesPaid,
      "dependants" -> dependants
    )

    val suppliedParams = for ((k, Some(v)) <- params) yield k -> v

    val auditData = Map("method" -> "calculate-threshold") ++ suppliedParams

    auditor.publishEvent(auditEvent(SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, thresholdResponse: ThresholdResponse): Unit = {
    auditor.publishEvent(auditEvent(SEARCH_RESULT, auditEventId,
      Map(
        "method" -> "calculate-threshold",
        "result" -> thresholdResponse
      )
    ))
  }

  private def calculateThresholdForStudentType(studentType: StudentType,
                                               inLondon: Option[Boolean],
                                               courseStartDate: Option[LocalDate],
                                               courseEndDate: Option[LocalDate],
                                               courseExtensionEndDate: Optional[LocalDate],
                                               tuitionFees: Option[BigDecimal],
                                               tuitionFeesPaid: Option[BigDecimal],
                                               accommodationFeesPaid: Option[BigDecimal],
                                               dependants: Option[Int]): ResponseEntity[ThresholdResponse] = {

    val minCourseLengthWithDependants = maintenanceThresholdCalculator.minNonDoctorateCourseLengthWithDependants

    val courseLength = CourseLengthCalculator.calculateCourseLength(courseStartDate, courseEndDate, courseExtensionEndDate)

    studentType match {

      case NonDoctorate =>

        val leaveToRemain = LeaveToRemainCalculator.calculateLeaveToRemain(courseStartDate, courseEndDate, courseExtensionEndDate,
          nonDoctorateLeaveToRemainBoundary, nonDoctorateShortLeaveToRemain, nonDoctorateLongLeaveToRemain)

        val courseMinLength = maintenanceThresholdCalculator.nonDoctorateMinCourseLength

        val validatedInputs = validateInputs(NonDoctorate, inLondon, courseLength, tuitionFees, tuitionFeesPaid,
          accommodationFeesPaid, dependants, courseMinLength, minCourseLengthWithDependants, leaveToRemain)

        calculateThreshold(validatedInputs, calculateNonDoctorate)

      case Doctorate =>
        val fixedCourseLength = maintenanceThresholdCalculator.doctorateFixedCourseLength
        val validatedInputs = validateInputs(Doctorate, inLondon, None, None, None,
          accommodationFeesPaid, dependants, fixedCourseLength, minCourseLengthWithDependants, None)

        calculateThreshold(validatedInputs, calculateDoctorate)

      case DoctorDentist | StudentSabbaticalOfficer =>
        val courseMinLength = maintenanceThresholdCalculator.pgddSsoMinCourseLength
        val validatedInputs = validateInputs(DoctorDentist, inLondon, courseLength, None, None,
          accommodationFeesPaid, dependants, courseMinLength, minCourseLengthWithDependants, None)

        calculateThreshold(validatedInputs, calculateDoctorDentistSabbatical)

      case Unknown(unknownType) =>
        buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_STUDENT_TYPE(studentTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
    }
  }

  private def calculateThreshold(validatedInputs: Either[Seq[(String, String, HttpStatus)], ValidatedInputs], calculate: ValidatedInputs => Option[ThresholdResponse]) = {

    validatedInputs match {
      case Right(inputs) =>
        val thresholdResponse = calculate(inputs)
        thresholdResponse match {
          case Some(response) => new ResponseEntity[ThresholdResponse](response, HttpStatus.OK)
          case None => buildErrorResponse(headers, serviceMessages.REST_INTERNAL_ERROR, serviceMessages.UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        }
      case Left(errorList) =>
        // We should be returning all the error messages and not just the first
        buildErrorResponse(headers, errorList.head._1, errorList.head._2, errorList.head._3)
    }
  }

  private def calculateDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDoctorate(inner, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateDoctorDentistSabbatical(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateDesPgddSso(inner, length, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateNonDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         length <- inputs.courseLength
         tFees <- inputs.tuitionFees
         tFeesPaid <- inputs.tuitionFeesPaid
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         leaveToRemain <- inputs.leaveToRemain
    } yield {
      val (threshold, cappedValues) = maintenanceThresholdCalculator.calculateNonDoctorate(inner, length, tFees, tFeesPaid, aFeesPaid, deps, leaveToRemain)
      new ThresholdResponse(Some(threshold), cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
