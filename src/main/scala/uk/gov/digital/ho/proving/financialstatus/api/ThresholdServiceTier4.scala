package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.util.{Optional, UUID}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.PropertySource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.api.validation.{ServiceMessages, ThresholdParameterValidator}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions.{auditEvent, nextId}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventType._
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain._


@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatus/v1/t4/maintenance"))
@ControllerAdvice
class ThresholdServiceTier4 @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculatorT4,
                                         val studentTypeChecker: StudentTypeChecker,
                                         val courseTypeChecker: CourseTypeChecker,
                                         val serviceMessages: ServiceMessages,
                                         val auditor: AuditEventPublisher,
                                         val authenticator: Authentication,
                                         val deploymentConfig: DeploymentDetails
                                   ) extends FinancialStatusBaseController with ThresholdParameterValidator {

  private val LOGGER = LoggerFactory.getLogger(classOf[ThresholdServiceTier4])

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
                         @RequestParam(value = "dependantsOnly") dependantsOnly: Optional[JBoolean],

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
      tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, validatedCourseType, dependantsOnly)

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
    auditor.publishEvent(auditEvent(deploymentConfig, principal, SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, thresholdResponse: ThresholdResponse, userProfile: Option[UserProfile]): Unit = {
    auditor.publishEvent(auditEvent(deploymentConfig, userProfile match {
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
                                               courseType: CourseType,
                                               dependantsOnly: Option[Boolean]
                                              ): ResponseEntity[ThresholdResponse] = {

    studentType match {

      case GeneralStudent =>

        courseType match {
          case UnknownCourse(course) => buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_COURSE_TYPE(courseTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
          case _ => val validatedInputs = validateInputs(GeneralStudent, inLondon, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType, dependantsOnly)
            calculateThreshold(validatedInputs, calculateGeneral)
        }

      case DoctorateExtensionStudent =>
        val validatedInputs = validateInputs(DoctorateExtensionStudent, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType, dependantsOnly)
        calculateThreshold(validatedInputs, calculateDoctorateExtension)

      case PostGraduateDoctorDentistStudent =>
        val validatedInputs = validateInputs(PostGraduateDoctorDentistStudent, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType, dependantsOnly)
        calculateThreshold(validatedInputs, calculatePGDD)

      case StudentUnionSabbaticalOfficerStudent =>
        val validatedInputs = validateInputs(StudentUnionSabbaticalOfficerStudent, inLondon, None, None, accommodationFeesPaid, dependants, courseStartDate, courseEndDate, originalCourseStartDate, courseType, dependantsOnly)
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

  private def calculateDoctorateExtension(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         dependantsOnly <- inputs.dependantsOnly
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateDoctorateExtensionScheme(inner, aFeesPaid, deps, dependantsOnly)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculatePGDD(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         dependantsOnly <- inputs.dependantsOnly
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculatePostGraduateDoctorDentist(inner, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, dependantsOnly)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateSUSO(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         dependantsOnly <- inputs.dependantsOnly
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateStudentUnionSabbaticalOfficer(inner, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, dependantsOnly)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def calculateGeneral(inputs: ValidatedInputs): Option[ThresholdResponse] = {
    for {inner <- inputs.inLondon
         dependantsOnly <- inputs.dependantsOnly
         tFees <- inputs.tuitionFees
         tFeesPaid <- inputs.tuitionFeesPaid
         aFeesPaid <- inputs.accommodationFeesPaid
         deps <- inputs.dependants
         startDate <- inputs.courseStartDate
         endDate <- inputs.courseEndDate
    } yield {
      val (threshold, cappedValues, leaveToRemain) = maintenanceThresholdCalculator.calculateGeneral(inner, tFees, tFeesPaid, aFeesPaid, deps, startDate, endDate, inputs.originalCourseStartDate, inputs.isPreSessional, dependantsOnly)
      new ThresholdResponse(Some(threshold), leaveToRemain, cappedValues, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK))
    }
  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
