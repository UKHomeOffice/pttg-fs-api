package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.Optional
import java.util.UUID

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
@RequestMapping(value = Array("/pttg/financialstatus/v1/{tier:t2|t5}/maintenance"))
@ControllerAdvice
class ThresholdServiceTier2And5 @Autowired()(val maintenanceThresholdCalculator: MaintenanceThresholdCalculatorT2AndT5,
                                             val applicantTypeChecker: ApplicantTypeChecker,
                                             val serviceMessages: ServiceMessages,
                                             val auditor: AuditEventPublisher,
                                             val authenticator: Authentication,
                                             val deploymentConfig: DeploymentDetails
                                            ) extends FinancialStatusBaseController {

  private val LOGGER = LoggerFactory.getLogger(classOf[ThresholdServiceTier2And5])

  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@PathVariable(value = "tier") tier: String,
                         @RequestParam(value = "applicantType") applicantType: Optional[String],
                         @RequestParam(value = "variantType", defaultValue = "Temporary") variantType: Optional[String],
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

    def threshold = calculateThresholdForApplicantType(tier, variantType, validatedApplicantType, dependants)

    auditSearchResult(auditEventId, threshold.getBody, userProfile)
    threshold
  }

  private val YOUTHMOBILITY_APPLICANT_TYPE = Option("youth")


  private def calculateThresholdForApplicantType(tier: String, variantType: Option[String], validatedApplicantType: ApplicantType, dependants: Option[Int]): ResponseEntity[ThresholdResponse] = {
    LOGGER.error("\ncalculateThresholdForApplicantType\n" + tier + "\n" + variantType + "\n" + validatedApplicantType + "\n" + dependants + "\n")
    validatedApplicantType match {
      case UnknownApplicant(_) => buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_APPLICANT_TYPE(applicantTypeChecker.values.mkString(",")), HttpStatus.BAD_REQUEST)
      case MainApplicant =>
        dependants match {
          case _ => dependants match {
            case Some(validDependants) =>
              if (validDependants >= 0) {
                if (validDependants > 0) {
                  variantType match {
                    case YOUTHMOBILITY_APPLICANT_TYPE => buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.iNVALID_DEPENDANTS_NOTALLOWED, HttpStatus.BAD_REQUEST)
                    case _ =>
                      val threshold = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(tier, validatedApplicantType, variantType, validDependants)
                      new ResponseEntity(ThresholdResponse(threshold, None, None, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK)), HttpStatus.OK)
                  }
                } else {
                  val threshold = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(tier, validatedApplicantType, variantType, validDependants)
                  new ResponseEntity(ThresholdResponse(threshold, None, None, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK)), HttpStatus.OK)
                }
              } else {
                buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)
              }
            case None => buildErrorResponse(headers, serviceMessages.REST_INVALID_PARAMETER_VALUE, serviceMessages.INVALID_DEPENDANTS, HttpStatus.BAD_REQUEST)
          }
        }
      case DependantApplicant =>
        val threshold = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(tier, validatedApplicantType, variantType, dependants.getOrElse(0))
        new ResponseEntity(ThresholdResponse(threshold, None, None, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK)), HttpStatus.OK)

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

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] =
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)

}
