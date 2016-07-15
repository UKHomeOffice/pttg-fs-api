package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.net.SocketTimeoutException
import java.time.LocalDate

import org.apache.http.conn.HttpHostConnectException
import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.client.{HttpClientErrorException, ResourceAccessException}
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker}

import scala.util._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(path = Array("/pttg/financialstatusservice/v1/accounts/"))
@ControllerAdvice
class DailyBalanceService @Autowired()(val accountStatusChecker: AccountStatusChecker,
                                       val messageSource: ResourceBundleMessageSource) extends FinancialStatusBaseController {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])

  // TODO Temporary error code until these are finalised or removed
  val TEMP_ERROR_CODE = "0000"

  val BIG_DECIMAL_SCALE = 2
  val sortCodePattern = """^[0-9]{6}$""".r
  val accountNumberPattern = """^[0-9]{8}$""".r

  val headers = new HttpHeaders()

  val INVALID_ACCOUNT_NUMBER = getMessage("invalid.account.number")
  val INVALID_SORT_CODE = getMessage("invalid.sort.code")
  val INVALID_MINIMUM_VALUE = getMessage("invalid.minimum.value")

  val CONNECTION_TIMEOUT = getMessage("connection.timeout")
  val CONNECTION_REFUSED = getMessage("connection.refused")
  val UNKNOWN_CONNECTION_EXCEPTION = getMessage("unknown.connection.exception")
  val INVALID_FROM_DATE = getMessage("invalid.from.date")
  val INVALID_TO_DATE = getMessage("invalid.to.date")

  def NO_RECORDS_FOR_ACCOUNT(params: String*) = getMessage("no.records.for.account", params)

  def INVALID_DATES(params: Int*) = getMessage("invalid.dates", params)

  val INVALID_SORT_CODE_VALUE = "000000"
  val INVALID_ACCOUNT_NUMBER_VALUE = "00000000"
  val OK = "OK"

  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  logStartupInformation()

  @RequestMapping(value = Array("{sortCode:[0-9]+|[0-9-]+}/{accountNumber:[0-9]+}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: String,
                         @PathVariable(value = "accountNumber") accountNumber: String,
                         @RequestParam(value = "minimum") minimum: JBigDecimal,
                         @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate,
                         @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate
                        ): ResponseEntity[AccountDailyBalanceStatusResponse] = {

    val cleanSortCode = sortCode.replace("-", "")
    val validDates = validateDates(fromDate, toDate)

    val response = if (validDates.isLeft) validDates.left.get
    else if  (!validateAccountNumber(accountNumber)) buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_ACCOUNT_NUMBER, HttpStatus.BAD_REQUEST)
    else if (!validateSortCode(cleanSortCode)) buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_SORT_CODE, HttpStatus.BAD_REQUEST)
    else if (!validateMinimum(minimum)) buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_MINIMUM_VALUE, HttpStatus.BAD_REQUEST)
    else validateDailyBalanceStatus(cleanSortCode, accountNumber, BigDecimal(minimum).setScale(BIG_DECIMAL_SCALE), fromDate, toDate)

    timer("dailyBalances") {
      val auditMessage = s"validateDailyBalance: accountNumber = $accountNumber, " +
        s"sortCode = $cleanSortCode, minimum = $minimum, fromDate = $fromDate, toDate = $toDate"
      audit(auditMessage) {
        response
      }
    }
  }

  def validateDailyBalanceStatus(sortCode: String, accountNumber: String, minimum: BigDecimal,
                                 fromDate: LocalDate, toDate: LocalDate): ResponseEntity[AccountDailyBalanceStatusResponse] = {
    val bankAccount = Account(sortCode, accountNumber)

    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(bankAccount, fromDate, toDate, minimum)

    dailyAccountBalanceCheck match {
      case Success(balanceCheck) => new ResponseEntity(AccountDailyBalanceStatusResponse(bankAccount, balanceCheck, StatusResponse("200", OK)), HttpStatus.OK)

      case Failure(exception: HttpClientErrorException) =>
        exception.getStatusCode match {
          case HttpStatus.NOT_FOUND => new ResponseEntity(
            AccountDailyBalanceStatusResponse(StatusResponse(TEMP_ERROR_CODE, NO_RECORDS_FOR_ACCOUNT(sortCode, accountNumber))), HttpStatus.NOT_FOUND
          )
          case _ => new ResponseEntity(
            AccountDailyBalanceStatusResponse(
              StatusResponse(exception.getStatusCode.toString, exception.getStatusText)), HttpStatus.INTERNAL_SERVER_ERROR)
        }

      case Failure(exception: ResourceAccessException) =>
        LOGGER.error("Connection refused by bank service : " + exception.getMessage)
        val message = exception.getCause match {
          case e: SocketTimeoutException => CONNECTION_TIMEOUT
          case e: HttpHostConnectException => CONNECTION_REFUSED
          case _ => UNKNOWN_CONNECTION_EXCEPTION
        }
        new ResponseEntity(AccountDailyBalanceStatusResponse(
          StatusResponse(TEMP_ERROR_CODE, message)), HttpStatus.INTERNAL_SERVER_ERROR)

      case Failure(exception: Throwable) =>
        LOGGER.error("Unknown bank service error: " + exception.getMessage)
        new ResponseEntity(AccountDailyBalanceStatusResponse(
          StatusResponse(TEMP_ERROR_CODE, exception.getMessage)), HttpStatus.INTERNAL_SERVER_ERROR)
    }

  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String,
                         statusMessage: String, status: HttpStatus): ResponseEntity[AccountDailyBalanceStatusResponse] = {
    new ResponseEntity(AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateAccountNumber(accountNumber: String): Boolean = accountNumberPattern.findFirstIn(accountNumber).nonEmpty && accountNumber != INVALID_ACCOUNT_NUMBER_VALUE

  def validateSortCode(sortCode: String): Boolean = sortCodePattern.findFirstIn(sortCode).nonEmpty && sortCode != INVALID_SORT_CODE_VALUE

  def validateMinimum(minimum: JBigDecimal) : Boolean = minimum != null && JBigDecimal.ZERO.compareTo(minimum) == -1

  def validateDates(fromDate: LocalDate, toDate: LocalDate): Either[ResponseEntity[AccountDailyBalanceStatusResponse], Boolean] = {

    if (fromDate == null) Left(buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_FROM_DATE, HttpStatus.BAD_REQUEST))
    else if (toDate == null) Left(buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_TO_DATE, HttpStatus.BAD_REQUEST))
    else if (!fromDate.isBefore(toDate) || (!fromDate.plusDays(accountStatusChecker.numberConsecutiveDays - 1).equals(toDate)))
      Left(buildErrorResponse(headers, TEMP_ERROR_CODE, INVALID_DATES(accountStatusChecker.numberConsecutiveDays - 1), HttpStatus.BAD_REQUEST))
    else Right(true)
  }

  override def logStartupInformation(): Unit = {
    LOGGER.info(accountStatusChecker.parameters)
  }

}
