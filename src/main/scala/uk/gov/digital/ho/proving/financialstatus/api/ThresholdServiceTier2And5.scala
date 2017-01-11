package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Optional, UUID}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.api.validation.{ServiceMessages, ThresholdParameterValidator}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions.{auditEvent, nextId}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventType._
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain._


@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatusservice/v1/{tier:t2|t5}/maintenance"))
@ControllerAdvice
class ThresholdServiceTier2And5 @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculatorT2AndT5,
                                             val applicantTypeChecker: ApplicantTypeChecker,
                                             val serviceMessages: ServiceMessages,
                                             val auditor: ApplicationEventPublisher,
                                             val authenticator: Authentication
                                            ) extends FinancialStatusBaseController  {

  private val LOGGER = LoggerFactory.getLogger(classOf[ThresholdServiceTier2And5])

  logStartupInformation()

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@RequestParam(value = "applicantType") applicantType: Optional[String],
                         @RequestParam(value = "dependants", required = false, defaultValue = "0") dependants: Optional[Integer],
                         @CookieValue(value = "kc-access") kcToken: Optional[String]
                        ): ResponseEntity[ThresholdResponse] = {

    val accessToken: Option[String] = kcToken

    // Get the user's profile
    val userProfile = accessToken match {
      case Some(token) => authenticator.getUserProfileFromToken(token)
      case None => None
    }

    val auditEventId = nextId
    auditSearchParams(auditEventId, applicantType, dependants, userProfile)

    val validatedApplicantType = applicantTypeChecker.getApplicantType(applicantType.getOrElse("Unknown"))

    def threshold = calculateThresholdForApplicantType(validatedApplicantType, dependants)

    auditSearchResult(auditEventId, threshold.getBody, userProfile)

    threshold
  }


  private def calculateThresholdForApplicantType(validatedApplicantType: ApplicantType, dependants: Option[Int]):  ResponseEntity[ThresholdResponse] = {
    validatedApplicantType match {
      case UnknownApplicant(_) =>buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_APPLICANT_TYPE(applicantTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
      case _ => dependants match {
        case Some(validDependants) =>
          val threshold = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(validatedApplicantType, validDependants)
          new ResponseEntity(ThresholdResponse(threshold, None, None, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK)),HttpStatus.OK)
        case None =>buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)

      }
    }
  }

  def auditSearchParams(auditEventId: UUID, applicantType: Option[String], dependants: Option[Int], userProfile: Option[UserProfile]): Unit = {

    val params = Map(
      "applicantType" -> applicantType,
      "dependants" -> dependants
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

  override def logStartupInformation(): Unit = LOGGER.info(maintenanceThresholdCalculator.parameters)

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
