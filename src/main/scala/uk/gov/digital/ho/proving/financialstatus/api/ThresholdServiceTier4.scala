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
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain._


@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatus/v1/t4/maintenance"))
@ControllerAdvice
class ThresholdServiceTier4 @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculator,
                                         val studentTypeChecker: StudentTypeChecker,
                                         val courseTypeChecker: CourseTypeChecker,
                                         val serviceMessages: ServiceMessages,
                                         val auditor: ApplicationEventPublisher,
                                         val authenticator: Authentication
                                   ) extends FinancialStatusBaseController with ThresholdParameterValidator {

  private val LOGGER = LoggerFactory.getLogger(classOf[ThresholdServiceTier4])

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "studentType") studentType: Optional[String],
                         @RequestParam(value = "inLondon") inLondon: Optional[JBoolean],
                         @RequestParam(value = "courseStartDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) courseStartDate: Optional[LocalDate],
                         @RequestParam(value = "courseEndDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) courseEndDate: Optional[LocalDate],
                         @RequestParam(value = "originalCourseStartDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) originalCourseStartDate: Optional[LocalDate],
                         @RequestParam(value = "tuitionFees", required = false) tuitionFees: Optional[JBigDecimal],
                         @RequestParam(value = "tuitionFeesPaid", required = false) tuitionFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "accommodationFeesPaid") accommodationFeesPaid: Optional[JBigDecimal],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer],
                         @RequestParam(value = "courseType", required = false) courseType: Optional[String],
                         @CookieValue(value = "kc-access") kcToken: Optional[String]
                        ): ResponseEntity[ThresholdResponse] = {

    val accessToken: Option[String] = kcToken

    // Get the user's profile
    val userProfile = accessToken match {
      case Some(token) => authenticator.getUserProfileFromToken(token)
      case None => None
    }

    val auditEventId = nextId
    auditSearchParams(auditEventId, studentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate,
      tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, userProfile, courseType)

    val validatedStudentType = studentTypeChecker.getStudentType(studentType.getOrElse("Unknown"))
    val validatedCourseType = courseTypeChecker.getCourseType(courseType.getOrElse("Unknown"))

    def threshold = calculateThresholdForStudentType(validatedStudentType, inLondon, courseStartDate, courseEndDate, originalCourseStartDate,
      tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, validatedCourseType)

    auditSearchResult(auditEventId, threshold.getBody, userProfile)

    threshold
  }

  def auditSearchParams(auditEventId: UUID, studentType: Option[String], inLondon: Option[Boolean],
                        courseStartDate: Optional[LocalDate], courseEndDate: Optional[LocalDate], originalCourseStartDate: Optional[LocalDate],
                        tuitionFees: Option[BigDecimal], tuitionFeesPaid: Option[BigDecimal],
                        accommodationFeesPaid: Option[BigDecimal], dependants: Option[Int], userProfile: Option[UserProfile], courseType: Optional[String]): Unit = {

    val params = Map(
      "studentType" -> studentType,
      "inLondon" -> inLondon,
      "courseStartDate" -> courseStartDate,
      "courseEndDate" -> courseEndDate,
      "originalCourseStartDate" -> originalCourseStartDate,
      "tuitionFees" -> tuitionFees,
      "tuitionFeesPaid" -> tuitionFeesPaid,
      "accommodationFeesPaid" -> accommodationFeesPaid,
      "dependants" -> dependants,
      "courseType" -> courseType
    )

    val suppliedParams = for ((k, Some(v)) <- params) yield k -> v

    val auditData = Map("method" -> "calculate-threshold") ++ suppliedParams

    val principal = userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }
    auditor.publishEvent(auditEvent(principal, SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, thresholdResponse: ThresholdResponse, userProfile: Option[UserProfile]): Unit = {
    auditor.publishEvent(auditEvent(userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }, SEARCH_RESULT, auditEventId,
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
                                               originalCourseStartDate: Optional[LocalDate],
                                               tuitionFees: Option[BigDecimal],
                                               tuitionFeesPaid: Option[BigDecimal],
                                               accommodationFeesPaid: Option[BigDecimal],
                                               dependants: Option[Int],
                                               courseType: CourseType
                                              ): ResponseEntity[ThresholdResponse] = {

    studentType match {

      case NonDoctorateStudent =>

        courseType match {
          case UnknownCourse(course) => buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_TYPE(courseTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
          case _ => val validatedInputs = validateInputs(NonDoctorateStudent, inLondon, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType)
            calculateThreshold(validatedInputs, calculateNonDoctorate)
        }

      case DoctorateStudent =>
        val validatedInputs = validateInputs(DoctorateStudent, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType)
        calculateThreshold(validatedInputs, calculateDoctorate)

      case DoctorDentistStudent  =>
        val validatedInputs = validateInputs(DoctorDentistStudent, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType)
        calculateThreshold(validatedInputs, calculatePGDD)

      case StudentSabbaticalOfficer =>
        val validatedInputs = validateInputs(StudentSabbaticalOfficer, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType)
        calculateThreshold(validatedInputs, calculateSUSO)

      case UnknownStudent(unknownType) =>
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
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateDES(inner, aFeesPaid, deps)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculatePGDD(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculatePGGD(inner, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, inputs.isContinuation)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateSUSO(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateSUSO(inner, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, inputs.isContinuation)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateNonDoctorate(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         tFees <- inputs.tuitionFees
         tFeesPaid <- inputs.tuitionFeesPaid
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateNonDoctorate(inner, tFees, tFeesPaid, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, inputs.isContinuation, inputs.isPreSessional)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
