package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.PropertySource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.{ResponseEntity, _}
import org.springframework.web.bind.annotation._
import org.springframework.web.client.HttpClientErrorException
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker}

import scala.util._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(path = Array("/pttg/financialstatusservice/v1/accounts/"))
@ControllerAdvice
class DailyBalanceService @Autowired()(val barclaysBankService: MockBankService,
                                       @Value("${daily-balance.days-to-check}") val daysToCheck: Int) {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])

  // TODO Temporary error code until these are finalised or removed
  val TEMP_ERROR_CODE = "0000"

  val sortCodePattern = """^[0-9]{6}$""".r
  val accountNumberPattern = """^[0-9]{8}$""".r

  val headers = new HttpHeaders()
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)

  // TODO get spring to handle LocalDate objects
  @RequestMapping(value = Array("{sortCode:[0-9]+|[0-9-]+}/{accountNumber:[0-9]+}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: String,
                         @PathVariable(value = "accountNumber") accountNumber: String,
                         @RequestParam(value = "minimum") minimum: JBigDecimal,
                         @RequestParam(value = "toDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) toDate: LocalDate,
                         @RequestParam(value = "fromDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) fromDate: LocalDate): ResponseEntity[AccountDailyBalanceStatusResponse] = {

    LOGGER.info("dailybalancestatus request received ")

    val cleanSortCode = sortCode.replace("-", "")
    val validDates = validateDates(fromDate, toDate)

    val response = if (validDates.isLeft) validDates.left.get
    else if (!validateAccountNumber(accountNumber)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid account number", HttpStatus.BAD_REQUEST)
    else if (!validateSortCode(cleanSortCode)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid sort code", HttpStatus.BAD_REQUEST)
    else if (!validateMinimum(minimum)) buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid value for minimum", HttpStatus.BAD_REQUEST)
    else validateDailyBalanceStatus(cleanSortCode, accountNumber, BigDecimal(minimum).setScale(2), fromDate, toDate)

    response
  }

  def validateDailyBalanceStatus(sortCode: String, accountNumber: String, minimum: BigDecimal, fromDate: LocalDate, toDate: LocalDate) = {
    val bankAccount = Account(sortCode, accountNumber)
    val accountStatusChecker = new AccountStatusChecker(barclaysBankService, daysToCheck)
    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(bankAccount, fromDate, toDate, minimum)

    dailyAccountBalanceCheck match {
      case Success(balanceCheck) => new ResponseEntity(AccountDailyBalanceStatusResponse(bankAccount, balanceCheck, StatusResponse("200", "OK")), HttpStatus.OK)

      case Failure(exception: HttpClientErrorException) =>
        exception.getStatusCode match {
          case HttpStatus.NOT_FOUND => new ResponseEntity(
            AccountDailyBalanceStatusResponse(StatusResponse(TEMP_ERROR_CODE, s"No records for sort code ${sortCode} and account number ${accountNumber}")), HttpStatus.NOT_FOUND
          )
          case _ => new ResponseEntity(
            AccountDailyBalanceStatusResponse(
              StatusResponse(exception.getStatusCode.toString, exception.getStatusText)), HttpStatus.INTERNAL_SERVER_ERROR)
        }

      case Failure(exception: Throwable) =>
        LOGGER.info("Unknown bank service error: " + exception.getMessage)
        new ResponseEntity(AccountDailyBalanceStatusResponse(
          StatusResponse(TEMP_ERROR_CODE, "Unknown bank service error")), HttpStatus.INTERNAL_SERVER_ERROR)
    }

  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    new ResponseEntity(AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateAccountNumber(accountNumber: String) = accountNumberPattern.findFirstIn(accountNumber).nonEmpty

  def validateSortCode(sortCode: String) = sortCodePattern.findFirstIn(sortCode).nonEmpty

  def validateMinimum(minimum: JBigDecimal) = minimum != null && JBigDecimal.ZERO.compareTo(minimum) == -1

  def validateDates(fromDate: LocalDate, toDate: LocalDate) = {
    if (fromDate == null) Left(buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid from date", HttpStatus.BAD_REQUEST))
    else if (toDate == null) Left(buildErrorResponse(headers, TEMP_ERROR_CODE, "Parameter error: Invalid to date", HttpStatus.BAD_REQUEST))
    else if (!fromDate.isBefore(toDate) || (!fromDate.plusDays(daysToCheck - 1).equals(toDate)))
      Left(buildErrorResponse(headers, TEMP_ERROR_CODE, s"Parameter error: Invalid dates, from date must be ${daysToCheck - 1} days before to date", HttpStatus.BAD_REQUEST))
    else Right(true)
  }

}
