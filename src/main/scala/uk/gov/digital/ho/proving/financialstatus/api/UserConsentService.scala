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
import org.springframework.web.client.HttpClientErrorException
import uk.gov.digital.ho.proving.financialstatus.api.validation.ServiceMessages
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions.{auditEvent, nextId}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventType._
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain._

import scala.util.{Failure, Success, Try}

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(value = Array("/pttg/financialstatus/v1/accounts/"))
@ControllerAdvice
class UserConsentService @Autowired()(val userConsentStatusChecker: UserConsentStatusChecker,
                                      val serviceMessages: ServiceMessages,
                                      val auditor: ApplicationEventPublisher,
                                      val authenticator: Authentication
                                     ) extends FinancialStatusBaseController {

  private val LOGGER = LoggerFactory.getLogger(classOf[UserConsentService])

  @RequestMapping(value = Array("{sortCode:[0-9]+|[0-9-]+}/{accountNumber:[0-9]+}/consent"),
    method = Array(RequestMethod.GET), produces = Array("application/json"))
  def bankConsent(@PathVariable(value = "sortCode") sortCode: Optional[String],
                  @PathVariable(value = "accountNumber") accountNumber: Optional[String],
                  @RequestParam(value = "dob") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dob: Optional[LocalDate],
                  @CookieValue(value = "kc-access") kcToken: Optional[String]
                 ): ResponseEntity[BankConsentResponse] = {

    val (userProfile, userId) = getUserProfile(kcToken)

    val auditEventId = nextId
    auditSearchParams(auditEventId, sortCode, accountNumber, userProfile)

    val response = Try {
      val consent = checkConsent(sortCode, accountNumber, dob, userId)

      consent match {
        case Some(result) => auditSearchResult(auditEventId, result.toString, userProfile)
          new ResponseEntity(BankConsentResponse(Option(consent.get.result.status), StatusResponse(HttpStatus.OK.value().toString, HttpStatus.OK.getReasonPhrase)), HttpStatus.OK)
        case None => buildErrorResponse(headers, "400", "400", HttpStatus.BAD_REQUEST)
      }
    }
    response match {
      case Success(success) => success
      case Failure(exception: HttpClientErrorException) => buildErrorResponse(headers, serviceMessages.REST_API_CLIENT_ERROR,
        serviceMessages.NO_RECORDS_FOR_ACCOUNT(sortCode.getOrElse(""), accountNumber.getOrElse("")), HttpStatus.valueOf(exception.getRawStatusCode))
      case Failure(exception) => buildErrorResponse(headers, serviceMessages.REST_INTERNAL_ERROR, exception.getMessage, HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

  def checkConsent(sortCode: Option[String], accountNumber: Option[String], dob: Option[LocalDate], userId: String) = {

    val consent = for {sCode <- sortCode
                       accountNo <- accountNumber
                       dateOfBirth <- dob} yield {

      val response = userConsentStatusChecker.checkUserConsent(sCode + accountNo, sCode, accountNo, dateOfBirth, userId)
      response
    }
    consent
  }

  def auditSearchParams(auditEventId: UUID, sortCode: Option[String], accountNumber: Option[String],
                        userProfile: Option[UserProfile]): Unit = {

    val params = Map(
      "sortCode" -> sortCode,
      "accountNumber" -> accountNumber,
      "userProfile" -> userProfile
    )

    val suppliedParams = for ((k, Some(v)) <- params) yield k -> v

    val auditData = Map("method" -> "daily-balance-status") ++ suppliedParams

    val principal = userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }
    auditor.publishEvent(auditEvent(principal, SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, response: String, userProfile: Option[UserProfile]): Unit = {
    auditor.publishEvent(auditEvent(userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }, SEARCH_RESULT, auditEventId,
      Map(
        "method" -> "bank-consent-status",
        "result" -> response
      )
    ))
  }

  private def getUserProfile(token: Option[String]): (Option[UserProfile], String) = {
    // Get the user's profile
    val userProfile = token match {
      case Some(token) => authenticator.getUserProfileFromToken(token)
      case None => None
    }

    userProfile match {
      case Some(profile) => (userProfile, profile.id)
      case None => (None, "")
    }

  }

  private def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) =
    new ResponseEntity(BankConsentResponse(None, StatusResponse(statusCode, statusMessage)), headers, status)

}
