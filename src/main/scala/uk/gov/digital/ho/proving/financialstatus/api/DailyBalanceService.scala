package uk.gov.digital.ho.proving.financialstatus.api

import java.math.{BigDecimal => JBigDecimal}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.slf4j.{Logger, LoggerFactory}
import org.springframework.beans.factory.annotation.{Autowired, Value}
import org.springframework.context.annotation.PropertySource
import org.springframework.http.{HttpStatus, MediaType, ResponseEntity}
import org.springframework.web.bind.annotation._
import uk.gov.digital.ho.proving.financialstatus.acl.MockBankService
import uk.gov.digital.ho.proving.financialstatus.domain.{Account, AccountStatusChecker}

@RestController
@PropertySource(value=Array("classpath:application.properties"))
@RequestMapping(path = Array("/pttg/financialstatusservice/v1/accounts/"))
class DailyBalanceService @Autowired()(val barclaysBankService: MockBankService,
                                       @Value("${daily-balance.days-to-check}") val daysToCheck: Int) {

  val LOGGER: Logger = LoggerFactory.getLogger(classOf[DailyBalanceService])

  LOGGER.info(s"days to check: ${daysToCheck}")

  // TODO get spring to handle LocalDate objects

  @RequestMapping(value = Array("{sortCode}/{accountNumber}/dailybalancestatus"),
    method = Array(RequestMethod.GET),
    produces = Array(MediaType.APPLICATION_JSON_VALUE))
  def dailyBalanceStatus(@PathVariable(value = "sortCode") sortCode: String,
                         @PathVariable(value = "accountNumber") accountNumber: String,
                         @RequestParam(value = "minimum") minimum: JBigDecimal,
                         @RequestParam(value = "toDate") toDate: String,
                         @RequestParam(value = "fromDate") fromDate: String) = {

    LOGGER.info("dailybalancestatus request received ")

    val bankAccount = Account(sortCode.replace("-", ""), accountNumber)
    val accountStatusChecker = new AccountStatusChecker(barclaysBankService, daysToCheck)
    val dailyAccountBalanceCheck = accountStatusChecker.checkDailyBalancesAreAboveMinimum(
      bankAccount, LocalDate.parse(fromDate, DateTimeFormatter.ofPattern("yyyy-M-d")), LocalDate.parse(toDate, DateTimeFormatter.ofPattern("yyyy-M-d")), BigDecimal(minimum).setScale(2, BigDecimal.RoundingMode.HALF_UP)
    )

    new ResponseEntity(AccountDailyBalanceStatusResponse(bankAccount, dailyAccountBalanceCheck, StatusResponse("200", "OK")), HttpStatus.OK)

  }
}
