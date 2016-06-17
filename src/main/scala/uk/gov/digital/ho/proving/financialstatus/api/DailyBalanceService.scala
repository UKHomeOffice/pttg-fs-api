package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpHeaders, HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import org.springframework.web.client.{HttpClientErrorException, RestClientException}
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker}

import scala.util._

@RestController
@PropertySource(value = Array("classpath:application.properties"))
@RequestMapping(path = Array("/pttg/financialstatusservice/v1/accounts/"))
class DailyBalanceService @Autowired()(val barclaysBankService: MockBankService,
                                       @Value("${daily-balance.days-to-check}") val daysToCheck: Int) {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])

  val sortCodePattern = """^[0-9]{6}$""".r
  val accountNumberPattern = """^[0-9]{8}$""".r

  val headers = new HttpHeaders();
  headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

  // TODO get spring to handle LocalDate objects
  @RequestMapping(value = Array("{sortCode}/{accountNumber}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: String,
                         @PathVariable(value = "accountNumber") accountNumber: String,
                         @RequestParam(value = "minimum") minimum: JBigDecimal,
                         @RequestParam(value = "toDate") toDate: String,
                         @RequestParam(value = "fromDate") fromDate: String): ResponseEntity[AccountDailyBalanceStatusResponse] = {

    LOGGER.info("dailybalancestatus request received ")

    val cleanSortCode = sortCode.replace("-", "")
    val validDates = validateDates(fromDate, toDate)

    val response = if (validDates.isLeft) validDates.left.get
    else if (!validateAccountNumber(accountNumber)) buildErrorResponse(headers, "0000", "Parameter error: Invalid account number", HttpStatus.BAD_REQUEST)
    else if (!validateSortCode(cleanSortCode)) buildErrorResponse(headers, "0000", "Parameter error: Invalid sort code", HttpStatus.BAD_REQUEST)
    else if (!validateMinimum(minimum)) buildErrorResponse(headers, "0000", "Parameter error: Invalid Total Funds Required", HttpStatus.BAD_REQUEST)
    else validateDailyBalanceStatus(cleanSortCode, accountNumber, BigDecimal(minimum).setScale(2), LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE), LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE))

    response
  }

  def validateDailyBalanceStatus(sortCode: String, accountNumber: String, minimum: BigDecimal, fromDate: LocalDate, toDate: LocalDate) = {
    val bankAccount = Account(sortCode, accountNumber)
    val accountStatusChecker = new AccountStatusChecker(barclaysBankService, daysToCheck)
    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(bankAccount, fromDate, toDate, minimum)

    dailyAccountBalanceCheck match {
      case Success(balanceCheck) => new ResponseEntity(AccountDailyBalanceStatusResponse(bankAccount, balanceCheck, StatusResponse("200", "OK")), HttpStatus.OK)
      case Failure(exception:HttpClientErrorException) =>new ResponseEntity(AccountDailyBalanceStatusResponse(
        StatusResponse(exception.getStatusCode.toString, exception.getStatusText)), exception.getStatusCode)
      case Failure(exception: Throwable) =>
        LOGGER.info("Unexpected error form bank service: " + exception.getMessage)
        new ResponseEntity(AccountDailyBalanceStatusResponse(
        StatusResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Unknown error")), HttpStatus.INTERNAL_SERVER_ERROR)
    }

  }

  def buildErrorResponse(headers: HttpHeaders, statusCode: String, statusMessage: String, status: HttpStatus) = {
    new ResponseEntity(AccountDailyBalanceStatusResponse(StatusResponse(statusCode, statusMessage)), headers, status)
  }

  def validateAccountNumber(accountNumber: String) = accountNumberPattern.findFirstIn(accountNumber).nonEmpty

  def validateSortCode(sortCode: String) = sortCodePattern.findFirstIn(sortCode).nonEmpty

  def validateMinimum(minimum: BigDecimal) = minimum > 0

  /**
    * Takes two dates in string format, tries to converts them to LocalDate objects and
    * ensures fromDate is before toDate.  If sucessfull it returns Right[true] otherwise
    * it returns a Left[ResponseEntity[AccountDailyBalanceStatusResponse]] containing
    * the appropriate error message
    *
    * This can be simplified once Spring is handling the String to LocalDate conversion
    *
    * @param fromDate
    * @param toDate
    * @return Either[ResponseEntity[AccountDailyBalanceStatusResponse], Boolean]
    */
  def validateDates(fromDate: String, toDate: String) = {
    val validFromDate = Try(LocalDate.parse(fromDate, DateTimeFormatter.ISO_DATE))
    val validToDate = Try(LocalDate.parse(toDate, DateTimeFormatter.ISO_DATE))

    if (validFromDate.isFailure) Left(buildErrorResponse(headers, "0000", "Parameter error: Invalid from date", HttpStatus.BAD_REQUEST))
    else if (validToDate.isFailure) Left(buildErrorResponse(headers, "0000", "Parameter error: Invalid to date", HttpStatus.BAD_REQUEST))
    else {
      val validDates =
        for {vfd <- validFromDate
             vtd <- validToDate} yield {
          vfd.isBefore(vtd)
        }

      if (validDates.get) Right(true)
      else Left(buildErrorResponse(headers, "0000", "Parameter error: Invalids dates, from date must be before to date", HttpStatus.BAD_REQUEST))
    }

  }

}
