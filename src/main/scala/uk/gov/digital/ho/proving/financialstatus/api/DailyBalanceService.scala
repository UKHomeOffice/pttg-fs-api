package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.util.{Optional, UUID}

import org.apache.http.conn.HttpHostConnectException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.client.{HttpClientErrorException, ResourceAccessException}
import uk.gov.digital.ho.proving.financialstatus.api.validation.{DailyBalanceParameterValidator, ServiceMessages}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditActions._
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventPublisher
import uk.gov.digital.ho.proving.financialstatus.audit.configuration.DeploymentDetails
import uk.gov.digital.ho.proving.financialstatus.authentication.Authentication
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker, UserProfile}
import uk.gov.digital.ho.proving.financialstatus.audit.AuditEventType._

import scala.util._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(path = Array("/pttg/financialstatus/v1/accounts/"))
@ControllerAdvice
class DailyBalanceService @Autowired()(val accountStatusChecker: AccountStatusChecker,
                                       val serviceMessages: ServiceMessages,
                                       val auditor: AuditEventPublisher,
                                       val authenticator: Authentication,
                                       val deploymentConfig: DeploymentDetails
                                      ) extends FinancialStatusBaseController with DailyBalanceParameterValidator {

  private val LOGGER = LoggerFactory.getLogger(classOf[DailyBalanceService])

  private val CONSENT_UNAVAILABLE = "consent unavailable"

  @RequestMapping(value = Array("{sortCode:[0-9]+|[0-9-]+}/{accountNumber:[0-9]+}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: Optional[String],
                         @PathVariable(value = "accountNumber") accountNumber: Optional[String],
                         @RequestParam(value = "minimum") minimum: Optional[JBigDecimal],
                         @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: Optional[LocalDate],
                         @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: Optional[LocalDate],
                         @RequestParam(value = "dob") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) dob: Optional[LocalDate],
                         @CookieValue(value = "kc-access") kcToken: Optional[String]
                        ): ResponseEntity[AccountDailyBalanceStatusResponse] = {


    val accessToken: Option[String] = kcToken

    // Get the user's profile
    val userProfile = accessToken match {
      case Some(token) => authenticator.getUserProfileFromToken(token)
      case None => None
    }
    val auditEventId = nextId
    auditSearchParams(auditEventId, sortCode, accountNumber, minimum, toDate, fromDate, userProfile)

    val cleanSortCode: Option[String] = if (sortCode.isPresent) Option(sortCode.get.replace("-", "")) else None

    val validatedInputs = validateInputs(cleanSortCode, accountNumber, minimum, fromDate, toDate, dob, userProfile)

    validatedInputs match {
      case Right(inputs) =>
        val responseEntity: ResponseEntity[AccountDailyBalanceStatusResponse] = validateDailyBalanceStatus(inputs)
        auditSearchResult(auditEventId, responseEntity.getBody, userProfile)
        responseEntity

      case Left(errorList) =>
        // We should be returning all the error messages and not just the first
        buildErrorResponse(headers, errorList.head._1, errorList.head._2, errorList.head._3)
    }
  }

  def auditSearchParams(auditEventId: UUID, sortCode: Option[String], accountNumber: Option[String],
                        minimum: Option[BigDecimal], toDate: Option[LocalDate], fromDate: Option[LocalDate],
                        userProfile: Option[UserProfile]): Unit = {

    val params = Map(
      "sortCode" -> sortCode,
      "accountNumber" -> accountNumber,
      "minimum" -> minimum,
      "toDate" -> toDate,
      "fromDate" -> fromDate,
      "userProfile" -> userProfile
    )

    val suppliedParams = for ((k, Some(v)) <- params) yield k -> v

    val auditData = Map("method" -> "daily-balance-status") ++ suppliedParams

    val principal = userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }
    auditor.publishEvent(auditEvent(deploymentConfig, principal, SEARCH, auditEventId, auditData.asInstanceOf[Map[String, AnyRef]]))
  }

  def auditSearchResult(auditEventId: UUID, response: AccountDailyBalanceStatusResponse, userProfile: Option[UserProfile]): Unit = {
    auditor.publishEvent(auditEvent(deploymentConfig, userProfile match {
      case Some(user) => user.id
      case None => "anonymous"
    }, SEARCH_RESULT, auditEventId,
      Map(
        "method" -> "daily-balance-status",
        "result" -> response
      )
    ))
  }

  def validateDailyBalanceStatus(inputs: ValidatedInputs): ResponseEntity[AccountDailyBalanceStatusResponse] = {

    val response = for {
      sortCode <- inputs.sortCode
      accountNumber <- inputs.accountNumber
      minimum <- inputs.minimum
      fromDate <- inputs.fromDate
      toDate <- inputs.toDate
      dob <- inputs.dob
      userId <- inputs.userId

    } yield {
      val bankAccount = Account(sortCode, accountNumber)

      val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(bankAccount, fromDate, toDate, minimum, dob, userId)

      dailyAccountBalanceCheck match {
        case Success(balanceCheck) => new ResponseEntity(AccountDailyBalanceStatusResponse(Some(bankAccount), Some(balanceCheck),
          StatusResponse(HttpStatus.OK.getReasonPhrase, serviceMessages.OK)), HttpStatus.OK)

        case Failure(exception: HttpClientErrorException) =>
          exception.getStatusCode match {
            case HttpStatus.BAD_REQUEST if exception.getResponseBodyAsString.toLowerCase.contains(CONSENT_UNAVAILABLE) =>
              new ResponseEntity(AccountDailyBalanceStatusResponse(StatusResponse(serviceMessages.REST_API_CLIENT_ERROR,
                serviceMessages.USER_CONSENT_NOT_GIVEN(sortCode, accountNumber))), HttpStatus.FORBIDDEN
              )

            case HttpStatus.NOT_FOUND => new ResponseEntity(
              AccountDailyBalanceStatusResponse(StatusResponse(serviceMessages.REST_API_CLIENT_ERROR,
                serviceMessages.NO_RECORDS_FOR_ACCOUNT(sortCode, accountNumber))), HttpStatus.NOT_FOUND
            )
            case _ => new ResponseEntity(
              AccountDailyBalanceStatusResponse(
                StatusResponse(exception.getStatusCode.toString, exception.getStatusText)), exception.getStatusCode)
          }

        case Failure(exception: ResourceAccessException) =>
          LOGGER.error("Connection refused by bank service : " + exception.getMessage)
          val message = exception.getCause match {
            case e: SocketTimeoutException => serviceMessages.CONNECTION_TIMEOUT
            case e: HttpHostConnectException => serviceMessages.CONNECTION_REFUSED
            case _ => serviceMessages.UNKNOWN_CONNECTION_EXCEPTION
          }
          new ResponseEntity(AccountDailyBalanceStatusResponse(
            StatusResponse(serviceMessages.REST_API_CLIENT_ERROR, message)), HttpStatus.INTERNAL_SERVER_ERROR)

        case Failure(exception: Throwable) =>
          LOGGER.error("Unknown bank service response: " + exception.getMessage)
          new ResponseEntity(AccountDailyBalanceStatusResponse(
            StatusResponse(serviceMessages.REST_API_CLIENT_ERROR, exception.getMessage)), HttpStatus.INTERNAL_SERVER_ERROR)
      }
    }
    response.getOrElse(new ResponseEntity(AccountDailyBalanceStatusResponse(
      StatusResponse(serviceMessages.REST_INTERNAL_ERROR, serviceMessages.UNEXPECTED_ERROR)), HttpStatus.INTERNAL_SERVER_ERROR))
  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String,
                         statusMessage: String, status: HttpStatus): ResponseEntity[AccountDailyBalanceStatusResponse] = {
    new ResponseEntity(AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

}
