package uk.gov.digital.ho.proving.financialstatus.api

import java.lang.{Boolean => JBoolean}
import java.math.{BigDecimal => JBigDecimal}
import java.util.{Optional, UUID}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.api.configuration.DependantsException
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
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
                                             val tierChecker: TierChecker,
                                             val applicantTypeChecker: ApplicantTypeChecker,
                                             val variantTypeChecker: VariantTypeChecker,
                                             val serviceMessages: ServiceMessages,
                                             val auditor: AuditEventPublisher,
                                             val authenticator: Authentication,
                                             val deploymentConfig: DeploymentDetails
                                            ) extends FinancialStatusBaseController {

  private val LOGGER = LoggerFactory.getLogger(classOf[ThresholdServiceTier2And5])
  private val YOUTH_MOBILITY_APPLICANT_TYPE = Option("youth")

  // N.B. RequestMapping receives non-required request params as Java Optional, so convert to Scala Option for pattern matching
  @RequestMapping(value = Array("/threshold"), method = Array(RequestMethod.GET), produces = Array("application/json"))
  def calculateThreshold(@PathVariable(value = "tier") tierType: String,
                         @RequestParam(value = "applicantType") applicantTypeParameter: String,
                         @RequestParam(value = "variantType") rawVariantTypeOptional: Optional[String],
                         @RequestParam(value = "dependants") dependantsOptional: Optional[Integer],
                         @CookieValue(value = "kc-access") kcTokenOptional: Optional[String]
                        ): ResponseEntity[ThresholdResponse] = {

    val tier = tierChecker.getTier(tierType)
    val applicantType = applicantTypeChecker.getApplicantType(applicantTypeParameter)
    val variantTypeOptional: Option[VariantType] = variantTypeChecker.getVariantType(rawVariantTypeOptional, applicantType)
    val numberOfDependants: Int = getNumberOfDependants(dependantsOptional)
    val userProfile = getUserProfile(kcTokenOptional)

    val auditEventId = nextId

    auditSearchParams(auditEventId, applicantType, numberOfDependants, userProfile)

    val threshold = calculateThresholdForApplicantType(tier, variantTypeOptional, applicantType, numberOfDependants)

    auditSearchResult(auditEventId, threshold.getBody, userProfile)

    threshold
  }

  def calculateThresholdForApplicantType(tier: Tier,
                                         variantTypeOptional: Option[VariantType],
                                         applicantType: ApplicantType,
                                         dependants: Int): ResponseEntity[ThresholdResponse] = {

    LOGGER.error("\ncalculateThresholdForApplicantType\n" + tier + "\n" + variantTypeOptional + "\n" + applicantType + "\n" + dependants + "\n")

    applicantType match {

        case DependantApplicant => produceThresholdResponse(tier, applicantType, variantTypeOptional, dependants)
        case MainApplicant =>

          if (dependants > 0 && variantTypeOptional == T5YouthMobilityVariant) {
            produceErrorResponse(headers,
                                serviceMessages.REST_INVALID_PARAMETER_VALUE,
                                serviceMessages.INVALID_DEPENDANTS_NOTALLOWED,
                                HttpStatus.BAD_REQUEST)
          } else {
            produceThresholdResponse(tier, applicantType, variantTypeOptional, dependants)
          }
      }
  }

  def produceThresholdResponse(tier: Tier,
                               validatedApplicantType: ApplicantType,
                               variantTypeOptional: Option[VariantType],
                               dependants: Int): ResponseEntity[ThresholdResponse] = {

    val threshold = maintenanceThresholdCalculator.calculateThresholdForT2AndT5(tier, validatedApplicantType, variantTypeOptional, dependants)
    new ResponseEntity(ThresholdResponse(threshold, None, None, StatusResponse(HttpStatus.OK.toString, serviceMessages.OK)), HttpStatus.OK)
  }

  def produceErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus): ResponseEntity[ThresholdResponse] = {
    new ResponseEntity(ThresholdResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def getNumberOfDependants(dependantsOptional: Optional[Integer]): Int = {
    val numberOfDependants = dependantsOptional.getOrElse(0)

    if (numberOfDependants < 0) {
      throw new DependantsException("Invalid number of dependants: " + numberOfDependants)
    }

    numberOfDependants
  }

  def getUserProfile(kcTokenOptional: Optional[String]): Option[UserProfile] = {
    val accessToken: Option[String] = kcTokenOptional

    accessToken match {
      case Some(token) => authenticator.getUserProfileFromToken(token)
      case None => None
    }
  }

  def auditSearchParams(auditEventId: UUID, applicantType: ApplicantType, numberOfDependants: Int, userProfile: Option[UserProfile]): Unit = {

    val auditData = Map("method" -> "calculate-threshold",
                        "applicantType" -> applicantTypeChecker.getApplicantTypeName(applicantType),
                        "dependants" -> numberOfDependants)

    val principal = userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }

    auditor.publishEvent(auditEvent(deploymentConfig, principal, SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, thresholdResponse: ThresholdResponse, userProfile: Option[UserProfile]): Unit = {

    auditor.publishEvent(auditEvent(deploymentConfig,
                                    userProfile match {
                                      case Some(user) => user.id
                                      case None => "anonymous"
                                    },
                                    SEARCH_RESULT,
                                    auditEventId,
                                    Map(
                                      "method" -> "calculate-threshold",
                                      "result" -> thresholdResponse
                                    )
    ))
  }

}
